package io.hellorin.edusearchai.repository;

import io.hellorin.edusearchai.component.MathComponent;
import io.hellorin.edusearchai.model.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryDocumentRepositoryTest {

    private InMemoryDocumentRepository repository;
    private Document doc1;
    private Document doc2;
    private Document doc3;

    @BeforeEach
    void setUp() {
        MathComponent mathComponent = Mockito.mock(MathComponent.class);
        Mockito.when(mathComponent.cosineSimilarity(Mockito.anyList(), Mockito.anyList())).thenCallRealMethod();
        repository = new InMemoryDocumentRepository(mathComponent);
        
        // Create test documents with embeddings
        doc1 = new Document();
        doc1.setId("1");
        doc1.setSource("source1");
        doc1.setEmbedding(Arrays.asList(1.0f, 0.0f, 0.0f));

        doc2 = new Document();
        doc2.setId("2");
        doc2.setSource("source1");
        doc2.setEmbedding(Arrays.asList(0.0f, 1.0f, 0.0f));

        doc3 = new Document();
        doc3.setId("3");
        doc3.setSource("source2");
        doc3.setEmbedding(Arrays.asList(0.0f, 0.0f, 1.0f));
    }

    @Test
    void saveAll_ShouldSaveAllDocuments() {
        List<Document> documents = Arrays.asList(doc1, doc2, doc3);
        List<Document> savedDocs = repository.saveAll(documents);
        
        assertEquals(3, repository.size());
        assertEquals(3, savedDocs.size());
    }

    @Test
    void findSimilarDocuments_ShouldReturnMostSimilarDocuments() {
        repository.saveAll(Arrays.asList(doc1, doc2, doc3));
        
        // Query vector similar to doc1
        List<Float> queryEmbedding = Arrays.asList(0.9f, 0.1f, 0.0f);
        List<Document> similarDocs = repository.findSimilarDocuments(queryEmbedding, 2);
        
        assertEquals(2, similarDocs.size());
        assertEquals("1", similarDocs.get(0).getId()); // Should be most similar to doc1
    }

    @Test
    void findSimilarDocuments_ShouldHandleEmptyRepository() {
        List<Float> queryEmbedding = Arrays.asList(1.0f, 0.0f, 0.0f);
        List<Document> similarDocs = repository.findSimilarDocuments(queryEmbedding, 5);
        
        assertTrue(similarDocs.isEmpty());
    }

    @Test
    void findSimilarDocuments_ShouldHandleDocumentsWithoutEmbeddings() {
        Document docWithoutEmbedding = new Document();
        docWithoutEmbedding.setId("4");
        docWithoutEmbedding.setSource("source3");
        
        repository.saveAll(Arrays.asList(doc1, docWithoutEmbedding));
        
        List<Float> queryEmbedding = Arrays.asList(1.0f, 0.0f, 0.0f);
        List<Document> similarDocs = repository.findSimilarDocuments(queryEmbedding, 5);
        
        assertEquals(1, similarDocs.size());
        assertEquals("1", similarDocs.get(0).getId());
    }

    @Test
    void size_ShouldReturnCorrectNumberOfDocuments() {
        assertEquals(0, repository.size());
        
        repository.saveAll(Arrays.asList(doc1, doc2));
        assertEquals(2, repository.size());
        
        repository.saveAll(Arrays.asList(doc3));
        assertEquals(3, repository.size());
    }
} 