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
    private final String description;

    public CommandBase(String command, String description) {
        this(command, 0, description);
    }

    public CommandBase(String command, boolean playerOnly, String description) {
        this(command, 0, playerOnly, description);
    }

    public CommandBase(String command, int requiredArguments, String description) {
        this(command, requiredArguments, requiredArguments, description);
    }

    public CommandBase(String command, int minArguments, int maxArguments, String description) {
        this(command, minArguments, maxArguments, false, description);
    }

    public CommandBase(String command, int requiredArguments, boolean playerOnly, String description) {
        this(command, requiredArguments, requiredArguments, playerOnly, description);
    }

    public CommandBase(String command, int minArguments, int maxArguments, boolean playerOnly, String description) {
        super(command);

        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
        this.playerOnly = playerOnly;
        this.description = description;
    }

    public CommandBase enableDelay(int delay) {
        this.delay = delay;
        this.delayedPlayers = new ArrayList<>();
        return this;
    }

    public String getDescription() {
        return description;
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

    public void error(CommandSender sender, String err, String syntax) {
        sender.sendMessage(ChatColor.RED + err);
        sender.sendMessage(ChatColor.GRAY + syntax);
    }

    public void error(CommandSender sender, String err) {
        sender.sendMessage(ChatColor.RED + err);
    }

    public void reply(CommandSender sender, String... messages) {
        for (String message: messages)
            sender.sendMessage(ChatColor.WHITE + message);
    }

    public void success(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GREEN + message);
    }
}
