package math;

public class Point {

    float x, y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float distance(Point p) {
        return (float) Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
    }

    public float distance(float x, float y) {
        return (float) Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
    }

    public Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    public Point add(float x, float y) {
        return new Point(this.x + x, this.y + y);
    }

    public Point sub(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    public Point sub(float x, float y) {
        return new Point(this.x - x, this.y - y);
    }

    public Point scale(float s) {
        return new Point(x * s, y * s);
    }

    public Point scale(float x, float y) {
        return new Point(this.x * x, this.y * y);
    }

    public Point lerp(Point p, float t) {
        return new Point(x + (p.x - x) * t, y + (p.y - y) * t);
    }

    public Point lerp(float x, float y, float t) {
        return new Point(this.x + (x - this.x) * t, this.y + (y - this.y) * t);
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;

        Point p = (Point) o;
        return x == p.x && y == p.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public int hashCode() {
        return (int) (x + y);
    }

    public Point copy() {
        return new Point(x, y);
    }

    public static Point zero() {
        return new Point(0, 0);
    }

    public static Point one() {
        return new Point(1, 1);
    }
}
