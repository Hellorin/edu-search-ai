package io.hellorin.edusearchai.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "objectives")
public class ObjectiveDocument {
    @Id
    private String id;
    private String title;
    private String content;
    private String source;
    private Long timestamp;

    public ObjectiveDocument() {}

    public ObjectiveDocument(String id, String title, String content, String source, Long timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.source = source;
        this.timestamp = timestamp;
    }

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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
} 