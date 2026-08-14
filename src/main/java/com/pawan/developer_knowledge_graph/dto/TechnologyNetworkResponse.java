package com.pawan.developer_knowledge_graph.dto;

import java.util.List;

public class TechnologyNetworkResponse {

    private String technology;

    private List<String> relatedTechnologies;

    public TechnologyNetworkResponse(
            String technology,
            List<String> relatedTechnologies) {

        this.technology = technology;
        this.relatedTechnologies = relatedTechnologies;
    }

    public String getTechnology() {
        return technology;
    }

    public List<String> getRelatedTechnologies() {
        return relatedTechnologies;
    }
}