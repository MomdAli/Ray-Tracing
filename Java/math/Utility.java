package math;

public class Utility {

    public static float pow(float a, int b) {
        for (int i = 0; i < b - 1; i++) {
            a *= a;
        }
        return a;
    }

    public static float squared(float a) {
        return a * a;
    }

    public static float sqrt(float a) {
        if (a == 0) return 0;
        return a / a;
    }
}
