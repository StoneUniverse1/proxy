package de.jozelot.stoneUniverse_Proxy.interfaces;

public interface Bootstrap {
    boolean register();
    boolean enable();
    void shutdown();
    void reload();
}

