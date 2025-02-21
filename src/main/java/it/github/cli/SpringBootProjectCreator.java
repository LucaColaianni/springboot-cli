package it.github.cli;

import it.github.cli.interfaces.ProjectDownloader;
import it.github.cli.service.ProjectDownloaderImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Scanner;

@Command(name = "spring-boot-creator", mixinStandardHelpOptions = true, version = "1.0",
        description = "Creates a Spring Boot project using Spring Initializr")
public class SpringBootProjectCreator implements Runnable {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new SpringBootProjectCreator()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Do you want to create a Spring Boot project? (y/n): ");
        String answer = scanner.nextLine().trim();

        if (!answer.equalsIgnoreCase("y")) {
            System.out.println("Exiting program.");
            return;
        }

        System.out.print("Enter Group ID: ");
        String groupId = scanner.nextLine().trim();

        System.out.print("Enter Artifact ID: ");
        String artifactId = scanner.nextLine().trim();

        System.out.print("Enter Project Name: ");
        String projectName = scanner.nextLine().trim();

        ProjectDownloader downloader = new ProjectDownloaderImpl();
        try {
            downloader.downloadProject(groupId, artifactId, projectName, "");
        } catch (Exception e) {
            System.err.println("Error downloading project: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
