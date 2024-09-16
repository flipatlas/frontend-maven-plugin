package com.github.eirslett.maven.plugins.frontend.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class ExecutableProvider {

    final Logger logger = LoggerFactory.getLogger(getClass());
    final InstallConfig config;

    public ExecutableProvider(InstallConfig config) {
        this.config = config;
    }

    public File getNode() {
        NodeVersionManager nodeVersionManager = findNvmAvailable();
        return new File(findExecutablePath(nodeVersionManager, "node"));
    }

    private String findExecutablePath(NodeVersionManager nvm, String executable) {
//        return execute(Arrays.asList(nvm.getExecutable(), "which", executable));
        // fixme only fnm
        execute(Arrays.asList("fnm env --json"));
        return execute(Arrays.asList("which", executable));
    }

    public File getNpm() {
        File nodeExec = getNode();
        return new File(nodeExec.getParent(), "npm");
    }

    public boolean isNvmAvailable() {
        NodeVersionManager nodeVersionManager = findNvmAvailable();
        logger.info("Found Node Version Manager: {}", nodeVersionManager);
        return nodeVersionManager != null;
    }

    public NodeVersionManager findNvmAvailable() {
//        execute(Arrays.asList("echo", "$HOME"));
//        execute(Arrays.asList("echo", "$PATH"));
//        execute(Arrays.asList("echo", "$NVM_DIR"));
        execute(Arrays.asList("echo", "$SHELL"));

        for (NodeVersionManager nvmType : NodeVersionManager.values()) {
            if(isCommandAvailable(nvmType.getExecutable())) return nvmType;
        }
        return null;
    }


    private boolean isCommandAvailable(String executable) {
        String result = execute(Arrays.asList("command", "-v", executable));
        if (!result.isEmpty()) {
            // this is so that we can mock out version managers in tests
            String version = getExecutableVersion(executable);
            return !version.isEmpty();
        }
        return false;
    }

    private boolean isValidVersion(String version) {
        return version.matches("v?\\d+\\.\\d+\\.\\d+");
    }

    private String getExecutableVersion(String executable) {
        return execute(Arrays.asList(executable, "--version"));
    }

    public void installNodeWithNvm(NodeVersionManager nodeVersionManager, String nodeVersion) {
        logger.info("install node version {}", nodeVersion);
//        execute(Arrays.asList("echo", "$PWD"));
        execute(Arrays.asList(
            nodeVersionManager.getExecutable(),
            nodeVersionManager.getInstallCommand(),
            nodeVersion
        ));
        // Fixme only fnm
        execute(Arrays.asList(
            nodeVersionManager.getExecutable(),
            "use",
            nodeVersion
        ));

        // verify installation, check command line node version
    }

    private String execute(List<String> command) {
        ProcessExecutor executor = new ProcessExecutor(
            config.getWorkingDirectory(),
            Collections.emptyList(),
            command,
            config.getPlatform(),
            Collections.emptyMap());

        try {
            List<String> sourcedCmd = new ArrayList<>();
//            sourcedCmd.add( "export NVM_DIR=\"$HOME/.nvm\";" +
//                "[ -s \"$NVM_DIR/nvm.sh\" ] && \\. \"$NVM_DIR/nvm.sh\";");
            sourcedCmd.addAll(command);

            logger.info("Command: {}", sourcedCmd);

            ProcessBuilder pb = new ProcessBuilder();
            // TODO find out which shell is available zsh, sh, bash, ksh
            pb.command("bash", "-l", "-c", String.join(" ", sourcedCmd));
            pb.directory(config.getWorkingDirectory());
            Process process = pb.start();
            int exitCode = process.waitFor();

            String result = readStream(process.getInputStream());
            String error = readStream(process.getErrorStream());

//            String result = executor.executeAndGetResult(logger);
            logger.info("Result ({}): {}\nerror: {}", exitCode, result, error);

            return result;
        } catch (IOException e) {
            return e.getMessage();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String readStream(InputStream stream) throws IOException {
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
            .filter(line -> !line.contains("Using Node")) // fnm will print that on starting new shell session
            .collect(Collectors.joining(System.getProperty("line.separator")));
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
        }
    }
}
