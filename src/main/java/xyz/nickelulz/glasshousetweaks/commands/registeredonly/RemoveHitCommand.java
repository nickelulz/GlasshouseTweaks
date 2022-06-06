package xyz.nickelulz.glasshousetweaks.commands.registeredonly;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.Hit;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

public class RemoveHitCommand extends CommandBase {
    public RemoveHitCommand() {
        super("removehit", 1, 2, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        User user = GlasshouseTweaks.getPlayersDatabase().findByProfile((Player) sender);
        User target = GlasshouseTweaks.getPlayersDatabase().findByIGN(args[0]);
        if (user == null) {
            error(sender, ConfigurationConstants.USER_NOT_REGISTERED);
            return true;
        }

        if (target == null) {
            error(sender, ConfigurationConstants.TARGET_NOT_FOUND);
            return true;
        }

        Hit hit = GlasshouseTweaks.getHitsDatabase().findHitByTarget(target);

        if (hit == null) {
            error(sender, "Could not find any hits with " + target.getProfile().getName() + " as the target.", getSyntax());
            return true;
        }

        if (!hit.getPlacer().equals(user)) {
            error(sender,"You are not the player that placed this hit.");
            return true;
        }

        if (GlasshouseTweaks.getHitsDatabase().remove(hit))
            error(sender,"Successfully removed hit against target " + target.getProfile().getName());
        return true;
    }

    @Override
    public String getSyntax() {
        return "/removehit <target>";
    }
}
