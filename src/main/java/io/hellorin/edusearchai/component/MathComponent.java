package io.hellorin.edusearchai.component;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MathComponent {
    /**
     * Calculates the cosine similarity between two vectors.
     * Cosine similarity measures the cosine of the angle between two vectors,
     * ranging from -1 to 1, where 1 indicates identical vectors.
     *
     * @param vec1 First vector
     * @param vec2 Second vector
     * @return Cosine similarity score between the vectors
     * @throws IllegalArgumentException if vectors have different dimensions
     */
    public double cosineSimilarity(List<Float> vec1, List<Float> vec2) {
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += vec1.get(i) * vec1.get(i);
            norm2 += vec2.get(i) * vec2.get(i);
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
