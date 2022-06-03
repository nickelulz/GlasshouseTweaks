package xyz.nickelulz.glasshousetweaks.util;

import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;

public class Configuration {
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
}
