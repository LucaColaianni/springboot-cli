package it.github.cli.service;

import it.github.cli.interfaces.ProjectDownloader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProjectDownloaderImpl implements ProjectDownloader {
    @Override
    public void downloadProject(String groupId, String artifactId, String projectName, String outputDir) throws IOException {
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current directory: " + currentDir);

        String downloadUrl = buildSpringInitializrUrl(groupId, artifactId, projectName);
        System.out.println("Downloading project from: " + downloadUrl);

        byte[] zipContent = downloadProject(downloadUrl);
        String zipFileName = currentDir + File.separator + projectName + ".zip";
        saveZip(zipContent, zipFileName);

        System.out.println("Project successfully downloaded to: " + zipFileName);
    }

    private String buildSpringInitializrUrl(String groupId, String artifactId, String projectName) {
        return "https://start.spring.io/starter.zip" +
                "?type=maven-project" +
                "&language=java" +
                "&bootVersion=3.4.3" +
                "&groupId=" + groupId +
                "&artifactId=" + artifactId +
                "&name=" + projectName +
                "&description=Demo+project+for+" + projectName +
                "&packageName=" + groupId + "." + artifactId +
                "&packaging=jar" +
                "&javaVersion=17" +
                "&dependencies=web" +
                "&dependencies=jpa" +
                "&dependencies=h2" +
                "&dependencies=lombok";
    }


    private byte[] downloadProject(String url) throws IOException {
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
    private void saveZip(byte[] zipContent, String outputPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(zipContent);
        }
    }
}
