package com.demokiller.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MyCustomGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        System.out.println("tttttttttttttttttttttttttttttt");
    }
}
