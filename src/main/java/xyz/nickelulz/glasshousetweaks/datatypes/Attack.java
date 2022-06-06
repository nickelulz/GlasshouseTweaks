package xyz.nickelulz.glasshousetweaks.datatypes;

import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.time.LocalDateTime;

public class Attack {
    private Player attacker;
    private Player victim;
    private LocalDateTime time;

    public Attack(Player attacker, Player victim, LocalDateTime time) {
        this.attacker = attacker;
        this.victim = victim;
        this.time = time;
    }

    @Override
    public String toString() {
        return String.format("%s attacked %s illegally at %s", attacker.getName(),
                victim.getName(), time.format(ConfigurationConstants.DATE_FORMAT));
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

    public Player getAttacker() {
        return attacker;
    }

    public void setAttacker(Player attacker) {
        this.attacker = attacker;
    }

    public Player getVictim() {
        return victim;
    }

    public void setVictim(Player victim) {
        this.victim = victim;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
