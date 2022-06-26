package xyz.nickelulz.glasshousetweaks.util;

import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;

import java.time.format.DateTimeFormatter;

public class ConfigurationConstants {
    public static final String SERVER_NAME = GlasshouseTweaks.getInstance().getConfig().getString("server-name");
    public static final int MINIMUM_HIT_PRICE = GlasshouseTweaks.getInstance().getConfig().getInt("minimum-hit-price");
    public static final int MAXIMUM_HIT_PRICE = GlasshouseTweaks.getInstance().getConfig().getInt("maximum-hit-price");
    public static final int HIRING_COOLDOWN = GlasshouseTweaks.getInstance().getConfig().getInt("hiring-cooldown");
    public static final int CONTRACTING_COOLDOWN = GlasshouseTweaks.getInstance().getConfig().getInt("contracting-cooldown");
    public static final int TARGETING_COOLDOWN = GlasshouseTweaks.getInstance().getConfig().getInt("targeting-cooldown");
    public static final int WAR_COOLDOWN = GlasshouseTweaks.getInstance().getConfig().getInt("war-cooldown");
    public static final String TARGET_WARNING = GlasshouseTweaks.getInstance().getConfig().getString("target-warning");
    public static String ANARCHY_DAY = GlasshouseTweaks.getInstance().getConfig().getString("anarchy-day").toLowerCase();
    public static final DateTimeFormatter VISUAL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    public static final DateTimeFormatter DATA_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final int LISTENER_PORT = GlasshouseTweaks.getInstance().getConfig().getInt("listener-port");
    public static final int BOT_PORT = GlasshouseTweaks.getInstance().getConfig().getInt("bot-port");
    public static final String adminPermission = GlasshouseTweaks.getInstance().getConfig().getString("admin-perm");

    // Error Messages
    public static final String USER_NOT_REGISTERED = GlasshouseTweaks.getInstance().getConfig().getString("user-not-registered");
    public static final String USER_NOT_ADMIN = GlasshouseTweaks.getInstance().getConfig().getString("user-not-admin");

    public static final String CONTRACTOR_NOT_FOUND = GlasshouseTweaks.getInstance().getConfig().getString("contractor-not-found");
    public static final String TARGET_NOT_FOUND = GlasshouseTweaks.getInstance().getConfig().getString("target-not-found");
    public static final String HIRER_NOT_FOUND = GlasshouseTweaks.getInstance().getConfig().getString("hirer-not-found");
    public static final String PLAYER_NOT_FOUND = GlasshouseTweaks.getInstance().getConfig().getString("player-not-found");

    public static final String INVALID_AMOUNT = GlasshouseTweaks.getInstance().getConfig().getString("invalid-amount");
    public static final String PRICE_TOO_LOW = GlasshouseTweaks.getInstance().getConfig().getString("price-too-low");
    public static final String PRICE_TOO_HIGH = GlasshouseTweaks.getInstance().getConfig().getString("price-too-high") +
            " The maximum price limit is set at " + MAXIMUM_HIT_PRICE + " diamonds.";

    public static final String TARGET_IS_BUSY = GlasshouseTweaks.getInstance().getConfig().getString("target-is-busy");
    public static final String USER_BUSY = GlasshouseTweaks.getInstance().getConfig().getString("user-busy");
    public static final String TOO_MANY_HITS = GlasshouseTweaks.getInstance().getConfig().getString("too-many-hits");

    public static final String CONTRACTOR_UNDER_COOLDOWN = GlasshouseTweaks.getInstance().getConfig().getString("contractor-under-cooldown");
    public static final String HIRER_UNDER_COOLDOWN = GlasshouseTweaks.getInstance().getConfig().getString("hirer-under-cooldown");
    public static final String TARGET_UNDER_COOLDOWN = GlasshouseTweaks.getInstance().getConfig().getString("target-under-cooldown");

    public static final String HIRER_IS_TARGET = GlasshouseTweaks.getInstance().getConfig().getString("hirer-is-target");
    public static final String CONTRACTOR_IS_TARGET = GlasshouseTweaks.getInstance().getConfig().getString("contractor-is-target");
    public static final String HIRER_IS_CONTRACTOR = GlasshouseTweaks.getInstance().getConfig().getString("hirer-is-contractor");
}
