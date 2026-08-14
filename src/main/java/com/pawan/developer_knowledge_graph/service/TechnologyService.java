package com.pawan.developer_knowledge_graph.service;

import com.pawan.developer_knowledge_graph.dto.TechnologyNetworkResponse;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TechnologyService {

    private final Driver driver;

    public TechnologyService(Driver driver) {
        this.driver = driver;
    }

    public List<String> getTechnologies(String developerName) {

        String query = """
                MATCH (d:Developer {name: $name})-[:KNOWS]->(t:Technology)
                RETURN t.name AS technology
                ORDER BY technology
                """;

        try (Session session = driver.session()) {

            return session.run(
                            query,
                            org.neo4j.driver.Values.parameters("name", developerName)
                    )
                    .list(record -> record.get("technology").asString());
        }
    }
    public List<String> getRelatedTechnologies(String developerName) {

        String query = """
            MATCH (d:Developer {name: $name})
                  -[:KNOWS]->(t:Technology)
                  -[:RELATED_TO*1..2]->(related:Technology)
            RETURN DISTINCT related.name AS technology
            ORDER BY technology
            """;

        try (Session session = driver.session()) {

            return session.run(
                            query,
                            org.neo4j.driver.Values.parameters("name", developerName)
                    )
                    .list(record -> record.get("technology").asString());
        }
    }
    public List<String> getTechnologyNetwork(String developerName) {

        String query = """
            MATCH (d:Developer {name: $name})
                  -[:KNOWS]->(start:Technology)
                  -[:RELATED_TO*1..3]->(related:Technology)
            RETURN DISTINCT related.name AS technology
            ORDER BY technology
            """;

        try (Session session = driver.session()) {

            return session.run(
                            query,
                            org.neo4j.driver.Values.parameters("name", developerName)
                    )
                    .list(record -> record.get("technology").asString());
        }
    }
    public List<Map<String, Object>> getTechnologyDetails(String developerName) {

        String query = """
        MATCH (d:Developer {name: $name})
              -[:KNOWS]->(t:Technology)
        OPTIONAL MATCH (t)-[:RELATED_TO]->(related:Technology)
        RETURN t.name AS technology,
               collect(DISTINCT related.name) AS relatedTechnologies
        ORDER BY technology
        """;

        try (Session session = driver.session()) {

            return session.run(
                    query,
                    org.neo4j.driver.Values.parameters("name", developerName)
            ).list(record -> {

                Map<String, Object> result = new java.util.HashMap<>();

                result.put(
                        "technology",
                        record.get("technology").asString()
                );

                result.put(
                        "relatedTechnologies",
                        record.get("relatedTechnologies").asList()
                );

                return result;
            });
        }
    }
    public List<TechnologyNetworkResponse> getTechnologyNetworkDetails(String developerName) {

        String query = """
            MATCH (d:Developer {name: $name})-[:KNOWS]->(technology:Technology)
            OPTIONAL MATCH (technology)-[:RELATED_TO]->(related:Technology)
            RETURN technology.name AS technology,
                   collect(related.name) AS relatedTechnologies
            ORDER BY technology
            """;

        try (Session session = driver.session()) {

            return session.run(
                            query,
                            org.neo4j.driver.Values.parameters("name", developerName)
                    )
                    .list(record -> new TechnologyNetworkResponse(
                            record.get("technology").asString(),
                            record.get("relatedTechnologies")
                                    .asList(value -> value.asString())
                    ));
        }
    }

}