package xyz.nickelulz.glasshousetweaks.datatypes;

import org.bukkit.OfflinePlayer;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static xyz.nickelulz.glasshousetweaks.util.Utils.playerEquals;
import static xyz.nickelulz.glasshousetweaks.util.Utils.timeSinceDate;

public class Duel {
    private OfflinePlayer initiator;
    private String initiator_name;
    private OfflinePlayer participator;
    private String participator_name;
    private LocalDateTime time;
    private boolean pending;

    public Duel(OfflinePlayer initiator,  OfflinePlayer participator) {
        this(initiator, participator, LocalDateTime.now(), true);
    }

    public Duel(OfflinePlayer initiator,  OfflinePlayer participator, LocalDateTime time, boolean pending) {
        this(initiator, initiator.getName(), participator, participator.getName(), time, pending);
    }

    public Duel(OfflinePlayer initiator, String initiator_name, OfflinePlayer participator,
                String participator_name, LocalDateTime time, boolean pending) {
        this.initiator = initiator;
        this.initiator_name = initiator_name;
        this.participator = participator;
        this.participator_name = participator_name;
        this.time = time;
        this.pending = pending;
    }

    public void accept() {
        pending = false;
        GlasshouseTweaks.getIllegalkillDatabase().save();
    }

    public boolean hasPlayer(OfflinePlayer player) {
        return this.initiator.getUniqueId().equals(player.getUniqueId()) ||
                this.participator.getUniqueId().equals(player.getUniqueId());
    }

    public boolean hasPlayers(OfflinePlayer playerA, OfflinePlayer playerB) {
        return hasPlayer(playerA) && hasPlayer(playerB);
    }

    public int timeLeft() {
        // 5 minute pending time.
        return Math.max((5 * 60 * 1000) - ((int)timeSinceDate(time)), 0);
    }

    public OfflinePlayer getInitiator() {
        return initiator;
    }

    public OfflinePlayer getOtherFighter(OfflinePlayer in) {
        if (playerEquals(in, initiator))
            return participator;
        else if (playerEquals(in, participator))
            return initiator;
        return null;
    }

    public String getInitiatorName() {
        return initiator_name;
    }

    public OfflinePlayer getParticipator() {
        return participator;
    }

    public String getParticipatorName() {
        return participator_name;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public boolean isPending() {
        return pending;
    }
}
