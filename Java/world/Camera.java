package world;

import math.Vector3;

public class Camera {

    Vector3 position;
    float yaw;
    float pitch;
    float fov;

    public Camera() {
        reset();
    }

    public Camera(Vector3 position, float yaw, float pitch, float fov) {
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
        this.fov = fov;
    }

    public Vector3 getPosition() { return position; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public float getFov() { return fov; }

    public void setPosition(Vector3 position) { this.position = position; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    public void setPitch(float pitch) { this.pitch = pitch; }
    public void setFov(float fov) { this.fov = fov; }

    public void translate(Vector3 v) {
        this.position = new Vector3(this.position.getX() + v.getX(),
                                this.position.getY() + v.getY(),
                                this.position.getZ() + v.getZ());
    }

    public void move(float x, float y, float z) {
        this.position = new Vector3(this.position.getX() + x,
                                this.position.getY() + y,
                                this.position.getZ() + z);
    }

    public void move(Vector3 v) {
        this.position = new Vector3(this.position.getX() + v.getX(),
                                this.position.getY() + v.getY(),
                                this.position.getZ() + v.getZ());
    }

    public void rotate(float yaw, float pitch) {
        this.yaw += yaw;
        this.pitch += pitch;
    }

    public void zoom(float fov) {
        this.fov += fov;
    }

    public void reset() {
        this.position = new Vector3(0, 0, 0);
        this.yaw = 0;
        this.pitch = 0;
        this.fov = 60;
    }
}
