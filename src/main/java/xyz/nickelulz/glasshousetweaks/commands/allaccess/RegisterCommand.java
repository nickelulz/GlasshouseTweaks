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
        super("register", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (GlasshouseTweaks.getPlayersDatabase().containsPlayer(player)) {
            error(sender, "You are already registered!");
            return true;
        }
        // using a placeholder
        if (GlasshouseTweaks.getPlayersDatabase().add(new User("placeholder", player)))
            success(sender, "You are now registered with the plugin and discord bot. You can now use commands to " +
                    "place hits, and have hits placed on you.");
        else
            error(sender, "Failed to register you! (Contact a server administrator..)");
        return true;
    }

    @Override
    public String getSyntax() {
        return "/register";
    }
}
