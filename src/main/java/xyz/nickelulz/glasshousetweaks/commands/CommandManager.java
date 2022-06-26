package xyz.nickelulz.glasshousetweaks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.adminonly.ManageIllegalKillsCommand;
import xyz.nickelulz.glasshousetweaks.commands.allaccess.*;
import xyz.nickelulz.glasshousetweaks.commands.registeredonly.*;

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
    public static final CheckRegisterCommand CHECK_REGISTER_COMMAND = new CheckRegisterCommand();
    public static final DuelCommand DUEL_COMMAND = new DuelCommand();
    public static final WarCommand WAR_COMMAND = new WarCommand();
    public static final DirectMessageCommand DIRECT_MESSAGE_COMMAND = new DirectMessageCommand();

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
        GlasshouseTweaks.getInstance().getCommand("glasshousetweaks").setExecutor(HELP_COMMAND); // root command
        registerCommand("checkregister", CHECK_REGISTER_COMMAND);
        registerCommand("duel", DUEL_COMMAND);
        registerCommand("dm", DIRECT_MESSAGE_COMMAND);

        // Registered Only
        registerCommand("bounty", BOUNTY_COMMAND);
        registerCommand("contract", CONTRACT_COMMAND);
        registerCommand("removehit", REMOVE_HIT_COMMAND);
        registerCommand("transfer", TRANSFER_MORBIUMS_COMMAND);
        registerCommand("war", WAR_COMMAND);

        // Admin Only
        registerCommand("illegalkills", MANAGE_ILLEGAL_KILLS_COMMAND);

        GlasshouseTweaks.log(Level.INFO, "Registered all commands.");
    }
}
