package xyz.nickelulz.glasshousetweaks.datatypes;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.time.LocalDateTime;
import java.util.logging.Level;

public class Attack {
    private OfflinePlayer attacker;
    private String attackerLastUsedName;
    private OfflinePlayer victim;
    private String victimLastUsedName;
    private LocalDateTime time;

    public Attack(OfflinePlayer attacker, String attackerLastUsedName, OfflinePlayer victim,
                  String victimLastUsedName, LocalDateTime time) {
        this.attacker = attacker;
        this.attackerLastUsedName = attackerLastUsedName;
        this.victim = victim;
        this.victimLastUsedName = victimLastUsedName;
        this.time = time;
    }

    @Override
    public String toString() {
        return String.format("%s attacked %s illegally at %s", attacker.getName(),
                victim.getName(), time.format(ConfigurationConstants.VISUAL_DATE_FORMAT));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Attack))
            return false;
        Attack other = (Attack) o;
        return attacker.equals(other.attacker) &&
                victim.equals(other.victim) &&
                time.equals(other.time);
    }

    public OfflinePlayer getAttacker() {
        return attacker;
    }

    public OfflinePlayer getVictim() {
        return victim;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getVictimName() {
        if (victim.getName() == null)
            return victimLastUsedName;
        return victim.getName();
    }

    public String getAttackerName() {
        if (attacker.getName() == null)
            return attackerLastUsedName;
        return attacker.getName();
    }
}
