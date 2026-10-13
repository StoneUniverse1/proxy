package de.jozelot.stoneuniverse.data.config.lang;

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

public class LangLoader {

    private final StoneUniverse plugin;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public LangLoader(StoneUniverse plugin, @DataDirectory Path dataDirectory) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.dataDirectory = dataDirectory;
    }

    public boolean load() {
        logger.info("Loading lang configuration (lang.yml)...");

        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            File configFile = dataDirectory.resolve("lang.yml").toFile();

            if (!configFile.exists()) {
                logger.info("lang.yml not found. Creating default lang configuration file.");
                try (InputStream in = getClass().getClassLoader().getResourceAsStream("lang.yml")) {
                    if (in == null) {
                        logger.error("Default lang.yml could not be found inside the jar resources!");
                        return false;
                    }
                    Files.copy(in, configFile.toPath());
                }
            }

            Yaml yaml = new Yaml();
            try (InputStream inputStream = new FileInputStream(configFile)) {
                plugin.getBootstrap().getLang().setData(yaml.load(inputStream));
                plugin.getBootstrap().getLang().load();
            }

            logger.info("Lang configuration successfully loaded.");
            return true;

        } catch (Exception e) {
            logger.error("Error while trying to load 'lang.yml'", e);
            return false;
        }
    }
}