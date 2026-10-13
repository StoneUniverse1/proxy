package de.jozelot.stoneuniverse.data.config.lang;

import de.jozelot.stoneuniverse.StoneUniverse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LangManager {

    private final StoneUniverse plugin;

    private Map<String, Object> configurationData;

    public LangManager(StoneUniverse plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (configurationData == null) {
            plugin.getLogger().error("Lang configuration data is null! Cannot load config.");
            return;
        }

    }

    public String format(String path, Map<String, String> replace) {
        String rawMessage = getString(path);

        if (rawMessage == null) {
            return "<red>" + path;
        }

        return replacePlaceholders(rawMessage, replace);
    }

    public List<String> formatList(String path, Map<String, String> replace) {
        List<String> rawList = getStringList(path);

        if (rawList.isEmpty()) {
            return List.of("<red>" + path);
        }

        List<String> formattedList = new ArrayList<>();
        for (String line : rawList) {
            formattedList.add(replacePlaceholders(line, replace));
        }

        return formattedList;
    }

    private String replacePlaceholders(String input, Map<String, String> replace) {
        if (input == null) return "";

        if (replace != null && !replace.isEmpty()) {
            for (Map.Entry<String, String> entry : replace.entrySet()) {
                input = input.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        return input;
    }


    // BEREICH FÜR DIE GENERELLEN GETTER METHODEN

    // STRING
    public String getString(String path) {
        Object obj = getObject(path);
        return obj != null ? obj.toString() : null;
    }

    public String getString(String path, String def) {
        String val = getString(path);
        return val != null ? val : def;
    }

    // LISTEN

    public List<?> getList(String path) {
        Object obj = getObject(path);
        return obj instanceof List ? (List<?>) obj : new ArrayList<>();
    }

    public List<String> getStringList(String path) {
        List<?> rawList = getList(path);
        List<String> list = new ArrayList<>();

        for (Object obj : rawList) {
            if (obj != null) {
                list.add(obj.toString());
            }
        }
        return list;
    }

    private Object getObject(String path) {
        String[] keys = path.split("\\.");
        Map<String, Object> currentMap = configurationData;

        for (int i = 0; i < keys.length; i++) {
            Object obj = currentMap.get(keys[i]);
            if (i == keys.length - 1) {
                return obj;
            }
            if (obj instanceof Map) {
                currentMap = (Map<String, Object>) obj;
            } else {
                break;
            }
        }
        return null;
    }

    public void setData(Map<String, Object> newConfigurationData) {
        configurationData = newConfigurationData;
    }
}
