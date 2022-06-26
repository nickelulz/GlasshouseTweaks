package xyz.nickelulz.glasshousetweaks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nickelulz.glasshousetweaks.commands.CommandManager;
import xyz.nickelulz.glasshousetweaks.databases.*;
import xyz.nickelulz.glasshousetweaks.datatypes.*;
import xyz.nickelulz.glasshousetweaks.util.ConfigurationConstants;
import xyz.nickelulz.glasshousetweaks.util.DiscordClientManager;
import xyz.nickelulz.glasshousetweaks.util.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GlasshouseTweaks extends JavaPlugin implements Listener {
    private static GlasshouseTweaks instance;
    private static PlayerDatabase players;
    private static HitDatabase hits;
    private static IllegalKillsDatabase illegalKills;
    private static DuelDatabase duels;
    private static WarDatabase wars;

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
        illegalKills = new IllegalKillsDatabase();
        duels = new DuelDatabase();
        wars = new WarDatabase();

        // Discord Bridge
        DiscordClientManager.initialize();
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        log(Level.INFO, player.getName() + " has joined.");
        if (!players.isRegistered(player)) {
            player.sendMessage(ChatColor.GRAY + "You are not registered. Make sure to register using " +
                    ChatColor.DARK_GREEN + "/register" + ChatColor.GRAY + ", as unregistered players cannot break " +
                    "blocks or see.");
            player.setPlayerListFooter("You are not registered! Use /register to register!");
        } else {
            if (ConfigurationConstants.SERVER_NAME != null && ConfigurationConstants.SERVER_NAME.trim() != "")
                player.setPlayerListHeader(ConfigurationConstants.SERVER_NAME);
        }

        if (player.isOp()) {
            player.setPlayerListName(ChatColor.DARK_RED + player.getName());
            player.setDisplayName(ChatColor.DARK_RED + player.getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        // Killer is a player.
        if (victim.getKiller() != null) {
            Player killer = victim.getKiller();
            User victimUser = null, killerUser = null;
            boolean illegal = true;
            boolean registered = false;

            if (Utils.playerEquals(victim, killer)) {
                illegal = false;
            }

            // Affirms that victim and killer are both registered players
            if (players.isRegistered(victim) && players.isRegistered(killer)) {
                victimUser = players.findByProfile(victim);
                killerUser = players.findByProfile(killer);
                if (victimUser != null && killerUser != null) {
                    registered = true;

                    /**
                     * Active war between players
                     */
                    War war = wars.findWarByOpposingParticipants(victimUser, killerUser);
                    if (war != null) {
                        war.incrementPlayerKills(victimUser);
                        killer.sendMessage(ChatColor.GREEN + "Successfully killed another member of the opposing " +
                                "army! To victory!");
                        victim.sendMessage(ChatColor.RED + "You were slain by a member of the opposing army! Stand " +
                                "tall, and don\'t give up!");
                    }

                    // Initial check: the killer is countering a contract placed on them.
                    Contract victimActiveContract = hits.findContract(killerUser, victimUser);
                    if (victimActiveContract != null) {
                        victimActiveContract.setClaimer(victimUser);
                        victimActiveContract.setTimeClaimed(LocalDateTime.now());
                        hits.claim(victimActiveContract);
                        illegal = false;
                    }

                    else {

                        /**
                         * Second check: Killer is countering a bounty placed on them.
                         */
                        Bounty victimActiveBounty = hits.findBountyByTarget(killerUser);
                        if (victimActiveBounty != null) {
                            victimActiveBounty.setClaimer(victimUser);
                            victimActiveBounty.setTimeClaimed(LocalDateTime.now());
                            hits.claim(victimActiveBounty);
                            illegal = false;
                        }
                        else {

                            /**
                             * Initial check: The killer is executing an outwards contract.
                             */
                            Contract killerActiveContract = hits.findContract(victimUser, killerUser);
                            if (killerActiveContract != null) {
                                killerActiveContract.setClaimer(killerUser);
                                killerActiveContract.setTimeClaimed(LocalDateTime.now());
                                hits.claim(killerActiveContract);
                                illegal = false;
                            }

                            else {

                                /**
                                 * Second check: The killer is executing an outwards bounty.
                                 */
                                Bounty activeBounty = hits.findBountyByTarget(victimUser);
                                if (activeBounty != null) {
                                    activeBounty.setClaimer(killerUser);
                                    activeBounty.setTimeClaimed(LocalDateTime.now());
                                    hits.claim(activeBounty);
                                    illegal = false;
                                }

                            }

                        }

                    }
                }
                else {
                    if (!players.isRegistered(killer))
                        killer.sendMessage(ChatColor.YELLOW + "Make sure to register, or the server owner will " +
                                "literally eat you.");
                    if (!players.isRegistered(victim))
                        victim.sendMessage(ChatColor.YELLOW + "Make sure to register, or the server owner will " +
                                "literally eat you.");
                }
            }

            /**
             * Active duel between the players
             */
            Duel duel = duels.findActiveDuel(victim, killer);
            if (duel != null) {
                GlasshouseTweaks.getDuelsDatabase().remove(duel);
                killer.sendMessage(ChatColor.GREEN + "You won the duel against " + victim.getName() + "!");
                victim.sendMessage(ChatColor.RED + "You lost the duel against " + killer.getName() + ". :(");
                illegal = false;
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
                if (ConfigurationConstants.ANARCHY_DAY == null)
                    broadcast(String.format(ChatColor.RED + "%s illegally killed %s! (You are not " + "allowed to kill without a hit.)",
                            killer.getName(), victim.getName()));
                else
                    broadcast(String.format(ChatColor.RED + "%s illegally killed %s! (You are not allowed to kill " +
                            "without a hit " + ChatColor.GREEN + "unless it is a " + ConfigurationConstants.ANARCHY_DAY + "" + ChatColor.RED + ".)",
                            killer.getName(), victim.getName()));
                illegalKills.add(new Attack(killer, killer.getName(), victim, victim.getName(), LocalDateTime.now()));
            }
            else {
                // legal
                if (registered) {
                    victimUser.increment("deaths", 1);
                    killerUser.increment("kills", 1);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log(Level.INFO, "Plugin stopped.");
        players.save();
        hits.save();
        illegalKills.save();
        DiscordClientManager.close();
    }

    public static GlasshouseTweaks getInstance() {
        return instance;
    }

    public static void broadcast(String... messages) {
        for (Player player: Bukkit.getOnlinePlayers())
            for (String msg: messages)
                player.sendMessage(ChatColor.ITALIC + "" + ChatColor.WHITE + msg);
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
        return illegalKills;
    }

    public static DuelDatabase getDuelsDatabase() {
        return duels;
    }

    public static WarDatabase getWarsDatabase() {
        return wars;
    }
}
