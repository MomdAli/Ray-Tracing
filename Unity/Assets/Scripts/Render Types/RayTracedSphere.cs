using UnityEngine;

public class RayTracedSphere : MonoBehaviour
{
    public RayTracingMaterial material;

    [SerializeField, HideInInspector] int materialObjectID;
    [SerializeField, HideInInspector] bool materialInitFlag;

    /// <summary>
    /// Called when the script is loaded or a value is changed in the
    /// inspector (Called in the editor only).
    /// </summary>
    void OnValidate()
    {
        if (!materialInitFlag)
        {
            materialInitFlag = true;
            material.SetDefaultValues();
        }

        if (TryGetComponent<MeshRenderer>(out var renderer))
        {
            if (materialObjectID != renderer.GetInstanceID())
            {
                renderer.sharedMaterial = new Material(renderer.sharedMaterial);
                materialObjectID = gameObject.GetInstanceID();
            }

            renderer.sharedMaterial.color = material.color;
        }
    }
}
