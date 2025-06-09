package io.hellorin.edusearchai.repository;

import io.hellorin.edusearchai.component.MathComponent;
import io.hellorin.edusearchai.model.Document;

import java.util.List;
import java.util.Map;

public abstract class DocumentRepository {

    private final MathComponent mathComponent;

    protected DocumentRepository(MathComponent mathComponent) {
        this.mathComponent = mathComponent;
    }

    /**
     * Finds documents similar to the query embedding using cosine similarity.
     * @param queryEmbedding The embedding vector to compare against
     * @param limit Maximum number of similar documents to return
     * @return List of documents sorted by similarity (most similar first)
     */
    public List<Document> findSimilarDocuments(List<Float> queryEmbedding, int limit) {
        return getDocumentsById().values().stream()
                .filter(doc -> doc.getEmbedding() != null && !doc.getEmbedding().isEmpty())
                .map(doc -> Map.entry(doc, mathComponent.cosineSimilarity(queryEmbedding, doc.getEmbedding())))
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    public abstract Map<String, Document> getDocumentsById();

    public abstract int size();

    public abstract List<Document> saveAll(List<Document> documents);
}
