package xyz.nickelulz.glasshousetweaks.databases;

import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

import java.util.Objects;

public final class PlayerDatabase extends Database<User> {

    public PlayerDatabase() {
        super("players.json", User.class, User[].class, new JSONHandlers.UserJSON());
    }

    public User findByIGN(String ign) {
        for (User u: getDataset())
            if (Objects.requireNonNull(u.getProfile().getName()).equalsIgnoreCase(ign))
                return u;
        return null;
    }

    public User findByProfile(Player profile) {
        for (User u: getDataset())
            if (u.getProfile().getUniqueId().equals(profile.getUniqueId()))
                return u;
        return null;
    }

    public boolean isRegistered(Player profile) {
        return !(findByProfile(profile) == null);
    }

    public boolean containsPlayer(Player p) {
        return isRegistered(p);
    }
}
