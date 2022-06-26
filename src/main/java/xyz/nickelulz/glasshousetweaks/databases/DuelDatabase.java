package xyz.nickelulz.glasshousetweaks.databases;

import org.bukkit.OfflinePlayer;
import xyz.nickelulz.glasshousetweaks.datatypes.Duel;

public class DuelDatabase extends Database<Duel> {
    public DuelDatabase() {
        super("duels.json", Duel.class, Duel[].class, new JSONHandlers.DuelJSON());
    }

    public Duel findActiveDuel(OfflinePlayer a, OfflinePlayer b) {
        for (Duel d: getDataset())
            if (!d.isPending() && d.hasPlayers(a, b))
                return d;
        return null;
    }

    public Duel findActiveDuel(OfflinePlayer a) {
        for (Duel d: getDataset())
            if (!d.isPending() && d.hasPlayer(a))
                return d;
        return null;
    }

    public Duel findPendingDuel(OfflinePlayer a, OfflinePlayer b) {
        for (Duel d: getDataset())
            if (d.isPending() && d.hasPlayers(a, b))
                return d;
        return null;
    }

    public boolean hasActiveDuel(OfflinePlayer player) {
        for (Duel d: getDataset())
            if (!d.isPending() && d.hasPlayer(player))
                return true;
        return false;
    }
}
