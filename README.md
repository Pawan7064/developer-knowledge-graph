# Developer Knowledge Graph

A simple knowledge graph application built using **Spring Boot, Java, Neo4j/CognoDB, Cypher and HTML/CSS/JavaScript**.

The main idea of this project is to represent a developer's technology knowledge as a graph instead of storing everything as unrelated rows or records.

For example:

A developer can know Java and Spring Boot.

Java can be related to Spring Boot.

Spring Boot can be related to REST API and Hibernate.

Hibernate can be related to SQL.

So instead of looking at these technologies as separate pieces of data, the application stores and explores the relationships between them.

---

# 1. Project Overview

The application is called **Developer Knowledge Graph**.

The project allows us to:

- Store developers as nodes
- Store technologies as nodes
- Connect developers with the technologies they know
- Connect related technologies with relationships
- Query the graph using Cypher
- Expose the graph data through REST APIs
- Display the result through a simple web interface
- Explore the technology relationships of a developer

Currently, the sample data contains a developer named:

`Pawan`

with technologies such as:

- Java
- Spring Boot
- REST API
- Hibernate
- SQL

---

# 2. Why I Built This Project

I wanted to understand how a **graph database** works in a real backend application instead of only learning the theory.

Before starting this project, I had not worked with CognoDB/Neo4j in this way.

So I first understood:

- What a graph database is
- What nodes and relationships mean
- How Cypher queries work
- How CognoDB can be used with Neo4j-compatible queries
- How Spring Boot can communicate with the graph database
- How REST APIs can expose graph data
- How the frontend can consume those APIs

Then I built the application step by step.

The main learning for me was not just creating the UI. It was understanding how a graph database represents relationships and how those relationships can be queried from a backend application.

---

# 3. Why a Graph Database?

A normal relational database is very good when the data is mostly structured around tables and rows.

But in this project, the important part is the **relationship between entities**.

For example:


Pawan
  |
  | KNOWS
  ↓
Java
  |
  | RELATED_TO
  ↓
Spring Boot
  |
  | RELATED_TO
  ↓
Hibernate
  |
  | RELATED_TO
  ↓
SQL
The relationship itself is meaningful.

A graph database represents this naturally using:

Nodes
Relationships
Properties

This makes it convenient to ask questions such as:

Which technologies does Pawan know?

or:

Which technologies are related to the technologies Pawan knows?

or:

What does the technology network around Pawan look like?

Instead of manually joining multiple tables, we can traverse relationships in the graph using Cypher.

# 4. Data Model

The basic data model contains two types of nodes.

Developer

Example:

(:Developer {
    name: "Pawan",
    experience: "Fresher"
})
Technology

Example:

(:Technology {
    name: "Java",
    category: "Backend"
})
Relationships

Developer to technology:

(:Developer)-[:KNOWS]->(:Technology)

Technology to technology:

(:Technology)-[:RELATED_TO]->(:Technology)
 
# 5 Technologies Used
Backend
Java
Spring Boot
Spring Web
Maven
REST APIs
Database
CognoDB
Neo4j Java Driver
Cypher
Frontend
HTML
CSS
JavaScript
Development Tools
IntelliJ IDEA
Git
GitHub 
# 6.CognoDB Setup

CognoDB was used as the graph database for this project.

The application connects to the CognoDB database through the Neo4j Java Driver.

The important configuration values are:

URI
Username
Password

These values are kept in the application's configuration and should not be committed publicly.

For example:

cogno.db.uri=YOUR_DATABASE_URI
cogno.db.username=YOUR_USERNAME
cogno.db.password=YOUR_PASSWORD

The actual credentials should be replaced with the credentials of the CognoDB database being used.

# 7 How Spring Boot Connects to CognoDB

The application creates a Neo4j Driver.

The driver is then injected into the services.

For example:

private final Driver driver;

public TechnologyService(Driver driver) {
    this.driver = driver;
}

The service opens a database session:

try (Session session = driver.session()) {
    ...
}

Then it executes a Cypher query:

session.run(query, parameters);

The returned records are converted into Java objects or lists.

So the basic flow is:

Frontend
   ↓
REST API
   ↓
Spring Controller
   ↓
Service
   ↓
Neo4j Java Driver
   ↓
CognoDB
   ↓
Cypher Query
   ↓
Graph Result
   ↓
Service
   ↓
Controller
   ↓
JSON Response
   ↓
Frontend 

# 8.Data Loading

The project contains a DataLoader class implementing CommandLineRunner.

Its purpose is to insert the initial graph data when the Spring Boot application starts.

The important part is:

CREATE (d:Developer {
    name: 'Pawan',
    experience: 'Fresher'
})

Then technology nodes are created:

CREATE (java:Technology {
    name: 'Java',
    category: 'Backend'
})

CREATE (spring:Technology {
    name: 'Spring Boot',
    category: 'Backend'
})

CREATE (rest:Technology {
    name: 'REST API',
    category: 'Backend'
})

CREATE (hibernate:Technology {
    name: 'Hibernate',
    category: 'Database'
})

CREATE (sql:Technology {
    name: 'SQL',
    category: 'Database'
})

Then relationships are created:

CREATE (d)-[:KNOWS]->(java)
CREATE (d)-[:KNOWS]->(spring)
CREATE (d)-[:KNOWS]->(rest)
CREATE (d)-[:KNOWS]->(hibernate)
CREATE (d)-[:KNOWS]->(sql)

Technology relationships are also created:

CREATE (java)-[:RELATED_TO]->(spring)
CREATE (spring)-[:RELATED_TO]->(rest)
CREATE (spring)-[:RELATED_TO]->(hibernate)
CREATE (hibernate)-[:RELATED_TO]->(sql)

After successful loading, the application prints:

Developer knowledge graph data loaded successfully!
# 9.Main Cypher Queries
Get all developers
MATCH (d:Developer)
RETURN d.name;

Example result:

Pawan
Get technologies known by a developer
MATCH (d:Developer {name: $name})-[:KNOWS]->(t:Technology)
RETURN t.name AS technology
ORDER BY technology;

For Pawan:

[
  "Hibernate",
  "Java",
  "REST API",
  "SQL",
  "Spring Boot"
]
Get related technologies
MATCH (d:Developer {name: $name})
      -[:KNOWS]->(t:Technology)
      -[:RELATED_TO*1..2]->(related:Technology)
RETURN DISTINCT related.name AS technology
ORDER BY technology;

This follows technology relationships up to two levels.

Get technology network
MATCH (d:Developer {name: $name})
      -[:KNOWS]->(start:Technology)
      -[:RELATED_TO*1..3]->(related:Technology)
RETURN DISTINCT related.name AS technology
ORDER BY technology;

This allows the application to explore a larger part of the technology graph.

Get technology details
MATCH (d:Developer {name: $name})
      -[:KNOWS]->(t:Technology)
OPTIONAL MATCH (t)-[:RELATED_TO]->(related:Technology)
RETURN t.name AS technology,
       collect(DISTINCT related.name) AS relatedTechnologies
ORDER BY technology;

The response looks like:

[
  {
    "technology": "Hibernate",
    "relatedTechnologies": [
      "SQL"
    ]
  },
  {
    "technology": "Java",
    "relatedTechnologies": [
      "Spring Boot"
    ]
  },
  {
    "technology": "REST API",
    "relatedTechnologies": []
  },
  {
    "technology": "SQL",
    "relatedTechnologies": []
  },
  {
    "technology": "Spring Boot",
    "relatedTechnologies": [
      "REST API",
      "Hibernate"
    ]
  }
]

This response is used by the frontend to display the technology details.

# 10.REST API Endpoints

The main controller is:

DeveloperController

Base URL:

/api/developers
Get all developers
GET /api/developers

Example:

[
  "Pawan"
]
Get developer technologies
GET /api/developers/Pawan/technologies

Example:

[
  "Hibernate",
  "Java",
  "REST API",
  "SQL",
  "Spring Boot"
]
Get related technologies
GET /api/developers/Pawan/related-technologies

This returns technologies connected through RELATED_TO relationships.

Get technology network
GET /api/developers/Pawan/technology-network

This explores the technology graph up to multiple relationship levels.

Get technology details
GET /api/developers/Pawan/technology-details

Example:

[
  {
    "technology": "Hibernate",
    "relatedTechnologies": ["SQL"]
  },
  {
    "technology": "Java",
    "relatedTechnologies": ["Spring Boot"]
  },
  {
    "technology": "REST API",
    "relatedTechnologies": []
  },
  {
    "technology": "SQL",
    "relatedTechnologies": []
  },
  {
    "technology": "Spring Boot",
    "relatedTechnologies": ["REST API", "Hibernate"]
  }
]
# 11 Backend Workflow

When a user searches for a developer, the following workflow happens:

User enters developer name
          ↓
       Explore
          ↓
Frontend sends HTTP request
          ↓
DeveloperController
          ↓
TechnologyService
          ↓
Cypher Query
          ↓
Neo4j Java Driver
          ↓
CognoDB
          ↓
Graph traversal
          ↓
JSON response
          ↓
Frontend receives response
          ↓
UI displays technologies
and relationships
# 12.Frontend

The frontend is a simple static web interface created using:

HTML
CSS
JavaScript

The frontend communicates with the Spring Boot backend using the REST APIs.

For example:

fetch("/api/developers/" + developerName + "/technology-details")

The JSON response is then used to create the technology cards and relationship visualization.

# 13 Knowledge Graph Visualization

The frontend represents the developer and technologies visually.

 
The developer is treated as the central node.

Technologies are connected to the developer through:

KNOWS

Technologies are connected with each other through:

RELATED_TO

This makes the relationships easier to understand visually.

# 14 Technology Details Panel

The right side of the UI shows technology details.

For every technology, the application can display:

Technology:
Spring Boot

Related Technologies:
REST API
Hibernate

This information comes directly from the graph database through the REST API.

The frontend does not hard-code these relationships.

The backend queries CognoDB and returns the actual graph data.

# 15 .Important Point About Developer Data

Currently the database contains sample data for:

Pawan

Therefore, if a user searches for:

Ramesh

the application cannot magically know Ramesh's technology stack.

The graph database must first contain Ramesh's data.

For example:

CREATE (r:Developer {
    name: 'Ramesh',
    experience: 'Fresher'
})

CREATE (r)-[:KNOWS]->(java)
CREATE (r)-[:KNOWS]->(sql)

After inserting the data, the same APIs can be used for Ramesh.

This is an important property of the application:

The application does not guess a developer's skills. It reads the developer's knowledge graph from the database.

 

 
# 16. Setup and Run Instructions
Step 1: Clone the repository
git clone <YOUR_GITHUB_REPOSITORY_URL>

Go inside the project:

cd developer-knowledge-graph
Step 2: Configure CognoDB

Create/configure the CognoDB database.

Add the database connection information to the application configuration.

Do not commit passwords or private credentials to GitHub.

Step 3: Build the project

Using Maven:

mvn clean install
Step 4: Run the application
mvn spring-boot:run

Or run:

DeveloperKnowledgeGraphApplication

from IntelliJ IDEA.

Step 5: Open the application

Open:

http://localhost:8080/

The frontend should load.

# 17. Testing the APIs

The APIs can be tested using a browser, Postman or any REST client.

For example:

http://localhost:8080/api/developers

Then:

http://localhost:8080/api/developers/Pawan/technologies

Then:

http://localhost:8080/api/developers/Pawan/technology-details

And:

http://localhost:8080/api/developers/Pawan/technology-network
# 18 Resetting Developer Data

If the data needs to be reset from CognoDB, Cypher can be executed from the CognoDB query editor.

For example:

MATCH (d:Developer)
DETACH DELETE d;

This deletes developer nodes and their connected relationships.

The application can then be restarted so the DataLoader can insert the sample developer data again.

# 19 What I Learned From This Project

This project helped me understand several things beyond basic CRUD development.

I learned:

How graph databases represent data
Difference between nodes and relationships
How Cypher works
How graph traversal works
How to connect Spring Boot with a Neo4j-compatible graph database
How the Neo4j Java Driver works
How to execute parameterized Cypher queries
How to expose graph data through REST APIs
How JSON responses are generated from database records
How a frontend consumes backend APIs
How graph relationships can be represented visually
Why graph databases can be useful when relationships are important

One of the most useful parts for me was understanding that the database is not simply storing:

Pawan -> Java
Pawan -> Spring Boot
Pawan -> SQL

It is also storing relationships between technologies:

Java -> Spring Boot
Spring Boot -> REST API
Spring Boot -> Hibernate
Hibernate -> SQL

That relationship information is what makes the data a knowledge graph.

# 20  Full-Stack Workflow

The complete application works like this:

                 FRONTEND
        HTML + CSS + JavaScript
                     |
                     | HTTP Request
                     ↓
             SPRING BOOT
                     |
                     ↓
          DeveloperController
                     |
                     ↓
           TechnologyService
                     |
                     ↓
          Neo4j Java Driver
                     |
                     ↓
                CognoDB
                     |
                     ↓
               Cypher Query
                     |
                     ↓
             Graph Traversal
                     |
                     ↓
             Query Result
                     |
                     ↓
             Java Response
                     |
                     ↓
                JSON
                     |
                     ↓
                FRONTEND
                     |
                     ↓
          Graph Visualization
# 21. Example End-to-End Request

Suppose the user enters:

Pawan

and clicks:

Explore

The frontend sends:

GET /api/developers/Pawan/technology-details

The controller receives the request:

@GetMapping("/{name}/technology-details")
public List<Map<String, Object>> getTechnologyDetails(
        @PathVariable String name) {

    return technologyService.getTechnologyDetails(name);
}

The service executes a Cypher query.

CognoDB searches the graph.

The database returns something similar to:

[
  {
    "technology": "Java",
    "relatedTechnologies": ["Spring Boot"]
  },
  {
    "technology": "Spring Boot",
    "relatedTechnologies": [
      "REST API",
      "Hibernate"
    ]
  },
  {
    "technology": "Hibernate",
    "relatedTechnologies": ["SQL"]
  }
]

Spring Boot converts this into a JSON response.

JavaScript receives the response.

The frontend creates the technology cards and graph visualization.

So the user sees the developer's technology knowledge and the relationships between technologies.

# 22. Data Loading + Querying + Visualization

The project can therefore be divided into three main parts:

1. Data Loading

Create:

Developer nodes
Technology nodes
KNOWS relationships
RELATED_TO relationships
2. Data Querying

Use Cypher to answer questions such as:

What technologies does this developer know?
What technologies are related?
What does the technology network look like?
What technologies are directly connected?
3. Visualization

Use the REST API response to display:

Developer
   ↓
Technologies
   ↓
Related Technologies

in the frontend.

 
# 23. Conclusion

This project started as an attempt to understand graph databases in a practical way.

Instead of only creating a simple CRUD application, I wanted to understand how data and relationships can be represented as a graph.

The final application demonstrates:

Java
   ↓
Spring Boot
   ↓
REST API
   ↓
Hibernate
   ↓
SQL

and connects that technology graph with a developer:

Pawan
   |
  KNOWS
   |
Technologies
   |
RELATED_TO
   |
Technology Network

The project gave me practical experience with:

Java
Spring Boot
REST APIs
Neo4j Java Driver
CognoDB
Cypher
Graph Databases
HTML
CSS
JavaScript
JSON
Git/GitHub

Most importantly, I learned how a backend application can use a graph database to work with relationships instead of treating every piece of data as an isolated record.
# This Project gave me the knowledge of CognoDB and What is Neo4j Driver . Honestly If I say so  even I listened This name(CognoDB) first Time but now I can say I have hands on experience with CognoDB.

 