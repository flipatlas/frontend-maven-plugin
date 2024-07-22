package com.github.eirslett.maven.plugins.frontend.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class ExecutableProvider {

    final Logger logger = LoggerFactory.getLogger(getClass());
    final InstallConfig config;


    public ExecutableProvider(InstallConfig config) {
        this.config = config;
    }

    public boolean isNodeAvailable() {
        String version = getExecutableVersion("node");
        return isValidVersion(version);
    }

    public File getNode() {
        return new File(findExecutable("node"));
    }

    public boolean isNpmAvailable() {
        String version = getExecutableVersion("npm");
        return isValidVersion(version);
    }

    public File getNpm() {
        return new File(findExecutable("npm"));
    }

    public boolean isNvmAvailable() {
        NodeVersionManager nodeVersionManager = findNvmAvailable();
        return nodeVersionManager != null;
    }

    public NodeVersionManager findNvmAvailable() {
        for (NodeVersionManager nvmType : NodeVersionManager.values()) {
            String version = getExecutableVersion(nvmType.getExecutable());
            if(isValidVersion(version)) return nvmType;
        }
        return null;
    }

    private String findExecutable(String executable) {
        List<String> command;
        if (config.getPlatform().isWindows()) {
            command = Arrays.asList("where", executable);
        } else {
            command = Arrays.asList("which", executable);
        }

        return execute(command);
    }

    private boolean isValidVersion(String version) {
        return version.matches("v?\\d+\\.\\d+\\.\\d+");
    }

    private String getExecutableVersion(String executable) {
        return execute(Arrays.asList(executable, "--version"));
    }

    private String execute(List<String> command) {
        ProcessExecutor executor = new ProcessExecutor(
            config.getWorkingDirectory(),
            Collections.emptyList(),
            command,
            config.getPlatform(),
            Collections.emptyMap());

        try {
            return executor.executeAndGetResult(logger);
        } catch (ProcessExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
