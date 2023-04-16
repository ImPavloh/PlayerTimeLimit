package com.pavloh.ptl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTimeLimit extends JavaPlugin implements Listener {
    private int timeLimit;
    private BossBar bossBar;
    private Map < UUID, Integer > playerRemainingTime;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        playerRemainingTime = new HashMap < > ();
        bossBar = Bukkit.createBossBar("Tiempo restante: ", getBossBarColor(), getBossBarStyle());
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        savePlayerData();
        saveConfig();
    }

    private void loadConfigValues() {
        timeLimit = getConfig().getInt("timeLimit");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        int remainingTime = getConfig().getInt("players." + playerUUID + ".remainingTime", timeLimit);

        playerRemainingTime.put(playerUUID, remainingTime);
        bossBar.addPlayer(player);
        updateTimeBar(player);

        List < String > admins = getConfig().getStringList("infinite");
        if (admins.contains(player.getName()) || player.hasPermission("timelimit.admin")) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!playerRemainingTime.containsKey(playerUUID)) {
                    cancel();
                    return;
                }

                int newRemainingTime = playerRemainingTime.get(playerUUID) - 1;
                playerRemainingTime.put(playerUUID, newRemainingTime);

                if (newRemainingTime <= 0) {
                    player.kickPlayer(getMessage("kickMessage"));
                    playerRemainingTime.remove(playerUUID);
                    cancel();
                } else {
                    updateTimeBar(player);
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        int remainingTime = playerRemainingTime.getOrDefault(playerUUID, timeLimit);

        getConfig().set("players." + playerUUID + ".remainingTime", remainingTime);
        playerRemainingTime.remove(playerUUID);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("tiempo")) {
            if (args.length == 0) {
                if (bossBar.getPlayers().contains(player)) {
                    bossBar.removePlayer(player);
                    player.sendMessage(getMessage("hideTimeMessage"));
                } else {
                    bossBar.addPlayer(player);
                    updateTimeBar(player);
                    player.sendMessage(getMessage("activateTimeMessage"));
                }
                return true;
            } else if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("resetear")) {
                    if (!player.hasPermission("PlayerTimeLimit.resetear")) {
                        player.sendMessage("No tienes permiso para ejecutar este comando.");
                        return true;
                    }

                    if (args.length == 2) {
                        Player targetPlayer = Bukkit.getPlayer(args[1]);

                        if (targetPlayer == null) {
                            player.sendMessage("El jugador " + args[1] + " no está en línea.");
                            return true;
                        }

                        UUID targetPlayerUUID = targetPlayer.getUniqueId();
                        playerRemainingTime.put(targetPlayerUUID, timeLimit);
                        updateTimeBar(targetPlayer);
                        player.sendMessage(getMessage("resetTimeMessage").replace("%player%", targetPlayer.getName()));
                    } else {
                        player.sendMessage("Uso incorrecto del comando. Usa /tiempo resetear <jugador>.");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("ver")) {
                    if (!player.hasPermission("PlayerTimeLimit.ver")) {
                        player.sendMessage("No tienes permiso para ejecutar este comando.");
                        return true;
                    }

                    if (args.length == 2) {
                        Player targetPlayer = Bukkit.getPlayer(args[1]);

                        if (targetPlayer == null) {
                            player.sendMessage("El jugador " + args[1] + " no está en línea.");
                            return true;
                        }

                        UUID targetPlayerUUID = targetPlayer.getUniqueId();
                        int remainingTime = playerRemainingTime.getOrDefault(targetPlayerUUID, timeLimit);
                        player.sendMessage(getMessage("viewTimeMessage")
                            .replace("%player%", targetPlayer.getName())
                            .replace("%time%", formatTime(remainingTime)));
                    } else {
                        player.sendMessage("Uso incorrecto del comando. Usa /tiempo ver <jugador>.");
                    }
                    return true;
                } else {
                    player.sendMessage("Uso incorrecto del comando. Usa /tiempo [resetear <jugador>|ver <jugador>].");
                    return true;
                }
            }
        }

        return false;
    }

    private void updateTimeBar(Player player) {
        int remainingTime = playerRemainingTime.get(player.getUniqueId());
        bossBar.setTitle("Tiempo restante: " + formatTime(remainingTime));
        bossBar.setProgress((double) remainingTime / timeLimit);
    }

    private String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path));
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int remainingSeconds = seconds % 3600;
        int minutes = remainingSeconds / 60;
        remainingSeconds = remainingSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    private BarColor getBossBarColor() {
        String colorString = getConfig().getString("timeBarColor");
        return BarColor.valueOf(colorString.toUpperCase());
    }

    private BarStyle getBossBarStyle() {
        String styleString = getConfig().getString("timeBarStyle");
        return BarStyle.valueOf(styleString.toUpperCase());
    }

    private void savePlayerData() {
        for (Map.Entry < UUID, Integer > entry: playerRemainingTime.entrySet()) {
            getConfig().set("players." + entry.getKey() + ".remainingTime", entry.getValue());
        }
    }
}