package xyz.nickelulz.glasshousetweaks.datatypes;

import com.google.gson.*;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.util.Configuration;
import xyz.nickelulz.glasshousetweaks.util.PlayerDatabase;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Wrapper class for a Player, represents
 * a player and links them to the plugin.
 */
public class User {
    private String discordId;
    private Player profile;
    private LocalDateTime lastPlacedHit, lastTargetedHit, lastContractedHit;
    private int kills, deaths;

    /**
     * Fresh user constructor
     * @param discordId
     * @param profile
     */
    public User(String discordId, Player profile) {
        this(discordId, profile, null, null, null, 0, 0);
    }

    public User(String discordId, Player profile, int kills, int deaths) {
        this(discordId, profile, null, null, null, kills, deaths);
    }

    /**
     * Full constructor
     * @param discordId
     * @param profile
     * @param lastContractedHit
     * @param lastTargetedHit
     * @param lastPlacedHit
     * @param kills
     * @param deaths
     */
    public User(String discordId, Player profile, @Nullable LocalDateTime lastContractedHit,
                @Nullable LocalDateTime lastTargetedHit, @Nullable LocalDateTime lastPlacedHit, int kills, int deaths) {
        this.discordId = discordId;
        this.profile = profile;
        this.lastContractedHit = lastContractedHit;
        this.lastTargetedHit = lastTargetedHit;
        this.lastPlacedHit = lastPlacedHit;
        this.kills = kills;
        this.deaths = deaths;
    }

    /**
     * Calculates the time in minutes since a date.
     * @param time The time to compare to.
     * @return Difference of time in minutes.
     */
    private int minutesSinceDate(LocalDateTime time) {
        int mins = (int) ChronoUnit.MINUTES.between(time, LocalDateTime.now());
        int hours = (int) ChronoUnit.HOURS.between(time, LocalDateTime.now());
        return (hours * 60) + mins;
    }

    /**
     * Calculates the amount of hiring
     * cooldown time left for this player.
     * @return The cooldown time left.
     */
    public int hiringCooldown() {
        if (lastPlacedHit == null)
            return 0;
        else {
            int cooldown_raw = Configuration.HIRING_COOLDOWN - minutesSinceDate(lastPlacedHit);
            int cooldown = Math.signum(cooldown_raw) == 1.0f ? cooldown_raw : 0;
            if (cooldown == 0) {
                lastPlacedHit = null;
                PlayerDatabase.save();
            }
            return cooldown;
        }
    }

    /**
     * @return String representation of the hiring
     *         cooldown. (hours and minutes)
     */
    public String hiringCooldownString() {
        int cooldown = hiringCooldown();
        return String.format("%d hours, %d seconds", cooldown / 60, cooldown % 60);
    }

    /**
     * Calculates the amount of targeting
     * cooldown time left for this player.
     * @return The cooldown time left.
     */
    public int targettingCooldown() {
        if (lastTargetedHit == null)
            return 0;
        else {
            int cooldown_raw = Configuration.TARGETING_COOLDOWN - minutesSinceDate(lastTargetedHit);
            int cooldown = Math.signum(cooldown_raw) == 1.0f ? cooldown_raw : 0;
            if (cooldown == 0) {
                lastTargetedHit = null;
                PlayerDatabase.save();
            }
            return cooldown;
        }
    }

    /**
     * @return String representation of the targeting
     *         cooldown. (hours and minutes)
     */
    public String targettingCooldownString() {
        int cooldown = targettingCooldown();
        return String.format("%d hours, %d seconds", cooldown / 60, cooldown % 60);
    }

    /**
     * Calculates the amount of contracting
     * cooldown time left for this player.
     *
     * @return The cooldown time left.
     */
    public int contractingCooldown() {
        if (lastContractedHit == null)
            return 0;
        else {
            int cooldown_raw = Configuration.CONTRACTING_COOLDOWN - minutesSinceDate(lastContractedHit);
            int cooldown = Math.signum(cooldown_raw) == 1.0f ? cooldown_raw : 0;
            if (cooldown == 0) {
                lastContractedHit = null;
                PlayerDatabase.save();
            }
            return cooldown;
        }
    }

    /**
     * @return String representation of the contracting
     *         cooldown. (hours and minutes)
     */
    public String contractingCooldownString() {
        int cooldown = contractingCooldown();
        return String.format("%d hours, %d seconds", cooldown / 60, cooldown % 60);
    }

    @Override
    public String toString() {
        return String.format("USER %s DISCORDID %s KILLS: %d DEATHS %d", profile.getName(), discordId, kills, deaths);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User other = (User) o;
        return kills == other.kills && deaths == other.deaths &&
                discordId.equals(other.discordId) &&
                profile.equals(other.profile) &&
                lastPlacedHit.equals(other.lastPlacedHit) &&
                lastContractedHit.equals(other.lastContractedHit) &&
                lastTargetedHit.equals(other.lastTargetedHit);
    }

    /**
     * GETTERS AND SETTERS
     */

    public String getDiscordId() {
        return discordId;
    }
    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }
    public Player getProfile() {
        return profile;
    }
    public void setProfile(Player profile) {
        this.profile = profile;
    }
    public LocalDateTime getLastPlacedHit() {
        return lastPlacedHit;
    }
    public void setLastPlacedHit(LocalDateTime lastPlacedHit) {
        this.lastPlacedHit = lastPlacedHit;
    }
    public LocalDateTime getLastTargetedHit() {
        return lastTargetedHit;
    }
    public void setLastTargetedHit(LocalDateTime lastTargetedHit) {
        this.lastTargetedHit = lastTargetedHit;
    }
    public LocalDateTime getLastContractedHit() {
        return lastContractedHit;
    }
    public void setLastContractedHit(LocalDateTime lastContractedHit) {
        this.lastContractedHit = lastContractedHit;
    }
    public int getKills() {
        return kills;
    }
    public void setKills(int kills) {
        this.kills = kills;
    }
    public int getDeaths() {
        return deaths;
    }
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
}
