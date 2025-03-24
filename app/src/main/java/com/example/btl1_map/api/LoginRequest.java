package com.example.btl1_map.api;

public class LoginRequest {
    private String email;
    private String pid;

    public LoginRequest(String email, String pid) {
        this.email = email;
        this.pid = pid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
} 