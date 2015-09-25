package com.github.skazzyy.path;

import java.io.File;
import java.util.Arrays;

public class PathRevisor {

    public static void main(String[] args) {
        String systemPath = EnvironmentVariables.Path.getSystemRegistryValue();
        System.out.println("System path:");
        String[] paths = systemPath.split(File.pathSeparator);
        Arrays.stream(paths).forEach(path -> {
            System.out.println(path);
        });
        String userPath = EnvironmentVariables.Path.getUserRegistryValue();
        Arrays.stream(userPath.split(File.pathSeparator)).forEach(System.out::println);

    }
}
