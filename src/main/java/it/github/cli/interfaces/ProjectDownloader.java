package it.github.cli.interfaces;

import java.io.IOException;

public interface ProjectDownloader {
    void downloadProject(String groupId, String artifactId, String projectName, String outputDir) throws IOException;

}
