# 📚 EduSearch AI

A Spring Boot application that leverages AI capabilities for educational search and content processing. This project uses Spring AI and OpenAI integration to provide intelligent search and processing of educational content with MongoDB vector storage.

## Features

- **PDF Document Processing**: Uses Apache PDFBox for extracting text from PDF documents
- **AI-Powered Search**: Leverages Spring AI and OpenAI for intelligent document search and question answering
- **Vector Storage**: MongoDB Atlas vector store for efficient semantic search and content retrieval
- **Smart Sidenotes Integration**: Combines course content with student notes for comprehensive answers
- **Objectives Analysis**: Performs vector search for each objective point against course content
- **Multi-document Support**: Handles PDF documents from different categories (courses, notes, objectives)
- **Interactive API Documentation**: Swagger UI for exploring and testing the API
- **Intelligent Document Chunking**: Smart text chunking for better search results

## Technology Stack

- **Java 17** with Spring Boot 3.2.3
- **Spring AI 1.0.0** for AI/ML capabilities
- **OpenAI API** for embeddings and chat completions
- **MongoDB Atlas** for vector storage
- **Apache PDFBox 2.0.7** for PDF processing
- **SpringDoc OpenAPI 2.3.0** for API documentation
- **Maven** for dependency management

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- OpenAI API key
- MongoDB Atlas account (for vector storage)

## Environment Variables

Set the following environment variables:

```bash
# Required
OPENAI_API_KEY=your_openai_api_key_here

# Optional (defaults to local MongoDB)
MONGODB_URI=mongodb://localhost:27017/edusearchai
```

## Getting Started

1. **Clone the repository:**
```bash
git clone https://github.com/yourusername/edu-search-ai.git
cd edu-search-ai
```

2. **Configure your environment variables:**
   - Set the `OPENAI_API_KEY` environment variable
   - Optionally set `MONGODB_URI` if using a different MongoDB instance

3. **Build the project:**
```bash
mvn clean install
```

4. **Run the application:**
```bash
mvn spring-boot:run
```

5. **Access the API:**
   - **Swagger UI**: `http://localhost:8080/swagger-ui.html`
   - **OpenAPI JSON**: `http://localhost:8080/api-docs`
   - **API Base URL**: `http://localhost:8080/api`

## API Documentation

### Swagger UI
The application includes comprehensive API documentation powered by Swagger/OpenAPI 3. Once the application is running, you can access the interactive API documentation at:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

The Swagger UI provides:
- Interactive API exploration
- Request/response examples
- Parameter validation
- Try-it-out functionality for testing endpoints
- Detailed documentation for each endpoint

### API Endpoints

#### Document Search Endpoints

**POST /api/search/query**
- **Description**: Search documents and get AI-generated answers
- **Request Body**: 
```json
{
  "query": "What is the recommended amount of water intake?"
}
```
- **Response**: 
```json
{
  "answer": "Based on the Swiss nutrition pyramid...",
  "query": "What is the recommended amount of water intake?",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### Objectives Endpoints

**POST /api/objectives/search**
- **Description**: Search objectives against course documents
- **Request Body**: `"weekend1.pdf"` (objective document name)
- **Response**: Structured results showing which course content is most relevant to each objective

**GET /api/objectives/list**
- **Description**: Get available objective documents
- **Response**: List of available objective document names

## Project Structure

```
eduSearchAi/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── io/hellorin/edusearchai/
│   │   │       ├── controller/           # REST API endpoints
│   │   │       │   ├── InDocumentSearchController.java
│   │   │       │   └── ObjectivesController.java
│   │   │       ├── service/              # Business logic
│   │   │       │   ├── InDocumentSearchService.java
│   │   │       │   ├── ObjectivesService.java
│   │   │       │   ├── ObjectiveExtractionService.java
│   │   │       │   ├── OpenAIEmbeddingService.java
│   │   │       │   └── PDFProcessingService.java
│   │   │       ├── repository/           # Data access layer
│   │   │       │   ├── CourseVectorRepository.java
│   │   │       │   ├── NoteVectorRepository.java
│   │   │       │   ├── DocumentRepository.java
│   │   │       │   └── ObjectiveDocumentRepository.java
│   │   │       ├── model/                # Data models
│   │   │       │   ├── SearchRequest.java
│   │   │       │   ├── SearchResponse.java
│   │   │       │   ├── DocumentChunk.java
│   │   │       │   └── ObjectiveDocument.java
│   │   │       ├── config/               # Configuration
│   │   │       │   ├── OpenApiConfig.java
│   │   │       │   ├── VectorStoreConfig.java
│   │   │       │   └── DocumentLoader.java
│   │   │       ├── component/            # Components
│   │   │       │   └── MathComponent.java
│   │   │       └── EduSearchAiApplication.java
│   │   └── resources/
│   │       ├── application.properties     # Application configuration
│   │       └── documents/                # PDF documents
│   │           ├── objectives/           # Course objectives
│   │           │   └── weekend1.pdf
│   │           └── public/              # Public course documents
│   │               └── Recommandations-nutritionnelles_version-longue_F.pdf
│   └── test/
│       └── java/
│           └── io/hellorin/edusearchai/
│               └── controller/           # Controller tests
│                   ├── InDocumentSearchControllerTest.java
│                   └── ObjectivesControllerTest.java
├── pom.xml                              # Maven configuration
└── README.md                           # This file
```

## Core Features

### 1. Document Search with AI
The application provides intelligent document search using:
- **Vector Similarity Search**: Uses OpenAI embeddings to find semantically similar content
- **AI-Generated Answers**: Leverages OpenAI's chat model to generate comprehensive answers
- **Source Attribution**: Always mentions sources in responses
- **Fallback Handling**: Gracefully handles cases where no relevant content is found

### 2. Smart Sidenotes Integration
The system intelligently combines course content with student notes:
- **Two-Stage Search**: First searches course documents, then student notes if needed
- **Contextual Enhancement**: Uses student notes to provide additional insights
- **Structured Responses**: Clearly separates course content from sidenotes

### 3. Objectives Analysis
Advanced objective analysis capabilities:
- **Objective Parsing**: Uses AI to intelligently extract individual objective points
- **Vector Search per Objective**: Performs semantic search for each objective point
- **Structured Results**: Provides detailed breakdown of objective coverage

### 4. PDF Processing
Robust PDF document handling:
- **Apache PDFBox Integration**: Extracts text from PDF documents
- **Intelligent Chunking**: Splits documents into optimal chunks for search
- **Metadata Preservation**: Maintains document metadata for source attribution

## Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# OpenAI Configuration
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.model=text-embedding-ada-002

# MongoDB Configuration
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/edusearchai}
spring.data.mongodb.database=edusearchai

# Vector Store Configuration
spring.ai.vectorstore.mongodb.course.collection-name=courseEmbedding
spring.ai.vectorstore.mongodb.note.collection-name=noteEmbedding
spring.ai.vectorstore.mongodb.objectives.collection-name=objectives

# Document Processing
app.document.vector-dimension=1536
app.pdf.chunk-size=1000

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## Example Usage

### Document Search
```bash
curl -X POST "http://localhost:8080/api/search/query" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "What is the recommended amount of water intake according to the Swiss nutrition pyramid?"
  }'
```

### Objectives Search
```bash
curl -X POST "http://localhost:8080/api/objectives/search" \
  -H "Content-Type: text/plain" \
  -d "weekend1.pdf"
```

### List Available Objectives
```bash
curl -X GET "http://localhost:8080/api/objectives/list"
```

## Development

### Building
```bash
mvn clean install
```

### Testing
```bash
mvn test
```

### Running Tests
The project includes comprehensive tests for:
- Controller endpoints
- Service layer functionality
- API documentation

## Dependencies

### Core Dependencies
- **Spring Boot 3.2.3**: Main application framework
- **Spring AI 1.0.0**: AI/ML capabilities
- **Spring Boot Starter Web**: Web application support
- **Spring Boot Starter Data MongoDB**: MongoDB integration
- **Apache PDFBox 2.0.7**: PDF processing
- **SpringDoc OpenAPI 2.3.0**: API documentation

### AI/ML Dependencies
- **spring-ai-starter-model-openai**: OpenAI integration
- **spring-ai-starter-vector-store-mongodb-atlas**: MongoDB vector store

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## Support

For questions or issues, please:
1. Check the [Swagger UI](http://localhost:8080/swagger-ui.html) for API documentation
2. Review the test files for usage examples
3. Open an issue on GitHub
