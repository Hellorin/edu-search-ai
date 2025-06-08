# EduSearch AI

A Spring Boot application that leverages AI capabilities for educational search and content processing. This project uses Spring AI and OpenAI integration to provide intelligent search and processing of educational content.

## Features

- PDF document processing using Apache PDFBox
- AI-powered search capabilities using Spring AI
- OpenAI integration for advanced text processing
- Vector store support for efficient content retrieval

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- OpenAI API key

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/eduSearchAi.git
cd eduSearchAi
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
│   │       ├── privatedocuments/    # Directory for storing private/restricted PDF documents (not in git)
│   │       └── publicdocuments/     # Directory for storing publicly accessible PDF documents
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
