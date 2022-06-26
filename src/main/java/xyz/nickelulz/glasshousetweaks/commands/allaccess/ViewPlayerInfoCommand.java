package xyz.nickelulz.glasshousetweaks.commands.allaccess;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;
import xyz.nickelulz.glasshousetweaks.util.Utils;

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

        reply(sender, ChatColor.YELLOW + "-------------------------------------------------");
        reply(sender, "Username: " + ChatColor.WHITE + player.getProfile().getName());
        // for future: add get discord name
        reply(sender, "Discord ID: " + ChatColor.WHITE + player.getDiscordId());
        reply(sender, "Kills: " + ChatColor.WHITE + player.getKills());
        reply(sender, "Deaths: " + ChatColor.WHITE + player.getDeaths());
        reply(sender, "Morbiums: " + ChatColor.WHITE + player.getMorbiums());
        String contractorStatus = "Contractor Status: ",
                targetingStatus = "Targetting Status: ",
                hiringStatus = "Hit Hiring Status: ";

        if (GlasshouseTweaks.getHitsDatabase().isContractor(player))
            contractorStatus += ChatColor.RED + "Active Contractor.";
        else if (player.contractingCooldown() > 0)
            contractorStatus += ChatColor.DARK_GRAY + player.contractingCooldownString() + ".";
        else
            contractorStatus += ChatColor.GREEN + "Available for a contract.";

        reply(sender, contractorStatus);

        if (GlasshouseTweaks.getHitsDatabase().isTarget(player))
            targetingStatus += ChatColor.RED + "Active Target.";
        else if (player.targettingCooldown() > 0)
            targetingStatus += ChatColor.DARK_GRAY + player.targettingCooldownString() + ".";
        else
            targetingStatus += ChatColor.GREEN + "Available to be targetted.";

        reply(sender, targetingStatus);

        if (Utils.playerEquals((Player) sender, player.getProfile())) {
            if (GlasshouseTweaks.getHitsDatabase().isContractor(player))
                hiringStatus += ChatColor.RED + "Has a placed hit.";
            else if (player.hiringCooldown() > 0)
                hiringStatus += ChatColor.GOLD + player.hiringCooldownString() + ".";
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
            reply(sender, ChatColor.YELLOW + "-------------------------------------------------");
        } else {
            reply(sender, ChatColor.YELLOW + "-------------------------------------------------");
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
