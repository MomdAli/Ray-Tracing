package math;

import java.awt.Color;

public class Ray {

    private Vector3 origin;
    private Vector3 direction;
    private Color color;

    public Ray(Vector3 origin, Vector3 direction, Color color) {
        this.origin = origin;
        this.direction = direction;
        this.color = color;
    }

    public Ray(Vector3 origin, Vector3 direction) {
        this.origin = origin;
        this.direction = direction;
        this.color = Color.WHITE;
    }
    public Ray(Vector3 origin) {
        this.origin = origin;
        this.direction = Vector3.zero();
        this.color = Color.WHITE;
    }

    public Ray() {
        this.origin = Vector3.zero();
        this.direction = Vector3.zero();
        this.color = Color.WHITE;
    }

    public Color getColor() { return color; }
    public Vector3 getOrigin() { return origin; }
    public Vector3 getDirection() { return direction; }

    public Vector3 getPoint(float t) {
        return origin.add(direction.scale(t));
    }

    public Ray copy() {
        return new Ray(origin.copy(), direction.copy(), color);
    }

    public void reverse() {
        direction.scale(-1);
    }

    public void rotate(float angle, Vector3 axis) {
        direction.rotate(angle, axis);
    }

    public void scale(float s) {
        origin.scale(s);
        direction.scale(s);
    }

    public void lerp(Ray r, float t) {
        origin.lerp(r.origin, t);
        direction.lerp(r.direction, t);
    }

    public void add(Vector3 v) {
        origin.add(v);
    }

    public void sub(Vector3 v) {
        origin.sub(v);
    }

    public void project(Vector3 v) {
        direction.project(v);
    }

    public void reflect(Vector3 normal) {
        direction.reflect(normal);
    }

    public float distance(Vector3 v) {
        return origin.distance(v);
    }

    public float angle(Vector3 v) {
        return direction.angle(v);
    }

    public float length() {
        return direction.length();
    }

    public float dot(Vector3 v) {
        return direction.dot(v);
    }

    public Vector3 cross(Vector3 v) {
        return direction.cross(v);
    }

    public Vector3 normalize() {
        return direction.normalize();
    }

    public Vector3 getNormal() {
        return direction;
    }

    public Vector3 getNormal(float t) {
        return direction.scale(t);
    }

    public boolean isZero() {
        return direction.isZero();
    }

    @Override
    public String toString() {
        return String.format("Ray: (%s, %s)", origin, direction);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ray)) return false;
        Ray r = (Ray) o;
        return origin.equals(r.origin) && direction.equals(r.direction);
    }
}
