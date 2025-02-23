package it.github.cli.interfaces;

import it.github.cli.config.ProjectConfig;

import java.io.IOException;

public interface ClassGenerator {

    void generateClasses(String projectPath, ProjectConfig config) throws IOException;


}
