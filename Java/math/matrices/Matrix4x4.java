package math.matrices;

import math.Vector3;

public class Matrix4x4 {

    private float[][] matrix = new float[4][4];

    public Matrix4x4(float[][] matrix) {
        this.matrix = matrix;
    }

    public Matrix4x4() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    public float[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(float[][] matrix) {
        this.matrix = matrix;
    }

    public float get(int i, int j) {
        return matrix[i][j];
    }

    public void set(int i, int j, float value) {
        matrix[i][j] = value;
    }

    public Matrix4x4 multiply(Matrix4x4 matrix) {
        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result[i][j] += this.matrix[i][k] * matrix.get(k, j);
                }
            }
        }
        return new Matrix4x4(result);
    }

    public Vector3 multiply(Vector3 vector) {
        float[] result = new float[4];
        for (int i = 0; i < 4; i++) {
            result[i] = 0;
            for (int j = 0; j < 4; j++) {
                result[i] += this.matrix[i][j] * vector.get(j);
            }
        }
        return new Vector3(result[0], result[1], result[2]);
    }

    public Matrix4x4 copy() {
        float[][] copy = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                copy[i][j] = matrix[i][j];
            }
        }
        return new Matrix4x4(copy);
    }

    public Matrix4x4 zero() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = 0;
            }
        }
        return this;
    }

    public Matrix4x4 identity() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == j) {
                    matrix[i][j] = 1;
                } else {
                    matrix[i][j] = 0;
                }
            }
        }
        return this;
    }

    public Matrix4x4 transpose() {
        float[][] transposed = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                transposed[i][j] = matrix[j][i];
            }
        }
        return new Matrix4x4(transposed);
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result += matrix[i][j] + " ";
            }
            result += "\n";
        }
        return result;
    }

    public float determinant() {
        return matrix[0][0] * matrix[1][1] * matrix[2][2] * matrix[3][3];
    }

    public Vector3 multVectMatrix(Vector3 v) {
        float a, b, c, w;

        a = v.get(0) * matrix[0][0] + v.get(1) * matrix[1][0] + v.get(2) * matrix[2][0] + matrix[3][0];
        b = v.get(0) * matrix[0][1] + v.get(1) * matrix[1][1] + v.get(2) * matrix[2][1] + matrix[3][1];
        c = v.get(0) * matrix[0][2] + v.get(1) * matrix[1][2] + v.get(2) * matrix[2][2] + matrix[3][2];
        w = v.get(0) * matrix[0][3] + v.get(1) * matrix[1][3] + v.get(2) * matrix[2][3] + matrix[3][3];

        return new Vector3(a / w, b / w, c / w);
    }

    public Vector3 multDirMatrix(Vector3 v) {
        float a, b, c;

        a = v.get(0) * matrix[0][0] + v.get(1) * matrix[1][0] + v.get(2) * matrix[2][0];
        b = v.get(0) * matrix[0][1] + v.get(1) * matrix[1][1] + v.get(2) * matrix[2][1];
        c = v.get(0) * matrix[0][2] + v.get(1) * matrix[1][2] + v.get(2) * matrix[2][2];

        return new Vector3(a, b, c);
    }

    public static Matrix4x4 identityMatrix() {
        return new Matrix4x4(new float[][]{{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}});
    }
}
