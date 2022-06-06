package xyz.nickelulz.glasshousetweaks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.adminonly.ManageIllegalKillsCommand;
import xyz.nickelulz.glasshousetweaks.commands.allaccess.LeaderboardCommand;
import xyz.nickelulz.glasshousetweaks.commands.allaccess.RegisterCommand;
import xyz.nickelulz.glasshousetweaks.commands.allaccess.TestCommand;
import xyz.nickelulz.glasshousetweaks.commands.registeredonly.BountyCommand;
import xyz.nickelulz.glasshousetweaks.commands.registeredonly.ContractCommand;
import xyz.nickelulz.glasshousetweaks.commands.registeredonly.RemoveHitCommand;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class CommandManager {
    public static CommandMap getCommandMap() {
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);

                return (CommandMap) field.get(Bukkit.getPluginManager());
            }
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void registerCommand(String commandLabel, Command command) {
        CommandMap commandMap = getCommandMap();
        if (commandMap != null) {
            commandMap.register(commandLabel, command);
            GlasshouseTweaks.log(Level.INFO, "Registered command " + commandLabel + ".");
        }
    }

    public static void initialize() {
        // All Access
        registerCommand("register", new RegisterCommand());
        registerCommand("test", new TestCommand());
        registerCommand("leaderboard", new LeaderboardCommand());

        // Registered Only
        registerCommand("bounty", new BountyCommand());
        registerCommand("contract", new ContractCommand());
        registerCommand("removehit", new RemoveHitCommand());

        // Admin Only
        registerCommand("illegalkills", new ManageIllegalKillsCommand());
    }
}
