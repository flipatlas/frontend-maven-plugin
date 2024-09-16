package com.github.eirslett.maven.plugins.frontend.lib;

import java.io.File;

public enum NodeVersionManager {
//    MISE("mise", "use"),
//    ASDF("asdf", "use"),
//    NVS("nvs", "use"),
    FNM("fnm", "install"),
    NVM("nvm", "install");


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
