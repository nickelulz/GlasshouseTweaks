package xyz.nickelulz.glasshousetweaks.databases;

import xyz.nickelulz.glasshousetweaks.datatypes.*;

import java.util.ArrayList;

public class HitDatabase {
    ActiveHitsDatabase activeHitsDatabase;
    CompletedHitsDatabase completedHitsDatabase;

    public HitDatabase() {
        activeHitsDatabase = new ActiveHitsDatabase();
        completedHitsDatabase = new CompletedHitsDatabase();
    }

    public ActiveHitsDatabase getActiveHitsDatabase() {
        return activeHitsDatabase;
    }

    public CompletedHitsDatabase getCompletedHitsDatabase() {
        return completedHitsDatabase;
    }

    public boolean add(Hit input) {
        return activeHitsDatabase.add(input);
    }

    public boolean remove(Hit input) {
        return activeHitsDatabase.remove(input);
    }

    public boolean containsHit(Hit hit) {
        for (Hit h: activeHitsDatabase.getDataset())
            if (hit.equals(h))
                return true;
        return false;
    }

    public Hit findHitByTarget(User target) {
        for (Hit h: activeHitsDatabase.getDataset())
            if (h.getTarget().equals(target))
                return h;
        return null;
    }

    public Bounty findBountyByTarget(User target) {
        for (Hit h: activeHitsDatabase.getDataset())
            if (h instanceof Bounty && h.getTarget().equals(target))
                return (Bounty) h;
        return null;
    }

    public Hit findHitByPlacer(User placer) {
        for (Hit h: activeHitsDatabase.getDataset())
            if (h.getPlacer().equals(placer))
                return h;
        return null;
    }

    public Contract findContract(User target, User contractor) {
        for (Hit h: activeHitsDatabase.getDataset())
            if (h instanceof Contract && h.getTarget().equals(target) && ((Contract) h).getContractor().equals(contractor))
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
        for (Hit h: activeHitsDatabase.getDataset())
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
            success = completedHitsDatabase.add(hit);
        }
        return success;
    }

    public ArrayList<Contract> getContracts(User contractor) {
        ArrayList<Contract> contracts = new ArrayList<>();
        for (Hit h: activeHitsDatabase.getDataset())
            if (h instanceof Contract && ((Contract) h).getContractor().equals(contractor))
                contracts.add((Contract) h);
        return contracts;
    }

    public ArrayList<Hit> getActiveHits() {
        return activeHitsDatabase.getDataset();
    }
}

class ActiveHitsDatabase extends Database<Hit> {
    public ActiveHitsDatabase() {
        super("hits.json", Hit.class, Hit[].class, new JSONHandlers.HitJSON());
    }
}

class CompletedHitsDatabase extends Database<Hit> {
    public CompletedHitsDatabase() {
        super("completed_hits.json", Hit.class, Hit[].class, new JSONHandlers.HitJSON());
    }
}