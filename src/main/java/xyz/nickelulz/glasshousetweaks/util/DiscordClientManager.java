package xyz.nickelulz.glasshousetweaks.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import xyz.nickelulz.glasshousetweaks.GlasshouseTweaks;
import xyz.nickelulz.glasshousetweaks.databases.Database;
import xyz.nickelulz.glasshousetweaks.databases.JSONHandlers;
import xyz.nickelulz.glasshousetweaks.datatypes.User;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DiscordClientManager {
    public static boolean bot_connected = false;
    private static final String API_URL = "http://127.0.0.1:" + ConfigurationConstants.BOT_PORT;
    private static HttpServer server;
    private static java.net.http.HttpClient client;

    public static void initialize() {
        try {
            server = HttpServer.create(new InetSocketAddress(ConfigurationConstants.LISTENER_PORT), 0);
            GlasshouseTweaks.log(Level.INFO, "Webserver started. Listening on http://127.0.0.1:"
                    + ConfigurationConstants.LISTENER_PORT);
            server.createContext("/players", new GetPlayerDR());
            server.createContext("/activehits", new GetActiveHitsDR());
            server.createContext("/completedhits", new GetCompletedHitsDR());
            server.createContext("/illegalkills", new GetIllegalKillsDR());
            server.createContext("/init", new BotConnect());
            try {
                for (User user: GlasshouseTweaks.getPlayersDatabase().getDataset()) {
                    if (user.getProfile().getName() != null)
                        server.createContext("/" + user.getProfile().getName().toLowerCase().replace("_", ""),
                                new GetIndividualPlayerRequest(user));
                }
            } catch (Exception e) {
                GlasshouseTweaks.log(Level.SEVERE, "Cannot load individual player endpoints.");
            }
            server.setExecutor(null);
            server.start();
            client = java.net.http.HttpClient.newHttpClient();
        } catch (IOException e) {
            GlasshouseTweaks.log(Level.INFO ,"Cannot initialize webserver connection.");
            e.printStackTrace();
        }
    }

    private static String apiPOST(String endpoint, String json) throws IOException {
        URL url = new URL("http://localhost:3225/" + endpoint);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST"); // PUT is another valid option
        http.setDoOutput(true);

        byte[] out = json.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

        String response = null;
        try (InputStream stream = http.getInputStream()) {
            response = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        return response;
    }

    public static String registerRequest(String discordName) {
        try {
            String out = apiPOST("register/name", "{\"name\":\"" + discordName + "\"}");
            return out;
        } catch (Exception e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not register user " + discordName);
            e.printStackTrace();
            return null;
        }
    }

    public static void sendDirectMessageRequest(String message, String id) {
        try {
            apiPOST("dm/","{\"id\":\"" + id + "\",\"message\":\"" + message + "\"}");
        } catch (Exception e) {
            GlasshouseTweaks.log(Level.SEVERE, "Could not DM user " + id);
            e.printStackTrace();
        }
    }

    private static class BotConnect implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            GlasshouseTweaks.log(Level.INFO, "Bot connected to server API.");
            String response = "Success";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream output = exchange.getResponseBody();
            output.write(response.getBytes(StandardCharsets.UTF_8));
            output.close();
            bot_connected = true;
            exchange.close();
        }
    }

    private static class DatabaseRequest implements HttpHandler {
        private Database database;
        private String name;
        public DatabaseRequest(String name, Database database) {
            this.database = database;
            this.name = name;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            GlasshouseTweaks.log(Level.INFO, "Recieved incoming " + name + " database request from discord bot.");
            String response = database.toJSON();

            if (response == null) {
                GlasshouseTweaks.log(Level.SEVERE, "Could not get raw JSON string from " + name + " database.");
                exchange.sendResponseHeaders(500, response.length());
            } else {
                GlasshouseTweaks.log(Level.INFO, "Returning " + name + " database JSON to discord bot.");
                exchange.sendResponseHeaders(200, response.length());
            }

            OutputStream output = exchange.getResponseBody();
            output.write(response.getBytes(StandardCharsets.UTF_8));
            output.close();
        }
    }

    private static class GetPlayerDR extends DatabaseRequest {
        public GetPlayerDR() {
            super("players", GlasshouseTweaks.getPlayersDatabase());
        }
    }

    private static class GetActiveHitsDR extends DatabaseRequest {
        public GetActiveHitsDR() {
            super("active hits", GlasshouseTweaks.getHitsDatabase().getActiveHitsDatabase());
        }
    }

    private static class GetCompletedHitsDR extends DatabaseRequest {
        public GetCompletedHitsDR() {
            super("completed hits", GlasshouseTweaks.getHitsDatabase().getCompletedHitsDatabase());
        }
    }

    private static class GetIllegalKillsDR extends DatabaseRequest {
        public GetIllegalKillsDR() {
            super("illegal kills", GlasshouseTweaks.getHitsDatabase().getCompletedHitsDatabase());
        }
    }

    private static class GetIndividualPlayerRequest implements HttpHandler {
        private User player;

        public GetIndividualPlayerRequest(User player) {
            this.player = player;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            GlasshouseTweaks.log(Level.INFO, "Recieved incoming request for player " + player.getProfile()
                    .getName() + " from discord bot.");
            Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new JSONHandlers.UserJSON())
                    .setPrettyPrinting().create();

            String response = gson.toJson(player);

            if (response == null) {
                GlasshouseTweaks.log(Level.SEVERE, "Could not get raw JSON string from " +
                        player.getProfile().getName() + ".");
                exchange.sendResponseHeaders(500, response.length());
            } else {
                GlasshouseTweaks.log(Level.INFO, "Returning " + player.getProfile().getName() +
                        " JSON to discord bot.");
                exchange.sendResponseHeaders(200, response.length());
            }

            OutputStream output = exchange.getResponseBody();
            output.write(response.getBytes(StandardCharsets.UTF_8));
            output.close();
        }
    }
}
