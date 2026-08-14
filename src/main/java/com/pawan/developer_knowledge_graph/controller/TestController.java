//package com.pawan.developer_knowledge_graph.controller;
//
//import org.neo4j.driver.Driver;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class TestController {
//
//    private final Driver driver;
//
//    public TestController(Driver driver) {
//        this.driver = driver;
//    }
//
//    @GetMapping("/test-db")
//    public String testDatabase() {
//
//        try (var session = driver.session()) {
//
//            var result = session.run("RETURN 1 AS result");
//
//            var record = result.single();
//
//            return "CognoDB Connected! Result = "
//                    + record.get("result").asInt();
//        }
//    }
//}
  //Connection created now---.....
package com.pawan.developer_knowledge_graph.controller;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final Driver driver;

    public TestController(Driver driver) {
        this.driver = driver;
    }

    @GetMapping("/create-graph")
    public String createGraph() {

        try (Session session = driver.session()) {

            session.run("""
                CREATE (d:Developer {name: 'Pawan'})
                CREATE (j:Technology {name: 'Java'})
                CREATE (s:Technology {name: 'Spring Boot'})
                CREATE (r:Technology {name: 'REST API'})
                CREATE (p:Project {name: 'AI Email Assistant'})

                CREATE (d)-[:KNOWS]->(j)
                CREATE (d)-[:KNOWS]->(s)
                CREATE (d)-[:KNOWS]->(r)
                CREATE (d)-[:BUILT]->(p)
                CREATE (p)-[:USES]->(j)
                CREATE (p)-[:USES]->(s)
                CREATE (p)-[:USES]->(r)
                """);

            return "Developer Knowledge Graph created successfully!";
        }
    }
}