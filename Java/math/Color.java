package math;

public class Color {

    private float r, g, b, a;

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1;
    }

    public Color() {
        this.r = 0;
        this.g = 0;
        this.b = 0;
        this.a = 1;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public float getA() {
        return a;
    }

    public void setR(float r) {
        this.r = r;
        clamp();
    }

    public void setG(float g) {
        this.g = g;
        clamp();
    }

    public void setB(float b) {
        this.b = b;
        clamp();
    }

    public void setA(float a) {
        this.a = a;
        clamp();
    }

    public Color set(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        clamp();

        return this;
    }

    public Color set(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1;
        clamp();

        return this;
    }

    public Color set(Color c) {
        this.r = c.getR();
        this.g = c.getG();
        this.b = c.getB();
        this.a = c.getA();
        clamp();

        return this;
    }

    public Color add(Color c) {
        this.r += c.getR();
        this.g += c.getG();
        this.b += c.getB();
        this.a += c.getA();
        clamp();

        return this;
    }

    public Color add(float r, float g, float b, float a) {
        this.r += r;
        this.g += g;
        this.b += b;
        this.a += a;
        clamp();

        return this;
    }

    public Color add(float r, float g, float b) {
        this.r += r;
        this.g += g;
        this.b += b;
        this.a += 1;
        clamp();

        return this;
    }

    public Color add(float r) {
        this.r += r;
        this.g += r;
        this.b += r;
        this.a += r;
        clamp();

        return this;
    }

    public Color mult(float r, float g, float b, float a) {
        this.r *= r;
        this.g *= g;
        this.b *= b;
        this.a *= a;
        clamp();

        return this;
    }

    public Color mult(float r, float g, float b) {
        this.r *= r;
        this.g *= g;
        this.b *= b;
        this.a *= 1;
        clamp();

        return this;
    }

    public Color mult(Color c) {
        this.r *= c.getR();
        this.g *= c.getG();
        this.b *= c.getB();
        this.a *= c.getA();
        clamp();

        return this;
    }

    public Color mult(float brightness) {
        brightness = Math.min(1, brightness);
        return new Color(r * brightness, g * brightness, b * brightness);
    }

    public void clamp() {
        if (r > 1) r = 1;
        if (g > 1) g = 1;
        if (b > 1) b = 1;
        if (a > 1) a = 1;

        if (r < 0) r = 0;
        if (g < 0) g = 0;
        if (b < 0) b = 0;
        if (a < 0) a = 0;
    }

    public java.awt.Color toAWTColor() {
        return new java.awt.Color(r, g, b, a);
    }

    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(1, 1, 1);
    public static final Color RED = new Color(1, 0, 0);
    public static final Color GREEN = new Color(0, 1, 0);
    public static final Color BLUE = new Color(0, 0, 1);

    @Override
    public String toString() {
        return String.format("Color: (%s, %s, %s, %s)", r, g, b, a);
    }
}
