package xyz.nickelulz.glasshousetweaks.commands.allaccess;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

public class ViewPlayerInfoCommand extends CommandBase {

    public ViewPlayerInfoCommand() {
        super("playerinfo", 1, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        User player = GlasshouseTweaks.getPlayersDatabase().findByIGN(args[0]);

        if (player == null) {
            error(sender, ConfigurationConstants.PLAYER_NOT_FOUND);
            return true;
        }

        reply(sender, "Username: " + player.getProfile().getName());
        // for future: add get discord name
        reply(sender, "Discord ID: " + player.getProfile().getName());
        reply(sender, "Kills: " + player.getKills());
        reply(sender, "Deaths: " + player.getDeaths() + ".");
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
            targetingStatus += ChatColor.RED + "Active Contractor.";
        else if (player.targettingCooldown() > 0)
            targetingStatus += ChatColor.DARK_GRAY + player.targettingCooldownString() + ".";
        else
            targetingStatus += ChatColor.GREEN + "Available to be targetted.";

        reply(sender, targetingStatus);

        if (GlasshouseTweaks.getHitsDatabase().isContractor(player))
            hiringStatus += ChatColor.RED + "Has a placed hit.";
        else if (player.hiringCooldown() > 0)
            hiringStatus += ChatColor.DARK_GRAY + player.hiringCooldownString() + ".";
        else
            hiringStatus += ChatColor.GREEN + "Ready to place a hit.";

        reply(sender, hiringStatus);
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
