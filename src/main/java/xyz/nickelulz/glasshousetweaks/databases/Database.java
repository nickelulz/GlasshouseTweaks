package xyz.nickelulz.glasshousetweaks.databases;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public abstract class Database<T> {
    private ArrayList<T> dataset;
    private final File database;
    private final Gson jsonParser;
    private final Type jsonTypeFrom;

    public Database(String filename, Class<T> jsonTypeTo, Type jsonTypeFrom, Object typeAdapter, boolean inheritanceMode) {
        this.dataset = new ArrayList<>();
        this.database = new File(GlasshouseTweaks.getInstance().getDataFolder().getAbsolutePath() + "/" + filename);
        // test?
//        if (inheritanceMode)
//            this.jsonParser = new GsonBuilder().registerTypeHierarchyAdapter(jsonTypeTo, typeAdapter).setPrettyPrinting().create();
//        else
            this.jsonParser = new GsonBuilder().registerTypeAdapter(jsonTypeTo, typeAdapter).setPrettyPrinting().create();
        this.jsonTypeFrom = jsonTypeFrom;

        try {
            ensureExists();
        } catch (IOException e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not create database file " + filename + ".");
        }

        reload();

        for (T data: dataset)
            System.out.println(data);
    }

    public void ensureExists() throws IOException {
        if (!database.getParentFile().exists())
            database.getParentFile().mkdirs();
        if (!database.exists())
            database.createNewFile();
    }

    public boolean reload() {
        try {
            ensureExists();
            FileReader in = new FileReader(database);
            dataset = new ArrayList<>(Arrays.asList(jsonParser.fromJson(in, jsonTypeFrom)));
            GlasshouseTweaks.log(Level.INFO, "Loaded database " + database.getName() + ".");
            return true;
        } catch (Exception e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not load database " + database.getName() +
                    " due to " + e.getClass().getName());
            if (!(e instanceof IOException || e instanceof NullPointerException))
                e.printStackTrace();
            dataset = new ArrayList<>();
        }
        return false;
    }

    public boolean save() {
        try {
            ensureExists();
            FileWriter out = new FileWriter(database);
            jsonParser.toJson(dataset, out);
            out.flush();
            out.close();
            GlasshouseTweaks.log(Level.INFO, "Saved database " + database.getName() + ".");
            return true;
        } catch (Exception e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not save database " + database.getName() +
                    " due to " + e.getClass().getName());
            if (!(e instanceof IOException || e instanceof NullPointerException))
                e.printStackTrace();
            return false;
        }
    }


    public int size() {
        return dataset.size();
    }

    public boolean remove(T input) {
        return dataset.remove(input);
    }

    public boolean add(T input) {
        if (contains(input) || input == null)
            return false;
        else {
            boolean success = dataset.add(input) && save();
            if (success)
                GlasshouseTweaks.log(Level.INFO, "Appended data to database " + database.getName() + ".");
            else
                GlasshouseTweaks.log(Level.WARNING, "Could not append data to database " + database.getName() + ".");
            return success;
        }
    }

    public boolean contains(T input) {
        return dataset.contains(input);
    }

    public ArrayList<T> getDataset() {
        return dataset;
    }

    public File getDatabase() {
        return database;
    }

    public Gson getJsonParser() {
        return jsonParser;
    }
}
