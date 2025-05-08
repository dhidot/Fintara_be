package com.fintara.dtos;

public class CloudinaryUploadResponse {
    private String ktpPhotoUrl;
    private String selfiePhotoUrl;

    public CloudinaryUploadResponse() {}

    public CloudinaryUploadResponse(String ktpPhotoUrl, String selfiePhotoUrl) {
        this.ktpPhotoUrl = ktpPhotoUrl;
        this.selfiePhotoUrl = selfiePhotoUrl;
    }

    public String getKtpPhotoUrl() {
        return ktpPhotoUrl;
    }

    public void setKtpPhotoUrl(String ktpPhotoUrl) {
        this.ktpPhotoUrl = ktpPhotoUrl;
    }

    public String getSelfiePhotoUrl() {
        return selfiePhotoUrl;
    }

    public void setSelfiePhotoUrl(String selfiePhotoUrl) {
        this.selfiePhotoUrl = selfiePhotoUrl;
    }
}
