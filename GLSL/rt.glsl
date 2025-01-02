#version 300 es

precision mediump float;
out vec4 FragColor;

uniform vec2 u_resolution;
uniform float u_time;
uniform vec2 u_mouse;
// Structs

struct Ray
{
    vec3 origin;
    vec3 direction;
};

struct material
{
    vec3 albedo;
    float roughness;
    float metallic;
    float reflectance;
};

struct hit_record
{
    vec3 p;
    vec3 normal;
    float t;
    bool front_face;
    material surface;
};

struct sphere
{
    vec3 position;
    float radius;
    material surface;
};

// Global Variables
int seed;
int max_depth;

// Objects

#define NumElements 4
sphere objects[NumElements] = sphere[NumElements](
    sphere(vec3(0,-100.5,1),100.0, material(vec3(0.2706, 0.0706, 0.0706), 0.0, 0.0, 0.0)),
    sphere(vec3(0,0,1.2),0.5, material(vec3(1.0, 1.0, 0.13), 90., 0.0, 0.0)),
    sphere(vec3(-1,0,1),0.5, material(vec3(0.1451, 1.0, 0.0314), 9.0, 0.0, 0.0)),
    sphere(vec3(1.0, 0.0, 1.0), 0.5, material(vec3(1.0, 0.0, 0.0), 10., 0.0, 0.0))
);

// Function definitions

vec3 rayAt(const Ray ray, const float t);
vec3 ray_color(const Ray r);
float hit_sphere(const vec3 center, float radius, const Ray r);

// Intersections
bool hit(const Ray r, vec2 interval, const sphere s,
    inout hit_record rec);
void set_face_normal(const Ray r, const vec3 outward_normal, inout hit_record rec);
bool hit_list(const Ray r, vec2 interval, const sphere spheres[NumElements],
    inout hit_record rec);

// Utilities
int xorshift(in int value);
float random_float();
float random_float(float minVal, float maxVal);
vec3 random();
vec3 random(float minVal, float maxVal);

vec3 random_unit_vector();



// Utility
#define PI 3.1415926535897932384626433832795
#define FLT_MAX 3.402823466e+38
#define FLT_MIN 1.175494351e-38

int xorshift(in int value)
{
    // Xorshift*32
    // Based on George Marsaglia's work: http://www.jstatsoft.org/v08/i14/paper
    value ^= value << 13;
    value ^= value >> 17;
    value ^= value << 5;
    return value;
}

float random_float()
{
    seed = xorshift(seed);
    return abs(fract(float(seed) / 3141.592653));
}

float random_float(float minVal, float maxVal)
{
    return minVal + (maxVal-minVal)*random_float();
}

vec3 random()
{
    return vec3(random_float(),
                random_float(),
                random_float());
}

vec3 random(float minVal, float maxVal)
{
    return vec3(random_float(minVal, maxVal),
                random_float(minVal, maxVal),
                random_float(minVal, maxVal));
}



// Functions

vec3 rayAt(const Ray ray, const float t) {
    return ray.origin + t * ray.direction;
}

bool hit(const Ray r, vec2 interval, const sphere s,
    inout hit_record rec)
{
    vec3 oc = s.position - r.origin;
    float a = dot(r.direction, r.direction);
    float h = dot(r.direction, oc);
    float c = dot(oc, oc) - s.radius * s.radius;
    float discriminant = h*h - a*c;

    if (discriminant < 0.0)
        return false;

    float sqrtd = sqrt(discriminant);

    // Find the nearest root that lies in the acceptable range
    float root = (h - sqrtd) / a;
    if (root <= interval.x || interval.y <= root) {
        root = (h + sqrtd) / a;
        if (root <= interval.x || interval.y <= root)
            return false;
    }

    rec.t = root;
    rec.p = rayAt(r, rec.t);
    vec3 outward_normal = (rec.p - s.position) / s.radius;
    set_face_normal(r, outward_normal, rec);

    rec.surface = s.surface;

    return true;
}

bool hit_list(const Ray r, vec2 interval, const sphere spheres[NumElements],
    inout hit_record rec)
{
    hit_record temp_rec;
    bool hit_anything = false;
    float closest_so_far = interval.y;

    for (int i = 0; i < NumElements; i++)
    {
        if (hit(r, interval, spheres[i], temp_rec))
        {
            hit_anything = true;
            closest_so_far = temp_rec.t;
            rec = temp_rec;
        }
    }

    return hit_anything;
}

void set_face_normal(const Ray r, const vec3 outward_normal, inout hit_record rec)
{
    rec.front_face = dot(r.direction, outward_normal) < 0.0;
    rec.normal = rec.front_face ? outward_normal : -outward_normal;
}

vec3 random_unit_vector()
{
    vec3 p = random(-1.0, 1.0);
    float lensq = dot(p, p);
    if (lensq <= 1.0)
        return p;

    return p * (1.0 / lensq);
}

vec3 random_on_hemisphere(const vec3 point, const vec3 normal, float roughness)
{
    vec3 circle = vec3(random_float(), random_float(), 0.0) * roughness;
    vec3 target = normal + circle;
    vec3 on_hemisphere = target - point;
    return on_hemisphere;
}

// Scattering hemisphere depending on the dot product
bool scatter(const Ray r, const hit_record rec, inout vec3 attenuation, inout Ray scattered)
{
    scattered = Ray(rec.p, random_on_hemisphere(rec.p, rec.normal, rec.surface.roughness));
    attenuation = rec.surface.albedo;
    return true;
}


// RAY COLORING FOR EACH PIXEL

vec3 ray_color(const Ray r)
{
    hit_record rec;
    Ray temp_ray = r;
    vec3 color = vec3(1);

    for (int i = 0; i < max_depth; i++)
    {
        if (hit_list(temp_ray, vec2(0.001,FLT_MAX), objects, rec))
        {
            Ray scattered;
            vec3 attenuation;
            if (scatter(temp_ray, rec, attenuation, scattered))
            {
                color = color * attenuation;
                temp_ray = scattered;
                continue;
            }
            return vec3(0.0);
        } else {
            float a = .5 * (normalize(r.direction).y + 1.0);
            color = color * ((1.0 - a) * vec3(1.0, 1.0, 1.0) + a * vec3(0.1333, 0.4627, 0.9529));
            return color;
        }
    }

    return vec3(0.0, 0.0, 0.0);
}

// Main

int max_samples = 10;
float sensitivity = 10.0;
float fov = 60.0;

void main()
{
    seed = int(gl_FragCoord.x) + int(gl_FragCoord.y) * int(u_resolution.x);
    max_depth = 10;

    float ar = 16.0 / 9.0;
    float sample_scale = 1.0 / float(max_samples);
    vec3 col = vec3(0.0);

    for (int i = 0; i < max_samples; i++)
    {
        // Sampling the ray to a random direction
        vec2 sample_square = vec2(random_float() - 0.5,
                            random_float() - 0.5);

        // Normalized pixel coordinates (from -0.5 to 0.5)
        vec2 uv = (gl_FragCoord.xy+sample_square)/u_resolution.xy - 0.5;

        // Sending Ray
        float mouseX = u_mouse.x / u_resolution.x;
        float mouseY = u_mouse.y / u_resolution.y;
        vec3 mouseMove = vec3(-cos(PI * mouseX), -cos(PI * mouseY) + 0.5, -cos(PI * mouseX - 0.5 * PI));
        vec3 center = vec3(-0.2,1.,-2);
        vec3 lookAt = normalize(mouseMove);
        vec3 vup = vec3(0,1,0);

        float h = tan(radians(fov)/2.);

        float viewport_height = h * (u_resolution.x / u_resolution.y);
        float viewport_width = viewport_height * ar;

        vec3 w = normalize(lookAt - center);
        vec3 u = normalize(cross(vup, w));
        vec3 v = normalize(cross(w, u));

        vec3 viewport_u = u * viewport_width;
        vec3 viewport_v = v * viewport_height;

        vec3 pixel_location = center + viewport_u * uv.x + viewport_v * uv.y + w;
        Ray r = Ray(pixel_location, pixel_location - center);

        // Calculating the color
        col += ray_color(r);
    }

    // Output to screen
    FragColor = vec4(sqrt(sample_scale * col), 1.0);
}