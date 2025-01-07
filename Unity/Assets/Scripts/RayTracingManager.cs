using System;
using System.Collections.Generic;
using Unity.VisualScripting;
using UnityEngine;
using static UnityEngine.Mathf;

[ExecuteInEditMode, ImageEffectAllowedInSceneView]
public class RayTracingManager : MonoBehaviour
{
    public const int TriangleLimit = 1500;

    [Header("Ray Tracing Settings")]
    [SerializeField, Range(0, 64)] private int maxBounceCount = 10;
    [SerializeField, Range(0, 32)] private int numRaysPerPixel = 2;
    [SerializeField, Min(0)] private float divergeStrength = 0;
    [SerializeField, Min(0)] private float convergenceStrength = 0;
    [SerializeField, Min(0)] private float focusDistance = 1;
    [SerializeField] EntvironmentSettings environmentSettings;

    [Header("View Settings")]
    [SerializeField] private bool useShaderInSceneView;

    [Header("References")]
    [SerializeField] private Shader rayTracingShader;
    [SerializeField] private Shader accumulateShader;

    [Header("Info")]
    [SerializeField] private int numRenderedFrames;
    [SerializeField] private int numMeshChunks;
    [SerializeField] private int numTriangles;

    [Header("Debug")]
    [SerializeField] private Mesh debugFocusMesh;
    [SerializeField] private bool debugFocus = false;
    [SerializeField] private bool debugBoundingBox = false;


    private Material rayTracingMaterial;
    private Material accumulateMaterial;
    private RenderTexture resultTexture;

    private ComputeBuffer sphereBuffer;
    private ComputeBuffer triangleBuffer;
	private ComputeBuffer meshInfoBuffer;
	private List<Triangle> allTriangles;
	private List<MeshInfo> allMeshInfo;


    void Start()
    {
        numRenderedFrames = 0;
    }

    // Called after each camera (e.g. game or scene camera) has finished rendering into the src texture
    void OnRenderImage(RenderTexture src, RenderTexture dest) {
        if (!useShaderInSceneView)
        {
            Graphics.Blit(src, dest);
            return;
        }

        if (!Application.isPlaying)
        {
            InitFrame();
            Graphics.Blit(null, dest, rayTracingMaterial);
        } else { // * Rendering fully

            InitFrame();

            // Create copy of previous Frame
            RenderTexture prevFrameCopy = RenderTexture.GetTemporary(src.width, src.height, 0, ShaderHelper.RGBA_SFloat);
            Graphics.Blit(resultTexture, prevFrameCopy);

            // Run the ray tracing shader and draw the result to a temp texture
            rayTracingMaterial.SetInt("Frame", numRenderedFrames);
            RenderTexture currentFrame = RenderTexture.GetTemporary(src.width, src.height, 0, ShaderHelper.RGBA_SFloat);
            Graphics.Blit(null, currentFrame, rayTracingMaterial);

            // Accumulate the results
            accumulateMaterial.SetInt("_Frame", numRenderedFrames);
            accumulateMaterial.SetTexture("_PrevFrame", prevFrameCopy);
            Graphics.Blit(currentFrame, resultTexture, accumulateMaterial);

            // Draw result to screen
            Graphics.Blit(resultTexture, dest);

            // Release temps
            RenderTexture.ReleaseTemporary(currentFrame);
            RenderTexture.ReleaseTemporary(prevFrameCopy);

            numRenderedFrames++;
        }
    }

    void InitFrame()
    {
        // Create materials used in blits
        ShaderHelper.InitMaterial(rayTracingShader, ref rayTracingMaterial);
        ShaderHelper.InitMaterial(accumulateShader, ref accumulateMaterial);

        // Create result render texture
        ShaderHelper.CreateRenderTexture(ref resultTexture, Screen.width, Screen.height, FilterMode.Bilinear,
                    ShaderHelper.RGBA_SFloat, "Result");

        // Update data
        UpdateCameraParams(Camera.current);
        CreateSpheres();
        CreateMeshes();
        SetShaderParams();
    }

    void CreateMeshes()
    {
        RayTracedMesh[] meshObjects = FindObjectsOfType<RayTracedMesh>();

        allTriangles ??= new List<Triangle>();
        allMeshInfo ??= new List<MeshInfo>();
        allTriangles.Clear();
        allMeshInfo.Clear();

        for (int i = 0; i < meshObjects.Length; i++)
        {
            MeshChunk[] chunks = meshObjects[i].GetSubMeshes();
            foreach (MeshChunk chunk in chunks)
            {
                RayTracingMaterial material = meshObjects[i].GetMaterial(chunk.subMeshIndex);
                allMeshInfo.Add(new MeshInfo(allTriangles.Count, chunk.triangles.Length, material, chunk.bounds));
                allTriangles.AddRange(chunk.triangles);
            }
        }

        numMeshChunks = allMeshInfo.Count;
        numTriangles = allTriangles.Count;

        ShaderHelper.CreateStructuredBuffer(ref triangleBuffer, allTriangles);
        ShaderHelper.CreateStructuredBuffer(ref meshInfoBuffer, allMeshInfo);
        rayTracingMaterial.SetBuffer("Triangles", triangleBuffer);
        rayTracingMaterial.SetBuffer("AllMeshInfo", meshInfoBuffer);
        rayTracingMaterial.SetInt("NumMeshes", allMeshInfo.Count);
    }

    void CreateSpheres()
    {
        RayTracedSphere[] sphereObjects = FindObjectsOfType<RayTracedSphere>();
        Sphere[] spheres = new Sphere[sphereObjects.Length];
        for (int i = 0; i < spheres.Length; i++)
        {
            spheres[i] = new Sphere
            {
                position = sphereObjects[i].transform.position,
                radius = sphereObjects[i].transform.localScale.x * 0.5f,
                material = sphereObjects[i].material
            };
        }

        // Create buffer containing all data, and send it to the shader
        ShaderHelper.CreateStructuredBuffer(ref sphereBuffer, spheres);
        rayTracingMaterial.SetBuffer("Spheres", sphereBuffer);
        rayTracingMaterial.SetInt("NumSpheres", spheres.Length);
    }

    void SetShaderParams()
    {
        rayTracingMaterial.SetInt("MaxBounceCount", maxBounceCount);
        rayTracingMaterial.SetInt("NumRaysPerPixel", numRaysPerPixel);
        rayTracingMaterial.SetFloat("DivergeStrength", divergeStrength);
        rayTracingMaterial.SetFloat("DefocusStrength", convergenceStrength);
        rayTracingMaterial.SetInt("DebugFocus", debugFocus ? 1 : 0);

        rayTracingMaterial.SetInteger("EntvironmentEnabled", environmentSettings.enabled ? 1 : 0);
        rayTracingMaterial.SetColor("GroundColor", environmentSettings.groundColor);
        rayTracingMaterial.SetColor("SkyColorHorizon", environmentSettings.skyColorHorizon);
        rayTracingMaterial.SetColor("SkyColorZenith", environmentSettings.skyColorZenith);
        rayTracingMaterial.SetFloat("SunFocus", environmentSettings.sunFocus);
        rayTracingMaterial.SetFloat("SunIntensity", environmentSettings.sunIntensity);
    }

    void UpdateCameraParams(Camera cam)
    {
        float planeHeight = focusDistance * Tan(cam.fieldOfView * 0.5f * Deg2Rad) * 2.0f;
        float planeWidth = planeHeight * cam.aspect;

        // Send data to the shader
        rayTracingMaterial.SetVector("ViewParams", new Vector3(planeWidth, planeHeight, focusDistance));
        rayTracingMaterial.SetMatrix("CamLocalToWorldMatrix", cam.transform.localToWorldMatrix);
    }

    void OnDisable()
    {
        ShaderHelper.Release(sphereBuffer, triangleBuffer, meshInfoBuffer);
        ShaderHelper.Release(resultTexture);
    }

    void OnValidate()
    {
        maxBounceCount = Math.Max(0, maxBounceCount);
        numRaysPerPixel = Math.Max(1, numRaysPerPixel);
        environmentSettings.sunFocus = Mathf.Max(1, environmentSettings.sunFocus);
        environmentSettings.sunIntensity = Mathf.Max(0, environmentSettings.sunIntensity);
    }

    void OnDrawGizmos(){
        if (debugFocus)
        {
            Transform cam = Camera.main.transform;
            Vector3 pos = cam.position + cam.forward * (focusDistance + 1f);
            Quaternion rot = Quaternion.LookRotation(cam.position - pos, cam.up)
                                 * Quaternion.Euler(90, 0, 0);
            Gizmos.color = new Color(0, 0.8f, 0, 0.2f);
            Gizmos.DrawMesh(debugFocusMesh, 0, pos, rot, Vector3.one * 10);
        }

        if (debugBoundingBox)
        {
            Gizmos.color = new Color(.8f, 0, 0, 0.2f);
        }
	}
}
