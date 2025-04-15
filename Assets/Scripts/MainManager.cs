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

    private void Start()
    {
        // Setup image buttons
        SetupImageButtons();
    }

    private void SetupImageButtons()
    {
        // Add click handlers to Images by adding EventTrigger components
        if (findImageButton != null) 
        {
            AddEventTrigger(findImageButton.gameObject, () => SceneManager.LoadScene("CameraScene"));
        }
        
        if (findPlatformButton != null) 
        {
            AddEventTrigger(findPlatformButton.gameObject, () => SceneManager.LoadScene("PositionScene"));
        }
        
        if (logoutButton != null) 
        {
            AddEventTrigger(logoutButton.gameObject, OnLogoutButtonClicked);
        }
    }

    private void AddEventTrigger(GameObject obj, UnityEngine.Events.UnityAction action)
    {
        // Add EventTrigger if it doesn't exist
        EventTrigger trigger = obj.GetComponent<EventTrigger>();
        if (trigger == null)
            trigger = obj.AddComponent<EventTrigger>();
            
        // Create entry for click/tap event
        EventTrigger.Entry entry = new EventTrigger.Entry();
        entry.eventID = EventTriggerType.PointerClick;
        
        // Add callback
        entry.callback.AddListener((eventData) => { action.Invoke(); });
        
        // Add entry to trigger
        trigger.triggers.Add(entry);
    }

    private void OnLogoutButtonClicked()
    {
        // Clear user data
        PlayerPrefs.DeleteKey("UserToken");
        PlayerPrefs.DeleteKey("UserName");
        PlayerPrefs.Save();
        
        // Return to login screen
        SceneManager.LoadScene("SignScene");
    }
}