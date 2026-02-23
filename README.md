# API Automation Framework

A professional-grade test automation framework for REST API and GraphQL testing built with Java, REST-Assured, TestNG, and Maven.

## Features

- **REST API Testing** - Comprehensive CRUD test cases using JSONPlaceholder API
- **GraphQL Testing** - Query and mutation tests against SpaceX GraphQL API
- **Fluent Assertions** - Custom assertion utilities and response validators
- **Allure Reports** - Rich HTML test reports with detailed execution information
- **Log4j2 Logging** - Configurable logging with daily file rotation
- **Data-Driven Tests** - Parameterized tests with JSON test data
- **CI/CD Integration** - GitHub Actions workflow for automated test execution
- **Multi-Environment** - Maven profiles for dev, staging, and production environments

## Technology Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Programming language |
| Maven | 3.9+ | Build and dependency management |
| REST-Assured | 5.3.2 | REST API testing |
| TestNG | 7.8.0 | Test framework |
| Log4j2 | 2.20.0 | Logging framework |
| Allure | 2.21.0 | Test reporting |
| Jackson | 2.15.2 | JSON processing |
| Lombok | 1.18.28 | Boilerplate reduction |

## Project Structure

```
src/
├── main/java/com/automation/
│   ├── client/
│   │   ├── RestApiClient.java       # REST API client with fluent builder
│   │   └── GraphQLClient.java       # GraphQL client for queries/mutations
│   ├── config/
│   │   ├── ConfigurationManager.java # Singleton config loader
│   │   └── Environment.java          # Environment enum
│   ├── models/
│   │   ├── request/
│   │   │   └── PostRequest.java
│   │   └── response/
│   │       ├── PostResponse.java
│   │       └── UserResponse.java
│   ├── utils/
│   │   ├── AssertionUtils.java       # Custom API assertions
│   │   ├── ResponseValidator.java    # Fluent response validator
│   │   └── TestDataBuilder.java      # Test data factory
│   ├── constants/
│   │   ├── ApiConstants.java         # REST API constants
│   │   └── GraphQLConstants.java     # GraphQL queries and constants
│   └── listeners/
│       └── TestListener.java         # TestNG lifecycle listener
└── test/
    ├── java/com/automation/
    │   ├── api/tests/
    │   │   ├── PostTests.java        # CRUD tests for Posts
    │   │   └── UserTests.java        # Tests for Users
    │   └── graphql/tests/
    │       └── SpaceXTests.java      # SpaceX GraphQL tests
    └── resources/
        ├── config/
        │   ├── application.properties
        │   └── log4j2.xml
        ├── testdata/
        │   └── test-data.json
        ├── allure/
        │   └── allure.properties
        └── testng.xml
```

## Prerequisites

- Java 17 or higher
- Maven 3.9+
- Internet access (for external API calls)

## Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/maagd90/api-automation-framework.git
   cd api-automation-framework
   ```

2. **Install dependencies**
   ```bash
   mvn install -DskipTests
   ```

## Running Tests

### Run all tests
```bash
mvn test
```

### Run with a specific environment
```bash
mvn test -Pdev
mvn test -Pstaging
mvn test -Pprod
```

### Run only REST API tests
```bash
mvn test -Dgroups="REST API Testing"
```

### Run only GraphQL tests
```bash
mvn test -Dgroups="GraphQL Testing"
```

### Generate Allure report
```bash
mvn allure:serve
```

## Test Cases

### REST API Tests (JSONPlaceholder)

| Test | Description |
|------|-------------|
| `testCreatePost` | Creates a post and verifies 201 status |
| `testGetPostById` | Reads post by ID and validates structure |
| `testUpdatePost` | Updates post with PUT and verifies changes |
| `testPatchPost` | Partially updates post with PATCH |
| `testDeletePost` | Deletes a post and verifies success |
| `testGetAllPosts` | Retrieves all posts, verifies count = 100 |
| `testGetPostsByUserId` | Filters posts by userId |
| `testGetNonExistentPost` | Verifies 404 for missing resource |
| `testCreatePostDataDriven` | Data-driven create with 3 datasets |
| `testResponseTime` | Verifies response time < 10 seconds |
| `testGetAllUsers` | Gets all 10 users |
| `testGetUserById` | Gets user by ID, validates fields |
| `testUserResponseStructure` | Validates full user JSON structure |
| `testGetUserPosts` | Gets posts for a specific user |
| `testUsersHaveValidEmails` | Validates email format for all users |

### GraphQL Tests (SpaceX API)

| Test | Description |
|------|-------------|
| `testQueryRockets` | Queries all rockets, validates structure |
| `testQueryLaunches` | Queries past 5 launches |
| `testQueryRocketWithVariables` | Queries rocket by ID using variables |
| `testQueryCompanyInfo` | Queries SpaceX company information |
| `testInvalidQueryReturnsErrors` | Verifies error handling for invalid query |
| `testRocketResponseStructure` | Validates rocket response fields |

## Writing New Tests

### REST API Test Example

```java
@Test
@Story("My Feature")
@Description("Describe what this test validates")
public void testMyFeature() {
    RestApiClient client = new RestApiClient();
    Response response = client.get("/my-endpoint");

    ResponseValidator.of(response)
        .statusCode(200)
        .fieldNotNull("id")
        .fieldEquals("status", "active")
        .responseTimeBelow(5000);
}
```

### GraphQL Test Example

```java
@Test
public void testMyGraphQLQuery() {
    GraphQLClient client = new GraphQLClient();
    String query = "{ myQuery { id name } }";

    Response response = client.executeQuery(query);

    ResponseValidator.of(response)
        .statusCode(200)
        .noGraphQLErrors()
        .graphQLDataNotNull("myQuery");
}
```

### Data-Driven Test Example

```java
@Test(dataProvider = "myDataProvider")
public void testWithData(String title, String body, int userId) {
    PostRequest request = TestDataBuilder.buildPostRequest(title, body, userId);
    Response response = client.post("/posts", request);
    AssertionUtils.assertStatusCode(response, 201);
}

@DataProvider(name = "myDataProvider")
public Object[][] myDataProvider() {
    return new Object[][] {
        {"Title 1", "Body 1", 1},
        {"Title 2", "Body 2", 2}
    };
}
```

## Configuration

Edit `src/test/resources/config/application.properties`:

```properties
api.base.url=https://jsonplaceholder.typicode.com
graphql.base.url=https://spacex-production.up.railway.app/
api.connection.timeout=5000
api.read.timeout=10000
api.max.retries=3
```

## CI/CD

The GitHub Actions workflow (`.github/workflows/test-automation.yml`) runs on:
- Push to `main` or `develop` branches
- Pull requests to `main`
- Manual trigger with environment selection

Artifacts produced:
- Surefire test reports
- Allure results and HTML report
- Application logs
