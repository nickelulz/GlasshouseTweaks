package xyz.nickelulz.glasshousetweaks.commands.registeredonly;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.Hit;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;
import xyz.nickelulz.glasshousetweaks.database.HitDatabase;
import xyz.nickelulz.glasshousetweaks.database.PlayerDatabase;

public class RemoveHitCommand extends CommandBase {
    public RemoveHitCommand() {
        super("removehit", 1, 2, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        User user = PlayerDatabase.findByProfile((Player) sender);
        User target = PlayerDatabase.findByIGN(args[0]);
        if (user == null) {
            error(sender, ConfigurationConstants.USER_NOT_REGISTERED);
            return true;
        }

        if (target == null) {
            error(sender, ConfigurationConstants.TARGET_NOT_FOUND);
            return true;
        }

        Hit hit = HitDatabase.findHitByTarget(target);

        if (hit == null) {
            error(sender, "Could not find any hits with " + target.getProfile().getName() + " as the target.", getSyntax());
            return true;
        }

        if (!hit.getPlacer().equals(user)) {
            error(sender,"You are not the player that placed this hit.");
            return true;
        }

        if (HitDatabase.remove(hit))
            error(sender,"Successfully removed hit against target " + target.getProfile().getName());
        return true;
    }

    @Override
    public String getSyntax() {
        return "/removehit <target>";
    }
}
