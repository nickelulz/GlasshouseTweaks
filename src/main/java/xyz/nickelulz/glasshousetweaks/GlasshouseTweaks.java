package xyz.nickelulz.glasshousetweaks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nickelulz.glasshousetweaks.commands.CommandManager;
import xyz.nickelulz.glasshousetweaks.databases.HitDatabase;
import xyz.nickelulz.glasshousetweaks.databases.IllegalKillsDatabase;
import xyz.nickelulz.glasshousetweaks.databases.PlayerDatabase;
import xyz.nickelulz.glasshousetweaks.datatypes.Attack;
import xyz.nickelulz.glasshousetweaks.datatypes.Bounty;
import xyz.nickelulz.glasshousetweaks.datatypes.Contract;
import xyz.nickelulz.glasshousetweaks.datatypes.User;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GlasshouseTweaks extends JavaPlugin implements Listener {
    private static GlasshouseTweaks instance;
    private static PlayerDatabase players;
    private static HitDatabase hits;
    private static IllegalKillsDatabase illegalkills;

    @Override
    public void onEnable() {
        // Plugin startup logic
        log(Level.INFO, "Initializing this plugin...");
        instance = this;

        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();

        // Command Registry
        CommandManager.initialize();

        // Load databases
        players = new PlayerDatabase();
        hits = new HitDatabase();
        illegalkills = new IllegalKillsDatabase();
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        log(Level.INFO, "Player " + player.getName() + " has joined.");
        if (!player.hasPlayedBefore()) {
            player.sendMessage(ChatColor.GRAY + "Hello " + event.getPlayer().getName() + ", make sure to " + ChatColor.DARK_GREEN +
                    "read the rules " + ChatColor.GRAY + "and " + ChatColor.DARK_GREEN + "register with the discord bot " +
                    ChatColor.GRAY + " (if you haven't already done so) before playing.");
            broadcast(ChatColor.GRAY + player.getName() + " has joined the server for the first time! " +
                    "DO NOT place hits or attack them for their first day on the server, as that is their grace " +
                    "period!");
        }
        else if (!players.isRegistered(player)) {
            player.sendMessage(ChatColor.AQUA + "You are not registered. Make sure to register using " + ChatColor.DARK_GREEN + "/register" + ChatColor.AQUA + ".");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        // Killer is a player.
        if (victim.getKiller() != null) {
            Player killer = victim.getKiller();
            boolean illegal = true;

            // Affirms that victim and killer are both registered players
            if (players.isRegistered(victim) && players.isRegistered(killer)) {
                User victimUser = players.findByProfile(victim);
                User killerUser = players.findByProfile(killer);
                if (victimUser != null && killerUser != null) {

                    // Initial check: the killer is countering a contract placed on them.
                    Contract victimActiveContract = hits.findContract(killerUser, victimUser);
                    if (victimActiveContract != null) {
                        hits.claim(killerUser, victimActiveContract);
                        illegal = false;
                    }

                    else {

                        /**
                         * Second check: Killer is "countering" a bounty placed on them.
                         */
                        Bounty victimActiveBounty = hits.findBountyByTarget(killerUser);
                        if (victimActiveBounty != null)
                            // Cannot claim, but not illegal.
                            illegal = false;
                        else {

                            /**
                             * Initial check: The killer is executing an outwards contract.
                             */
                            Contract killerActiveContract = hits.findContract(victimUser, killerUser);
                            if (killerActiveContract != null) {
                                hits.claim(killerUser, killerActiveContract);
                                illegal = false;
                            }

                            else {

                                /**
                                 * Second check: The killer is executing an outwards bounty.
                                 */
                                Bounty activeBounty = hits.findBountyByTarget(victimUser);
                                if (activeBounty != null) {
                                    hits.claim(killerUser, activeBounty);
                                    illegal = false;
                                }

                            }

                        }

                    }
                }
            }

            /**
             * Anarchy day active
             */
            if (LocalDate.now().getDayOfWeek().name().equalsIgnoreCase(ConfigurationConstants.ANARCHY_DAY))
                illegal = false;

            /**
             * If still illegal, broadcast to all users and add to
             * illegal kills database.
             */
            if (illegal) {
                event.setDeathMessage(String.format(ChatColor.RED + "%s illegally killed %s! You are not " +
                                "allowed to kill without a hit " + ChatColor.GREEN + " unless it is a saturday. ",
                        killer.getName(), victim.getName()));
                illegalkills.add(new Attack(killer, victim, LocalDateTime.now()));
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log(Level.INFO, "Plugin stopped.");
        players.save();
    }

    public static GlasshouseTweaks getInstance() {
        return instance;
    }

    public static void broadcast(String... messages) {
        for (Player player: Bukkit.getOnlinePlayers())
            for (String msg: messages)
                player.sendMessage(ChatColor.GRAY + msg);
    }

    public static void log(Level level, String outputMessage) {
        Logger.getLogger("Minecraft").log(level, "[GlasshouseTweaks] " + outputMessage);
    }

    public static PlayerDatabase getPlayersDatabase() {
        return players;
    }

    public static HitDatabase getHitsDatabase() {
        return hits;
    }

    public static IllegalKillsDatabase getIllegalkillDatabase() {
        return illegalkills;
    }
}
