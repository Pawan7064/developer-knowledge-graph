package com.pawan.developer_knowledge_graph;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final Driver driver;

    public DataLoader(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void run(String... args) {

        try (Session session = driver.session()) {

            session.run("""
                MERGE (d:Developer {name: 'Pawan'})
                SET d.experience = 'Fresher'

                MERGE (java:Technology {name: 'Java'})
                SET java.category = 'Backend'

                MERGE (spring:Technology {name: 'Spring Boot'})
                SET spring.category = 'Backend'

                MERGE (rest:Technology {name: 'REST API'})
                SET rest.category = 'Backend'

                MERGE (hibernate:Technology {name: 'Hibernate'})
                SET hibernate.category = 'Database'

                MERGE (sql:Technology {name: 'SQL'})
                SET sql.category = 'Database'

                MERGE (d)-[:KNOWS]->(java)
                MERGE (d)-[:KNOWS]->(spring)
                MERGE (d)-[:KNOWS]->(rest)
                MERGE (d)-[:KNOWS]->(hibernate)
                MERGE (d)-[:KNOWS]->(sql)

                MERGE (java)-[:RELATED_TO]->(spring)
                MERGE (spring)-[:RELATED_TO]->(rest)
                MERGE (spring)-[:RELATED_TO]->(hibernate)
                MERGE (hibernate)-[:RELATED_TO]->(sql)
                """);

            System.out.println("Developer knowledge graph data loaded successfully!");
        }
    }
}