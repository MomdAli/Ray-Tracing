package world;

import math.Vector3;

public class Light {

    private Vector3 position;
    private float intensity;

    public Light(Vector3 position, float intensity) {
        this.position = position;
        this.intensity = intensity;
    }

    public Light(Vector3 position) {
        this.position = position;
        this.intensity = 1.0f;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
