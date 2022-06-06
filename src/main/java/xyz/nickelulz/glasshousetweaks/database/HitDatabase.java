package xyz.nickelulz.glasshousetweaks.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.checkerframework.checker.units.qual.A;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.datatypes.*;

import java.io.*;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class HitDatabase {
    ActiveHitsDatabase activeHitsDatabase;
    CompletedHitsDatabase completedHitsDatabase;

    public HitDatabase() {
        activeHitsDatabase = new ActiveHitsDatabase();
        completedHitsDatabase = new CompletedHitsDatabase();
    }

    public boolean containsHit(Hit hit) {
        for (Hit h: hits)
            if (hit.equals(h))
                return true;
        return false;
    }

    public Hit findHitByTarget(User target) {
        for (Hit h: hits)
            if (h.getTarget().equals(target))
                return h;
        return null;
    }

    public Bounty findBountyByTarget(User target) {
        for (Hit h: hits)
            if (h instanceof Bounty && h.getTarget().equals(target))
                return (Bounty) h;
        return null;
    }

    public Hit findHitByPlacer(User placer) {
        for (Hit h: hits)
            if (h.getPlacer().equals(placer))
                return h;
        return null;
    }

    public Contract findContract(User target, User contractor) {
        for (Hit h: hits)
            if (h instanceof Contract && ((Contract) h).getTarget().equals(target) && ((Contract) h).getContractor().equals(contractor))
                return (Contract) h;
        return null;
    }

    public boolean isTarget(User target) {
        return !(findHitByTarget(target) == null);
    }

    public boolean isPlacer(User user) {
        return !(findHitByPlacer(user) == null);
    }

    public boolean isActivePlacer(User user) {
        for (Hit h: hits)
            if (h.getPlacer().equals(user) && h instanceof Bounty ||
                    (h instanceof Contract && !((Contract) h).isPending()))
                return true;
        return false;
    }

    public boolean claim(User claimer, Hit hit) {
        boolean success = remove(hit);
        if (success) {
            claimer.setKills(claimer.getKills()+1);
            hit.getTarget().setDeaths(hit.getTarget().getDeaths()+1);
            success = completedHits.add(hit);
        }
        return success;
    }

    public ArrayList<Contract> getContracts(User contractor) {
        ArrayList<Contract> contracts = new ArrayList<>();
        for (Hit h: hits)
            if (h instanceof Contract && ((Contract) h).getContractor().equals(contractor))
                contracts.add((Contract) h);
        return contracts;
    }

    public ArrayList<Hit> getHits() {
        return hits;
    }
}

public class ActiveHitsDatabase extends Database<Hit> {

    public ActiveHitsDatabase() {
        super("hits.json", Hit.class, new JSONHandlers.HitJSON());
    }

    @Override
    public boolean reload() {
        return false;
    }

    @Override
    public boolean save() {
        return false;
    }
}

public class CompletedHitsDatabase extends Database<Hit> {
    public CompletedHitsDatabase() {
        super("completed_hits.json", Hit.class, new JSONHandlers.HitJSON());
    }

    @Override
    public boolean reload() {
        return false;
    }

    @Override
    public boolean save() {
        return false;
    }
}