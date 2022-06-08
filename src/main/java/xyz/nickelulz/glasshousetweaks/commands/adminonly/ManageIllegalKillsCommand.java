package xyz.nickelulz.glasshousetweaks.commands.adminonly;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.Attack;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.util.ArrayList;

public class ManageIllegalKillsCommand extends CommandBase {
    public ManageIllegalKillsCommand() {
        super("illegalkills", 1, 2, false, "Manage and deal with any logged illegal kills.");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String mode = args[0];

        if (sender instanceof Player) {
            User user = GlasshouseTweaks.getPlayersDatabase().findByProfile((Player) sender);

            if (user == null) {
                error(sender, ConfigurationConstants.USER_NOT_REGISTERED);
                return true;
            }

            if (!user.getProfile().getPlayer().hasPermission("glasshouse.admin")) {
                error(sender, ConfigurationConstants.USER_NOT_ADMIN);
                return true;
            }
        }

        switch (mode) {
            case "list":
            {
                if (args.length != 1) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                ArrayList<Attack> illegalAttacks = GlasshouseTweaks.getIllegalkillDatabase().getDataset();
                if (illegalAttacks.size() == 0)
                    reply(sender, "There are no pending illegal kills.");
                else {
                    reply(sender, "Pending Illegal Kills:");
                    for (int i = 0; i < illegalAttacks.size(); i++)
                        reply(sender, (i+1) + ": " + illegalAttacks.get(i).toString());
                }
                return true;
            }

            case "remove":
            {
                if (args.length != 3) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                Attack illegalAttack = GlasshouseTweaks.getIllegalkillDatabase().find(args[1], args[2]);

                if (illegalAttack == null) {
                    error(sender, "There was no illegal attack found with " + args[1] + " as " +
                            "the victim and " + args[2] + " as the attacker.");
                    return true;
                }

                GlasshouseTweaks.getIllegalkillDatabase().remove(illegalAttack);
                reply(sender, "Removed illegal attack by " + args[1] + " on " + args[2] + ".");

                return true;
            }

            default:
                sendSyntax(sender);
                return true;
        }
    }

    @Override
    public String getSyntax() {
        return "/illegalkills <list/remove>";
    }

    public void sendSpecializedSyntax(CommandSender sender, String mode) {
        sender.sendMessage(ChatColor.GRAY + getSpecializedSyntax(mode));
    }

    public String getSpecializedSyntax(String mode) {
        switch (mode) {
            case "list":
                return "/illegalkills list";

            case "remove":
                return "/illegalkills remove <attacker> <victim>";

            default:
                return getSyntax();
        }
    }
}
