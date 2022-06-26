package xyz.nickelulz.glasshousetweaks.datatypes;

import org.bukkit.ChatColor;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class War {
    private User attacking_commander;
    private User defending_commander;
    private Map<User, Integer> attacking_army;
    private Map<User, Integer> defending_army;
    private LocalDateTime time_declared;
    private boolean attacking_commander_over;
    private boolean defending_commander_over;

    // new war constructor
    public War(User attacking_commander, User defending_commander, LocalDateTime time_declared) {
        this(attacking_commander, defending_commander, new TreeMap<>(), new TreeMap<>(), time_declared, false, false);
        attacking_army.put(attacking_commander, 0);
        defending_army.put(defending_commander, 0);
    }

    // existing war constructor
    public War(User attacking_commander, User defending_commander, Map<User, Integer> attacking_army,
               Map<User, Integer> defending_army, LocalDateTime time_declared, boolean attackingCommanderOverVote,
               boolean defendingCommanderOverVote) {
        this.attacking_commander = attacking_commander;
        this.defending_commander = defending_commander;
        this.attacking_army = attacking_army;
        this.defending_army = defending_army;
        this.time_declared = time_declared;
        this.attacking_commander_over = attackingCommanderOverVote;
        this.defending_commander_over = defendingCommanderOverVote;
    }

    public boolean enlist(User conscript, User commander) {
        if (attacking_commander.equals(commander) && !attacking_army.containsKey(conscript)) {
            attacking_army.put(conscript, 0);
            GlasshouseTweaks.getWarsDatabase().save();
            return true;
        }
        else if (defending_commander.equals(commander) && !defending_army.containsKey(conscript)) {
            defending_army.put(conscript, 0);
            GlasshouseTweaks.getWarsDatabase().save();
            return true;
        }
        return false;
    }

    public boolean containsPlayer(User player) {
        return attacking_army.containsKey(player) ||
                defending_army.containsKey(player);
    }

    public void end() {
        GlasshouseTweaks.getWarsDatabase().remove(this);

        // determine the winner
        int attacking_total = 0, defending_total = 0;
        for (Integer kills: attacking_army.values())
            attacking_total += kills;
        for (Integer kills: defending_army.values())
            defending_total += kills;

        if (attacking_total > defending_total) {
            // Attacking Army
            for (User player: attacking_army.keySet()) {
                player.directMessage("Your current war has ended! Your side, led by " +
                        attacking_commander.getProfile().getName() + ", has won by a difference of " +
                        (attacking_total - defending_total) + " kills.", ChatColor.GREEN);
                player.directMessage("During this war, in total you accumulated " + attacking_army.get(player) +
                        " kills, with the total difference in kills being (ATK) " + attacking_total + " - (DEF) " + defending_total + ".",
                        ChatColor.GRAY);
            }

            // Defending Army
            for (User player: defending_army.keySet()) {
                player.directMessage("Your current war has ended! Your side, led by " +
                        defending_commander.getProfile().getName() + ", lost by a difference of " +
                        (attacking_total - defending_total) + " kills.", ChatColor.RED);
                player.directMessage("During this war, in total you accumulated " + defending_army.get(player) +
                        " kills, with the total difference in kills being (DEF) " + defending_total + " - (ATK) " + attacking_total + ".",
                        ChatColor.GRAY);
            }
        } else if (defending_total > attacking_total) {
            // Attacking Army
            for (User player: attacking_army.keySet()) {
                player.directMessage("Your current war has ended! Your side, led by " +
                        attacking_commander.getProfile().getName() + ", lost by a difference of " +
                        (defending_total - attacking_total) + " kills.", ChatColor.RED);
                player.directMessage("During this war, in total you accumulated " + defending_army.get(player) +
                        " kills, with the total difference in kills being (ATK) " + attacking_total + " - " +
                        "(DEF) " + defending_total + ".", ChatColor.GRAY);
            }

            // Defending Army
            for (User player: attacking_army.keySet()) {
                player.directMessage("Your current war has ended! Your side, led by " +
                        defending_commander.getProfile().getName() + ", has won by a difference of " +
                        (defending_total - attacking_total) + " kills.", ChatColor.GREEN);
                player.directMessage("During this war, in total you accumulated " + defending_army.get(player) +
                        " kills, with the total difference in kills being (DEF) " + defending_total + " - " +
                        "(ATK) " + attacking_total + ".", ChatColor.GRAY);
            }
        }
    }

    public User getAttackingCommander() {
        return attacking_commander;
    }

    public User getDefendingCommander() {
        return defending_commander;
    }

    public Map<User, Integer> getAttackingArmy() {
        return attacking_army;
    }

    public Map<User, Integer> getDefendingArmy() {
        return defending_army;
    }

    public ArrayList<String> getAttackingArmyUUIDS() {
        ArrayList<String> out = new ArrayList<>();
        for (User player: attacking_army.keySet())
            out.add(player.getProfile().getName());
        return out;
    }

    public ArrayList<String> getDefendingArmyUUIDS() {
        ArrayList<String> out = new ArrayList<>();
        for (User player: defending_army.keySet())
            out.add(player.getProfile().getName());
        return out;
    }

    public boolean incrementPlayerKills(User player) {
        if (attacking_army.containsKey(player)) {
            attacking_army.put(player, attacking_army.get(player) + 1);
            return true;
        }
        else if (defending_army.containsKey(player)) {
            defending_army.put(player, defending_army.get(player) + 1);
            return true;
        }
        return false;
    }

    public LocalDateTime getTimeDeclared() {
        return time_declared;
    }

    public boolean attacking_commander_vote() {
        return attacking_commander_over;
    }

    public void set_attacking_commander_vote(boolean attacking_commander_over) {
        this.attacking_commander_over = attacking_commander_over;
    }

    public boolean defending_commander_vote() {
        return defending_commander_over;
    }

    public void set_defending_commander_vote(boolean defending_commander_over) {
        this.defending_commander_over = defending_commander_over;
    }
}
