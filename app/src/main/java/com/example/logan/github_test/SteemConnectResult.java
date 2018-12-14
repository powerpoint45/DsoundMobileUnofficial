package com.example.logan.github_test;

class SteemConnectResult {
    private String refreshToken;
    private String accessToken;
    private String userName;

    String getUserName() {
        return userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getRefreshToken() {
        return refreshToken;
    }

    void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    String getAccessToken() {
        return accessToken;
    }

    void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
