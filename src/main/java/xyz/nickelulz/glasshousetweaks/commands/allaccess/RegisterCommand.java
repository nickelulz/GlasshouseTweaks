package xyz.nickelulz.glasshousetweaks.commands.allaccess;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.DiscordClientManager;

import java.util.logging.Level;

/**
 * Requires the use of a discord id for now, but going to change to
 * verify with the bot later. :)
 */
public class RegisterCommand extends CommandBase {
    public RegisterCommand() {
        super("register", 1, true, "Register with the plugin and the discord bot.");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (GlasshouseTweaks.getPlayersDatabase().containsPlayer(player)) {
            error(sender, "You are already registered!");
            return true;
        }

        String discordName = DiscordClientManager.registerRequest(args[0]);
        if (discordName == null) {
            error(sender, "Could not register you, as the name you input could not be found. Make sure to check your " +
                    "spelling and capitalization, and remember that the discord format is name#0000", getSyntax());
            return true;
        }

        if (GlasshouseTweaks.getPlayersDatabase().add(new User(discordName, player)))
            success(sender, "You are now registered with the plugin and discord bot. You can now use commands to " +
                    "place hits, and have hits placed on you.");
        else
            error(sender, "Failed to register you! (Contact a server administrator..)");
        return true;
    }

    @Override
    public String getSyntax() {
        return "/register <discordname> (Replace any spaces with double asterisks)";
    }
}
