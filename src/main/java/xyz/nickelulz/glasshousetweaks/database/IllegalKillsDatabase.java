package xyz.nickelulz.glasshousetweaks.database;

import xyz.nickelulz.glasshousetweaks.datatypes.Attack;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

public class IllegalKillsDatabase extends Database<Attack> {

    public IllegalKillsDatabase() {
        super("illegalkills.json", Attack.class, new JSONHandlers.AttackJSON());
    }

    @Override
    public boolean reload() {
        return false;
    }

    @Override
    public boolean save() {
        return false;
    }

    public Attack find(User attacker, User victim) {
        for (Attack a: getDataset())
            if (a.getAttacker().equals(attacker) && a.getVictim().equals(victim))
                return a;
        return null;
    }
}
