package shapes;

import math.Color;
import math.Ray;
import math.Vector3;

public abstract class Solid {

    protected Vector3 centre;
    protected Color color;

    public Vector3 getCentre() { return centre; }

    public Color getColor() { return color; }
    public void setCentre(Vector3 centre) { this.centre = centre; }
    public void setColor(Color color) { this.color = color; }

    public void translate(Vector3 v) {
        this.centre = this.centre.add(v);
    }

    public abstract Vector3 calculateIntersection(Ray ray);
    public abstract Vector3 getNormalAt(Vector3 point);
}
