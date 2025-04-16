using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using TMPro;
using UnityEngine.EventSystems;

public class MainManager : MonoBehaviour
{
    [Header("Navigation")]
    [SerializeField] private Image findImageButton;      // For CameraScene
    [SerializeField] private Image findPlatformButton;   // For ARScene/PositionScene
    [SerializeField] private Image logoutButton;         // Logout button in footer
    [SerializeField] private Image backButton;           // Back button if present

    private void Awake()
    {
        // Make sure we have the EventSystem
        if (FindAnyObjectByType<EventSystem>() == null)
        {
            Debug.LogError("No EventSystem found in the scene. Adding one.");
            GameObject eventSystem = new GameObject("EventSystem");
            eventSystem.AddComponent<EventSystem>();
            eventSystem.AddComponent<StandaloneInputModule>();
        }
    }

    private void Start()
    {
        // Log what buttons we have
        Debug.Log($"MainManager buttons - Image: {findImageButton != null}, Platform: {findPlatformButton != null}, " +
                  $"Logout: {logoutButton != null}, Back: {backButton != null}");
        
        // Setup image buttons
        SetupImageButtons();
        SetupBackButton(); // Set up the back button separately with the method that works in cam.cs
        Debug.Log("MainManager started and buttons set up");
    }

    // This direct button test can be called from Unity UI Button OnClick() event
    public void OnImageButtonClicked()
    {
        Debug.Log("Image button clicked directly");
        SceneManager.LoadScene("CameraScene");
    }

    public void OnPlatformButtonClicked()
    {
        Debug.Log("Platform button clicked directly");
        SceneManager.LoadScene("ARScene");
    }

    public void OnLogoutButtonClickedDirect()
    {
        Debug.Log("Logout button clicked directly");
        OnLogoutButtonClicked();
    }

    public void OnBackButtonClickedDirect()
    {
        Debug.Log("Back button clicked directly from Inspector");
        GoToSignScene();
    }

    private void SetupBackButton()
    {
        if (backButton != null)
        {
            Debug.Log("Setting up back button with cam.cs method");
            
            // Make sure the GameObject is active
            backButton.gameObject.SetActive(true);
            
            // Clean slate - remove any existing components that could interfere
            EventTrigger existingTrigger = backButton.gameObject.GetComponent<EventTrigger>();
            if (existingTrigger != null)
            {
                Destroy(existingTrigger);
            }
            
            Button existingButton = backButton.gameObject.GetComponent<Button>();
            if (existingButton != null)
            {
                Destroy(existingButton);
            }
            
            // Add a Button component for direct clicking
            Button btn = backButton.gameObject.AddComponent<Button>();
            btn.onClick.AddListener(() => {
                Debug.Log("Back button clicked via Button.onClick");
                GoToSignScene();
            });
            
            // Also add EventTrigger as a fallback
            EventTrigger trigger = backButton.gameObject.AddComponent<EventTrigger>();
            EventTrigger.Entry entry = new EventTrigger.Entry();
            entry.eventID = EventTriggerType.PointerClick;
            entry.callback.AddListener((data) => {
                Debug.Log("Back button clicked via EventTrigger");
                GoToSignScene();
            });
            trigger.triggers.Add(entry);
            
            // Add a BoxCollider2D for better touch detection
            BoxCollider2D existingCollider = backButton.gameObject.GetComponent<BoxCollider2D>();
            if (existingCollider != null)
            {
                Destroy(existingCollider);
            }
            
            BoxCollider2D collider = backButton.gameObject.AddComponent<BoxCollider2D>();
            RectTransform rect = backButton.GetComponent<RectTransform>();
            if (rect != null)
            {
                // Make the collider larger for easier clicking
                collider.size = new Vector2(rect.sizeDelta.x * 1.5f, rect.sizeDelta.y * 1.5f);
                Debug.Log($"Added BoxCollider2D to back button with size {collider.size}");
            }
            
            Debug.Log("Back button fully set up");
        }
    }

    private void GoToSignScene()
    {
        Debug.Log("Navigating to SignScene");
        SceneManager.LoadScene("SignScene");
    }

    private void SetupImageButtons()
    {
        // Try both approaches - event triggers and direct button clicks
        if (findImageButton != null) 
        {
            ClearAndAddEventTrigger(findImageButton.gameObject, () => {
                Debug.Log("Find Image button clicked via event trigger - loading CameraScene");
                SceneManager.LoadScene("CameraScene");
            });
            
            // Add a direct button listener if the Image has a Button component
            Button imgButton = findImageButton.GetComponent<Button>();
            if (imgButton == null)
            {
                // Add a Button component if it doesn't have one
                imgButton = findImageButton.gameObject.AddComponent<Button>();
                Debug.Log("Added Button component to findImageButton");
            }
            imgButton.onClick.AddListener(OnImageButtonClicked);
        }
        
        if (findPlatformButton != null) 
        {
            ClearAndAddEventTrigger(findPlatformButton.gameObject, () => {
                Debug.Log("Find Platform button clicked via event trigger - loading ARScene");
                SceneManager.LoadScene("ARScene");
            });
            
            // Add a direct button listener if the Image has a Button component
            Button platformButton = findPlatformButton.GetComponent<Button>();
            if (platformButton == null)
            {
                // Add a Button component if it doesn't have one
                platformButton = findPlatformButton.gameObject.AddComponent<Button>();
                Debug.Log("Added Button component to findPlatformButton");
            }
            platformButton.onClick.AddListener(OnPlatformButtonClicked);
        }
        
        if (logoutButton != null) 
        {
            ClearAndAddEventTrigger(logoutButton.gameObject, () => {
                Debug.Log("Logout button clicked via event trigger");
                OnLogoutButtonClicked();
            });
            
            // Add a direct button listener if the Image has a Button component
            Button logoutBtn = logoutButton.GetComponent<Button>();
            if (logoutBtn == null)
            {
                // Add a Button component if it doesn't have one
                logoutBtn = logoutButton.gameObject.AddComponent<Button>();
                Debug.Log("Added Button component to logoutButton");
            }
            logoutBtn.onClick.AddListener(OnLogoutButtonClickedDirect);
            
            // Add a BoxCollider2D if needed for better touch detection
            EnsureCollider(logoutButton.gameObject);
        }
    }

    private void ClearAndAddEventTrigger(GameObject obj, UnityEngine.Events.UnityAction action)
    {
        // Remove any existing triggers to avoid duplication
        EventTrigger existingTrigger = obj.GetComponent<EventTrigger>();
        if (existingTrigger != null)
        {
            Debug.Log($"Removing existing EventTrigger from {obj.name}");
            Destroy(existingTrigger);
        }
        
        // Add new EventTrigger
        EventTrigger trigger = obj.AddComponent<EventTrigger>();
        Debug.Log($"Added new EventTrigger to {obj.name}");
            
        // Create entry for click/tap event
        EventTrigger.Entry entry = new EventTrigger.Entry();
        entry.eventID = EventTriggerType.PointerClick;
        
        // Add callback
        entry.callback.AddListener((eventData) => { 
            Debug.Log($"EventTrigger activated for {obj.name}");
            action.Invoke(); 
        });
        
        // Add entry to trigger
        trigger.triggers.Add(entry);
        
        // Ensure it has a collider for better interaction
        EnsureCollider(obj);
    }
    
    private void EnsureCollider(GameObject obj)
    {
        // Add a collider if one doesn't exist
        if (obj.GetComponent<Collider2D>() == null)
        {
            BoxCollider2D collider = obj.AddComponent<BoxCollider2D>();
            RectTransform rect = obj.GetComponent<RectTransform>();
            if (rect != null)
            {
                // Make the collider a bit larger than the actual UI element for easier clicking
                collider.size = new Vector2(rect.sizeDelta.x * 1.2f, rect.sizeDelta.y * 1.2f);
                Debug.Log($"Added BoxCollider2D to {obj.name} with size {collider.size}");
            }
            else
            {
                Debug.LogWarning($"No RectTransform found on {obj.name}, using default collider size");
            }
        }
        else
        {
            Debug.Log($"{obj.name} already has a collider");
        }
    }

    private void OnLogoutButtonClicked()
    {
        Debug.Log("Executing logout...");
        
        // Clear user data
        PlayerPrefs.DeleteKey("UserToken");
        PlayerPrefs.DeleteKey("UserName");
        PlayerPrefs.Save();
        
        // Return to login screen
        SceneManager.LoadScene("SignScene");
    }
}