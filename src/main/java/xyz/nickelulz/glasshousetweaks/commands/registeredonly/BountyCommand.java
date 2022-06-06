package xyz.nickelulz.glasshousetweaks.commands.registeredonly;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.Bounty;
import xyz.nickelulz.glasshousetweaks.datatypes.Hit;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;
import xyz.nickelulz.glasshousetweaks.database.HitDatabase;
import xyz.nickelulz.glasshousetweaks.database.PlayerDatabase;

import java.time.LocalDateTime;

public class BountyCommand extends CommandBase {
    public BountyCommand() {
        super("bounty", 1, 3, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String mode = args[0];
        switch (mode) {
            case "place":
            {
                User user = PlayerDatabase.findByProfile((Player) sender);
                User target = PlayerDatabase.findByIGN(args[2]);
                int price = 0;

                if (user == null) {
                    error(sender, ConfigurationConstants.USER_NOT_REGISTERED);
                    return true;
                }

                if (target == null) {
                    error(sender, ConfigurationConstants.TARGET_NOT_FOUND, getSpecializedSyntax(mode));
                    return true;
                }

                try {
                    price = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    error(sender, ConfigurationConstants.INVALID_PRICE, getSpecializedSyntax(mode));
                    return true;
                }

                if (price < ConfigurationConstants.MINIMUM_HIT_PRICE) {
                    error(sender, ConfigurationConstants.PRICE_TOO_LOW);
                    return true;
                }

                if (HitDatabase.isTarget(target)) {
                    error(sender, ConfigurationConstants.TARGET_IS_BUSY);
                    return true;
                }

                if (HitDatabase.isActivePlacer(user)) {
                    error(sender, ConfigurationConstants.HIRER_BUSY);
                    return true;
                }

                if (user.hiringCooldown() > 0) {
                    error(sender, ConfigurationConstants.HIRER_UNDER_COOLDOWN);
                    reply(sender, "Cooldown time left: " + user.hiringCooldownString());
                    return true;
                }

                if (target.targettingCooldown() > 0) {
                    error(sender, ConfigurationConstants.TARGET_UNDER_COOLDOWN);
                    reply(sender, "Target cooldown time left: " + target.targettingCooldownString());
                    return true;
                }

                if (!HitDatabase.add(new Bounty(user, target, price, LocalDateTime.now()))) {
                    error(sender, "Could not place this hit. :(");
                    return true;
                }

                reply(sender, "Successfully placed new bounty on " + target.getProfile().getName() +
                        " for " + price + "diamonds.");
                return true;
            }

            case "list":
            {
                reply(sender, "===== BOUNTIES =====");
                int index = 1;
                for (Hit h : HitDatabase.getHits())
                    if (h instanceof Bounty)
                        reply(sender, index++ + ": " + h.toSimpleString());
                reply(sender, "====================");
                return true;
            }

            default:
                sendSyntax(sender);
                return true;
        }
    }

    @Override
    public String getSyntax() {
        return "/bounty <place/list>";
    }

    public String getSpecializedSyntax(String mode) {
        switch (mode) {
            case "place":
                return "/bounty place <price> <target>";


            case "list":
                return "/bounty list";

            default:
                return getSyntax();
        }
    }
}
