package xyz.nickelulz.glasshousetweaks.commands.registeredonly;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.Bounty;
import xyz.nickelulz.glasshousetweaks.datatypes.Hit;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BountyCommand extends CommandBase {
    public BountyCommand() {
        super("bounty", 1, 3, true, "Place a bounty or list a bounty.");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String mode = args[0];
        switch (mode) {
            case "place":
            {
                if (args.length != 3) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                User user = GlasshouseTweaks.getPlayersDatabase().findByProfile((Player) sender);
                User target = GlasshouseTweaks.getPlayersDatabase().findByIGN(args[2]);
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
                    error(sender, ConfigurationConstants.INVALID_AMOUNT, getSpecializedSyntax(mode));
                    return true;
                }

                if (price < ConfigurationConstants.MINIMUM_HIT_PRICE) {
                    error(sender, ConfigurationConstants.PRICE_TOO_LOW);
                    return true;
                }

                if (price > ConfigurationConstants.MAXIMUM_HIT_PRICE) {
                    error(sender, ConfigurationConstants.PRICE_TOO_HIGH);
                    return true;
                }

                if (GlasshouseTweaks.getHitsDatabase().isActivePlacer(user)) {
                    error(sender, ConfigurationConstants.TOO_MANY_HITS);
                    return true;
                }

                if (user.equals(target)) {
                    error(sender, ConfigurationConstants.HIRER_IS_TARGET);
                    return true;
                }

                if (GlasshouseTweaks.getHitsDatabase().isTarget(target)) {
                    error(sender, ConfigurationConstants.TARGET_IS_BUSY);
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

                if (!GlasshouseTweaks.getHitsDatabase().add(new Bounty(user, target, price, LocalDateTime.now()))) {
                    error(sender, "Could not place this hit. :(");
                    return true;
                }

                success(sender, "Successfully placed new bounty on " + target.getProfile().getName() +
                        " for " + price + " diamonds.");
                target.directMessage(ConfigurationConstants.TARGET_WARNING, ChatColor.RED);
                return true;
            }

            case "list":
            {
                if (args.length != 1) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                ArrayList<Bounty> bounties = GlasshouseTweaks.getHitsDatabase().getBounties();
                if (bounties.isEmpty()) {
                    reply(sender, "There are no bounties currently placed.");
                    return true;
                }
                else {
                    reply(sender, "===== BOUNTIES =====");
                    int index = 1;
                    for (Hit h : GlasshouseTweaks.getHitsDatabase().getActiveHits())
                        if (h instanceof Bounty)
                            reply(sender, index++ + ": " + h.toSimpleString() + ".");
                    reply(sender, "===================");
                    return true;
                }
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

    public void sendSpecializedSyntax(CommandSender sender, String mode) {
        sender.sendMessage(ChatColor.GRAY + getSpecializedSyntax(mode));
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
