package xyz.nickelulz.glasshousetweaks.databases;

import org.bukkit.OfflinePlayer;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

public final class PlayerDatabase extends Database<User> {

    public PlayerDatabase() {
        super("players.json", User.class, User[].class, new JSONHandlers.UserJSON());
    }

    public User findById(String discordId) {
        for (User u: getDataset())
            if (u.getDiscordId().equals(discordId))
                return u;
        return null;
    }

    public User findByIGN(String ign) {
        for (User u: getDataset())
            if (u.getProfile().getName().equalsIgnoreCase(ign))
                return u;
        return null;
    }

    public User findByProfile(OfflinePlayer profile) {
        for (User u: getDataset())
            if (u.getProfile().equals(profile))
                return u;
        return null;
    }

    public boolean isRegistered(OfflinePlayer profile) {
        return !(findByProfile(profile) == null);
    }

    public boolean containsPlayer(OfflinePlayer p) {
        for (User u: getDataset())
            if (u.getProfile().equals(p))
                return true;
        return false;
    }
}
