package de.jozelot.stoneuniverse.data.config;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import de.jozelot.stoneuniverse.StoneUniverse;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {

    private final StoneUniverse plugin;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public ConfigLoader(StoneUniverse plugin, @DataDirectory Path dataDirectory) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.dataDirectory = dataDirectory;
    }

    public boolean load() {
        logger.info("Loading configuration (config.yml)...");

        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            File configFile = dataDirectory.resolve("config.yml").toFile();

            if (!configFile.exists()) {
                logger.info("config.yml not found. Creating default configuration file.");
                try (InputStream in = getClass().getClassLoader().getResourceAsStream("config-example.yml")) {
                    if (in == null) {
                        logger.error("Default config-example.yml could not be found inside the jar resources!");
                        return false;
                    }
                    Files.copy(in, configFile.toPath());
                }
            }

            Yaml yaml = new Yaml();
            try (InputStream inputStream = new FileInputStream(configFile)) {
                plugin.getBootstrap().getConfig().setConfigurationData(yaml.load(inputStream));
                plugin.getBootstrap().getConfig().load();
            }

            logger.info("Configuration successfully loaded.");
            return true;

        } catch (Exception e) {
            logger.error("Error while trying to load 'config.yml'", e);
            return false;
        }
    }
}