package it.github.cli;

import it.github.cli.config.ProjectConfig;
import it.github.cli.interfaces.ClassGenerator;
import it.github.cli.interfaces.ProjectDownloader;
import it.github.cli.service.ClassGeneratorImpl;
import it.github.cli.service.ProjectDownloaderImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Scanner;

@Command(
        name = "spring-boot-creator",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Creates a Spring Boot project using Spring Initializr")
public class SpringBootProjectCreator implements Runnable {

    private final Scanner scanner;
    private final ProjectDownloader downloader;
    private final ClassGenerator classGenerator;

    public SpringBootProjectCreator() {
        this.scanner = new Scanner(System.in);
        this.downloader = new ProjectDownloaderImpl();
        this.classGenerator = new ClassGeneratorImpl();
    }
    public static void main(String[] args) {
        int exitCode = new CommandLine(new SpringBootProjectCreator()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        try {
            if (!confirmProjectCreation()) {
                System.out.println("Exiting program.");
                return;
            }

            ProjectConfig config = collectProjectConfiguration();
            String projectPath = downloader.downloadProject(config);
            classGenerator.generateClasses(projectPath, config);

        } catch (Exception e) {
            System.err.println("Error creating project: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean confirmProjectCreation() {
        System.out.print("Do you want to create a Spring Boot project? (y/n): ");
        return scanner.nextLine().trim().equalsIgnoreCase("y");
    }

    private ProjectConfig collectProjectConfiguration() {
        System.out.print("Enter Group ID: ");
        String groupId = scanner.nextLine().trim();

        System.out.print("Enter Artifact ID: ");
        String artifactId = scanner.nextLine().trim();

        System.out.print("Enter Project Name: ");
        String projectName = scanner.nextLine().trim();

        return new ProjectConfig(groupId, artifactId, projectName);
    }
}
