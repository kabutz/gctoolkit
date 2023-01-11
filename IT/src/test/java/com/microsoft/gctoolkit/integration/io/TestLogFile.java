package com.microsoft.gctoolkit.integration.io;

import java.io.File;
import java.util.Arrays;

public class TestLogFile {
    private final File logFile;

    private static final String[] relativePaths = {
            "./",
            "preunified/",
            "preunified/cms/parnew/details/tenuring/",
            "preunified/verbose/tenuring/",
            "preunified/ps/details/tenuring/",
            "preunified/ps/details/",
            "unified/",
            "streaming/",
            "safepoint/",
    };

    public TestLogFile(String fileName) {
        File cwd = new File(".");
        logFile = Arrays.stream(relativePaths)
                .flatMap(path -> Arrays.stream(new String[]{
                        "./" + path,
                        "../" + path,
                        "../../" + path,
                        "./gclogs/" + path,
                        "../gclogs/" + path,
                        "../../gclogs/" + path,
                }))
                .map(path -> new File(path + File.separator + fileName))
                .filter(File::exists)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(fileName + " not found"));
    }

    public TestLogFile(File file) {
        this.logFile = file;
    }

    public String getPath() {
        return logFile.getPath();
    }

    public File getFile() {
        return logFile;
    }
}
