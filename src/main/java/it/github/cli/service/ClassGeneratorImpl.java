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


    @Override
    public void generateClasses(String projectZipPath, ProjectConfig config) throws IOException {
        // Extract the project from the zip file
        String projectDir = extractProject(projectZipPath, config);

        // Define "src/main/java" as the root directory for sources
        File srcMainJavaDir = new File(projectDir, MAIN_DIR_PATH);
        // Try to recursively find the directory containing the Application class
        File baseSourceDir = findBasePackageDirectory(srcMainJavaDir);
        if (baseSourceDir == null) {
            // If we don't find Application, use the computed package as a fallback
            String basePackageName = buildPackageName(config);
            String basePackagePath = basePackageName.replace(".", File.separator);
            baseSourceDir = new File(srcMainJavaDir, basePackagePath);
            if (!baseSourceDir.exists()) {
                baseSourceDir.mkdirs();
            }
        }

        // Compute the package from the relative path to src/main/java
        String basePackage = getPackageFromPath(srcMainJavaDir, baseSourceDir);

        // For tests, reconstruct the base directory using the same structure
        File srcTestJavaDir = new File(projectDir, TEST_DIR_PATH);
        File baseTestDir = new File(srcTestJavaDir, basePackage.replace(".", File.separator));
        if (!baseTestDir.exists()) {
            baseTestDir.mkdirs();
        }
        // Create (if it does not exist) the "config" directory for tests
        File testConfigDir = new File(baseTestDir, CONFIG_DIR);
        if (!testConfigDir.exists()) {
            testConfigDir.mkdirs();
        }

        /**
         * Here you can add a new method to generate other classes.
         */
        // Generate test classes in the identified folder
        generateAllTestClasses(testConfigDir, config);
        // Generate component classes (controller, service, repository, entity) in baseSourceDir
        generateSourcePackagesWithClasses(baseSourceDir, basePackage);
        // Generate the application.properties template file in src/main/resources
        generateApplicationProperties(projectDir, config);
        // Generate the application-test.properties file for tests in src/test/resources
        generateTestApplicationProperties(projectDir, config);
    }


    private void generateSourcePackagesWithClasses(File baseSourceDir, String basePackage) throws IOException {
        // For each subfolder, create the directory (if it does not exist) and generate the corresponding class

        // Controller
        File controllerDir = new File(baseSourceDir, "controller");
        if (!controllerDir.exists()) {
            controllerDir.mkdirs();
        }
        generateControllerClass(controllerDir, basePackage);

        // Service
        File serviceDir = new File(baseSourceDir, "service");
        if (!serviceDir.exists()) {
            serviceDir.mkdirs();
        }
        generateServiceClass(serviceDir, basePackage);

        // Repository
        File repositoryDir = new File(baseSourceDir, "repository");
        if (!repositoryDir.exists()) {
            repositoryDir.mkdirs();
        }
        generateRepositoryClass(repositoryDir, basePackage);

        // Entity
        File entityDir = new File(baseSourceDir, "entity");
        if (!entityDir.exists()) {
            entityDir.mkdirs();
        }
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
                                    
                }
                """, basePackage);
        writeClassFile(dir, "Service.java", content);
    }

    private void generateRepositoryClass(File dir, String basePackage) throws IOException {
        String content = String.format("""
                package %s.repository;
                                
                import org.springframework.stereotype.Repository;
                                
                public interface Repository {
                                    
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
                                    
                }
                """, basePackage);
        writeClassFile(dir, "Entity.java", content);
    }

    private void generateAllTestClasses(File configDir, ProjectConfig config) throws IOException {
        String basePackage = buildPackageName(config);
        generateBaseTest(configDir, basePackage);
        generateBaseUnitTest(configDir, basePackage);
        generateBaseE2ETest(configDir, basePackage);
        generateBaseIntegrationTest(configDir, basePackage);
    }

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

    private void generateApplicationProperties(String projectDir, ProjectConfig config) throws IOException {
        // Define the resources folder
        File resourcesDir = new File(projectDir, "src/main/resources");
        if (!resourcesDir.exists()) {
            resourcesDir.mkdirs();
        }

        File appPropsFile = new File(resourcesDir, "application.properties");
        // Create a template for application.properties
        String content = String.format("""
        spring.application.name=%s
        server.port=8080

        # MySQL Database Configuration
        #spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name?useSSL=false&serverTimezone=UTC
        #spring.datasource.username=your_username
        #spring.datasource.password=your_password
        #spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

        # JPA/Hibernate Configuration
        #spring.jpa.hibernate.ddl-auto=update
        #spring.jpa.show-sql=true
        #spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
        """, config.getProjectName());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appPropsFile))) {
            writer.write(content);
        }
        System.out.println("Generated: " + appPropsFile.getAbsolutePath());
    }

    private void generateTestApplicationProperties(String projectDir, ProjectConfig config) throws IOException {
        // Define the test resources folder
        File testResourcesDir = new File(projectDir, "src/test/resources");
        if (!testResourcesDir.exists()) {
            testResourcesDir.mkdirs();
        }
        File testAppPropsFile = new File(testResourcesDir, "application-test.properties");
        String content = String.format("""
                spring.application.name=%s
                                       
                # Setup database H2
                spring.datasource.url=jdbc:h2:mem:your_database_name
                spring.datasource.username=your_username
                spring.datasource.password=your_password
                spring.datasource.driver-class-name=org.h2.Driver
                spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
                
                spring.jpa.show-sql=true
                
                spring.jpa.hibernate.ddl-auto=none
                spring.sql.init.mode=always
                spring.sql.init.schema-locations=classpath:schema.sql
                spring.sql.init.data-locations=classpath:data.sql
                """, config.getProjectName());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testAppPropsFile))) {
            writer.write(content);
        }
        System.out.println("Generated: " + testAppPropsFile.getAbsolutePath());
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

    private String buildPackageName(ProjectConfig config) {
        // If artifactId matches the last token of groupId, the base package will be just the groupId,
        // otherwise, it will be groupId.artifactId
        String lastToken = config.getGroupId().substring(config.getGroupId().lastIndexOf('.') + 1);
        return config.getArtifactId().equalsIgnoreCase(lastToken)
                ? config.getGroupId()
                : config.getGroupId() + "." + config.getArtifactId();
    }

    /**
     * Recursively searches in 'dir' for the directory containing a file that ends with "Application.java".
     */
    private File findBasePackageDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        File result = findBasePackageDirectory(file);
                        if (result != null) {
                            return result;
                        }
                    } else if (file.getName().endsWith("Application.java")) {
                        return file.getParentFile();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Computes the package in dot notation from the base source directory.
     * For example, if srcMainJavaDir = ".../src/main/java" and baseDir = ".../src/main/java/it/github/myapp",
     * it returns "it.github.myapp".
     */
    private String getPackageFromPath(File srcMainJavaDir, File baseDir) {
        String srcMainPath = srcMainJavaDir.getAbsolutePath();
        String basePath = baseDir.getAbsolutePath();
        // Remove the root path and replace separators with dots
        String relative = basePath.substring(srcMainPath.length());
        if (relative.startsWith(File.separator)) {
            relative = relative.substring(1);
        }
        return relative.replace(File.separator, ".");
    }
}