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
        // Estrae il progetto dalla cartella zip nella directory di destinazione
        ZipUtils.extractZip(projectZipPath, projectDir);
        System.out.println("Project unzipped to: " + projectDir);

        // Verifica se artifactId è uguale all'ultimo token del groupId per evitare duplicazioni
        String packageName;
        String lastToken = groupId.substring(groupId.lastIndexOf('.') + 1);
        if (artifactId.equalsIgnoreCase(lastToken)) {
            packageName = groupId;
        } else {
            packageName = groupId + "." + artifactId;
        }

        // Converte il package in un percorso di directory
        String packagePath = packageName.replace(".", File.separator);

        // Percorso della directory dei test: src/test/java/{packagePath}/config
        File testDir = new File(projectDir, "src/test/java/" + packagePath + File.separator + "config");
        if (!testDir.exists()) {
            boolean created = testDir.mkdirs();
            if (created) {
                System.out.println("Directory created: " + testDir.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + testDir.getAbsolutePath());
            }
        } else {
            System.out.println("Directory already exists: " + testDir.getAbsolutePath());
        }

        // Crea il file della classe di test
        File testClassFile = new File(testDir, "TestGeneratedClass.java");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testClassFile))) {
            writer.write("package " + packageName + ".config;\n\n");
            writer.write("public class TestGeneratedClass {\n");
            writer.write("    public TestGeneratedClass() {\n");
            writer.write("        System.out.println(\"TestGeneratedClass generated successfully\");\n");
            writer.write("    }\n");
            writer.write("}\n");
        }

        System.out.println("Test class created at: " + testClassFile.getAbsolutePath());
    }
}