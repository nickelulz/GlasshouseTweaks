package xyz.nickelulz.glasshousetweaks.commands.allaccess;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

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
        if (GlasshouseTweaks.getPlayersDatabase().containsPlayer(player)) {
            player.sendMessage(ChatColor.RED + "You are already registered!");
            return true;
        }
        String discordId = args[0];
        if (GlasshouseTweaks.getPlayersDatabase().add(new User(discordId, player)))
            player.sendMessage(ChatColor.GREEN + "You are now registered as " + ChatColor.GRAY + discordId + ChatColor.GREEN + ".");
        else
            player.sendMessage(ChatColor.RED + "Failed to register you! (Contact a server administrator..)");
        return true;
    }

    @Override
    public String getSyntax() {
        return "/register <discordId>";
    }
}
