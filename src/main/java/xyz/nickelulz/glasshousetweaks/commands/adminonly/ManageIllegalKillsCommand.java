package xyz.nickelulz.glasshousetweaks.commands.adminonly;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.Attack;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;
import xyz.nickelulz.glasshousetweaks.database.HitDatabase;
import xyz.nickelulz.glasshousetweaks.database.PlayerDatabase;

import java.util.ArrayList;

public class ManageIllegalKillsCommand extends CommandBase {
    public ManageIllegalKillsCommand() {
        super("illegalkills", 1, 2, false);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String mode = args[0];

        if (sender instanceof Player) {
            User user = PlayerDatabase.findByProfile((Player) sender);

            if (user == null) {
                error(sender, ConfigurationConstants.USER_NOT_REGISTERED);
                return true;
            }

            if (!user.getProfile().hasPermission("glasshouse.admin")) {
                error(sender, ConfigurationConstants.USER_NOT_ADMIN);
                return true;
            }
        }

        switch (mode) {
            case "list":
            {
                ArrayList<Attack> illegalAttacks = HitDatabase.getIllegalAttacks();
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
                User attacker = PlayerDatabase.findByIGN(args[1]);
                User victim = PlayerDatabase.findByIGN(args[2]);

                if (attacker == null) {
                    error(sender, "Attacker not found in registry.", getSpecializedSyntax(mode));
                    return true;
                }

                if (victim == null) {
                    error(sender, "Victim not found in registry.", getSpecializedSyntax(mode));
                    return true;
                }

                Attack illegalAttack = HitDatabase.findIllegalAttack(attacker, victim);

                if (illegalAttack == null) {
                    error(sender, "There was no illegal attack found with " + victim.getProfile().getName() + " as " +
                            "the victim and " + attacker.getProfile().getName() + " as the attacker.");
                    return true;
                }

                HitDatabase.removeIllegalAttack(illegalAttack);
                reply(sender, "Removed illegal attack by " + attacker.getProfile().getName() + " on " + victim.getProfile().getName() + ".");

                return true;
            }

            default:
                sendSyntax(sender);
                return true;
        }
    }

    @Override
    public String getSyntax() {
        return "/illegalkills <list/revoke/accept>";
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
