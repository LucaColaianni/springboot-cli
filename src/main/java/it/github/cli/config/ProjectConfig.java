package it.github.cli.config;

public class ProjectConfig {

    private final String groupId;
    private final String artifactId;
    private final String projectName;

    public ProjectConfig(String groupId, String artifactId, String projectName) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.projectName = projectName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getProjectName() {
        return projectName;
    }
}
