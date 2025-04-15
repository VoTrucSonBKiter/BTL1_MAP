using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Networking;
using UnityEngine.SceneManagement;
using TMPro;
using System;

public class SignManager : MonoBehaviour
{
    [Header("UI References")]
    [SerializeField] private TMP_InputField emailInput;
    [SerializeField] private TMP_InputField pidInput;
    [SerializeField] private Button loginButton;
    [SerializeField] private TextMeshProUGUI statusText;

    [Header("Settings")]
    [SerializeField] private string apiUrl = "http://localhost:3000/api/login";
    [SerializeField] private bool useBackend = false; // Set to false for simple login

    private void Start()
    {
        // Add listeners
        loginButton.onClick.AddListener(OnLoginButtonClicked);

        // Clear status text
        if (statusText != null)
            statusText.text = "";
    }

    private void OnLoginButtonClicked()
    {
        string email = emailInput.text;
        string pid = pidInput.text;

        // Check if fields are filled
        if (string.IsNullOrEmpty(email) || string.IsNullOrEmpty(pid))
        {
            DisplayStatus("Please fill in all fields", Color.red);
            return;
        }

        if (useBackend)
        {
            // Use backend for login (for future implementation)
            StartCoroutine(LoginUser(email, pid));
        }
        else
        {
            // Simple login (accepts any input)
            // Store user data in PlayerPrefs
            PlayerPrefs.SetString("UserToken", "sample-token");
            PlayerPrefs.SetString("UserName", email.Split('@')[0]); // Use part of email as name
            PlayerPrefs.Save();
            
            DisplayStatus("Login successful!", Color.green);
            
            // Load main scene after a short delay to show success message
            StartCoroutine(DelayedSceneLoad("MainScene", 1.0f));
        }
    }

    private IEnumerator DelayedSceneLoad(string sceneName, float delay)
    {
        yield return new WaitForSeconds(delay);
        SceneManager.LoadScene(sceneName);
    }

    private IEnumerator LoginUser(string email, string pid)
    {
        // Create the login data
        LoginData loginData = new LoginData
        {
            email = email,
            pid = pid
        };

        string jsonData = JsonUtility.ToJson(loginData);
        
        // Create the request
        using (UnityWebRequest request = new UnityWebRequest(apiUrl, "POST"))
        {
            byte[] bodyRaw = System.Text.Encoding.UTF8.GetBytes(jsonData);
            request.uploadHandler = new UploadHandlerRaw(bodyRaw);
            request.downloadHandler = new DownloadHandlerBuffer();
            request.SetRequestHeader("Content-Type", "application/json");

            DisplayStatus("Logging in...", Color.yellow);
            
            // Send the request
            yield return request.SendWebRequest();

            if (request.result == UnityWebRequest.Result.ConnectionError || 
                request.result == UnityWebRequest.Result.ProtocolError)
            {
                Debug.LogError("Login Error: " + request.error);
                DisplayStatus("Login failed: " + request.error, Color.red);
            }
            else
            {
                Debug.Log("Login Success: " + request.downloadHandler.text);
                
                try
                {
                    // Parse response
                    LoginResponse response = JsonUtility.FromJson<LoginResponse>(request.downloadHandler.text);
                    
                    if (response.success)
                    {
                        // Save user data if needed
                        PlayerPrefs.SetString("UserToken", response.token);
                        PlayerPrefs.SetString("UserName", response.name);
                        PlayerPrefs.Save();
                        
                        // Show success message
                        DisplayStatus("Login successful!", Color.green);
                        
                        // Load main scene
                        SceneManager.LoadScene("MainScene");
                    }
                    else
                    {
                        DisplayStatus("Login failed: " + response.message, Color.red);
                    }
                }
                catch (Exception e)
                {
                    Debug.LogError("Failed to parse response: " + e.Message);
                    DisplayStatus("Login error: Invalid response", Color.red);
                }
            }
        }
    }

    private void DisplayStatus(string message, Color color)
    {
        if (statusText != null)
        {
            statusText.text = message;
            statusText.color = color;
        }
    }

    // Data classes for JSON serialization/deserialization
    [Serializable]
    private class LoginData
    {
        public string email;
        public string pid;
    }

    [Serializable]
    private class LoginResponse
    {
        public bool success;
        public string message;
        public string token;
        public string name;
    }
}