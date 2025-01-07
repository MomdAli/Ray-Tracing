using UnityEngine;

public class BVH
{
    public Vector3 Min = Vector3.positiveInfinity;
    public Vector3 Max = Vector3.negativeInfinity;
    public Vector3 Center => (Min + Max) * 0.5f;

    public void GrowToInclude(Vector3 point)
    {
        Min = Vector3.Min(Min, point);
        Max = Vector3.Max(Max, point);
    }


}