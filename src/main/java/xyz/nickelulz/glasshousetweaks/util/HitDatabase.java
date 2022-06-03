package xyz.nickelulz.glasshousetweaks.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.datatypes.Hit;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

import java.io.*;
import java.lang.reflect.InaccessibleObjectException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class HitDatabase {
    private static ArrayList<Hit> hits = new ArrayList<>();
    private static File database = new File(GlasshouseTweaks.getInstance().getDataFolder().getAbsolutePath() +
            "/hits.json");
    private static Gson jsonParser =
            new GsonBuilder().setPrettyPrinting().create();

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
            Hit[] list = jsonParser.fromJson(in, Hit[].class);
            if (list == null) {
                GlasshouseTweaks.log(Level.SEVERE, "Could not parse hit database. (Possibly empty.)");
                list = new Hit[0];
            }
            hits = new ArrayList<Hit>(Arrays.asList(list));
            GlasshouseTweaks.log(Level.INFO, "Loaded Hit database.");
            return true;
        } catch (IOException | NullPointerException | InaccessibleObjectException e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not load hits database due to " + e.getClass().getName());
        }
        return false;
    }

    public static boolean save() {
        try {
            ensureExists();
            FileWriter out = new FileWriter(database);
            jsonParser.toJson(hits, out);
            out.flush();
            out.close();
            GlasshouseTweaks.log(Level.INFO, "Saved hit database.");
            return true;
        } catch (IOException | NullPointerException | InaccessibleObjectException e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not save hits database due to " + e.getClass().getName());
        }
        return false;
    }

    public static Hit remove(int index) {
        Hit hit = hits.remove(index);
        save();
        return hit;
    }

    public static int size() {
        return hits.size();
    }

    public static Hit get (int index) {
        return hits.get(index);
    }

    public static boolean add(Hit hit) {
        if (hits.contains(hit))
            return false;
        else {
            hits.add(hit);
            return save();
        }
    }

    public static boolean containsHit(Hit hit) {
        for (Hit h: hits)
            if (hit.equals(h))
                return true;
        return false;
    }
}
