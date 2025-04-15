using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using UnityEngine.EventSystems;

public class CameraController : MonoBehaviour
{
    [SerializeField] private RawImage display;
    [SerializeField] private Image backButtonImage;  // Changed from Button to Image
    [SerializeField] private string mainSceneName = "MainScene";
    [SerializeField] private bool isARScene = false;
    
    private bool camAvailable;
    private WebCamTexture cameraTexture;
    private Texture defaultBackground;
    private ScreenOrientation currentOrientation;

    // Start is called once before the first execution of Update after the MonoBehaviour is created
    void Start()
    {
        // Store default texture and setup back button
        defaultBackground = display.texture;
        SetupBackButton();
        
        // Initialize camera
        InitializeCamera();
    }

    private void SetupBackButton()
    {
        if (backButtonImage != null)
        {
            // Add event trigger component if it doesn't already exist
            EventTrigger trigger = backButtonImage.gameObject.GetComponent<EventTrigger>();
            if (trigger == null)
            {
                trigger = backButtonImage.gameObject.AddComponent<EventTrigger>();
            }
            
            // Clear existing entry if any
            if (trigger.triggers != null)
            {
                trigger.triggers.Clear();
            }
            
            // Create a pointer click entry
            EventTrigger.Entry entry = new EventTrigger.Entry();
            entry.eventID = EventTriggerType.PointerClick;
            entry.callback.AddListener((data) => {
                // Clean up camera resources before returning to main scene
                if (cameraTexture != null && cameraTexture.isPlaying)
                {
                    cameraTexture.Stop();
                }
                SceneManager.LoadScene(mainSceneName);
            });
            
            // Add the entry to the trigger
            trigger.triggers.Add(entry);
        }
    }
    
    private void InitializeCamera()
    {
        WebCamDevice[] devices = WebCamTexture.devices;
        
        if (devices.Length == 0)
        {
            Debug.Log("No camera detected");
            camAvailable = false;
            return;
        }
        
        // Default to using any available camera
        WebCamDevice selectedDevice = devices[0];
        
        // Try to find rear-facing camera for regular camera scene
        if (!isARScene)
        {
            for (int i = 0; i < devices.Length; i++)
            {
                if (!devices[i].isFrontFacing)
                {
                    selectedDevice = devices[i];
                    break;
                }
            }
        }
        // For AR scene, we might prefer back camera as well
        else
        {
            for (int i = 0; i < devices.Length; i++)
            {
                if (!devices[i].isFrontFacing)
                {
                    selectedDevice = devices[i];
                    break;
                }
            }
        }
        
        // Determine best resolution based on screen
        int requestedWidth = Screen.width;
        int requestedHeight = Screen.height;
        
        // Create the camera texture
        cameraTexture = new WebCamTexture(selectedDevice.name, requestedWidth, requestedHeight, 30);
        
        if (cameraTexture == null)
        {
            Debug.LogError("Unable to initialize camera");
            camAvailable = false;
            return;
        }
        
        // Start the camera
        cameraTexture.Play();
        display.texture = cameraTexture;
        camAvailable = true;
        
        // Store initial orientation
        currentOrientation = Screen.orientation;
    }

    // Update is called once per frame
    void Update()
    {
        if (!camAvailable || cameraTexture == null)
            return;
            
        // Handle any orientation changes
        if (currentOrientation != Screen.orientation)
        {
            currentOrientation = Screen.orientation;
            AdjustRawImageSize();
        }
        
        // Handle video rotation - important for Android devices
        int orient = -cameraTexture.videoRotationAngle;
        
        // Apply rotation based on device orientation
        switch (Screen.orientation)
        {
            case ScreenOrientation.Portrait:
                // Default portrait adjustment
                break;
            case ScreenOrientation.LandscapeLeft:
                orient += 90;
                break;
            case ScreenOrientation.LandscapeRight:
                orient -= 90;
                break;
            case ScreenOrientation.PortraitUpsideDown:
                orient += 180;
                break;
        }
        
        // Apply rotation
        display.rectTransform.localEulerAngles = new Vector3(0, 0, orient);
        
        // Check if we need to adjust the size (in case camera texture dimensions change)
        if (cameraTexture.width > 100 && display.rectTransform != null)
        {
            AdjustRawImageSize();
        }
    }
    
    private void AdjustRawImageSize()
    {
        if (cameraTexture == null || !camAvailable || display == null)
            return;
            
        // Get camera texture dimensions
        float textureWidth = cameraTexture.width;
        float textureHeight = cameraTexture.height;
        
        if (textureWidth <= 0 || textureHeight <= 0)
            return;
            
        // Get screen dimensions
        float screenWidth = Screen.width;
        float screenHeight = Screen.height;
        
        // Calculate aspect ratios
        float textureRatio = textureWidth / textureHeight;
        float screenRatio = screenWidth / screenHeight;
        
        // Set the RawImage size based on the canvas parent
        RectTransform canvasRect = display.canvas.GetComponent<RectTransform>();
        if (canvasRect != null)
        {
            // Make RawImage cover the entire screen while maintaining aspect ratio
            if (textureRatio > screenRatio)
            {
                // Camera texture is wider than screen, match height and center horizontally
                float height = canvasRect.rect.height;
                float width = height * textureRatio;
                display.rectTransform.sizeDelta = new Vector2(width, height);
            }
            else
            {
                // Camera texture is taller than screen, match width and center vertically
                float width = canvasRect.rect.width;
                float height = width / textureRatio;
                display.rectTransform.sizeDelta = new Vector2(width, height);
            }
            
            // Center the RawImage
            display.rectTransform.anchoredPosition = Vector2.zero;
        }
    }
    
    void OnDestroy()
    {
        // Clean up camera resources
        if (cameraTexture != null && cameraTexture.isPlaying)
        {
            cameraTexture.Stop();
        }
    }
    
    void OnApplicationPause(bool pauseStatus)
    {
        // Handle app pausing - crucial for Android
        if (cameraTexture != null)
        {
            if (pauseStatus)
            {
                cameraTexture.Stop();
            }
            else if (camAvailable)
            {
                cameraTexture.Play();
            }
        }
    }
}