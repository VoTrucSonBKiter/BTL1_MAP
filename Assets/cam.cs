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
    [SerializeField] private Button stopCameraButton;
    [SerializeField] private string mainSceneName = "MainScene";
    [SerializeField] private bool isARScene = false;

    private Texture2D lastFrameTexture;
    WebCamTexture webcam;    
    // Start is called once before the first execution of Update after the MonoBehaviour is created
    void Start()
    {
        // Force footer to be visible and positioned correctly at the start
        PositionFooterAtBottom();
        ForceFooterVisibility();
        
        // Log what we have
        Debug.Log($"CameraController - Display: {display != null}, BackButton: {backButtonImage != null}, " +
                 $"Footer: {footerImage != null}");
        
        if (display != null)
        {
            Debug.Log($"RawImage dimensions: {display.rectTransform.rect.width}x{display.rectTransform.rect.height}");
        }
        
        webcam = new WebCamTexture();
        display.texture = webcam;
        webcam.Play();
        Debug.Log("WebCamTexture started");

        StartCoroutine(AdjustCameraOrientation());
        
        // Set up back button immediately
        SetupBackButton();
        
        // Ensure EventSystem exists
        EnsureEventSystem();
        
        // Set up the stop camera button
        if (stopCameraButton != null)
        {
            stopCameraButton.onClick.AddListener(OnStopCameraButtonClicked);
            Debug.Log("Stop camera button set up");
        }
        else
        {
            Debug.LogError("Stop camera button reference is missing");
        }
    }
    
// Public method that can be called from UI Button OnClick
    // Public method that can be called from UI Button OnClick
    public void OnStopCameraButtonClicked()
    {
        Debug.Log("Camera play/pause button clicked");
        
        if (webcam != null)
        {
            if (webcam.isPlaying)
            {
                // Make sure the webcam has had time to initialize properly
                if (!webcam.didUpdateThisFrame)
                {
                    Debug.Log("Waiting for a valid frame before pausing...");
                    // You could add a loading indicator here if needed
                    StartCoroutine(PauseCameraWhenReady());
                    return;
                }
                
                // Ensure texture is created with correct dimensions
                if (lastFrameTexture == null || lastFrameTexture.width != webcam.width || lastFrameTexture.height != webcam.height)
                {
                    lastFrameTexture = new Texture2D(webcam.width, webcam.height, TextureFormat.RGBA32, false);
                }
                
                // Copy the current webcam frame to our texture - ensure we have a valid frame
                lastFrameTexture.SetPixels32(webcam.GetPixels32());
                lastFrameTexture.Apply();
                
                // IMPORTANT: Set the display texture BEFORE stopping the camera
                display.texture = lastFrameTexture;
                display.color = Color.white;
                
                // Now pause by stopping the camera
                webcam.Stop();
                
                Debug.Log("Camera paused - last frame captured and displayed");
            }
            else
            {
                // Rest of your code remains the same...
                webcam = new WebCamTexture();
                display.texture = webcam;
                
                webcam.Play();
                display.color = Color.white;
                Debug.Log("Camera restarted with new WebCamTexture");
                
                StartCoroutine(AdjustCameraOrientation());
            }
        }
    }

    // New coroutine to wait for a valid frame before pausing
    private IEnumerator PauseCameraWhenReady()
    {
        // Wait until we have a valid frame to capture
        int attemptCount = 0;
        int maxAttempts = 10;
        
        while (!webcam.didUpdateThisFrame && attemptCount < maxAttempts)
        {
            yield return new WaitForEndOfFrame();
            attemptCount++;
        }
        
        // Now try pausing again
        OnStopCameraButtonClicked();
    }
    // Add this coroutine to wait for the webcam to initialize before adjusting
    private IEnumerator AdjustCameraOrientation()
    {
        // Wait a frame to ensure the webcam has initialized
        yield return new WaitForEndOfFrame();
        
        if (webcam != null && webcam.isPlaying && display != null)
        {
            // Wait a bit longer for camera to fully initialize
            yield return new WaitForSeconds(0.5f);
            
            // Get the device orientation to determine how to adjust
            DeviceOrientation orientation = Input.deviceOrientation;
            Debug.Log($"Device orientation: {orientation}, WebCam dimensions: {webcam.width}x{webcam.height}");
            
            // Set rotation to -90 degrees (clockwise rotation)
            int rotationAngle = -90;
            
            // Apply rotation to the RawImage
            display.rectTransform.localEulerAngles = new Vector3(0, 0, rotationAngle);
            
            // Reset the size to fill the container
            RectTransform parentRect = display.transform.parent.GetComponent<RectTransform>();
            if (parentRect != null)
            {
                // Set the size to match the parent's size
                display.rectTransform.sizeDelta = Vector2.zero;
                
                // Center it in the parent
                display.rectTransform.anchorMin = new Vector2(0, 0);
                display.rectTransform.anchorMax = new Vector2(1, 1);
                display.rectTransform.pivot = new Vector2(0.5f, 0.5f);
                display.rectTransform.offsetMin = Vector2.zero;
                display.rectTransform.offsetMax = Vector2.zero;
                
                // Calculate the aspect ratio adjustment needed for the rotation
                float screenAspect = (float)Screen.width / Screen.height;
                float webcamAspect = (float)webcam.width / webcam.height;
                
                // Since we rotated the camera, we need to invert the aspect ratio
                float rotatedWebcamAspect = 1.0f / webcamAspect;
                
                // Calculate proper scale to maintain aspect ratio
                float scaleY = 1.0f;
                float scaleX = 1.0f;
                
                if (rotatedWebcamAspect < screenAspect)
                {
                    // Camera is taller than screen, fit to width and extend height
                    scaleX = 1.0f;
                    scaleY = screenAspect / rotatedWebcamAspect;
                }
                else
                {
                    // Camera is wider than screen, fit to height and extend width
                    scaleX = rotatedWebcamAspect / screenAspect;
                    scaleY = 1.0f;
                }
                
                // Apply different fill factors for width and height
                float yFillFactor = 0.9f;   // Keep vertical fill the same
                float xFillFactor = 1.2f;   // Reduce horizontal stretch
                
                // Apply the adjusted scaling
                display.rectTransform.localScale = new Vector3(scaleX * xFillFactor, scaleY * yFillFactor, 1);
                
                Debug.Log($"Applied non-uniform scaling - X:{scaleX * xFillFactor}, Y:{scaleY * yFillFactor}");
                Debug.Log($"Screen aspect: {screenAspect}, Webcam aspect: {webcamAspect}, Rotated: {rotatedWebcamAspect}");
            }
            
            Debug.Log($"Camera orientation adjusted: Rotation={rotationAngle}");
        }
        else
        {
            Debug.LogError("WebCam not initialized properly");
        }
    }
    // Position the footer at the bottom of the screen
    private void PositionFooterAtBottom()
    {
        if (footerImage != null)
        {
            // Get the footer's RectTransform
            RectTransform footerRect = footerImage.GetComponent<RectTransform>();
            if (footerRect != null)
            {
                // Anchor it to the bottom of the screen
                footerRect.anchorMin = new Vector2(0, 0);
                footerRect.anchorMax = new Vector2(1, 0);
                footerRect.pivot = new Vector2(0.5f, 0);
                
                // Position it at the bottom with zero offset
                footerRect.anchoredPosition = new Vector2(0, 0);
                
                Debug.Log("Footer positioned at the bottom of the screen");
            }
            else
            {
                Debug.LogError("Footer RectTransform is missing");
            }
        }
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
        
        // Return to main scene
        SceneManager.LoadScene(mainSceneName);
    }

}
    
        
 
    