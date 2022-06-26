package xyz.nickelulz.glasshousetweaks.commands.allaccess;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.Bounty;
import xyz.nickelulz.glasshousetweaks.datatypes.Duel;
import xyz.nickelulz.glasshousetweaks.datatypes.Hit;
import xyz.nickelulz.glasshousetweaks.util.Utils;

import java.util.ArrayList;

public class DuelCommand extends CommandBase {

    public DuelCommand() {
        super("duel", 1, 2, true, "Place a duel, Accept a duel, or Deny a duel.");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        String mode = args[0];
        switch (mode) {
            case "request":
            {
                if (args.length != 2) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (GlasshouseTweaks.getDuelsDatabase().hasActiveDuel(player)) {
                    error(sender, "You already have an active duel!");
                    return true;
                }

                if (GlasshouseTweaks.getDuelsDatabase().hasActiveDuel(target)) {
                    error(sender, "The player you selected already has an active duel!");
                    return true;
                }

                if (Utils.playerEquals(player, target)) {
                    error(sender, "You cannot duel yourself.");
                    return true;
                }

                if (!GlasshouseTweaks.getDuelsDatabase().add(new Duel(player, target))) {
                    error(sender, "You already have an active duel request for this player!");
                    return true;
                }

                target.sendMessage(ChatColor.GREEN + player.getName() + " wants to duel you! Use /duel accept/deny to " +
                    "respond!");
                success(sender, "Sent duel request to " + target.getName());
                return true;
            }

            case "accept":
            case "deny":
            {
                if (args.length != 2) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                Player initiator = Bukkit.getPlayer(args[1]);
                Duel duel = GlasshouseTweaks.getDuelsDatabase().findPendingDuel(player, initiator);
                if (duel == null) {
                    error(sender, "Could not find a duel from this user.");
                    return true;
                }

                switch (mode) {
                    case "accept":
                        if (duel.timeLeft() > 0) {
                            duel.accept();
                            success(sender, "Accepted duel from " + initiator.getName());
                            initiator.sendMessage(ChatColor.GREEN + player.getName() + " has " +
                                    "accepted your duel request!");
                        }
                        else
                            error(sender, "This duel request has expired.");
                        return true;

                    case "deny":
                        GlasshouseTweaks.getDuelsDatabase().remove(duel);
                        success(sender, "Denied duel from " + initiator.getName());
                        initiator.sendMessage(ChatColor.RED + player.getName() + " has " +
                                "denied your duel request!");
                        return true;
                }
                return true;
            }

            case "forfeit":
            {
                if (args.length != 1) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                Duel found = GlasshouseTweaks.getDuelsDatabase().findActiveDuel(player);
                OfflinePlayer otherPlayer = found.getOtherFighter(player);
                if (found == null) {
                    error(player, "You have no active duel!");
                    return false;
                }

                GlasshouseTweaks.getDuelsDatabase().remove(found);
                reply(sender, ChatColor.YELLOW + "Forfeitted duel with " + otherPlayer.getName());
                if (otherPlayer.isOnline())
                    otherPlayer.getPlayer().sendMessage(ChatColor.YELLOW + "" + player.getName() + " has forfeitted " +
                            "your duel.");
                return true;
            }

            case "view":
            {
                if (GlasshouseTweaks.getDuelsDatabase().hasActiveDuel(player)) {
                    Duel active = GlasshouseTweaks.getDuelsDatabase().findActiveDuel(player);
                    OfflinePlayer other = active.getOtherFighter(player);
                    if (other.getName() != null)
                        reply(sender, ChatColor.WHITE + "Active duel: " + other.getName());
                    else
                        reply(sender, ChatColor.YELLOW + "You currently have an active duel.");
                }
                reply(sender, ChatColor.YELLOW + "--------[" + ChatColor.WHITE + " PENDING DUELS " +
                        ChatColor.YELLOW + "]--------");
                int index = 1;
                ArrayList<Duel> dataset = GlasshouseTweaks.getDuelsDatabase().getDataset();
                for (int i = 0; i < dataset.size(); i++)
                    if (dataset.get(i).hasPlayer(player))
                        reply(sender, index + ": " + dataset.get(i).getOtherFighter(player).getName());
                reply(sender, ChatColor.YELLOW + "-------------------------------");
                return true;
            }

            default:
                error(sender, "The mode you select has to be 'request', 'forfeit', 'accept', or 'deny'");
                return true;
        }
    }

    @Override
    public String getSyntax() {
        return "/duel <request/forfeit/accept/deny/view>";
    }

    public void sendSpecializedSyntax(CommandSender sender, String mode) {
        error(sender, getSpecializedSyntax(mode));
    }

    public String getSpecializedSyntax(String mode) {
        switch (mode) {
            case "request":
                return "/duel request <playername>";
            case "forfeit":
                return "/duel forfeit <all>";
            case "accept":
                return "/duel accept <playername>";
            case "deny":
                return "/duel deny <playername>";
            case "view":
                return "/duel view";
            default:
                return getSyntax();
        }
    }
}
