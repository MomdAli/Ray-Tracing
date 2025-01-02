package world;

import java.util.LinkedList;

import math.Ray;
import math.RayHit;
import math.Vector3;
import shapes.Solid;

public class Scene {

    private LinkedList<Solid> solids;
    private Light globaLight;
    private Camera camera;

    public Scene() {
        this.solids = new LinkedList<>();
        this.camera = new Camera();
        this.globaLight = new Light(new Vector3(-2, 2, 0), 1f);
    }

    public void addSolid(Solid solid) {
        solids.add(solid);
    }

    public LinkedList<Solid> getSolids() {
        return solids;
    }

    public Camera getCamera() {
        return camera;
    }

    public RayHit raycast(Ray ray) {
        RayHit closestHit = null;
        for (Solid solid : solids) {
            if (solid == null)
                continue;

            Vector3 hitPos = solid.calculateIntersection(ray);
            if (hitPos != null && (closestHit == null || Vector3.distance(closestHit.getHitPosition(),
                                ray.getOrigin()) > Vector3.distance(hitPos, ray.getOrigin()))) {
                closestHit = new RayHit(ray, solid, hitPos);
            }
        }
        return closestHit;
    }

    public Light getGlobalLight() {
        return globaLight;
    }
}
