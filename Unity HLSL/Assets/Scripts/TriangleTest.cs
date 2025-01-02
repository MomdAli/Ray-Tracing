using System.Collections.Generic;
using UnityEngine;

[System.Serializable]
public class TriangleTest : MonoBehaviour
{
    private int totalTriangleCount;
    private List<Triangle> triangles;
    void Start()
    {
        Mesh[] meshes = GameObject.FindObjectsOfType<Mesh>();


        foreach (Mesh mesh in meshes)
        {
            int triangleCount = mesh.triangles.Length;
            totalTriangleCount += triangleCount / 3;
            triangles = new List<Triangle>(triangleCount);
            for (int i = 0; i < triangleCount; i++)
            {
                triangles.Add(new Triangle(
                    mesh.vertices[mesh.triangles[i * 3]],
                    mesh.vertices[mesh.triangles[i * 3 + 1]],
                    mesh.vertices[mesh.triangles[i * 3 + 2]],
                    mesh.normals[mesh.triangles[i * 3]],
                    mesh.normals[mesh.triangles[i * 3 + 1]],
                    mesh.normals[mesh.triangles[i * 3 + 2]]
                ));
            }
        }
    }

    void OnDrawGizmos()
    {

    }
}
