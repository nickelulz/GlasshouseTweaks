package xyz.nickelulz.glasshousetweaks.util;

import com.google.gson.*;
import com.google.gson.stream.JsonToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.datatypes.Bounty;
import xyz.nickelulz.glasshousetweaks.datatypes.Contract;
import xyz.nickelulz.glasshousetweaks.datatypes.Hit;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class JSONHandlers {
    public static class UserJSON implements JsonSerializer<User>, JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject values = json.getAsJsonObject();
            String discordId = values.get("discordId").getAsString();
            Player profile = Bukkit.getPlayer(values.get("profile").getAsString());
            int kills = values.get("kills").getAsInt();
            int deaths = values.get("deaths").getAsInt();

            // date parsing
            String lastPlacedHitRaw = values.get("lastPlacedHit").getAsString();
            LocalDateTime lastPlacedHit = (lastPlacedHitRaw.equalsIgnoreCase("none") || lastPlacedHitRaw == null) ?
                    null : LocalDateTime.parse(lastPlacedHitRaw);

            String lastContractedHitRaw = values.get("lastContractedHitRaw").getAsString();
            LocalDateTime lastContractedHit =
                    (lastContractedHitRaw.equalsIgnoreCase("none") || lastContractedHitRaw == null) ?
                    null : LocalDateTime.parse(lastContractedHitRaw);

            String lastTargetedHitRaw = values.get("lastTargetedHit").getAsString();
            LocalDateTime lastTargetedHit = (lastTargetedHitRaw.equalsIgnoreCase("none")|| lastTargetedHitRaw == null) ?
                    null : LocalDateTime.parse(lastTargetedHitRaw);

            return new User(discordId, profile, lastContractedHit, lastTargetedHit, lastPlacedHit, kills, deaths);
        }

        @Override
        public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("discordId", src.getDiscordId());
            json.addProperty("profile", src.getProfile().getName());
            json.addProperty("kills", src.getKills());
            json.addProperty("deaths", src.getDeaths());
            json.addProperty("lastPlacedHit", (src.getLastPlacedHit() == null) ? "none" : src.getLastPlacedHit().toString());
            json.addProperty("lastContractedHit", (src.getLastContractedHit() == null) ? "none" :
                    src.getLastContractedHit().toString());
            json.addProperty("lastTargetedHit", (src.getLastTargetedHit() == null) ? "none" :
                    src.getLastTargetedHit().toString());
            return json;
        }

    }

    public static abstract class HitJSON implements JsonSerializer<Hit>, JsonDeserializer<Hit> {
        @Override
        public Hit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return null;
        }

        @Override
        public JsonElement serialize(Hit src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("placer", src.getPlacer().getProfile().getName());
            json.addProperty("target", src.getTarget().getProfile().getName());
            json.addProperty("price", src.getPrice());
            json.addProperty("timePlaced", src.getTimePlaced().toString());
            json.addProperty("claimer", (src.getClaimer() == null) ? "none" : src.getClaimer().getProfile().getName());
            json.addProperty("timeClaimed", (src.getTimeClaimed() == null) ? "none" : src.getTimeClaimed().toString());
            return json;
        }
    }

    public static class ContractJSON extends HitJSON {
        @Override
        public Hit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return super.deserialize(json, typeOfT, context);
        }

        @Override
        public JsonElement serialize(Hit src, Type typeOfSrc, JsonSerializationContext context) {
            Contract contract = (Contract) src;
            JsonObject json = (JsonObject) super.serialize(contract, typeOfSrc, context);
            json.addProperty("type", "contract");
            json.addProperty("contractor", contract.getContractor().getProfile().getName());
            json.addProperty("pending", contract.isPending());
            return json;
        }
    }

    public static class BountyJSON extends HitJSON {
        @Override
        public Hit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return super.deserialize(json, typeOfT, context);
        }

        @Override
        public JsonElement serialize(Hit src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = (JsonObject) super.serialize(src, typeOfSrc, context);
            Bounty bounty = (Bounty) src;
            json.addProperty("type", "bounty");
            return json;
        }
    }
}
