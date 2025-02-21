package it.github.cli.interfaces;

import java.io.IOException;

public interface ClassGenererator {

    void generateTestClass(String projectZipPath, String projectDir, String groupId, String artifactId) throws IOException;


}
