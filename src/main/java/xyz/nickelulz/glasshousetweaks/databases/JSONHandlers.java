package xyz.nickelulz.glasshousetweaks.databases;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.datatypes.*;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.UUID;

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
                    null : LocalDateTime.parse(lastPlacedHitRaw, ConfigurationConstants.DATE_FORMAT);

            String lastContractedHitRaw = values.get("lastContractedHit").getAsString();
            LocalDateTime lastContractedHit = lastContractedHitRaw == null || lastContractedHitRaw.equalsIgnoreCase("none") ?
                    null : LocalDateTime.parse(lastContractedHitRaw, ConfigurationConstants.DATE_FORMAT);

            String lastTargetedHitRaw = values.get("lastTargetedHit").getAsString();
            LocalDateTime lastTargetedHit =  lastTargetedHitRaw == null || lastTargetedHitRaw.equalsIgnoreCase("none") ?
                    null : LocalDateTime.parse(lastTargetedHitRaw, ConfigurationConstants.DATE_FORMAT);

            return new User(discordId, profile, lastContractedHit, lastTargetedHit, lastPlacedHit, kills, deaths,
                    morbiums);
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
                    src.getLastPlacedHit().format(ConfigurationConstants.DATE_FORMAT));
            json.addProperty("lastContractedHit", (src.getLastContractedHit() == null) ? "none" :
                    src.getLastContractedHit().format(ConfigurationConstants.DATE_FORMAT));
            json.addProperty("lastTargetedHit", (src.getLastTargetedHit() == null) ? "none" :
                    src.getLastTargetedHit().format(ConfigurationConstants.DATE_FORMAT));
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
                    ConfigurationConstants.DATE_FORMAT);

            String claimerString = in.get("claimer").getAsString();
            boolean claimed = !claimerString.equals("none");

            if (type.equals("contract")) {
                User contractor = GlasshouseTweaks.getPlayersDatabase().findByIGN(in.get("contractor").getAsString());
                boolean pending = in.get("pending").getAsBoolean();

                if (claimed) {
                    User claimer = in.get("claimer").getAsString().equals("none") ? null :
                            GlasshouseTweaks.getPlayersDatabase().findByIGN(in.get("claimer").getAsString());
                    LocalDateTime timeClaimed = in.get("timeClaimed").getAsString().equals("none") ? null :
                            LocalDateTime.parse(in.get("timeClaimed").getAsString(), ConfigurationConstants.DATE_FORMAT);
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
                            LocalDateTime.parse(in.get("timeClaimed").getAsString(), ConfigurationConstants.DATE_FORMAT);
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
            json.addProperty("timePlaced", src.getTimePlaced().format(ConfigurationConstants.DATE_FORMAT));
            json.addProperty("claimer", (src.getClaimer() == null) ? "none" : src.getClaimer().getProfile().getName());
            json.addProperty("timeClaimed", (src.getTimeClaimed() == null) ? "none" :
                    src.getTimeClaimed().format(ConfigurationConstants.DATE_FORMAT));
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
            Player attacker = Bukkit.getPlayer(in.get("attacker").getAsString());
            Player victim = Bukkit.getPlayer(in.get("victim").getAsString());
            LocalDateTime timeCommitted = LocalDateTime.parse(in.get("timeCommitted").getAsString(),
                    ConfigurationConstants.DATE_FORMAT);
            return new Attack(attacker, victim, timeCommitted);
        }

        @Override
        public JsonElement serialize(Attack src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("attacker", src.getAttacker().getName());
            json.addProperty("victim", src.getVictim().getName());
            json.addProperty("timeCommitted", src.getTime().format(ConfigurationConstants.DATE_FORMAT));
            return json;
        }
    }
}
