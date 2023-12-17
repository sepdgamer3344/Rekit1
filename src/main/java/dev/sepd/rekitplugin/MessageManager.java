package dev.sepd.rekitplugin;
public class MessageManager {

    private final RekitPlugin plugin;

    public MessageManager(RekitPlugin plugin) {
        this.plugin = plugin;
    }

    public String getFormattedMessage(String key, Object... args) {
        String message = plugin.getConfig().getString("messages." + key);
        if (message != null) {
            return String.format(message, args);
        }
        return "";
    }
}
