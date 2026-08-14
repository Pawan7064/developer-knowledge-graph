package com.pawan.developer_knowledge_graph.service;

import com.pawan.developer_knowledge_graph.repository.DeveloperRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeveloperService {

    private final DeveloperRepository developerRepository;

    public DeveloperService(DeveloperRepository developerRepository) {
        this.developerRepository = developerRepository;
    }

    public List<String> getAllDevelopers() {
        return developerRepository.getAllDevelopers();
    }
}