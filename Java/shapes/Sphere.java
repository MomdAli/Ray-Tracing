package shapes;

import math.Color;
import math.Ray;
import math.Vector3;

public class Sphere extends Solid {

    private float radius;

    public Sphere(Vector3 centre, float radius, Color color) {
        this.centre = centre;
        this.radius = radius;
        this.color = color;
    }

    public Sphere(Vector3 centre, float radius) {
        this.centre = centre;
        this.radius = radius;
        this.color = Color.WHITE;
    }

    public Sphere(Vector3 centre) {
        this.centre = centre;
        this.radius = 1f;
        this.color = Color.WHITE;
    }

    public Sphere(float radius) {
        this.centre = Vector3.zero();
        this.radius = radius;
        this.color = Color.WHITE;
    }

    public Sphere() {
        this.centre = Vector3.zero();
        this.radius = 1f;
        this.color = Color.WHITE;
    }

    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }

    @Override
    public Vector3 calculateIntersection(Ray ray) {
        float t = Vector3.dot(centre.sub(ray.getOrigin()), ray.getDirection());
        Vector3 p =  ray.getOrigin().add(ray.getDirection().scale(t));

        float y = centre.sub(p).length();
        if (y < radius) {
            float x = (float) Math.sqrt(radius*radius - y*y);
            float t1 = t-x;
            if (t1 > 0) return ray.getOrigin().add(ray.getDirection().scale(t1));
            else return null;
        } else {
            return null;
        }
    }

    @Override
    public Vector3 getNormalAt(Vector3 point) {
        return point.sub(centre).normalize();
    }
}
