package xyz.nickelulz.glasshousetweaks.commands.allaccess;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.nickelulz.glasshousetweaks.commands.CommandBase;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.database.PlayerDatabase;

import java.util.ArrayList;

public class LeaderboardCommand extends CommandBase {

    public LeaderboardCommand() {
        super("leaderboard", 1, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {

        switch (args[0]) {
            case "kills": {
                if (PlayerDatabase.size() == 0) {
                    sender.sendMessage(ChatColor.GRAY + "Nobody is currently registered.");
                    return true;
                }

                ArrayList<User> killsLeaderboard = new ArrayList<>(PlayerDatabase.getUsers());
                sort(1, killsLeaderboard);
                sender.sendMessage(ChatColor.GRAY + "===== KILLS =====");
                for (int i = 0; i < killsLeaderboard.size(); i++)
                    sender.sendMessage(ChatColor.GRAY + " " + (i+1) + ": " + killsLeaderboard.get(i).getProfile().getName() +
                            " - " + killsLeaderboard.get(i).getKills());
                sender.sendMessage(ChatColor.GRAY + "=================");
                break;
            }

            case "deaths": {
                if (PlayerDatabase.size() == 0) {
                    sender.sendMessage(ChatColor.GRAY + "Nobody is currently registered.");
                    return true;
                }

                ArrayList<User> deathsLeaderboard = new ArrayList<>(PlayerDatabase.getUsers());
                sort(0, deathsLeaderboard);
                sender.sendMessage(ChatColor.GRAY + "===== DEATHS =====");
                for (int i = 0; i < deathsLeaderboard.size(); i++)
                    sender.sendMessage(ChatColor.GRAY + "" + (i+1) + ": " + deathsLeaderboard.get(i).getProfile().getName() +
                            " - " + deathsLeaderboard.get(i).getDeaths());
                sender.sendMessage(ChatColor.GRAY +  "==================");
                break;
            }

            default: {
                sendSyntax(sender);
            }
        }

        return true;
    }

    /**
     * Using selection sort and custom sort method to
     * sort each arraylist
     *
     * modes:
     * 0 -> deaths
     * 1 -> kills
     */
    private void sort(int mode, ArrayList<User> list) {
        for (int i = 0; i < list.size()-1; i++) {
            int smallestPos = 0;
            for (int j = i+1; j < list.size(); j++)
                if (mode == 1 ? list.get(i).getKills() < list.get(j).getKills() :
                        list.get(i).getDeaths() < list.get(j).getDeaths())
                    smallestPos = j;
            User temp = list.get(i);
            list.set(i, list.get(smallestPos));
            list.set(smallestPos, temp);
        }
    }

    @Override
    public String getSyntax() {
        return "/leaderboard <kills/deaths>";
    }
}
