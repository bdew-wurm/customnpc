package net.bdew.wurm.customnpc.config;

public class ConfigLoadError extends Exception {
    public ConfigLoadError(String message) {
        super(message);
    }

    public ConfigLoadError(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigLoadError(Throwable cause) {
        super(cause);
    }
}
