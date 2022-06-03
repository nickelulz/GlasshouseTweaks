package xyz.nickelulz.glasshousetweaks.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.datatypes.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.PlayerDatabase;

/**
 * Requires the use of a discord id for now, but going to change to
 * verify with the bot later. :)
 */
public class RegisterCommand extends CommandBase {
    public RegisterCommand() {
        super("register", 1, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (PlayerDatabase.containsPlayer(player)) {
            player.sendMessage(ChatColor.RED + "You are already registered!");
            return true;
        }
        String discordId = args[0];
        if (PlayerDatabase.add(new User(discordId, player)))
            player.sendMessage(ChatColor.GREEN + "You are now registered as " + ChatColor.GRAY + discordId);
        else
            player.sendMessage(ChatColor.RED + "Failed to register you! (Contact a server administrator..)");
        return true;
    }

    @Override
    public String getUsage() {
        return "/register <discordId>";
    }
}
