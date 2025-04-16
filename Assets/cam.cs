using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using UnityEngine.EventSystems;
using System.Collections;

public class CameraController : MonoBehaviour
{
    [SerializeField] private RawImage display;
    [SerializeField] private Image backButtonImage;  // Back button image
    [SerializeField] private Image footerImage;      // Footer image instead of GameObject
    [SerializeField] private string mainSceneName = "MainScene";
    [SerializeField] private bool isARScene = false;
    [SerializeField] private bool maintainAspect = true;  // Keep camera aspect ratio
    
    private bool camAvailable;
    private WebCamTexture cameraTexture;
    private Texture defaultBackground;
    private ScreenOrientation currentOrientation;

    // Start is called once before the first execution of Update after the MonoBehaviour is created
    void Start()
    {
        // Force footer to be visible and active at the start
        ForceFooterVisibility();
        
        // Log what we have
        Debug.Log($"CameraController - Display: {display != null}, BackButton: {backButtonImage != null}, " +
                 $"Footer: {footerImage != null}");
        
        // Set up back button immediately
        SetupBackButton();
        
        // Ensure EventSystem exists
        EnsureEventSystem();
        
        // Initialize camera with permission request
        StartCoroutine(RequestCameraPermissionAndInitialize());
    }
    
    // Call this on Start and whenever needed to force footer visibility
    private void ForceFooterVisibility() 
    {
        if (footerImage != null)
        {
            // Ensure footer is active
            if (!footerImage.gameObject.activeSelf)
            {
                footerImage.gameObject.SetActive(true);
                Debug.Log("Forced footer to be visible");
            }
            
            // Force it to be visible in case it's set to transparent
            Color footerColor = footerImage.color;
            footerImage.color = new Color(footerColor.r, footerColor.g, footerColor.b, 1.0f);
            
            // Move it to the front if needed
            Canvas canvas = footerImage.GetComponentInParent<Canvas>();
            if (canvas != null) 
            {
                footerImage.transform.SetAsLastSibling();
                Debug.Log("Set footer as last sibling to ensure visibility");
            }
        }
        else
        {
            Debug.LogError("Footer image reference is missing");
        }
    }
    
    private void EnsureEventSystem()
    {
        if (FindAnyObjectByType<EventSystem>() == null)
        {
            Debug.LogError("No EventSystem found in the scene. Adding one.");
            GameObject eventSystem = new GameObject("EventSystem");
            eventSystem.AddComponent<EventSystem>();
            eventSystem.AddComponent<StandaloneInputModule>();
        }
    }

    private IEnumerator RequestCameraPermissionAndInitialize()
    {
        Debug.Log("Requesting camera permission...");
        
        // Force footer visible again before permission
        ForceFooterVisibility();
        
        // First request camera permission
        yield return Application.RequestUserAuthorization(UserAuthorization.WebCam);
        
        // Force footer visible again after permission
        ForceFooterVisibility();
        
        if (Application.HasUserAuthorization(UserAuthorization.WebCam))
        {
            Debug.Log("Camera permission granted");
            InitializeCamera();
        }
        else
        {
            Debug.LogError("Camera permission denied");
            // Show message to user
            if (display != null)
            {
                GameObject textObj = new GameObject("PermissionDeniedText");
                textObj.transform.SetParent(display.transform.parent);
                
                // Add and configure text component
                Text text = textObj.AddComponent<Text>();
                text.text = "Camera permission denied. Please allow camera access in settings.";
                text.color = Color.red;
                text.fontSize = 24;
                text.alignment = TextAnchor.MiddleCenter;
                
                // Position the text
                RectTransform rectTransform = text.GetComponent<RectTransform>();
                rectTransform.anchorMin = new Vector2(0, 0);
                rectTransform.anchorMax = new Vector2(1, 1);
                rectTransform.offsetMin = Vector2.zero;
                rectTransform.offsetMax = Vector2.zero;
            }
        }
        
        // Force footer visible one more time
        ForceFooterVisibility();
    }

    private void SetupBackButton()
    {
        if (backButtonImage != null)
        {
            Debug.Log("Setting up back button");
            
            // Make sure the GameObject is active
            backButtonImage.gameObject.SetActive(true);
            
            // Clean slate - remove any existing components that could interfere
            EventTrigger existingTrigger = backButtonImage.gameObject.GetComponent<EventTrigger>();
            if (existingTrigger != null)
            {
                Destroy(existingTrigger);
            }
            
            Button existingButton = backButtonImage.gameObject.GetComponent<Button>();
            if (existingButton != null)
            {
                Destroy(existingButton);
            }
            
            // Add a Button component for direct clicking
            Button btn = backButtonImage.gameObject.AddComponent<Button>();
            btn.onClick.AddListener(() => {
                Debug.Log("Back button clicked via Button.onClick");
                OnBackButtonClicked();
            });
            
            // Also add EventTrigger as a fallback
            EventTrigger trigger = backButtonImage.gameObject.AddComponent<EventTrigger>();
            EventTrigger.Entry entry = new EventTrigger.Entry();
            entry.eventID = EventTriggerType.PointerClick;
            entry.callback.AddListener((data) => {
                Debug.Log("Back button clicked via EventTrigger");
                OnBackButtonClicked();
            });
            trigger.triggers.Add(entry);
            
            // Add a BoxCollider2D for better touch detection
            BoxCollider2D existingCollider = backButtonImage.gameObject.GetComponent<BoxCollider2D>();
            if (existingCollider != null)
            {
                Destroy(existingCollider);
            }
            
            BoxCollider2D collider = backButtonImage.gameObject.AddComponent<BoxCollider2D>();
            RectTransform rect = backButtonImage.GetComponent<RectTransform>();
            if (rect != null)
            {
                // Make the collider larger for easier clicking
                collider.size = new Vector2(rect.sizeDelta.x * 1.5f, rect.sizeDelta.y * 1.5f);
                Debug.Log($"Added BoxCollider2D to back button with size {collider.size}");
            }
            
            Debug.Log("Back button fully set up");
        }
        else
        {
            Debug.LogError("Back button image reference is missing");
        }
    }
    
    // Public method that can be called from UI Button OnClick
    public void OnBackButtonClicked()
    {
        Debug.Log("Back button clicked in camera scene - returning to: " + mainSceneName);
        
        // Clean up camera resources before returning to main scene
        if (cameraTexture != null && cameraTexture.isPlaying)
        {
            cameraTexture.Stop();
        }
        
        // Return to main scene
        SceneManager.LoadScene(mainSceneName);
    }
    
    private void InitializeCamera()
    {
        Debug.Log("Initializing camera...");
        
        // Force footer visible at camera initialization
        ForceFooterVisibility();
        
        WebCamDevice[] devices = WebCamTexture.devices;
        
        if (devices.Length == 0)
        {
            Debug.Log("No camera detected");
            camAvailable = false;
            return;
        }
        
        // List available cameras
        for (int i = 0; i < devices.Length; i++)
        {
            Debug.Log($"Camera {i}: {devices[i].name}, isFrontFacing: {devices[i].isFrontFacing}");
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
        
        Debug.Log($"Selected camera: {selectedDevice.name}");
        
        // Clean up any existing camera texture
        if (cameraTexture != null)
        {
            if (cameraTexture.isPlaying)
                cameraTexture.Stop();
                
            cameraTexture = null;
        }
        
        // Create the camera texture with a moderate resolution
        cameraTexture = new WebCamTexture(selectedDevice.name, 1280, 720, 30);
        
        if (cameraTexture == null)
        {
            Debug.LogError("Unable to initialize camera");
            camAvailable = false;
            return;
        }
        
        // Start the camera
        cameraTexture.Play();
        
        // Wait a moment for camera to start
        StartCoroutine(WaitForCameraAndSetup());
    }
    
    private IEnumerator WaitForCameraAndSetup()
    {
        yield return new WaitForSeconds(0.5f);
        
        // Assign camera texture to display
        if (display != null)
        {
            display.texture = cameraTexture;
            camAvailable = true;
        }
        
        // Force footer visible after camera is running
        ForceFooterVisibility();
    }

    // Update is called once per frame
    void Update()
    {
        // Check and ensure footer is visible EVERY frame
        if (footerImage != null && !footerImage.gameObject.activeSelf)
        {
            ForceFooterVisibility();
        }
        
        if (!camAvailable || cameraTexture == null || !cameraTexture.isPlaying)
            return;
            
        // Handle video rotation for different device orientations
        int orient = -cameraTexture.videoRotationAngle;
        
        // Apply rotation to display if needed
        if (display != null)
        {
            display.rectTransform.localEulerAngles = new Vector3(0, 0, orient);
            
            // Check if video is mirrored and fix if needed
            if (cameraTexture.videoVerticallyMirrored)
            {
                display.rectTransform.localScale = new Vector3(1, -1, 1);
            }
            else
            {
                display.rectTransform.localScale = Vector3.one;
            }
        }
    }
    
    // Called when the camera scene is being left
    void OnDestroy()
    {
        // Clean up camera resources
        Debug.Log("CameraController being destroyed");
        if (cameraTexture != null && cameraTexture.isPlaying)
        {
            cameraTexture.Stop();
        }
    }
    
    void OnDisable()
    {
        Debug.Log("CameraController disabled");
        if (cameraTexture != null && cameraTexture.isPlaying)
        {
            cameraTexture.Stop();
        }
    }
    
    void OnApplicationPause(bool pauseStatus)
    {
        // Handle app pausing - crucial for Android
        Debug.Log($"Application paused: {pauseStatus}");
        
        // Force footer visibility on app resume
        if (!pauseStatus)
        {
            ForceFooterVisibility();
        }
        
        if (cameraTexture != null)
        {
            if (pauseStatus)
            {
                if (cameraTexture.isPlaying)
                    cameraTexture.Stop();
            }
            else if (camAvailable)
            {
                if (!cameraTexture.isPlaying)
                    cameraTexture.Play();
            }
        }
    }
}