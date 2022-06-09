package xyz.nickelulz.glasshousetweaks.commands.allaccess;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

import java.util.ArrayList;

public class LeaderboardCommand extends CommandBase {

    public LeaderboardCommand() {
        super("leaderboard", 1, true, "Get the leaderboard for kills, deaths, or morbiums.");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {

        if (GlasshouseTweaks.getPlayersDatabase().size() == 0) {
            reply(sender, "Nobody is currently registered.");
            return true;
        }

        switch (args[0]) {
            case "kills": {
                ArrayList<User> killsLeaderboard = new ArrayList<>(GlasshouseTweaks.getPlayersDatabase().getDataset());
                sort(1, killsLeaderboard);
                reply(sender, ChatColor.YELLOW + "-----[" + ChatColor.WHITE + " KILLS " + ChatColor.YELLOW + "]-----");
                for (int i = 0; i < killsLeaderboard.size(); i++)
                    reply(sender, " " + (i+1) + ": " + killsLeaderboard.get(i).getProfile().getName() +
                            " - " + killsLeaderboard.get(i).getKills());
                reply(sender, ChatColor.YELLOW + "-----------------");
                break;
            }

            case "deaths": {
                ArrayList<User> deathsLeaderboard = new ArrayList<>(GlasshouseTweaks.getPlayersDatabase().getDataset());
                sort(0, deathsLeaderboard);
                reply(sender, ChatColor.YELLOW + "-----[" + ChatColor.WHITE + " DEATHS " + ChatColor.YELLOW + "]-----");
                for (int i = 0; i < deathsLeaderboard.size(); i++)
                    reply(sender, "" + (i+1) + ": " + deathsLeaderboard.get(i).getProfile().getName() +
                            " - " + deathsLeaderboard.get(i).getDeaths());
                reply(sender, ChatColor.YELLOW + "------------------");
                break;
            }

            case "morbiums": {
                ArrayList<User> morbiumsLeaderboard = new ArrayList<>(GlasshouseTweaks.getPlayersDatabase().getDataset());
                sort(2, morbiumsLeaderboard);
                reply(sender, ChatColor.YELLOW + "-----[" + ChatColor.WHITE + " MORBIUMS " + ChatColor.YELLOW + "]-----");
                for (int i = 0; i < morbiumsLeaderboard.size(); i++)
                    reply(sender, "" + (i+1) + ": " + morbiumsLeaderboard.get(i).getProfile().getName() +
                            " - M$" + morbiumsLeaderboard.get(i).getMorbiums());
                reply(sender, ChatColor.YELLOW + "--------------------");
                break;
            }

            default: {
                sendSyntax(sender);
            }
        }

        return true;
    }

    /**
     * Using bubble sort and custom sort method to
     * sort each arraylist
     *
     * modes:
     * 0 -> deaths
     * 1 -> kills
     * 2 -> morbiums
     */
    private void sort(int mode, ArrayList<User> list) {
        for (int i = 0; i < list.size()-1; i++) {
            for (int j = 0; j < list.size(); j++) {
                if (sortHieristic(mode, list.get(i), list.get(j))) {
                    User temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }

    private boolean sortHieristic(int mode, User a, User b) {
        switch (mode) {
            case 0: // deaths
                return a.getDeaths() < b.getDeaths();

            case 1: // kills
                return a.getKills() < b.getKills();

            case 2: // morbiums
                return a.getMorbiums() < b.getMorbiums();

            default:
                return false;
        }
    }

    @Override
    public String getSyntax() {
        return "/leaderboard <kills/deaths/morbiums>";
    }
}
