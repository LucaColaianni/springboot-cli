package it.github.cli.service;

import it.github.cli.config.ProjectConfig;
import it.github.cli.interfaces.ProjectDownloader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProjectDownloaderImpl implements ProjectDownloader {

    private static final String STAGING_DIR = "STAGING";
    private static final String SPRING_INITIALIZR_URL = "https://start.spring.io/starter.zip";

    @Override
    public String downloadProject(ProjectConfig config) throws IOException {
        String stagingDir = createStagingDirectory();
        String zipFilePath = downloadAndSaveProject(config, stagingDir);
        return zipFilePath;
    }

    private String createStagingDirectory() {
        String currentDir = System.getProperty("user.dir");
        String stagingPath = currentDir + File.separator + STAGING_DIR;
        File stagingFolder = new File(stagingPath);

        if (!stagingFolder.exists() && stagingFolder.mkdirs()) {
            System.out.println("Created STAGING directory: " + stagingPath);
        }

        return stagingPath;
    }

    private String downloadAndSaveProject(ProjectConfig config, String stagingDir) throws IOException {
        String downloadUrl = buildSpringInitializrUrl(config);
        byte[] zipContent = downloadProjectContent(downloadUrl);
        String zipFilePath = stagingDir + File.separator + config.getProjectName() + ".zip";

        try (FileOutputStream fos = new FileOutputStream(zipFilePath)) {
            fos.write(zipContent);
        }

        System.out.println("Project downloaded to: " + zipFilePath);
        return zipFilePath;
    }

    private String buildSpringInitializrUrl(ProjectConfig config) {
        return SPRING_INITIALIZR_URL +
                "?type=maven-project" +
                "&language=java" +
                "&bootVersion=3.4.3" +
                "&groupId=" + config.getGroupId() +
                "&artifactId=" + config.getArtifactId() +
                "&name=" + config.getProjectName() +
                "&description=Demo+project+for+" + config.getProjectName() +
                "&packageName=" + config.getGroupId() + "." + config.getArtifactId() +
                "&packaging=jar" +
                "&javaVersion=17" +
                "&dependencies=web,jpa,h2,lombok";
    }
    private byte[] downloadProjectContent(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }
}