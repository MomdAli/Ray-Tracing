package math;

public class Vector3 {

    float x, y, z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setZ(float z) { this.z = z; }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vector3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public float get(int j) {
        if (j == 0) return x;
        if (j == 1) return y;
        if (j == 2) return z;
        return 0;
    }

    public Vector3 add(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public Vector3 sub(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    public float dot(Vector3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vector3 cross(Vector3 v) {
        return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3 normalize() {
        float len = length();
        return new Vector3(x / len, y / len, z / len);
    }

    public float distance(Vector3 v) {
        return (float) Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) + (z - v.z) * (z - v.z));
    }

    public Vector3 reflect(Vector3 normal) {
        return sub(normal.scale(2 * dot(normal)));
    }

    public Vector3 scale(float s) {
        return new Vector3(x * s, y * s, z * s);
    }

    public Vector3 project(Vector3 v) {
        return v.scale(dot(v) / v.dot(v));
    }

    public float angle(Vector3 v) {
        return (float) Math.acos(dot(v) / (length() * v.length()));
    }

    public Vector3 rotate(float angle, Vector3 axis) {
        return axis.scale((float) Math.cos(angle))
                    .add(axis.cross(this)
                    .scale((float) Math.sin(angle)))
                    .add(this.cross(axis)
                    .scale((float) Math.sin(angle)));
    }

    public Vector3 lerp(Vector3 v, float t) {
        return new Vector3(x + (v.x - x) * t, y + (v.y - y) * t, z + (v.z - z) * t);
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public Vector3 copy() {
        return new Vector3(x, y, z);
    }

    public Vector3 abs() {
        return new Vector3(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public Vector3 min(Vector3 v) {
        return new Vector3(Math.min(x, v.x), Math.min(y, v.y), Math.min(z, v.z));
    }

    public Vector3 max(Vector3 v) {
        return new Vector3(Math.max(x, v.x), Math.max(y, v.y), Math.max(z, v.z));
    }

    public Vector3 clamp(Vector3 min, Vector3 max) {
        return new Vector3(Math.max(Math.min(x, max.x), min.x),
                           Math.max(Math.min(y, max.y), min.y),
                           Math.max(Math.min(z, max.z), min.z));
    }

    public Vector3 squared() {
        return new Vector3((float) Math.pow(x, 2),
                            (float) Math.pow(y, 2),
                            (float) Math.pow(z, 2));
    }

    public Vector3 floor() {
        return new Vector3((float) Math.floor(x), (float) Math.floor(y), (float) Math.floor(z));
    }

    public Vector3 ceil() {
        return new Vector3((float) Math.ceil(x), (float) Math.ceil(y), (float) Math.ceil(z));
    }

    public Vector3 round() {
        return new Vector3((float) Math.round(x), (float) Math.round(y), (float) Math.round(z));
    }

    public Vector3 rotateYP(float yaw, float pitch) {
        // Convert to radians
        double yawRads = Math.toRadians(yaw);
        double pitchRads = Math.toRadians(pitch);

        // Step one: Rotate around X axis (pitch)
        float _y = (float) (y*Math.cos(pitchRads) - z*Math.sin(pitchRads));
        float _z = (float) (y*Math.sin(pitchRads) + z*Math.cos(pitchRads));

        // Step two: Rotate around the Y axis (yaw)
        float _x = (float) (x*Math.cos(yawRads) + _z*Math.sin(yawRads));
        _z = (float) (-x*Math.sin(yawRads) + _z*Math.cos(yawRads));

        return new Vector3(_x, _y, _z);
    }

    public static Vector3 lerp(Vector3 v1, Vector3 v2, float t) {
        return v1.lerp(v2, t);
    }

    public static Vector3 zero() {
        return new Vector3(0, 0, 0);
    }

    public static Vector3 one() {
        return new Vector3(1, 1, 1);
    }

    public static Vector3 random() {
        return new Vector3((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    public static Vector3 random(float min, float max) {
        return new Vector3((float) Math.random() * (max - min) + min, (float) Math.random() * (max - min) + min, (float) Math.random() * (max - min) + min);
    }

    public static Vector3 up() {
        return new Vector3(0, 1, 0);
    }

    public static Vector3 down() {
        return new Vector3(0, -1, 0);
    }

    public static Vector3 left() {
        return new Vector3(-1, 0, 0);
    }

    public static Vector3 right() {
        return new Vector3(1, 0, 0);
    }

    public static Vector3 forward() {
        return new Vector3(0, 0, 1);
    }

    public static Vector3 backward() {
        return new Vector3(0, 0, -1);
    }

    public static Vector3 normalize(Vector3 v) {
        return v.scale(1 / v.length());
    }

    public static float distance(Vector3 v1, Vector3 v2) {
        return (float) Math.sqrt((v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y) + (v1.z - v2.z) * (v1.z - v2.z));
    }

    public static Vector3 cross(Vector3 v1, Vector3 v2) {
        return new Vector3(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
    }

    public static float dot(Vector3 v1, Vector3 v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vector3)) return false;

        Vector3 v = (Vector3) o;
        return x == v.x && y == v.y && z == v.z;
    }

    @Override
    public String toString() {
        return String.format("Vector3: (%s, %s, %s)", x, y, z);
    }
}
