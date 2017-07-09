package main;

import java.util.ArrayList;

/**
 * Created by Jed on 09-Jul-17.
 */
public class RTIProject {

    private String name;
    private ProjectType projectType;
    private ArrayList<ProjectProperty> projectProperties;


    public RTIProject(String name, ProjectType projectType) {
        this.name = name;
        this.projectType = projectType;

        projectProperties = new ArrayList<>();
        projectProperties.add(new ProjectProperty("Name", name));
        projectProperties.add(new ProjectProperty("Type", projectType.toString()));
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public ArrayList<ProjectProperty> getProjectProperties() {
        return projectProperties;
    }

    public void setProjectProperties(ArrayList<ProjectProperty> projectProperties) {
        this.projectProperties = projectProperties;
    }
}
