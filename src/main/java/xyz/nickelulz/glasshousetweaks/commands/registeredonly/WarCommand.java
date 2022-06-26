package xyz.nickelulz.glasshousetweaks.commands.registeredonly;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.datatypes.War;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.time.LocalDateTime;

public class WarCommand extends CommandBase {
    public WarCommand() {
        super("war", 1, 3, true, "The blanket command dealing with wars.");
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
            case "declare":
            {
                if (args.length != 2) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                User target = GlasshouseTweaks.getPlayersDatabase().findByIGN(args[1]);
                if (target == null) {
                    error(sender, ConfigurationConstants.TARGET_NOT_FOUND);
                    return true;
                }

                if (user.commanderCooldown() > 0) {
                    error(sender, "You are still under a cooldown! " + ChatColor.GRAY + user.commanderCooldownString() +
                            ChatColor.RED + " left.");
                    return true;
                }

                if (GlasshouseTweaks.getWarsDatabase().currentlyEnlisted(target)) {
                    error(sender, "The target you have selected is already currently in a war!");
                    return true;
                }

                if (target.commanderCooldown() > 0) {
                    error(sender, "The target you have selected is still under a cooldown! " +
                            ChatColor.GRAY + target.commanderCooldownString() + ChatColor.RED + " left.");
                    return true;
                }

                GlasshouseTweaks.getWarsDatabase().add(new War(user, target, LocalDateTime.now()));
                success(sender, "Officially declared war against " + target.getProfile().getName() + "!");
                target.directMessage("War has officially been declared on you by " +
                        user.getProfile().getName() + "!", ChatColor.RED);
                GlasshouseTweaks.broadcast(ChatColor.RED + user.getProfile().getName().toUpperCase() + " HAS " +
                        "OFFICIALLY DECLARED WAR ON " + target.getProfile().getName().toUpperCase() + "!");

                return true;
            }

            case "unvote":
            {
                if (args.length != 1) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                War found = GlasshouseTweaks.getWarsDatabase().findWarByParticipant(user);
                if (found == null) {
                    error(sender, "You are not currently in any wars.");
                    return true;
                }

                if (!( user.equals(found.getAttackingCommander()) || user.equals(found.getDefendingCommander()) )) {
                    error(sender, "You are not a commander of this war!");
                    return true;
                }

                if (user.equals(found.getAttackingCommander())) {
                    if (!found.attacking_commander_vote()) {
                        error(sender, "You already did not vote to end the war!");
                        return true;
                    }

                    found.set_attacking_commander_vote(false);
                    found.getDefendingCommander().directMessage("The attacking commander undid their vote to end the " +
                            "war. The battle shall persist.", ChatColor.RED);
                    success(sender, "Unvoted for the war to end. May the battle progress!");
                    return true;
                } else if (user.equals(found.getDefendingCommander())){
                    if (!found.defending_commander_vote()) {
                        error(sender, "You already did not vote to end the war!");
                        return true;
                    }

                    found.set_defending_commander_vote(false);
                    found.getAttackingCommander().directMessage("The defending commander undid their vote to end the " +
                            "war. The battle shall persist.", ChatColor.RED);
                    success(sender, "Unvoted for the war to end. May the battle progress!");
                    return true;
                } else {
                    error(sender, "You are not a commander!");
                    return true;
                }
            }

            case "end":
            {
                if (args.length != 1) {
                    sendSpecializedSyntax(sender, mode);
                    return true;
                }

                War found = GlasshouseTweaks.getWarsDatabase().findWarByParticipant(user);
                if (found == null) {
                    error(sender, "You are not currently in any wars.");
                    return true;
                }

                if (!( user.equals(found.getAttackingCommander()) || user.equals(found.getDefendingCommander()) )) {
                    error(sender, "You are not a commander of this war!");
                    return true;
                }

                if (user.equals(found.getAttackingCommander())) { // attacking commander
                    if (found.defending_commander_vote()) { // end war
                        success(sender, "Formally ending the war! Both sides have voted for its release.");
                        found.end();
                        return true;
                    } else { // place request
                        success(sender, "Voted to end the war.");
                        found.set_attacking_commander_vote(true);
                        return true;
                    }
                } else { // defending commander
                    if (found.attacking_commander_vote()) { // end war
                        success(sender, "Formally ending the war! Both sides have voted for its release.");
                        found.end();
                        return true;
                    } else {
                        success(sender, "Voted to end the war.");
                        found.set_defending_commander_vote(true);
                        found.getAttackingCommander().directMessage("The opposing commander of your current war has " +
                                "voted to end it! Use \"/war end\" to officially end it.", ChatColor.GRAY);
                        return true;
                    }
                }
            }

            case "enlist":
            {
                return true;
            }

            default:
                sendSyntax(sender);
                return true;
        }
    }

    @Override
    public String getSyntax() {
        return "/war <declare/end/enlist/unvote>";
    }

    public void sendSpecializedSyntax(CommandSender sender, String mode) {
        reply(sender, getSpecializedSyntax(mode));
    }

    public String getSpecializedSyntax(String mode) {
        switch (mode) {
            case "declare":
                return "/war declare <player>";
            case "end":
                return "/war end";
            case "enlist":
                return "/war enlist <commander>";
            case "unvote":
                return "/war unvote";
            default:
                return getSyntax();
        }
    }
}
