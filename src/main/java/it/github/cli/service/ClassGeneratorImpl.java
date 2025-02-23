package it.github.cli.service;

import it.github.cli.config.ProjectConfig;
import it.github.cli.interfaces.ClassGenerator;
import it.github.cli.utils.ZipUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClassGeneratorImpl implements ClassGenerator {
    private static final String TEST_DIR_PATH = "src/test/java";
    private static final String MAIN_DIR_PATH = "src/main/java";
    private static final String CONFIG_DIR = "config";
    private static final String[] SOURCE_PACKAGES = {"entity", "repository", "service", "controller"};

    @Override
    public void generateClasses(String projectZipPath, ProjectConfig config) throws IOException {
        String projectDir = extractProject(projectZipPath, config);
        String packagePath = determinePackagePath(config);

        File configDir = createDirectory(projectDir, TEST_DIR_PATH, packagePath, CONFIG_DIR);
        generateAllTestClasses(configDir, config);
        generateSourcePackagesWithClasses(projectDir, packagePath, config);
    }

    private void generateSourcePackagesWithClasses(String projectDir, String packagePath, ProjectConfig config) throws IOException {
        String basePackage = buildPackageName(config);

        // Controller
        File controllerDir = createDirectory(projectDir, MAIN_DIR_PATH, packagePath, "controller");
        generateControllerClass(controllerDir, basePackage);

        // Service
        File serviceDir = createDirectory(projectDir, MAIN_DIR_PATH, packagePath, "service");
        generateServiceClass(serviceDir, basePackage);

        // Repository
        File repositoryDir = createDirectory(projectDir, MAIN_DIR_PATH, packagePath, "repository");
        generateRepositoryClass(repositoryDir, basePackage);

        // Entity
        File entityDir = createDirectory(projectDir, MAIN_DIR_PATH, packagePath, "entity");
        generateEntityClass(entityDir, basePackage);
    }

    private void generateControllerClass(File dir, String basePackage) throws IOException {
        String content = String.format("""
                package %s.controller;
                            
                import org.springframework.web.bind.annotation.RestController;
                import org.springframework.web.bind.annotation.RequestMapping;
                            
                @RestController
                @RequestMapping("/api")
                public class Controller {
                    
                    public Controller() {
                        System.out.println("Controller initialized");
                    }
                }
                """, basePackage);
        writeClassFile(dir, "Controller.java", content);
    }

    private void generateServiceClass(File dir, String basePackage) throws IOException {
        String content = String.format("""
                package %s.service;
                            
                import org.springframework.stereotype.Service;
                            
                @Service
                public class Service {
                    
                    public Service() {
                        System.out.println("Service initialized");
                    }
                }
                """, basePackage);
        writeClassFile(dir, "Service.java", content);
    }

    private void generateRepositoryClass(File dir, String basePackage) throws IOException {
        String content = String.format("""
                package %s.repository;
                            
                import org.springframework.stereotype.Repository;
                            
                @Repository
                public class Repository {
                    
                    public Repository() {
                        System.out.println("Repository initialized");
                    }
                }
                """, basePackage);
        writeClassFile(dir, "Repository.java", content);
    }

    private void generateEntityClass(File dir, String basePackage) throws IOException {
        String content = String.format("""
                package %s.entity;
                            
                import jakarta.persistence.Entity;
                import jakarta.persistence.Id;
                import jakarta.persistence.GeneratedValue;
                import jakarta.persistence.GenerationType;
                            
                @Entity
                public class Entity {
                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    private Long id;
                    
                    public Entity() {
                        System.out.println("Entity initialized");
                    }
                }
                """, basePackage);
        writeClassFile(dir, "Entity.java", content);
    }

    private void createSourcePackages(String projectDir, String packagePath) {
        for (String packageName : SOURCE_PACKAGES) {
            File packageDir = createDirectory(projectDir, MAIN_DIR_PATH, packagePath, packageName);
            System.out.println("Created source package: " + packageDir.getAbsolutePath());
        }
    }


    private File createDirectory(String baseDir, String sourcePath, String packagePath, String targetDir) {
        File dir = new File(baseDir, sourcePath + File.separator + packagePath + File.separator + targetDir);
        if (!dir.exists() && dir.mkdirs()) {
            System.out.println("Created directory: " + dir.getAbsolutePath());
        }
        return dir;
    }

    private void generateAllTestClasses(File configDir, ProjectConfig config) throws IOException {
        String packageName = buildPackageName(config);

        generateBaseTest(configDir, packageName);
        generateBaseUnitTest(configDir, packageName);
        generateBaseE2ETest(configDir, packageName);
        generateBaseIntegrationTest(configDir, packageName);
    }

    // [Previous methods remain unchanged]
    private void generateBaseTest(File configDir, String packageName) throws IOException {
        String content = String.format("""
                package %s.config;
                            
                import org.springframework.boot.test.context.SpringBootTest;
                import org.springframework.test.context.TestPropertySource;
                            
                @SpringBootTest(properties = "spring.main.banner-mode=off")
                @TestPropertySource({"classpath:application-test.properties"})
                public abstract class BaseTest {
                }
                """, packageName);

        writeClassFile(configDir, "BaseTest.java", content);
    }

    private void generateBaseUnitTest(File configDir, String packageName) throws IOException {
        String content = String.format("""
                package %s.config;
                            
                import org.junit.jupiter.api.extension.ExtendWith;
                import org.mockito.junit.jupiter.MockitoExtension;
                            
                @ExtendWith(MockitoExtension.class)
                public abstract class BaseUnitTest extends BaseTest {
                }
                """, packageName);

        writeClassFile(configDir, "BaseUnitTest.java", content);
    }

    private void generateBaseE2ETest(File configDir, String packageName) throws IOException {
        String content = String.format("""
                package %s.config;
                            
                import org.springframework.boot.test.context.SpringBootTest;
                import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
                            
                @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
                @AutoConfigureMockMvc
                public abstract class BaseE2ETest extends BaseTest {
                }
                """, packageName);

        writeClassFile(configDir, "BaseE2ETest.java", content);
    }

    private void generateBaseIntegrationTest(File configDir, String packageName) throws IOException {
        String content = String.format("""
                package %s.config;
                            
                import org.springframework.boot.test.context.SpringBootTest;
                            
                @SpringBootTest
                public abstract class BaseIntegrationTest extends BaseTest {
                }
                """, packageName);

        writeClassFile(configDir, "BaseIntegrationTest.java", content);
    }

    private void writeClassFile(File directory, String fileName, String content) throws IOException {
        File classFile = new File(directory, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(classFile))) {
            writer.write(content);
        }
        System.out.println("Generated: " + classFile.getAbsolutePath());
    }

    private String extractProject(String projectZipPath, ProjectConfig config) throws IOException {
        String projectDir = new File(projectZipPath).getParent() + File.separator + config.getProjectName();
        ZipUtils.extractZip(projectZipPath, projectDir);
        return projectDir;
    }

    private String determinePackagePath(ProjectConfig config) {
        String lastToken = config.getGroupId().substring(config.getGroupId().lastIndexOf('.') + 1);
        String packageName = config.getArtifactId().equalsIgnoreCase(lastToken) ?
                config.getGroupId() :
                config.getGroupId() + "." + config.getArtifactId();
        return packageName.replace(".", File.separator);
    }

    private String buildPackageName(ProjectConfig config) {
        String lastToken = config.getGroupId().substring(config.getGroupId().lastIndexOf('.') + 1);
        return config.getArtifactId().equalsIgnoreCase(lastToken) ?
                config.getGroupId() :
                config.getGroupId() + "." + config.getArtifactId();
    }
}