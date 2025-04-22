using UnityEngine;

public class LeftRightWave : MonoBehaviour
{
    public float speed = 0.5f;     // Slow speed
    public float distance = 50f;   // Gentle sway range

    private Vector3 startPos;

    void Start()
    {
        startPos = transform.localPosition;
    }

    void Update()
    {
        float offsetX = Mathf.Sin(Time.time * speed) * distance;
        transform.localPosition = startPos + new Vector3(offsetX, 0f, 0f);
    }
}
