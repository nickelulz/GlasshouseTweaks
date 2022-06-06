package xyz.nickelulz.glasshousetweaks.datatypes;

import com.google.gson.*;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Objects;

abstract public class Hit {
    private User placer, target, claimer;
    private LocalDateTime timePlaced, timeClaimed;
    private int price;

    /**
     * Unclaimed Hit Constructor (New hit)
     * @param placer Player that placed this hit
     * @param target The target of this hit
     * @param price The price of this hit
     * @param timePlaced When the hit was placed initially
     */
    public Hit(User placer, User target, int price, LocalDateTime timePlaced) {
        this.placer = placer;
        this.target = target;
        this.price = price;
        this.timePlaced = timePlaced;
    }

    /**
     * Claimed hit constructor
     * @param placer Player that placed this hit
     * @param target The target of this hit
     * @param price The price of this hit
     * @param timePlaced When the hit was placed initially
     * @param claimer The player that claimed this hit
     * @param timeClaimed When this hit was claimed
     */
    public Hit(User placer, User target, int price, LocalDateTime timePlaced, User claimer, LocalDateTime timeClaimed) {
        this(placer, target, price, timePlaced);
        this.claimer = claimer;
        this.timeClaimed = timeClaimed;
    }

    @Override
    public String toString() {
        return String.format("Placed by %s at %s on %s for %d diamonds.", placer.getProfile().getName(),
                timePlaced.format(ConfigurationConstants.DATE_FORMAT), target.getProfile().getName(), price);
    }

    public String toSimpleString() {
        return String.format("Target: %s, Placed by %s for %d diamonds.", target.getProfile().getName(),
                placer.getProfile().getName(), price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Hit))
            return false;
        Hit other = (Hit) o;

        if (claimer == null || timeClaimed == null) {
            return price == other.price &&
                    placer.equals(other.placer) &&
                    target.equals(other.target) &&
                    timePlaced.equals(other.timePlaced);
        }
        else {
            return price == other.price &&
                    placer.equals(other.placer) &&
                    target.equals(other.target) &&
                    timePlaced.equals(other.timePlaced) &&
                    claimer.equals(other.claimer) &&
                    timeClaimed.equals(other.timeClaimed);
        }
    }

    /**
     * GETTERS AND SETTERS
     */

    public User getPlacer() {
        return placer;
    }

    public void setPlacer(User placer) {
        this.placer = placer;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public User getClaimer() {
        return claimer;
    }

    public void setClaimer(User claimer) {
        this.claimer = claimer;
    }

    public LocalDateTime getTimePlaced() {
        return timePlaced;
    }

    public void setTimePlaced(LocalDateTime timePlaced) {
        this.timePlaced = timePlaced;
    }

    public LocalDateTime getTimeClaimed() {
        return timeClaimed;
    }

    public void setTimeClaimed(LocalDateTime timeClaimed) {
        this.timeClaimed = timeClaimed;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
