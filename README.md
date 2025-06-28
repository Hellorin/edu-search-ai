# 📚 EduSearch AI

A Spring Boot application that leverages AI capabilities for educational search and content processing. This project uses Spring AI and OpenAI integration to provide intelligent search and processing of educational content.

## Features

- PDF document processing using Apache PDFBox
- AI-powered search capabilities using Spring AI
- OpenAI integration for advanced text processing
- Vector store support for efficient content retrieval
- Smart sidenotes integration that combines course content with student notes
- Intelligent document chunking for better search results
- **Document Search**: Search through course documents, notes, and objectives using natural language queries
- **Vector Search**: Find semantically similar content using OpenAI embeddings
- **Objectives Analysis**: Perform vector search for each objective point against course content
- **Multi-document Support**: Handles PDF documents from different categories (courses, notes, objectives)
- **API Documentation**: Interactive Swagger UI for exploring and testing the API

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- OpenAI API key

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/edu-search-ai.git
cd edu-search-ai
```

2. Configure your OpenAI API key:
   - Set the `OPENAI_API_KEY` environment variable, or
   - Add it to your application.properties/application.yml file

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

5. Access the Swagger UI:
   - Open your browser and navigate to: `http://localhost:8080/swagger-ui.html`
   - This provides an interactive interface to explore and test all API endpoints

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

### API Endpoints Overview

The following endpoints are available and documented in Swagger:

#### Document Search Endpoints
1. **POST /api/search/query** - Search documents and get AI-generated answers

#### Objectives Endpoints
1. **POST /api/objectives/search** - Search objectives against course documents  
2. **GET /api/objectives/list** - Get available objective documents

## Example Usage

Here's an example of how EduSearch AI processes and responds to queries about educational content:

**Input Query:**
```
What is recommend amount of water I should drink given the swiss nutrition pyramid?
```

**System Response:**
The recommended amount of water you should drink according to the Swiss nutrition pyramid is 1 to 2 liters of non-sugary beverages each day, with a preference for water. This includes options like tap water, mineral water, or herbal and fruit teas (Source: Recommandations-nutritionnelles_version-longue_F.pdf).

**Sidenotes:**
Additional insights from student notes:
- Remember that this recommendation may vary based on physical activity and climate
- Water intake should be spread throughout the day
- Other beverages like coffee and tea can contribute to daily fluid intake

The system processes the query by:
1. Analyzing the question using AI
2. Searching through the course document database
3. Finding relevant information in the Swiss nutrition pyramid document
4. Searching through student notes for additional context
5. Combining course content with relevant sidenotes
6. Providing a comprehensive answer with source references

## Project Structure

```
eduSearchAi/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── io/hellorin/edusearchai/
│   │   │       ├── controller/    # REST API endpoints and request handling
│   │   │       │   ├── InDocumentSearchController.java  # Document search endpoints
│   │   │       │   └── ObjectivesController.java        # Objectives-specific endpoints
│   │   │       ├── service/       # Business logic implementation
│   │   │       ├── repository/    # Data access layer
│   │   │       ├── model/         # Data models and entities
│   │   │       ├── config/        # Application configuration
│   │   │       └── EduSearchAiApplication.java  # Main application class
│   │   └── resources/   # Configuration files
│   │       ├── application.properties  # Application configuration
│   │       └── documents/    # Directory for storing PDF documents
│   │           ├── courses/     # Course-related PDF documents
│   │           ├── notes/       # Study notes and supplementary materials
│   │           ├── objectives/  # Course objectives documents
│   │           └── public/      # Publicly accessible PDF documents
├── pom.xml              # Maven configuration
└── README.md           # This file
```

## Dependencies

- Spring Boot 3.2.3
- Spring AI 1.0.0
- Apache PDFBox 2.0.7
- Spring Boot Starter Web
- Spring Context
- Spring Test

### Building

To build the project:
```bash
mvn clean install
```

### Testing

To run tests:
```bash
mvn test
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## API Endpoints

### Document Search Endpoints

#### 1. General Document Search
```
POST /api/search/query
Content-Type: application/json

{
  "query": "your search query here"
}
```
Returns AI-generated answers based on relevant document content.

### Objectives Endpoints

#### 1. Objectives Vector Search
```
POST /api/objectives/search
Content-Type: text/plain

"weekend1.pdf"
```
Performs vector search for each objective point in the specified objective document against course documents. Returns structured results showing which course content is most relevant to each objective.

#### 2. List Available Objectives
```
GET /api/objectives/list
```
Returns a list of all available objective documents that can be searched.

## How Objectives Search Works

1. **Document Parsing**: The system parses objective documents into individual objective points using pattern recognition (numbered lists, bullet points, keywords)
2. **Vector Embedding**: Each objective point is converted into a vector embedding using OpenAI's embedding model
3. **Similarity Search**: The system finds the most similar course content for each objective point using cosine similarity
4. **Structured Results**: Returns a detailed breakdown showing each objective point and its relevant course content

## Setup

1. Ensure you have the required environment variables for OpenAI API access
2. Place your PDF documents in the appropriate folders:
   - `src/main/resources/documents/public/` - Public course documents
   - `src/main/resources/documents/courses/` - Course materials
   - `src/main/resources/documents/notes/` - Student notes
   - `src/main/resources/documents/objectives/` - Course objectives
3. Run the application - documents will be automatically loaded and processed

## Example Usage

### Search for a specific topic:
```bash
curl -X POST http://localhost:8080/api/search/query \
  -H "Content-Type: application/json" \
  -d '{"query": "What are the main principles of nutrition?"}'
```

### Analyze objectives against courses:
```bash
curl -X POST http://localhost:8080/api/objectives/search \
  -H "Content-Type: text/plain" \
  -d "weekend1.pdf"
```

### List available objectives:
```bash
curl http://localhost:8080/api/objectives/list
```

## Architecture

- **Controllers**: Handle HTTP requests and responses
  - `InDocumentSearchController`: Manages general document search operations
  - `ObjectivesController`: Manages objectives-specific operations
- **Services**: Implement business logic
  - `InDocumentSearchService`: Handles document search and AI answer generation
  - `ObjectivesService`: Manages objectives analysis and processing
- **Repositories**: Data access layer for different document types
- **Models**: Data structures and entities
- **Configuration**: Application setup and document loading

The application follows a clean architecture pattern with clear separation of concerns between controllers, services, and repositories.
