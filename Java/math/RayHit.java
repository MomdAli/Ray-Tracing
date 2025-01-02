package math;

import shapes.Solid;

public class RayHit {

    private Ray ray;
    private Solid hitSolid;
    private Vector3 hitPosition;
    private Vector3 normal;

    public RayHit(Ray ray, Solid hitSolid, Vector3 hitPos) {
        this.ray = ray;
        this.hitSolid = hitSolid;
        this.hitPosition = hitPos;
        this.normal = hitSolid.getNormalAt(hitPos);
    }

    public Ray getRay() { return ray; }
    public Solid getHitSolid() { return hitSolid; }
    public Vector3 getHitPosition() { return hitPosition; }
    public Vector3 getNormal() { return normal; }
}
