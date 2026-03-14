package com.example.demo;

public class ChatResponse {
    private String reply;
    private long latency;

    public ChatResponse(String reply, long latency) {
        this.reply = reply;
        this.latency = latency;
    }

    public String getReply() { return reply; }
    public long getLatency() { return latency; }
}