package math;

public class Line {

    Vector3 v1, v2;

    public Line(Vector3 v1, Vector3 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public Vector3 getv1() {
        return v1;
    }

    public Vector3 getv2() {
        return v2;
    }

    public void setv1(Vector3 v1) {
        this.v1 = v1;
    }

    public void setv2(Vector3 v2) {
        this.v2 = v2;
    }

    public void set(Vector3 v1, Vector3 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public float getLength() {
        return (float) Math.sqrt(Math.pow(v1.getX() - v2.getX(), 2)
                    + Math.pow(v1.getY() - v2.getY(), 2)
                    + Math.pow(v1.getZ() - v2.getZ(), 2));
    }

    public Vector3 getMidPoint() {
        return new Vector3((v1.getX() + v2.getX()) / 2,
                        (v1.getY() + v2.getY()) / 2,
                        (v1.getZ() + v2.getZ()) / 2);
    }

    public String toString() {
        return String.format("Line: (%s %s)", v1, v2);
    }
}
