package xyz.nickelulz.glasshousetweaks.databases;

import xyz.nickelulz.glasshousetweaks.datatypes.Attack;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

public class IllegalKillsDatabase extends Database<Attack> {

    public IllegalKillsDatabase() {
        super("illegalkills.json", Attack.class, Attack[].class, new JSONHandlers.AttackJSON(), true);
    }

    public Attack find(User attacker, User victim) {
        for (Attack a: getDataset())
            if (a.getAttacker().equals(attacker) && a.getVictim().equals(victim))
                return a;
        return null;
    }
}
