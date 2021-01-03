package com.upstox.config;

import com.upstox.exception.UpstoxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public final class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    public static final String fileFolderPath;

    static {
        File configFile = new File("config.properties");
        try {
            FileReader reader = new FileReader(configFile);
            Properties properties = new Properties();
            properties.load(reader);
            fileFolderPath = properties.getProperty("file-folder-path");
            LOGGER.info("Trades Folder path {}", fileFolderPath);
        } catch (Exception ex) {
            throw new UpstoxException("Cannot load config properties", ex);
        }
    }
}
