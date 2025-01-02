import math.Color;
import math.Vector3;
import shapes.Cube;
import shapes.Sphere;
import world.Scene;

public class Main {
    public static Scene scene;

    public static void main(String[] args) {
        scene = new Scene();

        scene.getSolids().add(new Sphere(new Vector3(0, 2f, 3), .3f, new Color(1f, .3f, .1f)));
        scene.getSolids().add(new Sphere(new Vector3(4, 1f, 1), 1.3f, new Color(.1f, .7f, .7f)));
        scene.getSolids().add(new Sphere(new Vector3(-3, 0f, 2), .5f, new Color(.7f, .1f, .2f)));
        scene.getSolids().add(new Sphere(new Vector3(2, -2f, 5), .2f, new Color(.3f, .4f, .2f)));

        GUI gui = new GUI();

    }
}
