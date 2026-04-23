package com.example.AI.dto;

public class AiResponse {

    private String title;
    private String content;
    private String responseStatus;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public AiResponse(String title, String content, String responseStatus) {
        this.title = title;
        this.content = content;
        this.responseStatus = responseStatus;
    }

    public AiResponse() {
    }
}
