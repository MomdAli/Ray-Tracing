using UnityEngine;

[System.Serializable]
public struct EntvironmentSettings
{
    public bool enabled;
    public Color groundColor;
    public Color skyColorHorizon;
    public Color skyColorZenith;
    public float sunFocus;
    public float sunIntensity;
}