package com.fintara.dtos.customerDTO;

import lombok.Data;

@Data
public class NotificationRequest {
    private String token;
    private String title;
    private String body;

    public NotificationRequest() {
    }

    public NotificationRequest(String token, String title, String body) {
        this.token = token;
        this.title = title;
        this.body = body;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
