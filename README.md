# Spring Boot CLI

## What is Spring Boot CLI?

Spring Boot CLI is a Java program that allows you to generate Spring Boot projects equipped with predefined templates: classes, packages, and an `application.properties` file. The generated project automatically includes basic dependencies such as `h2`, `lombok`, `jpa`, and `spring web`. Additionally, test classes are provided to help you start Test Driven Development (TDD) immediately.

## How to Launch the CLI?

A release containing the jar file is available in this GitHub repository for download. If you prefer not to use the provided executable, you can download the project, run a `maven clean package`, and execute the generated jar.

To launch the application, navigate to the folder containing the executable and run the following command in your terminal:

    java -jar springboot-cli-1.0-SNAPSHOT.jar .

Once executed, a folder named `your Spring Boot project!` will be created. This folder contains the generated file along with a `.zip` version. The `.zip` version includes only the dependencies `h2`, `lombok`, `jpa`, and `web`, without any additional classes.

## How to Customize the Creation of the Spring Boot Project?

After launching the executable, you will be prompted with the following questions:

1. `Do you want to create a Spring Boot project? (y/n):`
2. `Enter Group ID:`
3. `Enter Artifact ID:`
4. `Enter Project Name:`

The project will be created using Java 17, Maven as the build tool, and Spring Boot version 3.4.3. To change these parameters, simply open the `ProjectDownloaderImpl` class, locate the `buildSpringInitializrUrl(...)` method, and update the desired settings.

## Conclusions

This project was developed with the goal of generating Spring Boot project templates in the shortest possible time. While there is room for further improvement in its implementation, the primary objective was to simplify and expedite the project creation process.
