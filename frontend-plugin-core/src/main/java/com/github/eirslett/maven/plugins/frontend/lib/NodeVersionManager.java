package com.github.eirslett.maven.plugins.frontend.lib;

public enum NodeVersionManager {
    MISE("mise", "use"),
    ASDF("asdf", "use"),
    FNM("fnm", "use"),
    NVS("nvs", "use"),
    NVM("nvm", "use");


    private final String executable;
    private final String installCommand;

    NodeVersionManager(String executable, String installCommand) {
        this.executable = executable;
        this.installCommand = installCommand;
    }

    public String getExecutable() {
        return executable;
    }

    public String getInstallCommand() {
        return installCommand;
    }
}
