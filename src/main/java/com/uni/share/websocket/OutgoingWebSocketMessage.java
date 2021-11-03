package com.uni.share.websocket;
// TODO FR: comments
public class OutgoingWebSocketMessage {
    private String key;
    private Object content;


    public OutgoingWebSocketMessage() {
    }


    public OutgoingWebSocketMessage(String key, Object content) {
        this.key = key;
        this.content = content;
    }


    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public Object getContent() {
        return content;
    }


    public void setContent(Object content) {
        this.content = content;
    }
}