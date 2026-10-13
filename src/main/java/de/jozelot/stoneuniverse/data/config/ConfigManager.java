package de.jozelot.stoneuniverse.data.config;

import de.jozelot.stoneuniverse.StoneUniverse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final StoneUniverse plugin;

    private Database database;
    private Redis redis;

    private Map<String, Object> configurationData;

    public ConfigManager(StoneUniverse plugin) {
        this.plugin = plugin;
        this.database = new Database();
        this.redis = new Redis();
    }

    public class Database {
        private String host;
        private int port;
        private String user;
        private String password;
        private String database;

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public String getDatabase() {
            return database;
        }
    }

    public class Redis {
        private String host;
        private int port;
        private String password;

        public String getHost() {
            return host;
        }
        public int getPort() {
            return port;
        }
        public String getPassword() {
            return password;
        }
    }

    public void load() {
        if (configurationData == null) {
            plugin.getLogger().error("Configuration data is null! Cannot load config.");
            return;
        }

        database.host = getString("database.host");
        database.port = getInt("database.port");
        database.user = getString("database.username");
        database.password = getString("database.password");
        database.database = getString("database.database");

        redis.host = getString("redis.host");
        redis.port = getInt("redis.port");
        redis.password = getString("redis.password");

    }


    public Database getDatabase() {
        return database;
    }
    public Redis getRedis() {
        return redis;
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

    // INT
    public int getInt(String path) {
        return getInt(path, 0);
    }

    public int getInt(String path, int def) {
        Object obj = getObject(path);
        return obj instanceof Number ? ((Number) obj).intValue() : def;
    }

    // BOOLEAN
    public boolean getBoolean(String path, boolean def) {
        Object obj = getObject(path);
        return obj instanceof Boolean ? (Boolean) obj : def;
    }

    public boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    // DOUBLE
    public double getDouble(String path, double def) {
        Object obj = getObject(path);
        return obj instanceof Number ? ((Number) obj).doubleValue() : def;
    }

    public double getDouble(String path) {
        return getDouble(path, 0.0);
    }

    public long getLong(String path) {
        return getLong(path, 0L);
    }

    public long getLong(String path, long def) {
        Object obj = getObject(path);
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                return def;
            }
        }
        return def;
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

    public List<Integer> getIntList(String path) {
        List<?> rawList = getList(path);
        List<Integer> list = new ArrayList<>();

        for (Object obj : rawList) {
            if (obj instanceof Number) {
                list.add(((Number) obj).intValue());
            }
        }
        return list;
    }

    public List<Double> getDoubleList(String path) {
        List<?> rawList = getList(path);
        List<Double> list = new ArrayList<>();

        for (Object obj : rawList) {
            if (obj instanceof Number) {
                list.add(((Number) obj).doubleValue());
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

    public List<Long> getLongList(String path) {
        List<?> rawList = getList(path);
        List<Long> list = new ArrayList<>();

        for (Object obj : rawList) {
            if (obj instanceof Number) {
                list.add(((Number) obj).longValue());
            } else if (obj != null) {
                try {
                    list.add(Long.parseLong(obj.toString()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return list;
    }

    public void setConfigurationData(Map<String, Object> newConfigurationData) {
        configurationData = newConfigurationData;
    }
}
