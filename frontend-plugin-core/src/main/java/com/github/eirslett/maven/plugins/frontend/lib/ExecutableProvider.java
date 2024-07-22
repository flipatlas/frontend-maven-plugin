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
        return new File(findExecutablePath("node"));
    }

    public boolean isNpmAvailable() {
        String version = getExecutableVersion("npm");
        return isValidVersion(version);
    }

    public File getNpm() {
        return new File(findExecutablePath("npm"));
    }

    public boolean isNvmAvailable() {
        NodeVersionManager nodeVersionManager = findNvmAvailable();
        logger.info("Found Node Version Manager: {}", nodeVersionManager);
        return nodeVersionManager != null;
    }

    public NodeVersionManager findNvmAvailable() {
        for (NodeVersionManager nvmType : NodeVersionManager.values()) {
            if(isCommandAvailable(nvmType.getExecutable())) return nvmType;
        }
        return null;
    }

    private String findExecutablePath(String executable) {
        List<String> command;
        if (config.getPlatform().isWindows()) {
            command = Arrays.asList("where", executable);
        } else {
            command = Arrays.asList("which", executable);
        }

        return execute(command);
    }

    private boolean isCommandAvailable(String executable) {
        String maybeVersion = getExecutableVersion(executable);
        logger.info("executable version {} > {}", executable, maybeVersion);

        return !maybeVersion.contains("not found") && !maybeVersion.contains("Exception");
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
            Collections.singletonList(config.getNodeVersionManagerDirectory().getAbsolutePath()),
            command,
            config.getPlatform(),
            Collections.emptyMap());

        try {
            return executor.executeAndGetResult(logger);
        } catch (ProcessExecutionException e) {
            // suppress terminal exception
            return e.getMessage();
        }
    }
}
