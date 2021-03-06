package xyz.nickelulz.glasshousetweaks.datatypes;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;
import xyz.nickelulz.glasshousetweaks.util.DiscordClientManager;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;

import static xyz.nickelulz.glasshousetweaks.util.Utils.playerEquals;

/**
 * Wrapper class for a Player, represents
 * a player and links them to the plugin.
 */
public class User {
    private String discordId;
    private OfflinePlayer profile;
    private LocalDateTime lastPlacedHit, lastTargetedHit, lastContractedHit, lastWar;
    private int kills, deaths, morbiums; // morbiums are just tokens users get for placing successful hits

     // Fresh user constructor
    public User(String discordId, OfflinePlayer profile) {
        this(discordId, profile, null, null, null, null, 0, 0, 0);
    }

    public User(String discordId, OfflinePlayer profile, int kills, int deaths, int morbiums) {
        this(discordId, profile, null, null, null, null, kills, deaths, morbiums);
    }

    //Full constructor
    public User(String discordId, OfflinePlayer profile, @Nullable LocalDateTime lastContractedHit,
                @Nullable LocalDateTime lastTargetedHit, @Nullable LocalDateTime lastPlacedHit,
                LocalDateTime lastWar, int kills, int deaths, int morbiums) {
        this.discordId = discordId;
        this.profile = profile;
        this.lastContractedHit = lastContractedHit;
        this.lastTargetedHit = lastTargetedHit;
        this.lastPlacedHit = lastPlacedHit;
        this.kills = kills;
        this.deaths = deaths;
        this.morbiums = morbiums;
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
            int cooldown_raw = ConfigurationConstants.HIRING_COOLDOWN - minutesSinceDate(lastPlacedHit);
            int cooldown = Math.max(cooldown_raw, 0);
            if (cooldown == 0) {
                lastPlacedHit = null;
                GlasshouseTweaks.getPlayersDatabase().save();
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
        return String.format("%d hours, %d minutes", cooldown / 60, cooldown % 60);
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
            int cooldown_raw = ConfigurationConstants.TARGETING_COOLDOWN - minutesSinceDate(lastTargetedHit);
            int cooldown = Math.max(cooldown_raw, 0);
            if (cooldown == 0) {
                lastTargetedHit = null;
                GlasshouseTweaks.getPlayersDatabase().save();
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
        return String.format("%d hours, %d minutes", cooldown / 60, cooldown % 60);
    }

    /**
     * DM's the user on both the server (if they are online) and discord.
     * @return Operation Success Code
     */
    public void directMessage(String message, org.bukkit.ChatColor color) {
        Player ingame = profile.getPlayer();
        if (profile.isOnline()) {
            ingame.sendMessage(color + message);
        }
        GlasshouseTweaks.log(Level.INFO, "Bot Connected?: " + DiscordClientManager.bot_connected);
        // discord dm
        if (DiscordClientManager.bot_connected) {
            boolean success = DiscordClientManager.sendDirectMessageRequest(message, discordId);
            if (!success && profile.isOnline())
                ingame.sendMessage(ChatColor.YELLOW + "The discord bot cannot direct message you due to either your " +
                        "settings or because you are not currently in a server with the bot. Make sure to contact the" +
                        " server administrator if you believe this is a mistake.");
        }
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
            int cooldown_raw = ConfigurationConstants.CONTRACTING_COOLDOWN - minutesSinceDate(lastContractedHit);
            int cooldown = Math.max(cooldown_raw, 0);
            if (cooldown == 0) {
                lastContractedHit = null;
                GlasshouseTweaks.getPlayersDatabase().save();
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
        return String.format("%d hours, %d minutes", cooldown / 60, cooldown % 60);
    }

    /**
     * Calculates the amount of war cooldown
     * time left for this player
     * @return The cooldown time left (in minutes).
     */
    public int commanderCooldown() {
         if (lastWar == null)
             return 0;
         else {
             int cooldown_raw = (ConfigurationConstants.WAR_COOLDOWN * 60) - minutesSinceDate(lastWar);
             int cooldown = Math.max(cooldown_raw, 0);
             if (cooldown == 0) {
                 lastWar = null;
                 GlasshouseTweaks.getPlayersDatabase().save();
             }
             return cooldown;
         }
    }

    /**
     * @return String representation of the commander
     *         cooldown. (in days/hours/minutes).
     */
    public String commanderCooldownString() {
        int cooldown = commanderCooldown();
        int days = cooldown / (60 * 60);
        cooldown = cooldown - days * 60 * 60;
        return String.format("%d days, %d hours, %d minutes", days, cooldown / 60, cooldown % 60);
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
                playerEquals(profile, other.profile) &&
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
    public OfflinePlayer getProfile() {
        return profile;
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

    public LocalDateTime getLastWar() {
        return lastWar;
    }

    public void setLastWar(LocalDateTime lastWar) {
        this.lastWar = lastWar;
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

    public int getMorbiums() {
        return morbiums;
    }

    public void setMorbiums(int morbiums) {
        this.morbiums = morbiums;
    }

    public void increment(String variable, int change) {
        switch (variable) {
            case "kills":
                kills += change;
                break;
            case "morbiums":
                morbiums += change;
                break;
            case "deaths":
                deaths += change;
                break;
        }
    }
}
