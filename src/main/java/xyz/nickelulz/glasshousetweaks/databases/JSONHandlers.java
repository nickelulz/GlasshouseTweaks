package xyz.nickelulz.glasshousetweaks.databases;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.datatypes.*;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;

public class JSONHandlers {
    public static class UserJSON implements JsonSerializer<User>, JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject values = json.getAsJsonObject();
            String discordId = values.get("discordId").getAsString();

            // if online or offline
            UUID uuid = UUID.fromString(values.get("uuid").getAsString());
            OfflinePlayer profile = Bukkit.getOfflinePlayer(uuid);

            int kills = values.get("kills").getAsInt();
            int deaths = values.get("deaths").getAsInt();
            int morbiums = values.get("morbiums").getAsInt();

            String lastPlacedHitRaw = values.get("lastPlacedHit").getAsString();
            LocalDateTime lastPlacedHit = lastPlacedHitRaw == null || lastPlacedHitRaw.equalsIgnoreCase("none") ?
                    null : LocalDateTime.parse(lastPlacedHitRaw, ConfigurationConstants.DATA_DATE_FORMAT);

            String lastContractedHitRaw = values.get("lastContractedHit").getAsString();
            LocalDateTime lastContractedHit = lastContractedHitRaw == null || lastContractedHitRaw.equalsIgnoreCase("none") ?
                    null : LocalDateTime.parse(lastContractedHitRaw, ConfigurationConstants.DATA_DATE_FORMAT);

            String lastTargetedHitRaw = values.get("lastTargetedHit").getAsString();
            LocalDateTime lastTargetedHit =  lastTargetedHitRaw == null || lastTargetedHitRaw.equalsIgnoreCase("none") ?
                    null : LocalDateTime.parse(lastTargetedHitRaw, ConfigurationConstants.DATA_DATE_FORMAT);

            String lastWarRaw = values.get("lastWar").getAsString();
            LocalDateTime lastWar = lastWarRaw == null || lastWarRaw.equalsIgnoreCase("none") ? null :
                    LocalDateTime.parse(lastWarRaw, ConfigurationConstants.DATA_DATE_FORMAT);

            return new User(discordId, profile, lastContractedHit, lastTargetedHit, lastPlacedHit, lastWar, kills,
                    deaths, morbiums);
        }

        @Override
        public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("discordId", src.getDiscordId());
            json.addProperty("uuid", src.getProfile().getUniqueId().toString());
            json.addProperty("username", src.getProfile().getName());
            json.addProperty("kills", src.getKills());
            json.addProperty("deaths", src.getDeaths());
            json.addProperty("morbiums", src.getMorbiums());
            json.addProperty("lastPlacedHit", (src.getLastPlacedHit() == null) ? "none" :
                    src.getLastPlacedHit().format(ConfigurationConstants.DATA_DATE_FORMAT));
            json.addProperty("lastContractedHit", (src.getLastContractedHit() == null) ? "none" :
                    src.getLastContractedHit().format(ConfigurationConstants.DATA_DATE_FORMAT));
            json.addProperty("lastTargetedHit", (src.getLastTargetedHit() == null) ? "none" :
                    src.getLastTargetedHit().format(ConfigurationConstants.DATA_DATE_FORMAT));
            json.addProperty("lastWar", (src.getLastWar() == null) ? "none" :
                    src.getLastWar().format(ConfigurationConstants.DATA_DATE_FORMAT));
            return json;
        }

    }

    public static class HitJSON implements JsonSerializer<Hit>, JsonDeserializer<Hit> {
        @Override
        public Hit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Hit out;
            JsonObject in = json.getAsJsonObject();
            String type = in.get("type").getAsString();
            User placer = GlasshouseTweaks.getPlayersDatabase().findByIGN(in.get("placer").getAsString());
            User target = GlasshouseTweaks.getPlayersDatabase().findByIGN(in.get("target").getAsString());
            int price = in.get("price").getAsInt();
            LocalDateTime timePlaced = LocalDateTime.parse(in.get("timePlaced").getAsString(),
                    ConfigurationConstants.DATA_DATE_FORMAT);

            String claimerString = in.get("claimer").getAsString();
            boolean claimed = !claimerString.equals("none");

            if (type.equals("contract")) {
                User contractor = GlasshouseTweaks.getPlayersDatabase().findByIGN(in.get("contractor").getAsString());
                boolean pending = in.get("pending").getAsBoolean();

                if (claimed) {
                    User claimer = in.get("claimer").getAsString().equals("none") ? null :
                            GlasshouseTweaks.getPlayersDatabase().findByIGN(in.get("claimer").getAsString());
                    LocalDateTime timeClaimed = in.get("timeClaimed").getAsString().equals("none") ? null :
                            LocalDateTime.parse(in.get("timeClaimed").getAsString(), ConfigurationConstants.DATA_DATE_FORMAT);
                    out = new Contract(placer, target, price, timePlaced, contractor, timeClaimed, claimer);
                } else {
                    out = new Contract(placer, target, price, timePlaced, contractor, pending);
                }
            }
            else { // bounty
                if (claimed) {
                    User claimer = in.get("claimer").getAsString().equals("none") ? null :
                            GlasshouseTweaks.getPlayersDatabase().findByIGN(in.get("claimer").getAsString());
                    LocalDateTime timeClaimed = in.get("timeClaimed").getAsString().equals("none") ? null :
                            LocalDateTime.parse(in.get("timeClaimed").getAsString(), ConfigurationConstants.DATA_DATE_FORMAT);
                    out = new Bounty(placer, target, price, timePlaced, claimer, timeClaimed);
                } else {
                    out = new Bounty(placer, target, price, timePlaced);
                }
            }

            return out;
        }

        @Override
        public JsonElement serialize(Hit src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("placer", src.getPlacer().getProfile().getName());
            json.addProperty("target", src.getTarget().getProfile().getName());
            json.addProperty("price", src.getPrice());
            json.addProperty("timePlaced", src.getTimePlaced().format(ConfigurationConstants.DATA_DATE_FORMAT));
            json.addProperty("claimer", (src.getClaimer() == null) ? "none" : src.getClaimer().getProfile().getName());
            json.addProperty("timeClaimed", (src.getTimeClaimed() == null) ? "none" :
                    src.getTimeClaimed().format(ConfigurationConstants.DATA_DATE_FORMAT));
            if (src instanceof Contract) {
                Contract contract = (Contract) src;
                json.addProperty("type", "contract");
                json.addProperty("contractor", contract.getContractor().getProfile().getName());
                json.addProperty("pending", contract.isPending());
            } else if (src instanceof Bounty){
                json.addProperty("type", "bounty");
            }
            return json;
        }
    }

    public static class AttackJSON implements JsonSerializer<Attack>, JsonDeserializer<Attack> {
        @Override
        public Attack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject in = json.getAsJsonObject();
            OfflinePlayer attacker = Bukkit.getOfflinePlayer( UUID.fromString(in.get("attackerUUID").getAsString()) );
            OfflinePlayer victim = Bukkit.getOfflinePlayer( UUID.fromString(in.get("victimUUID").getAsString()) );
            String attackerName = in.get("attackerName").getAsString();
            String victimName = in.get("victimName").getAsString();
            LocalDateTime timeCommitted = LocalDateTime.parse(in.get("timeCommitted").getAsString(),
                    ConfigurationConstants.DATA_DATE_FORMAT);
            return new Attack(attacker, attackerName, victim, victimName, timeCommitted);
        }

        @Override
        public JsonElement serialize(Attack src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("attackerUUID", src.getAttacker().getUniqueId().toString());
            json.addProperty("attackerName", src.getAttackerName());
            json.addProperty("victimUUID", src.getVictim().getUniqueId().toString());
            json.addProperty("victimName", src.getVictimName());
            json.addProperty("timeCommitted", src.getTime().format(ConfigurationConstants.DATA_DATE_FORMAT));
            return json;
        }
    }

    public static class DuelJSON implements JsonSerializer<Duel>, JsonDeserializer<Duel> {
        @Override
        public Duel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject in = json.getAsJsonObject();
            OfflinePlayer attackerA = Bukkit.getOfflinePlayer(UUID.fromString(in.get("initiator_uuid").getAsString()));
            String attackerAName = in.get("initiator_name").getAsString();
            OfflinePlayer attackerB = Bukkit.getOfflinePlayer(UUID.fromString(in.get("participator_uuid").getAsString()));
            String attackerBName = in.get("participator_name").getAsString();
            LocalDateTime time = LocalDateTime.parse(in.get("time").getAsString(),
                    ConfigurationConstants.DATA_DATE_FORMAT);
            boolean pending = in.get("pending").getAsBoolean();
            return new Duel(attackerA, attackerAName, attackerB, attackerBName, time, pending);
        }

        @Override
        public JsonElement serialize(Duel src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("initiator_uuid", src.getInitiator().getUniqueId().toString());
            json.addProperty("initiator_name", src.getInitiatorName());
            json.addProperty("participator_uuid", src.getParticipator().getUniqueId().toString());
            json.addProperty("participator_name", src.getParticipatorName());
            json.addProperty("time", src.getTime().format(ConfigurationConstants.DATA_DATE_FORMAT));
            json.addProperty("pending", src.isPending());
            return json;
        }
    }

    public static class WarJSON implements JsonSerializer<War>, JsonDeserializer<War> {
        private static Gson GSON = new Gson();
        @Override
        public War deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject in = json.getAsJsonObject();
            OfflinePlayer attacking_commander = Bukkit.getOfflinePlayer(
                    UUID.fromString(in.get("attacking_commander").getAsString()));
            User attacking_commander_user = GlasshouseTweaks.getPlayersDatabase()
                    .findByProfile((Player) attacking_commander);
            OfflinePlayer defending_commander = Bukkit.getOfflinePlayer(
                    UUID.fromString(in.get("defending_commander").getAsString()));
            User defending_commander_user = GlasshouseTweaks.getPlayersDatabase()
                    .findByProfile((Player) defending_commander);

            // Attacking Army Deserialization
            Map<User, Integer> attacking_army = new TreeMap<>();
            JsonObject attacking_army_json = in.getAsJsonObject("attacking_army");
            for (Map.Entry<String, JsonElement> entry: attacking_army_json.entrySet())
                attacking_army.put(GlasshouseTweaks.getPlayersDatabase().findByProfile(Bukkit.getPlayer(UUID
                        .fromString(entry.getKey()))), entry.getValue().getAsInt());

            // Defending Army Deserialization
            Map<User, Integer> defending_army = new TreeMap<>();
            JsonObject defending_army_json = in.getAsJsonObject("defending_army");
            for (Map.Entry<String, JsonElement> entry: defending_army_json.entrySet())
                defending_army.put(GlasshouseTweaks.getPlayersDatabase().findByProfile(Bukkit.getPlayer(UUID
                        .fromString(entry.getKey()))), entry.getValue().getAsInt());

            LocalDateTime time_declared = LocalDateTime.parse(in.get("time_declared").getAsString(),
                    ConfigurationConstants.DATA_DATE_FORMAT);

            boolean attacking_commander_over = in.get("ac_over_vote").getAsBoolean();
            boolean defending_commander_over = in.get("dc_over_vote").getAsBoolean();

            return new War(attacking_commander_user, defending_commander_user, attacking_army, defending_army,
                    time_declared, attacking_commander_over, defending_commander_over);
        }

        @Override
        public JsonElement serialize(War src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("attacking_commander", src.getAttackingCommander().getProfile().getUniqueId().toString());
            json.addProperty("defending_commander", src.getDefendingCommander().getProfile().getUniqueId().toString());

            // Attacking Army Serialization
            JsonObject attacking_army = new JsonObject();
            for (User player: src.getAttackingArmy().keySet())
                attacking_army.addProperty(player.getProfile().getName(), src.getAttackingArmy().get(player));
            json.add("attacking_army", GSON.toJsonTree(src.getAttackingArmyUUIDS()).getAsJsonArray());

            // Defending Army Serialization
            JsonObject defending_army = new JsonObject();
            for (User player: src.getDefendingArmy().keySet())
                defending_army.addProperty(player.getProfile().getName(), src.getDefendingArmy().get(player));
            json.add("defending_army", GSON.toJsonTree(src.getDefendingArmyUUIDS()).getAsJsonArray());

            json.addProperty("ac_over_vote", src.attacking_commander_vote());
            json.addProperty("dc_over_vote", src.defending_commander_vote());
            json.addProperty("time_declared", src.getTimeDeclared().format(ConfigurationConstants.DATA_DATE_FORMAT));
            return json;
        }
    }
}
