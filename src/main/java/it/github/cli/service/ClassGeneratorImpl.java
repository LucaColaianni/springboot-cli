package it.github.cli.service;

import it.github.cli.interfaces.ClassGenererator;
import it.github.cli.utils.ZipUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClassGeneratorImpl implements ClassGenererator {
    @Override
    public void generateTestClass(String projectZipPath, String projectDir, String groupId, String artifactId) throws IOException {

        ZipUtils.extractZip(projectZipPath, projectDir);
        System.out.println("Project unzipped to: " + projectDir);

        // Costruisci il percorso del package (es. com.example.demo)
        String packagePath = (groupId + "." + artifactId).replace(".", File.separator);
        // Percorso della cartella dei test: src/test/java/{packagePath}/config
        File testDir = new File(projectDir, "src/test/java/" + packagePath + File.separator + "config");
        if (!testDir.exists()) {
            testDir.mkdirs();
        }

        File testClassFile = new File(testDir, "TestGeneratedClass.java");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testClassFile))) {
            writer.write("package " + groupId + "." + artifactId + ".config;\n\n");
            writer.write("public class TestGeneratedClass {\n");
            writer.write("    public TestGeneratedClass() {\n");
            writer.write("        System.out.println(\"TestGeneratedClass generated successfully\");\n");
            writer.write("    }\n");
            writer.write("}\n");
        }

        System.out.println("Test class created at: " + testClassFile.getAbsolutePath());

    }

}
