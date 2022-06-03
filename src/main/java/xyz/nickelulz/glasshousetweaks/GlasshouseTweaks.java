package xyz.nickelulz.glasshousetweaks;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nickelulz.glasshousetweaks.commands.RegisterCommand;
import xyz.nickelulz.glasshousetweaks.commands.TestCommand;
import xyz.nickelulz.glasshousetweaks.datatypes.CommandBase;
import xyz.nickelulz.glasshousetweaks.util.PlayerDatabase;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class GlasshouseTweaks extends JavaPlugin implements Listener {
    private static GlasshouseTweaks instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        log(Level.INFO, "Initializing this plugin...");
        instance = this;

        this.saveDefaultConfig();

        // Command Registry
        new RegisterCommand();
        new TestCommand();

        PlayerDatabase.load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log(Level.INFO, "Plugin stopped.");
        PlayerDatabase.save();
    }

    public static GlasshouseTweaks getInstance() {
        return instance;
    }

    public static void log(Level level, String outputMessage) {
        Logger.getLogger("Minecraft").log(level, "[GlassHouseTweaks] " + outputMessage);
    }
}
