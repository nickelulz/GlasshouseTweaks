package xyz.nickelulz.glasshousetweaks.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

import java.io.*;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public final class PlayerDatabase extends Database<User> {

    public PlayerDatabase() {
        super("players.json", User.class, new JSONHandlers.UserJSON());
    }

    @Override
    public boolean reload() {
        try {
            ensureExists();
            FileReader in = new FileReader(database);
            User[] list = jsonParser.fromJson(in, User[].class);
            if (list == null) {
                GlasshouseTweaks.log(Level.SEVERE, "Could not parse user database. List is NULL.");
                return false;
            }
            users = new ArrayList<>(Arrays.asList(list));
            GlasshouseTweaks.log(Level.INFO, "Loaded player database.");
            return true;
        } catch (NullPointerException | IOException | InaccessibleObjectException e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not load player database due to " + e.getClass().getName() + ".");
        }
        return false;
    }

    @Override
    public boolean save() {
        try {
            ensureExists();
            FileWriter out = new FileWriter(database, false);
            jsonParser.toJson(users, out);
            out.flush();
            out.close();
            GlasshouseTweaks.log(Level.INFO, "Saved player database.");
            return true;
        } catch (NullPointerException | IOException | InaccessibleObjectException e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not save player database due to " + e.getClass().getName() +
                    ".");
        }
        return false;
    }

    public static boolean remove(User u) {
        boolean out = users.remove(u);
        boolean success = false;
        if (out) {
            GlasshouseTweaks.log(Level.INFO, "Removed player " + u.getProfile().getName() + " from player database.");
            success = save();
        }
        return out && success;
    }

    public static User remove(int index) {
        User u = users.remove(index);
        if (u != null) {
            GlasshouseTweaks.log(Level.INFO, "Removed player " + u.getProfile().getName() + " from player database.");
            save();
        }
        return u;
    }

    public static int size() {
        return users.size();
    }

    public static User get(int index) {
        return users.get(index);
    }

    public static User findById(String discordId) {
        for (User u: users)
            if (u.getDiscordId().equals(discordId))
                return u;
        return null;
    }

    public static User findByIGN(String ign) {
        for (User u: users)
            if (u.getProfile().getDisplayName().equalsIgnoreCase(ign))
                return u;
        return null;
    }

    public static User findByProfile(Player profile) {
        for (User u: users)
            if (u.getProfile().equals(profile))
                return u;
        return null;
    }

    public static boolean isRegistered(Player profile) {
        return !(findByProfile(profile) == null);
    }

    public static boolean add(User u) {
        if (users.contains(u))
            return false;
        else {
            boolean success = users.add(u) && save();
            if (success)
                GlasshouseTweaks.log(Level.INFO, "Added player " + u.getProfile().getName() + " to the player " +
                        "database.");
            else
                GlasshouseTweaks.log(Level.WARNING, "Could not add player " + u.getProfile().getName() + " to the " +
                        "player database.");
            return success;
        }
    }

    public static boolean containsPlayer(Player p) {
        for (User u: users)
            if (u.getProfile().equals(p))
                return true;
        return false;
    }
}
