# 📚 EduSearch AI

A Spring Boot application that leverages AI capabilities for educational search and content processing. This project uses Spring AI and OpenAI integration to provide intelligent search and processing of educational content.

## Features

- PDF document processing using Apache PDFBox
- AI-powered search capabilities using Spring AI
- OpenAI integration for advanced text processing
- Vector store support for efficient content retrieval
- Smart sidenotes integration that combines course content with student notes
- Intelligent document chunking for better search results

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
