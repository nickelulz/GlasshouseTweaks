package xyz.nickelulz.glasshousetweaks.util;

import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;

import java.time.format.DateTimeFormatter;

public class ConfigurationConstants {
    /**
     * The minimum price required to place a hit on someone in diamonds.
     *
     * Default is 10 diamonds.
     */
    public static final int MINIMUM_HIT_PRICE = GlasshouseTweaks.getInstance().getConfig().getInt("minimum-hit-price");

    /**
     * The amount of cooldown time for buffering between hiring each hit
     * in minutes.
     *
     * Default is 2 hours.
     */
    public static final int HIRING_COOLDOWN = GlasshouseTweaks.getInstance().getConfig().getInt("hiring-cooldown");

    /**
     * The amount of cooldown time for buffering between contracting for
     * each hit in minutes.
     *
     * Default is 2 hours.
     */
    public static final int CONTRACTING_COOLDOWN = GlasshouseTweaks.getInstance().getConfig().getInt("contracting" +
            "-cooldown");

    /**
     * The amount of cooldown time for buffering between being targetted on
     * each hit in minutes.
     *
     * Default is 4 hours.
     */
    public static final int TARGETING_COOLDOWN = GlasshouseTweaks.getInstance().getConfig().getInt("targeting-cooldown");

    /**
     * The day designated as "anarchy day"
     */
    public static String ANARCHY_DAY = GlasshouseTweaks.getInstance().getConfig().getString("anarchy-day");

    /**
     * Global date output format
     */
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


    // Error messages
    public static final String USER_NOT_REGISTERED = "You are not a registered user!";
    public static final String TARGET_NOT_FOUND = "The target was not found in the registry. Either they are not a registered user, or" +
            " you spelled their name incorrectly.";
    public static final String INVALID_PRICE = "Your price has to be an integer.";
    public static final String PRICE_TOO_LOW = "Your price has to be greater than " + MINIMUM_HIT_PRICE + " diamonds.";
    public static final String CONTRACTOR_NOT_FOUND = "Your contractor was not found in the registry. Either they are not a " +
            "registered user, or you misspelled their name.";
    public static final String TARGET_IS_BUSY = "Your target already has an active hit out on them.";
    public static final String TOO_MANY_HITS = "You already have another hit placed on someone! Either wait until that hit" +
            " is completed, or remove that hit.";
    public static final String HIRER_NOT_FOUND = "The hirer was not found in the registry. Either they are not a registered " +
            "user, or you spelled their name incorrectly.";
    public static final String CONTRACTOR_UNDER_COOLDOWN = "Your contractor is currently under a cooldown.";
    public static final String HIRER_UNDER_COOLDOWN = "You are currently under a hiring cooldown.";
    public static final String TARGET_UNDER_COOLDOWN = "Your target is currently under a targetting cooldown.";
    public static final String HIRER_BUSY = "You already have an active hit! You cannot have more than one hit at " +
            "once!";
    public static final String USER_NOT_ADMIN = "This command requires you to be an admin.";
    public static final String PLAYER_NOT_FOUND = "The player you have selected was not found. Perhaps you have " +
            "misspelled their name.";
}
