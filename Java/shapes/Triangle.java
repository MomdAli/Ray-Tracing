package shapes;

import math.Vector3;

public class Triangle {

    Vector3 p1;
    Vector3 p2;
    Vector3 p3;

    public Triangle(Vector3 p1, Vector3 p2, Vector3 p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Vector3 getPoint(int i) {
        switch (i) {
            case 0:
                return p1;
            case 1:
                return p2;
            case 2:
                return p3;
            default:
                return null;
        }
    }

    public Vector3 getNormal() {
        Vector3 v1 = p2.sub(p1);
        Vector3 v2 = p3.sub(p1);
        return v1.cross(v2).normalize();
    }

    public float getArea() {
        Vector3 v1 = p2.sub(p1);
        Vector3 v2 = p3.sub(p1);
        return v1.cross(v2).length() / 2;
    }

    public Vector3 getCentroid() {
        return p1.add(p2).add(p3).scale(1 / 3);
    }

    public boolean isInside(Vector3 point) {
        Vector3 v1 = p2.sub(p1);
        Vector3 v2 = p3.sub(p1);
        Vector3 v3 = point.sub(p1);
        return v1.cross(v3).dot(v2.cross(v3)) != 0;
    }

    public String toString() {
        return String.format("Triangle: (%s %s %s)", p1, p2, p3);
    }
}
