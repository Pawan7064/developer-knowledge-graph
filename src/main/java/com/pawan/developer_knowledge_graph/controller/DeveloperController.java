package com.pawan.developer_knowledge_graph.controller;

import com.pawan.developer_knowledge_graph.dto.TechnologyNetworkResponse;
import com.pawan.developer_knowledge_graph.service.DeveloperService;
import com.pawan.developer_knowledge_graph.service.TechnologyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/developers")
public class DeveloperController {

    private final DeveloperService developerService;
    private final TechnologyService technologyService;

    public DeveloperController(
            DeveloperService developerService,
            TechnologyService technologyService) {

        this.developerService = developerService;
        this.technologyService = technologyService;
    }

    // Get all developers
    @GetMapping
    public List<String> getAllDevelopers() {
        return developerService.getAllDevelopers();
    }

    // Get technologies directly known by developer
    @GetMapping("/{name}/technologies")
    public List<String> getTechnologies(
            @PathVariable String name) {

        return technologyService.getTechnologies(name);
    }

    // Get related technologies
    @GetMapping("/{name}/related-technologies")
    public List<String> getRelatedTechnologies(
            @PathVariable String name) {

        return technologyService.getRelatedTechnologies(name);
    }

    // Get complete technology network
    @GetMapping("/{name}/technology-network")
    public List<String> getTechnologyNetwork(
            @PathVariable String name) {

        return technologyService.getTechnologyNetwork(name);
    }
    @GetMapping("/{name}/technology-details")
    public List<Map<String, Object>> getTechnologyDetails(
            @PathVariable String name) {

        return technologyService.getTechnologyDetails(name);
    }
    @GetMapping("/{name}/technology-network-details")
    public List<TechnologyNetworkResponse> getTechnologyNetworkDetails(
            @PathVariable String name) {

        return technologyService.getTechnologyNetworkDetails(name);
    }
}