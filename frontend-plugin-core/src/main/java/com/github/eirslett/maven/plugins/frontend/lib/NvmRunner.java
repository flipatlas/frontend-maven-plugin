package com.github.eirslett.maven.plugins.frontend.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NvmRunner {

    private final InstallConfig config;
    private final ExecutableProvider executableProvider;
    private final Logger logger;

    public NvmRunner(InstallConfig config) {
        this.config = config;
        this.logger = LoggerFactory.getLogger(getClass());
        this.executableProvider = new ExecutableProvider(config);
    }

    public boolean isNvmAvailable() {
        return executableProvider.isNvmAvailable();
    }

    public void installNode() {
        NodeVersionManager nodeVersionManager = executableProvider.findNvmAvailable();
        execute(Arrays.asList(
            nodeVersionManager.getExecutable(),
            nodeVersionManager.getInstallCommand()
        ));
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
