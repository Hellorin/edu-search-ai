package io.hellorin.edusearchai.repository;

import io.hellorin.edusearchai.model.DocumentChunk;

import java.util.List;
import java.util.Map;

public abstract class DocumentRepository {

    protected DocumentRepository() {

    }

    public abstract Map<String, DocumentChunk> getDocumentsById();

    public abstract int size();

    public abstract List<DocumentChunk> saveAll(List<DocumentChunk> documents);
}
