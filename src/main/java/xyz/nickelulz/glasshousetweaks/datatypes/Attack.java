package xyz.nickelulz.glasshousetweaks.datatypes;

import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.time.LocalDateTime;

public class Attack {
    private User attacker;
    private User victim;
    private LocalDateTime time;

    public Attack(User attacker, User victim, LocalDateTime time) {
        this.attacker = attacker;
        this.victim = victim;
        this.time = time;
    }

    @Override
    public String toString() {
        return String.format("%s attacked %s illegally at %s", attacker.getProfile().getName(),
                victim.getProfile().getName(), time.format(ConfigurationConstants.DATE_FORMAT));
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

    public User getAttacker() {
        return attacker;
    }

    public void setAttacker(User attacker) {
        this.attacker = attacker;
    }

    public User getVictim() {
        return victim;
    }

    public void setVictim(User victim) {
        this.victim = victim;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
