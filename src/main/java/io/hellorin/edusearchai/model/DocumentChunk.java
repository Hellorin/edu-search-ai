package io.hellorin.edusearchai.model;

import java.util.List;

public class DocumentChunk {
    private String id;
    private String title;
    private String content;
    private String source;
    private long timestamp;

    public DocumentChunk() {}

    public DocumentChunk(String id, String title, String content, List<Float> embedding, String source, long timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.source = source;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
} 