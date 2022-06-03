package xyz.nickelulz.glasshousetweaks.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

import java.io.*;
import java.lang.reflect.InaccessibleObjectException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public final class PlayerDatabase {
    private static ArrayList<User> users = new ArrayList<>();
    private static File database = new File(GlasshouseTweaks.getInstance().getDataFolder().getAbsolutePath() +
            "/players.json");
    private static Gson jsonParser =
            new GsonBuilder().registerTypeAdapter(User.class, new JSONHandlers.UserJSON()).setPrettyPrinting().create();

    public static void ensureExists() throws IOException {
        if (!database.getParentFile().exists())
            database.getParentFile().mkdirs();
        if (!database.exists())
            database.createNewFile();
    }

    public static boolean load() {
        try {
            ensureExists();
            FileReader in = new FileReader(database);
            User[] list = jsonParser.fromJson(in, User[].class);
            if (list == null) {
                GlasshouseTweaks.log(Level.SEVERE, "Could not parse user database. (Possibly empty.)");
                list = new User[0];
            }
            users = new ArrayList<>(Arrays.asList(list));
            GlasshouseTweaks.log(Level.INFO, "Loaded player database.");
            return true;
        } catch (NullPointerException | IOException | InaccessibleObjectException e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not load player database due to " + e.getClass().getName() + ".");
        }
        return false;
    }

    public static boolean save() {
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
        save();
        return out;
    }

    public static User remove(int index) {
        User u = users.remove(index);
        save();
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
            if (u.getProfile().getDisplayName().equals(ign))
                return u;
        return null;
    }

    public static boolean add(User u) {
        if (users.contains(u))
            return false;
        else {
            users.add(u);
            return save();
        }
    }

    public static boolean containsPlayer(Player p) {
        for (User u: users)
            if (u.getProfile().equals(p))
                return true;
        return false;
    }
}
