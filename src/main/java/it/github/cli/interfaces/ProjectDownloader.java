package it.github.cli.interfaces;

import it.github.cli.config.ProjectConfig;

import java.io.IOException;

public interface ProjectDownloader {
    String downloadProject(ProjectConfig config) throws IOException;

}
