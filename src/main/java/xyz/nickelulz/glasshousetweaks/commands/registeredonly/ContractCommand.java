package xyz.nickelulz.glasshousetweaks.commands.registeredonly;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.Contract;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ContractCommand extends CommandBase {

    public ContractCommand() {
        super("contract", 1, 4, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        User user = GlasshouseTweaks.getPlayersDatabase().findByProfile((Player) sender);
        String mode = args[0];

        if (user == null) {
            error(sender, ConfigurationConstants.USER_NOT_REGISTERED);
            return true;
        }

        switch (mode) {
            case "place":
            {
                if (args.length != 4) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                User target = GlasshouseTweaks.getPlayersDatabase().findByIGN(args[1]);
                User contractor = GlasshouseTweaks.getPlayersDatabase().findByIGN(args[3]);
                int price;

                if (target == null) {
                    error(sender, ConfigurationConstants.TARGET_NOT_FOUND, getSpecializedSyntax(mode));
                    return true;
                }

                try {
                    price = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    error(sender, ConfigurationConstants.INVALID_PRICE, getSpecializedSyntax(mode));
                    return true;
                }

                if (price < ConfigurationConstants.MINIMUM_HIT_PRICE) {
                    error(sender, ConfigurationConstants.PRICE_TOO_LOW);
                    return true;
                }

                if (contractor == null) {
                    error(sender, ConfigurationConstants.CONTRACTOR_NOT_FOUND, getSpecializedSyntax(mode));
                    return true;
                }

                if (GlasshouseTweaks.getHitsDatabase().isPlacer(user)) {
                    error(sender, ConfigurationConstants.TOO_MANY_HITS);
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

                GlasshouseTweaks.getHitsDatabase().add(new Contract(user, target, price, LocalDateTime.now(), contractor, true));
                reply(sender, String.format("Placed new contract on %s with %s as the contractor for %d diamonds.",
                        target.getProfile().getName(), contractor.getProfile().getName(), price));
                return true;
            }

            case "accept":
            case "deny":
            {
                if (args.length != 3) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                User target = GlasshouseTweaks.getPlayersDatabase().findByIGN(args[1]);
                User hirer = GlasshouseTweaks.getPlayersDatabase().findByIGN(args[2]);

                if (target == null) {
                    error(sender, ConfigurationConstants.TARGET_NOT_FOUND, getSpecializedSyntax(mode));
                    return true;
                }

                if (hirer == null) {
                    error(sender, ConfigurationConstants.HIRER_NOT_FOUND, getSpecializedSyntax(mode));
                    return true;
                }

                Contract contract = GlasshouseTweaks.getHitsDatabase().findContract(target, user);

                if (contract == null) {
                    error(sender, "Could not find contract against " + target.getProfile().getName() + " with" +
                            " you as the contractor", getSpecializedSyntax(mode));
                    return true;
                }

                switch (mode) {
                    case "accept":
                    {
                        contract.setPending(false);
                        // DM placer
                        reply(sender, "Accepted contract from " + contract.getPlacer().getProfile().getName() + " for" +
                                " " + contract.getPrice() + " diamonds.");
                        return true;
                    }

                    case "deny":
                    {
                        GlasshouseTweaks.getHitsDatabase().remove(contract);
                        // DM placer
                        reply(sender, "Denied contract from " + contract.getPlacer().getProfile().getName() + " on " +
                                contract.getTarget().getProfile().getName() + "for " + contract.getPrice() + " diamonds.");
                        return true;
                    }
                }

                break;
            }

            case "view":
            {
                if (args.length != 1) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                ArrayList<Contract> userContracts = GlasshouseTweaks.getHitsDatabase().getContracts(user);
                Contract active = null;

                for (Contract c: userContracts)
                    if (!c.isPending()) {
                        userContracts.remove(c);
                        active = c;
                    }

                reply(sender, "===== CONTRACTS: =====");

                if (active == null)
                    reply(sender, "You have no active contract.");
                else {
                    reply(sender, "Active Contract:");
                    reply(sender, ChatColor.GREEN + active.toSimpleString() + ".");
                }

                if (userContracts.size() == 0)
                    reply(sender, "You have no pending contracts.");
                else {
                    reply(sender, "\nPending Contracts:");
                    for (int i = 0; i < userContracts.size(); i++)
                        reply(sender, (i+1) + ": " + userContracts.get(i).toSimpleString() + ".");
                }

                reply(sender, "======================");

                break;
            }

            default:
                sendSyntax(sender);
        }

        return true;
    }

    @Override
    public String getSyntax() {
        return "/contract <place/view/accept/deny>";
    }

    public void sendSpecializedSyntax(CommandSender sender, String mode) {
        sender.sendMessage(ChatColor.GRAY + getSpecializedSyntax(mode));
    }

    public String getSpecializedSyntax(String mode) {
        switch (mode) {
            case "place":
                return "/contract place <target> <price> <contractor>";

            case "accept":
            case "deny":
                return "/contract accept <target> <hirer>";

            default:
                return getSyntax();
        }
    }
}
