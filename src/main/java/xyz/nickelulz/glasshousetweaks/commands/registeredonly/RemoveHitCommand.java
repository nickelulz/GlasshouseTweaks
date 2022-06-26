package xyz.nickelulz.glasshousetweaks.commands.registeredonly;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.Contract;
import xyz.nickelulz.glasshousetweaks.datatypes.Hit;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import static xyz.nickelulz.glasshousetweaks.util.Utils.playerEquals;

public class RemoveHitCommand extends CommandBase {
    public RemoveHitCommand() {
        super("removehit", true, "Remove a hit you placed.");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        User user = GlasshouseTweaks.getPlayersDatabase().findByProfile((Player) sender);

        if (user == null) {
            error(sender, ConfigurationConstants.USER_NOT_REGISTERED);
            return true;
        }

        Hit hit = GlasshouseTweaks.getHitsDatabase().findHitByPlacer(user);

        if (hit == null) {
            error(sender, "You have not placed any hits.");
            return true;
        }

        if (GlasshouseTweaks.getHitsDatabase().remove(hit)) {
            success(sender, "Successfully removed hit against target " + hit.getTarget().getProfile().getName());
            if (hit instanceof Contract) {
                ((Contract) hit).getContractor().directMessage("A hit you were a contractor for (placed by " +
                        user.getProfile().getName() + " on " + hit.getTarget().getProfile().getName() + ") was removed by the " +
                        "placer.", ChatColor.DARK_GRAY);
            }
            hit.getTarget().directMessage("A hit placed on you has been removed by the placer!", ChatColor.DARK_GRAY);
        }
        return true;
    }

    @Override
    public String getSyntax() {
        return "/removehit <target>";
    }
}
