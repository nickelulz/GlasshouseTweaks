package xyz.nickelulz.glasshousetweaks.commands.allaccess;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

public class ViewPlayerInfoCommand extends CommandBase {

    public ViewPlayerInfoCommand() {
        super("playerinfo", 1, true, "Lookup the info of a specific player.");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        User player = GlasshouseTweaks.getPlayersDatabase().findByIGN(args[0]);

        if (player == null) {
            error(sender, ConfigurationConstants.PLAYER_NOT_FOUND);
            return true;
        }

        reply(sender, "============================================");
        reply(sender, "Username: " + ChatColor.GREEN + player.getProfile().getName());
        // for future: add get discord name
        reply(sender, "Discord ID: " + ChatColor.GREEN + player.getDiscordId());
        reply(sender, "Kills: " + ChatColor.GREEN + player.getKills());
        reply(sender, "Deaths: " + ChatColor.GREEN + player.getDeaths() + ".");
        reply(sender, "Morbiums: " + ChatColor.GREEN + player.getMorbiums() + ".");
        String contractorStatus = "Contractor Status: ",
                targetingStatus = "Targetting Status: ",
                hiringStatus = "Hit Hiring Status: ";

        if (GlasshouseTweaks.getHitsDatabase().isContractor(player))
            contractorStatus += ChatColor.RED + "Active Contractor.";
        else if (player.contractingCooldown() > 0)
            contractorStatus += ChatColor.DARK_GRAY + player.contractingCooldownString() + ".";
        else
            contractorStatus += ChatColor.GREEN + "Available.";

        reply(sender, contractorStatus);

        if (GlasshouseTweaks.getHitsDatabase().isTarget(player))
            targetingStatus += ChatColor.RED + "Active Target.";
        else if (player.targettingCooldown() > 0)
            targetingStatus += ChatColor.DARK_GRAY + player.targettingCooldownString() + ".";
        else
            targetingStatus += ChatColor.GREEN + "Available to be targetted.";

        reply(sender, targetingStatus);

        if (sender.equals(player.getProfile())) {
            if (GlasshouseTweaks.getHitsDatabase().isContractor(player))
                hiringStatus += ChatColor.RED + "Has a placed hit.";
            else if (player.hiringCooldown() > 0)
                hiringStatus += ChatColor.DARK_GRAY + player.hiringCooldownString() + ".";
            else
                hiringStatus += ChatColor.GREEN + "Ready to place a hit.";

            reply(sender, hiringStatus);

            reply(sender, "");

            if (GlasshouseTweaks.getHitsDatabase().isActivePlacer(player))
                reply(sender, "\nYou placed: " + ChatColor.YELLOW +
                        GlasshouseTweaks.getHitsDatabase().findHitByPlacer(player).toSimpleBountyString() + ".");
            else
                reply(sender, "You have no placed hits currently.");

            if (GlasshouseTweaks.getHitsDatabase().isContractor(player))
                reply(sender, "You are the contractor for: " + ChatColor.YELLOW +
                        GlasshouseTweaks.getHitsDatabase().findActiveContract(player).toSimpleString() + ".");
            else
                reply(sender, "You are not the contractor for any hits currently.");
            reply(sender, "============================================");
        } else {
            reply(sender, "============================================");
        }
        return true;
    }

    @Override
    public void reply(CommandSender sender, String... messages) {
        for (String message: messages)
            sender.sendMessage(ChatColor.GRAY + message);
    }

    @Override
    public String getSyntax() {
        return "/playerinfo <playername>";
    }
}
