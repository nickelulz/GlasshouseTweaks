package xyz.nickelulz.glasshousetweaks.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;

import java.util.ArrayList;
import java.util.List;


/**
 * Credit:
 * https://www.youtube.com/watch?v=2jPrzfx3PMU
 * Worn Off Keys, 2021
 */
public abstract class CommandBase extends BukkitCommand implements CommandExecutor {
    private List<String> delayedPlayers;
    private int delay = 0;
    private final int minArguments;
    private final int maxArguments;
    private final boolean playerOnly;

    public CommandBase(String command) {
        this(command, 0);
    }

    public CommandBase(String command, boolean playerOnly) {
        this(command, 0, playerOnly);
    }

    public CommandBase(String command, int requiredArguments) {
        this(command, requiredArguments, requiredArguments);
    }

    public CommandBase(String command, int minArguments, int maxArguments) {
        this(command, minArguments, maxArguments, false);
    }

    public CommandBase(String command, int requiredArguments, boolean playerOnly) {
        this(command, requiredArguments, requiredArguments, playerOnly);
    }

    public CommandBase(String command, int minArguments, int maxArguments, boolean playerOnly) {
        super(command);

        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
        this.playerOnly = playerOnly;
    }

    public CommandBase enableDelay(int delay) {
        this.delay = delay;
        this.delayedPlayers = new ArrayList<>();
        return this;
    }

    public void removePlayer(Player player) {
        this.delayedPlayers.remove(player.getName());
    }

    public void sendSyntax(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + getSyntax());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Mismatched argument length
        if (args.length < minArguments || (args.length > maxArguments && maxArguments != -1)) {
            sendSyntax(sender);
            return true;
        }

        // Sender is not player
        if (playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.GRAY + "Only plays can use this command.");
            return true;
        }

        // Player does not have permissions
        String permission = getPermission();
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Player is delayed
        if (delayedPlayers != null && sender instanceof Player) {
            Player player = (Player) sender;
            if (delayedPlayers.contains(player.getName())) {
                player.sendMessage(ChatColor.GRAY + "Please wait before using this command again.");
                return true;
            }
            // Player is not delayed, but delay is active.
            delayedPlayers.add(player.getName());
            Bukkit.getScheduler().scheduleSyncDelayedTask(GlasshouseTweaks.getInstance(), () -> {
                delayedPlayers.remove(player.getName());
            }, 20L * delay);
        }

        if (!onCommand(sender, args)) {
            sendSyntax(sender);
        }

        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.onCommand(sender, args);
        return false;
    }

    public abstract boolean onCommand(CommandSender sender, String[] args);

    public abstract String getSyntax();

    public void error(CommandSender sender, String... errorMessages) {
        if (errorMessages.length > 1) {
            for (int i = 0; i < errorMessages.length-1; i++) // Last message is reserved for syntax
                sender.sendMessage(ChatColor.RED + errorMessages[i]);
            sender.sendMessage(ChatColor.GRAY + errorMessages[errorMessages.length-1]);
        }
        else
            // no syntax
            for (String errorMessage: errorMessages)
                sender.sendMessage(ChatColor.RED + errorMessage);
    }

    public void reply(CommandSender sender, String... messages) {
        for (String message: messages)
            sender.sendMessage(ChatColor.GREEN + message);
    }
}
