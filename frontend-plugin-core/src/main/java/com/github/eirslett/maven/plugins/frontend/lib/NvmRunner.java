package com.github.eirslett.maven.plugins.frontend.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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

    // TODO pass version to install
    public void installNode(String nodeVersion) {
        NodeVersionManager nodeVersionManager = executableProvider.findNvmAvailable();
        logger.info("Using Node Version Manager [{}] to install node", nodeVersionManager);

        executableProvider.installNodeWithNvm(nodeVersionManager, nodeVersion);
    }

    public File getNodeDirectory() {
        return executableProvider.getNode();
    }
}
