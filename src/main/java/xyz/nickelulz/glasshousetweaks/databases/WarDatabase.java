package xyz.nickelulz.glasshousetweaks.databases;

import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.datatypes.War;

public class WarDatabase extends Database<War> {
    public WarDatabase() {
        super("wars.json", War.class, War[].class, new JSONHandlers.WarJSON());
    }

    public boolean currentlyEnlisted(User player) {
        return !(findWarByParticipant(player) == null);
    }

    public War findWarByParticipant(User player) {
        for (War war: getDataset())
            if (war.containsPlayer(player))
                return war;
        return null;
    }

    public War findWarByOpposingParticipants(User playerA, User playerB) {
        for (War war: getDataset())
            if ((war.getAttackingArmy().containsKey(playerA) && war.getDefendingArmy().containsKey(playerB)) ||
                    (war.getAttackingArmy().containsKey(playerB) && war.getDefendingArmy().containsKey(playerA)))
                return war;
        return null;
    }
}
