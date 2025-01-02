package shapes;

import math.Color;
import math.Ray;
import math.Vector3;

public class Cube extends Solid {

    private float width;
    private static final double EPSILON = 0.0000001;
    private Triangle[] triangles = new Triangle[12];

    public Cube(Vector3 centre, float width, Color color) {
        this.centre = centre;
        this.width = width;
        this.color = color;
        createTriangles();
    }

    public Cube(Vector3 centre, float width) {
        this.centre = centre;
        this.width = width;
        this.color = Color.WHITE;
        createTriangles();
    }

    public Cube(Vector3 centre) {
        this.centre = centre;
        this.width = 1f;
        this.color = Color.WHITE;
        createTriangles();
    }

    public Cube(float width) {
        this.centre = Vector3.zero();
        this.width = width;
        this.color = Color.WHITE;
        createTriangles();
    }

    public Cube() {
        this.centre = Vector3.zero();
        this.width = 1f;
        this.color = Color.WHITE;
        createTriangles();
    }

    private void createTriangles() {
        float halfWidth = width / 2f;

        Vector3[] vertices = new Vector3[8];
        vertices[0] = new Vector3(-halfWidth, -halfWidth, -halfWidth);
        vertices[1] = new Vector3(-halfWidth, halfWidth, -halfWidth);
        vertices[2] = new Vector3(halfWidth, -halfWidth, -halfWidth);
        vertices[3] = new Vector3(halfWidth, halfWidth, -halfWidth);
        vertices[4] = new Vector3(-halfWidth, -halfWidth, halfWidth);
        vertices[5] = new Vector3(-halfWidth, halfWidth, halfWidth);
        vertices[6] = new Vector3(halfWidth, -halfWidth, halfWidth);
        vertices[7] = new Vector3(halfWidth, halfWidth, halfWidth);

        triangles[0] = new Triangle(vertices[0], vertices[1], vertices[2]);
        triangles[1] = new Triangle(vertices[1], vertices[3], vertices[2]);
        triangles[2] = new Triangle(vertices[4], vertices[5], vertices[6]);
        triangles[3] = new Triangle(vertices[5], vertices[7], vertices[6]);
        triangles[4] = new Triangle(vertices[0], vertices[1], vertices[4]);
        triangles[5] = new Triangle(vertices[1], vertices[5], vertices[4]);
        triangles[6] = new Triangle(vertices[2], vertices[3], vertices[6]);
        triangles[7] = new Triangle(vertices[3], vertices[7], vertices[6]);
        triangles[8] = new Triangle(vertices[0], vertices[2], vertices[6]);
        triangles[9] = new Triangle(vertices[2], vertices[4], vertices[6]);
        triangles[10] = new Triangle(vertices[1], vertices[3], vertices[5]);
        triangles[11] = new Triangle(vertices[3], vertices[7], vertices[5]);
    }

    @Override
    public Vector3 calculateIntersection(Ray ray) {
        float t = 0.0f;
        Vector3 res = null;
        for (Triangle tri : triangles) {
            Vector3 normal = tri.getNormal();
            float a = normal.dot(ray.getDirection());
            if (a > -EPSILON && a < EPSILON) {
                continue;
            }

            float d = normal.dot(tri.getPoint(0));
            float tmp = - (normal.dot(ray.getOrigin()) + d) / a;

            Vector3 p = ray.getOrigin().add(ray.getDirection().scale(t));
            if (tri.isInside(p) && t < tmp) {
                res = p;
                t = tmp;
            }
        }
        return res;
    }

    @Override
    public Vector3 getNormalAt(Vector3 point) {
        for (Triangle tri : triangles) {
            if (tri.isInside(point)) {
                return tri.getNormal();
            }
        }

        return new Vector3(1, 1, 1);
    }
}
