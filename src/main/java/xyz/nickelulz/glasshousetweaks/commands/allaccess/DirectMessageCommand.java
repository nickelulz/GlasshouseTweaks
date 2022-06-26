package xyz.nickelulz.glasshousetweaks.commands.allaccess;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

public class DirectMessageCommand extends CommandBase {
    public DirectMessageCommand() {
        super("dm", false, "Direct message another player");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        User target = GlasshouseTweaks.getPlayersDatabase().findByIGN(args[0]);
        if (target == null) {
            error(sender, ConfigurationConstants.TARGET_NOT_FOUND);
            return true;
        }

        String message = "";
        for (int i = 0; i < args.length; i++)
            message += args[i] + " ";
        target.directMessage("Recieved direct message from " + ((Player) sender).getName() + ": " + message,
                ChatColor.GRAY);
        return true;
    }

    @Override
    public String getSyntax() {
        return "/dm <player> <message>";
    }
}
