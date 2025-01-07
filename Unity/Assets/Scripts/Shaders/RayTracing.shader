Shader "Rendering/RayTracing"
{
    SubShader
    {
        // No culling or depth
        Cull Off ZWrite Off ZTest Always

        Pass
        {
            CGPROGRAM
            #pragma vertex vert
            #pragma fragment frag

            #include "UnityCG.cginc"

            struct appdata
            {
                float4 vertex : POSITION;
                float2 uv : TEXCOORD0;
            };

            struct v2f
            {
                float2 uv : TEXCOORD0;
                float4 vertex : SV_POSITION;
            };

            v2f vert (appdata v)
            {
                v2f o;
                o.vertex = UnityObjectToClipPos(v.vertex);
                o.uv = v.uv;
                return o;
            }

            // ! ---Settings and constants---

            #define PI 3.1415926535897932384626433832795
            #define FLT_MAX 3.402823466e+38F
            #define FLT_MIN 1.175494351e-38F

            uint Seed;

            // Ray Tracing Settings
            int MaxBounceCount;
            int NumRaysPerPixel;
            int Frame;

            // Camera Settings
            float DivergeStrength;
            float DefocusStrength;
            float3 ViewParams;
            float4x4 CamLocalToWorldMatrix;

            // Environment Settings
            int EntvironmentEnabled;
            float4 GroundColor;
            float4 SkyColorHorizon;
            float4 SkyColorZenith;
            float SunFocus;
            float SunIntensity;


            // ! ---Structures---

            struct Ray
            {
                float3 origin;
                float3 dir;
            };

            struct RayTracingMaterial
            {
                float4 color;
                float4 emissionColor;
                float4 specularColor;
                float emissionStrength;
                float smoothness;
                float specularProbability;
            };

            struct Sphere
            {
                float3 center;
                float radius;
                RayTracingMaterial material;
            };

            struct Triangle
            {
                float3 posA, posB, posC;
                float3 normalA, normalB, normalC;
            };

            struct MeshInfo
            {
                uint firstTriangleIndex;
                uint numTriangles;
                RayTracingMaterial material;
                float3 boundsMin;
                float3 boundsMax;
            };

            struct RecordHit
            {
                float3 hitPoint;
                float t;
                float dst;
                float3 normal;
                RayTracingMaterial material;
            };


            // ! ---Buffers---
            StructuredBuffer<Sphere> Spheres;
            int NumSpheres;

            StructuredBuffer<Triangle> Triangles;
            StructuredBuffer<MeshInfo> AllMeshInfo;
            int NumMeshes;

            // ! ---Functions---

            // * Random Functions
            float Random()
            {
                Seed = Seed * 747796405 + 2891336453;
                uint result = ((Seed >> ((Seed >> 28) + 4)) ^ Seed) * 277803737;
				result = (result >> 22) ^ result;
				return result / 4294967295.0f; // (2^32 - 1)
            }

            float Random(float minVal, float maxVal)
            {
                return minVal + (maxVal-minVal)*Random();
            }

            float RandomNormalDistribution()
            {
                // Thanks to https://stackoverflow.com/a/6178290
                float theta = 2 * PI * Random();
                float rho = sqrt(-2 * log(Random()));
                return rho * cos(theta);
            }

            float3 RandomDirection()
            {
                float x = RandomNormalDistribution();
                float y = RandomNormalDistribution();
                float z = RandomNormalDistribution();
                return normalize(float3(x,y,z));
            }

            float3 RandomHemisphereDirection(float3 normal)
            {
                float3 dir = RandomDirection();
                return dir * sign(dot(normal, dir));
            }

            float2 RandomPointInCircle()
            {
                float angle = Random() * 2 * PI;
                float2 pointOnCircle = float2(cos(angle), sin(angle));
                return pointOnCircle * sqrt(Random());
            }


            // Thanks to https://gist.github.com/DomNomNom/46bb1ce47f68d255fd5d
			bool RayBoundingBox(Ray ray, float3 boxMin, float3 boxMax)
			{
				float3 invDir = 1 / ray.dir;
				float3 tMin = (boxMin - ray.origin) * invDir;
				float3 tMax = (boxMax - ray.origin) * invDir;
				float3 t1 = min(tMin, tMax);
				float3 t2 = max(tMin, tMax);
				float tNear = max(max(t1.x, t1.y), t1.z);
				float tFar = min(min(t2.x, t2.y), t2.z);
				return tNear <= tFar;
			}

            float3 RayAt(const Ray ray, const float t) {
                return ray.origin + t * ray.dir;
            }

            bool HitSphere(const Ray r, Sphere s, inout RecordHit rec)
            {
                float3 oc = s.center - r.origin;
                float a = dot(r.dir, r.dir);
                float h = dot(r.dir, oc);
                float c = dot(oc, oc) - s.radius * s.radius;
                float discriminant = h*h - a*c;

                if (discriminant < 0.0)
                    return false;

                float sqrtd = sqrt(discriminant);

                // Find the nearest root that lies in the acceptable range.
                float t = (h - sqrtd) / a;
                if (t <= 0 || FLT_MAX <= t) {
                    t = (h + sqrtd) / a;
                    if (t <= 0 || FLT_MAX <= t)
                        return false;
                }

                rec.t = t;
                rec.hitPoint = RayAt(r, t);
                rec.normal = normalize(rec.hitPoint - s.center);
                rec.dst = length(rec.hitPoint - r.origin);
                return true;
            }

            bool HitTriangle(const Ray ray, Triangle tri, inout RecordHit rec)
            {
                float3 edgeAB = tri.posB - tri.posA;
                float3 edgeAC = tri.posC - tri.posA;
                float3 normalVector = cross(edgeAB, edgeAC);
                float3 ao = ray.origin - tri.posA;
                float3 dao = cross(ao, ray.dir);

                float determinant = -dot(ray.dir, normalVector);
                float invDet = 1 / determinant;

                // Calculate distance to triangle & barycentric coordinates of intersection point
                float dst = dot(ao, normalVector) * invDet;
                float u = dot(edgeAC, dao) * invDet;
                float v = -dot(edgeAB, dao) * invDet;
                float w = 1 - u - v;

                // Initialize hit record
                rec.hitPoint = ray.origin + dst * ray.dir;
                rec.normal = normalize(tri.normalA * w + tri.normalB * u + tri.normalC * v);
                rec.dst = dst;
                return determinant >= 1E-6 && dst >= 0 && u >= 0 && v >= 0 && w >= 0;
            }

            bool CalculateRayCollisions(Ray r, inout RecordHit closestHit)
            {
                closestHit.dst = 1.#INF;

                // For each sphere
                for (int i = 0; i < NumSpheres; i++)
                {
                    Sphere sphere = Spheres[i];
                    RecordHit rec = (RecordHit) 0;

                    if (HitSphere(r, sphere, rec) && rec.dst < closestHit.dst)
                    {
                        closestHit = rec;
                        closestHit.material = sphere.material;
                    }
                }

                // For each mesh
                for (int i = 0; i < NumMeshes; i++)
                {
                    MeshInfo meshInfo = AllMeshInfo[i];
                    if (!RayBoundingBox(r, meshInfo.boundsMin, meshInfo.boundsMax))
                    {
                        continue;
                    }

                    for (int j = 0; j < meshInfo.numTriangles; j++)
                    {
                        int triangleIndex = meshInfo.firstTriangleIndex + j;
                        Triangle tri = Triangles[triangleIndex];
                        RecordHit rec = (RecordHit) 0;

                        if (HitTriangle(r, tri, rec) && rec.dst < closestHit.dst)
                        {
                            closestHit = rec;
                            closestHit.material = meshInfo.material;
                        }
                    }
                }

                return closestHit.dst < 1.#INF;
            }

            float3 GetEnviromentLight(Ray ray)
            {
                if (EntvironmentEnabled == 0)
                    return 0;

                float skyGradientT = pow(smoothstep(0, 0.4, ray.dir.y), 0.35);
				float groundToSkyT = smoothstep(-0.01, 0, ray.dir.y);
				float3 skyGradient = lerp(SkyColorHorizon, SkyColorZenith, skyGradientT);
				float sun = pow(max(0, dot(ray.dir, _WorldSpaceLightPos0.xyz)), SunFocus) * SunIntensity;
				// Combine ground, sky, and sun
				float3 composite = lerp(GroundColor, skyGradient, groundToSkyT) + sun * (groundToSkyT>=1);
				return composite;
            }

            // * Trace the path of a ray of light (in reverse) as it travels form the camera,
            // * reflects off objects in the scene, and ends up at the light source.
            float3 Trace(Ray ray)
            {
                float3 incomingLight = 0;
                float3 rayColor = 1;
                for (uint i = 0; i <= MaxBounceCount; i++)
                {
                    RecordHit rec = (RecordHit) 0;
                    if (CalculateRayCollisions(ray, rec))
                    {
                        RayTracingMaterial material = rec.material;
                        bool isSpecularBounce = material.specularProbability >= Random();

                        ray.origin = rec.hitPoint;
                        float3 diffuseDir = normalize(rec.normal + RandomDirection());
                        float3 specularDir = reflect(ray.dir, rec.normal);
                        ray.dir = normalize(lerp(diffuseDir, specularDir, material.smoothness * isSpecularBounce));

                        float3 emittedLight = material.emissionColor * material.emissionStrength;
                        incomingLight += emittedLight * rayColor * 2;
                        rayColor *= lerp(material.color, material.specularColor, isSpecularBounce);

                        // Random early exit if ray color is nearly 0
                        float p = max(rayColor.r, max(rayColor.g, rayColor.b));
                        if (Random() >= p)
                        {
                            break;
                        }

                        rayColor *= 1.0f / p;

                    } else {
                        incomingLight += GetEnviromentLight(ray) * rayColor;
                        break;
                    }
                }

                return incomingLight;
            }


            // ? Main
            float4 frag (v2f i) : SV_Target
            {
                // Create seed for random number generator
                uint2 numPixels = _ScreenParams.xy;
				uint2 pixelCoord = i.uv * numPixels;
				uint pixelIndex = pixelCoord.y * numPixels.x + pixelCoord.x;
				Seed = pixelIndex + Frame * 719393;

                // Create a Ray
                float3 viewPointLocal = float3(i.uv - 0.5, 1) * ViewParams;
                float3 viewPoint = mul(CamLocalToWorldMatrix, float4(viewPointLocal, 1));
                float3 camRight = CamLocalToWorldMatrix._m00_m10_m20;
                float3 camUp = CamLocalToWorldMatrix._m01_m11_m21;

                // Calculate pixel Color
                Ray ray;
                float3 totalIncomingLight = 0;

                for (int rayIndex = 0; rayIndex < NumRaysPerPixel; rayIndex++)
                {
                    // Calculate Ray origin and direction
                    float2 defocusJitter = Random() * DefocusStrength / numPixels.x;
                    ray.origin = _WorldSpaceCameraPos + camRight * defocusJitter.x + camUp * defocusJitter.y;

                    float2 jitter = RandomPointInCircle() * DivergeStrength / numPixels.x;
                    float3 jitteredViewPoint = viewPoint + camRight * jitter.x + camUp * jitter.y;
                    ray.dir = normalize(jitteredViewPoint - ray.origin);

                    // Trace
                    totalIncomingLight += Trace(ray);
                }

                float3 pixelColor = totalIncomingLight / NumRaysPerPixel;
                return float4(pixelColor * pixelColor, 1);
            }
            ENDCG
        }
    }
}
