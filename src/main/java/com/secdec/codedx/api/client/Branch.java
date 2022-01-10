package com.secdec.codedx.api.client;

public class Branch {
    private String name;
    private int projectId;

    public int getId() {
        return projectId;
    }
    public void setId(int projectId) {
        this.projectId = projectId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
