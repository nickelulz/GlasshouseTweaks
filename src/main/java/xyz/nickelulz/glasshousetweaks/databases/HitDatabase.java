package xyz.nickelulz.glasshousetweaks.databases;

import org.bukkit.ChatColor;
import xyz.nickelulz.glasshousetweaks.datatypes.*;

import java.time.LocalDateTime;
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
            if (h.getTarget().equals(target) && h instanceof Bounty || (h instanceof Contract && !((Contract)h).isPending()))
                return h;
        return null;
    }

    public Bounty findBountyByTarget(User target) {
        for (Hit h: activeHitsDatabase.getDataset())
            if (h instanceof Bounty && h.getTarget().equals(target))
                return (Bounty) h;
        return null;
    }

    public ArrayList<Bounty> getBounties() {
        ArrayList<Bounty> out = new ArrayList<>();
        for (Hit h: activeHitsDatabase.getDataset())
            if (h instanceof Bounty)
                out.add((Bounty) h);
        return out;
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

    public boolean isTarget(User user) {
        return !(findHitByTarget(user) == null);
    }

    public boolean isPlacer(User user) {
        return !(findHitByPlacer(user) == null);
    }

    public Contract findActiveContract(User contractor) {
        for (Hit h: activeHitsDatabase.getDataset())
            if (h instanceof Contract && !((Contract) h).isPending() && ((Contract)h).getContractor().equals(contractor))
                return (Contract) h;
        return null;
    }

    public boolean isContractor(User contractor) {
        return !(findActiveContract(contractor) == null);
    }

    public boolean isActivePlacer(User user) {
        for (Hit h: activeHitsDatabase.getDataset())
            if (h.getPlacer().equals(user) && h instanceof Bounty ||
                    (h instanceof Contract && !((Contract) h).isPending()))
                return true;
        return false;
    }

    public boolean claim(Hit hit) {
        boolean success = remove(hit) && completedHitsDatabase.add(hit);
        if (success) {
            hit.getPlacer().directMessage(String.format("Your hit on %s for %d diamonds was claimed by %s " +
                    "successfully.", hit.getTarget().getProfile().getName(), hit.getPrice(),
                    hit.getClaimer().getProfile().getName()), ChatColor.GREEN);
            hit.getTarget().directMessage(String.format("%s executed a hit on you! All's fair in war...",
                    hit.getClaimer().getProfile().getName()), ChatColor.RED);
            hit.getClaimer().directMessage(String.format("Successfully claimed hit on %s for %d diamonds!",
                    hit.getTarget().getProfile().getName(), hit.getPrice()), ChatColor.GREEN);
            hit.getPlacer().increment("morbiums", 1);
            hit.getTarget().setLastTargetedHit(LocalDateTime.now());
            hit.getPlacer().setLastPlacedHit(LocalDateTime.now());
            if (hit instanceof Contract)
                ((Contract) hit).getContractor().setLastContractedHit(LocalDateTime.now());
        }
        return success;
    }

    public boolean save() {
        return activeHitsDatabase.save() && completedHitsDatabase.save();
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