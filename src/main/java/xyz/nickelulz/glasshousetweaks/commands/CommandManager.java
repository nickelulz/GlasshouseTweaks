package xyz.nickelulz.glasshousetweaks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.adminonly.ManageIllegalKillsCommand;
import xyz.nickelulz.glasshousetweaks.commands.allaccess.HelpCommand;
import xyz.nickelulz.glasshousetweaks.commands.allaccess.LeaderboardCommand;
import xyz.nickelulz.glasshousetweaks.commands.allaccess.RegisterCommand;
import xyz.nickelulz.glasshousetweaks.commands.allaccess.ViewPlayerInfoCommand;
import xyz.nickelulz.glasshousetweaks.commands.registeredonly.BountyCommand;
import xyz.nickelulz.glasshousetweaks.commands.registeredonly.ContractCommand;
import xyz.nickelulz.glasshousetweaks.commands.registeredonly.RemoveHitCommand;
import xyz.nickelulz.glasshousetweaks.commands.registeredonly.TransferMorbiumsCommand;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class CommandManager {
    public static final HelpCommand HELP_COMMAND = new HelpCommand();
    public static final LeaderboardCommand LEADERBOARD_COMMAND = new LeaderboardCommand();
    public static final RegisterCommand REGISTER_COMMAND = new RegisterCommand();
    public static final ViewPlayerInfoCommand VIEW_PLAYER_INFO_COMMAND = new ViewPlayerInfoCommand();
    public static final BountyCommand BOUNTY_COMMAND = new BountyCommand();
    public static final ContractCommand CONTRACT_COMMAND = new ContractCommand();
    public static final RemoveHitCommand REMOVE_HIT_COMMAND = new RemoveHitCommand();
    public static final TransferMorbiumsCommand TRANSFER_MORBIUMS_COMMAND = new TransferMorbiumsCommand();
    public static final ManageIllegalKillsCommand MANAGE_ILLEGAL_KILLS_COMMAND = new ManageIllegalKillsCommand();

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
        }
    }

    public static void initialize() {
        // All Access
        registerCommand("register", REGISTER_COMMAND);
        registerCommand("leaderboard", LEADERBOARD_COMMAND);
        registerCommand("playerinfo", VIEW_PLAYER_INFO_COMMAND);
        GlasshouseTweaks.getInstance().getCommand("ghtweakshelp").setExecutor(HELP_COMMAND);

        // Registered Only
        registerCommand("bounty", BOUNTY_COMMAND);
        registerCommand("contract", CONTRACT_COMMAND);
        registerCommand("removehit", REMOVE_HIT_COMMAND);
        registerCommand("transfer", TRANSFER_MORBIUMS_COMMAND);

        // Admin Only
        registerCommand("illegalkills", MANAGE_ILLEGAL_KILLS_COMMAND);

        GlasshouseTweaks.log(Level.INFO, "Registered all commands.");
    }
}
