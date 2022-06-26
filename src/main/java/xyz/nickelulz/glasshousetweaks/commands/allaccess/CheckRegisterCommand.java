package xyz.nickelulz.glasshousetweaks.commands.allaccess;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.databases.PlayerDatabase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

public class CheckRegisterCommand extends CommandBase {
    public CheckRegisterCommand() {
        super("checkregister", true, "Check if you are registered.");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User found = GlasshouseTweaks.getPlayersDatabase().findByProfile(p);
        if (found != null)
            reply(sender, ChatColor.GREEN + "You are registered! Found you as " + found.getProfile().getName());
        else
            reply(sender, ChatColor.RED + "You are not registered! Could not find you in the database.");
        return true;
    }

    @Override
    public String getSyntax() {
        return null;
    }
}
