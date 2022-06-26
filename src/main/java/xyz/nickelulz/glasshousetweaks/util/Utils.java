package xyz.nickelulz.glasshousetweaks.util;

import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Utils {
    /**
     * Calculates the time since a datetime in milliseconds.
     * @param time
     * @return Time in milliseconds
     */
    public static long timeSinceDate(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();
        int milliseconds = (int) ChronoUnit.MILLIS.between(time, now);
        int seconds = (int) ChronoUnit.SECONDS.between(time, now);
        int mins = (int) ChronoUnit.MINUTES.between(time, now);
        int hours = (int) ChronoUnit.HOURS.between(time, now);
        int days = (int) ChronoUnit.DAYS.between(time, now);
        return (days * 24 * 60 * 60 * 1000) + (hours * 60 * 60 * 1000) +
                (mins * 60 * 1000) + (seconds * 1000) + milliseconds;
    }

    public static boolean playerEquals(OfflinePlayer a, OfflinePlayer b) {
        return a.getUniqueId().equals(b.getUniqueId());
    }
}
