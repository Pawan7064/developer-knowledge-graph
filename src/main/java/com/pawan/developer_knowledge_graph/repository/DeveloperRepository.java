package com.pawan.developer_knowledge_graph.repository;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DeveloperRepository {

    private final Driver driver;

    public DeveloperRepository(Driver driver) {
        this.driver = driver;
    }

    public List<String> getAllDevelopers() {

        List<String> developers = new ArrayList<>();

        try (Session session = driver.session()) {

            String query = """
                    MATCH (d:Developer)
                    RETURN d.name AS name
                    ORDER BY d.name
                    """;

            List<Record> records = session.run(query).list();

            for (Record record : records) {
                developers.add(record.get("name").asString());
            }
        }

        return developers;
    }
}