package xyz.nickelulz.glasshousetweaks.commands.allaccess;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.commands.CommandManager;

/**
 * A bit different, going to be manually registered.
 * This command is for listing every other command\'s usages, essentially.
 */
public class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {
            error(sender, getSyntax());
        }

        if (args.length == 0) {
            reply(sender, ChatColor.YELLOW + "-------------[ " + ChatColor.WHITE + "GlasshouseTweaks Commands " +
                    ChatColor.YELLOW + "]-----------");
            // all access
            reply(sender, "1. /register - " + CommandManager.REGISTER_COMMAND.getDescription());
            reply(sender, "2. /glasshousetweaks - View this information.");
            reply(sender, "3. /playerinfo - " + CommandManager.VIEW_PLAYER_INFO_COMMAND.getDescription());
            reply(sender, "4. /leaderboard - " + CommandManager.LEADERBOARD_COMMAND.getDescription());
            reply(sender, "5. /checkregister - " + CommandManager.CHECK_REGISTER_COMMAND.getDescription());
            reply(sender, "6. /duel - " + CommandManager.DUEL_COMMAND.getDescription());
            reply(sender, "7. /dm - " + CommandManager.DIRECT_MESSAGE_COMMAND.getDescription());

            // registered only
            reply(sender, ChatColor.YELLOW + "8. /bounty - " + CommandManager.BOUNTY_COMMAND.getDescription());
            reply(sender, ChatColor.YELLOW + "9. /contract - " + CommandManager.CONTRACT_COMMAND.getDescription());
            reply(sender, ChatColor.YELLOW + "10. /removehit - " + CommandManager.REMOVE_HIT_COMMAND.getDescription());
            reply(sender, ChatColor.YELLOW + "11. /transfer - " + CommandManager.TRANSFER_MORBIUMS_COMMAND.getDescription());
            reply(sender, ChatColor.YELLOW + "12. /war - " + CommandManager.WAR_COMMAND.getDescription());

            // admin only
            reply(sender, ChatColor.RED + "13. /illegalkills - " + CommandManager.MANAGE_ILLEGAL_KILLS_COMMAND.getDescription());
            reply(sender, "");
            reply(sender, ChatColor.ITALIC + "" + ChatColor.GRAY + "(" + ChatColor.YELLOW + "Yellow " + ChatColor.GRAY +
                            "commands require you to be " + ChatColor.YELLOW + "registered " + ChatColor.GRAY + "and " +
                            ChatColor.RED + "red " + ChatColor.GRAY + "commands require you to have " +
                            ChatColor.RED + "administrator " + ChatColor.GRAY + "access.)");
            reply(sender, ChatColor.YELLOW + "--------------------------------------------------");
            return true;
        } else {
            switch (args[0]) {
                case "checkregister":
                    simpleResponse(sender, CommandManager.CHECK_REGISTER_COMMAND, false, false);
                    return true;

                case "duel":
                    reply(sender, ChatColor.GRAY + "/duel request <player>" + ChatColor.WHITE +
                            " - Request a duel against another player. (Pending duels expire after 5 minutes.)");
                    reply(sender, ChatColor.GRAY + "/duel forfeit <all>" + ChatColor.WHITE + " - Forfeit" +
                            " your current duel, or forfeit all of your pending duels plus your current duel " +
                            "(\"all\" optional)");
                    reply(sender, ChatColor.GRAY + "/duel accept <player>" + ChatColor.WHITE + " - Accept a " +
                            "pending duel.");
                    reply(sender, ChatColor.GRAY + "/duel deny <player>" + ChatColor.WHITE + " - Deny a " +
                            "pending duel.");
                    reply(sender, ChatColor.GRAY + "/duel view" + ChatColor.WHITE + " - View your " +
                            "active and pending duels.");
                    reply(sender, ChatColor.YELLOW + "This command requires you to be registered.");
                    return true;

                case "dm":
                    simpleResponse(sender, CommandManager.DIRECT_MESSAGE_COMMAND, false, false);
                    return true;

                case "war":
                    reply(sender, ChatColor.GRAY + "/war declare <player> " + ChatColor.WHITE + "- Declare war on " +
                            "another player.");
                    reply(sender, ChatColor.GRAY + "/war end" + ChatColor.WHITE + " - Vote to end the war. (Requires " +
                            "you to be a commander of the war)");
                    reply(sender, ChatColor.GRAY + "/war enlist <commander>" + ChatColor.WHITE + " - Join the a war " +
                            "on the side of your requested commander.");
                    reply(sender, ChatColor.GRAY + "/war unvote" + ChatColor.WHITE + " - Undo a vote to end a war (If" +
                            " you are a commander that voted previously.");
                    return true;

                case "glasshousetweaks":
                    reply(sender, "List out detailed information on each of the commands on this plugin.");
                    reply(sender, "Usage: " + ChatColor.GRAY + getSyntax());
                    return true;

                case "register":
                    simpleResponse(sender, CommandManager.REGISTER_COMMAND, true, false);
                    return true;

                case "playerinfo":
                    simpleResponse(sender, CommandManager.VIEW_PLAYER_INFO_COMMAND, true, false);
                    return true;

                case "leaderboard":
                    simpleResponse(sender, CommandManager.LEADERBOARD_COMMAND, true, false);
                    return true;

                case "bounty":
                    reply(sender, ChatColor.GRAY + "/bounty place <price> <target> " + ChatColor.WHITE + "- Place a " +
                            "hit.");
                    reply(sender, ChatColor.GRAY + "/bounty list " + ChatColor.WHITE + "- List out all open hits.");
                    reply(sender, ChatColor.YELLOW + "This command requires you to be registered.");
                    return true;

                case "contract":
                    reply(sender, ChatColor.GRAY + "/contract place <price> <target> <contractor> " + ChatColor.WHITE +
                            "- Place a new contract.");
                    reply(sender, ChatColor.GRAY + "/contract accept <target> <hirer> " + ChatColor.WHITE + " - Accept" +
                            " a pending contract.");
                    reply(sender, ChatColor.GRAY + "/contract deny <target> <hirer> " + ChatColor.WHITE + "- Deny a " +
                            "pending contract.");
                    reply(sender, ChatColor.YELLOW + "This command requires you to be registered.");
                    return true;

                case "removehit":
                    simpleResponse(sender, CommandManager.REMOVE_HIT_COMMAND, true, false);
                    return true;

                case "transfer":
                    simpleResponse(sender, CommandManager.TRANSFER_MORBIUMS_COMMAND, true, false);
                    return true;

                case "illegalkills":
                    reply(sender, ChatColor.GRAY + "/illegalkills list " + ChatColor.WHITE + " - List out all of the " +
                            "illegal kills " +
                            "still unremoved.");
                    reply(sender, "/illegalkills remove <attacker> <victim>" + ChatColor.WHITE + " - Remove an " +
                            "illegal kill from the " +
                            "registry.");
                    reply(sender, ChatColor.RED + "This command requires you to be registered and administrator permissions.");
                    return true;

                default:
                    error(sender, getSyntax(), ChatColor.YELLOW + "Invalid Command Name!");
                    return true;
            }
        }
    }

    public String getSyntax() {
        return "/glasshousetweaks (to list all commands) or /glasshousetweaks <command> (for specialized help)";
    }

    public void simpleResponse(CommandSender sender, CommandBase command, boolean requiresRegistered,
                               boolean requiresAdmin) {
        reply(sender, command.getDescription());
        reply(sender, "Usage: " + ChatColor.GRAY + command.getSyntax());
        if (requiresAdmin)
            reply(sender, ChatColor.RED + "This command requires you to be registered and administrator permissions.");
        else if (requiresRegistered)
            reply(sender, ChatColor.YELLOW + "This command requires you to be registered.");
    }

    public void error(CommandSender sender, String... errs) {
        for (String err: errs)
            sender.sendMessage(ChatColor.RED + err);
    }

    public void reply(CommandSender sender, String... messages) {
        for (String message: messages)
            sender.sendMessage(ChatColor.WHITE + message);
    }
}
