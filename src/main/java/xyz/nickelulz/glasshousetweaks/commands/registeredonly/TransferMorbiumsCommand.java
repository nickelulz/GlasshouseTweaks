package xyz.nickelulz.glasshousetweaks.commands.registeredonly;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

public class TransferMorbiumsCommand extends CommandBase {
    public TransferMorbiumsCommand() {
        super("transfer", 2, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        User user = GlasshouseTweaks.getPlayersDatabase().findByProfile((OfflinePlayer) sender);
        User recipient = GlasshouseTweaks.getPlayersDatabase().findByIGN(args[1]);
        int amount = 0;

        if (user == null) {
            error(sender, ConfigurationConstants.USER_NOT_REGISTERED);
            return true;
        }

        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            error(sender, ConfigurationConstants.INVALID_AMOUNT, getSyntax());
        }

        if (recipient == null) {
            error(sender, ConfigurationConstants.PLAYER_NOT_FOUND, getSyntax());
            return true;
        }

        if (amount < 1) {
            error(sender, "You cannot transfer less than 1 morbium.");
            return true;
        }

        if (amount > user.getMorbiums()) {
            error(sender, "You cannot transfer more morbiums than the amount you actually have.");
            return true;
        }

        success(sender, "Sent " + amount + " morbiums to " + recipient.getProfile().getName() + ".");
        user.increment("morbiums", -amount);
        recipient.increment("morbiums", amount);
        return true;
    }

    @Override
    public String getSyntax() {
        return "/transfer <amount> <recipient>";
    }
}
