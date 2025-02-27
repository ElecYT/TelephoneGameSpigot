package me.elec.telephoneGameSpigot.commands;


import me.elec.telephoneGameSpigot.BribeManager;
import me.elec.telephoneGameSpigot.TelephoneGameSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartGameCommand implements CommandExecutor {

    private final BribeManager bribeManager;
    private final TelephoneGameSpigot plugin;

    public StartGameCommand(BribeManager bribeManager, TelephoneGameSpigot plugin) {
        this.bribeManager = bribeManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("telephonegamespigot.startgame")) {
            bribeManager.populateMap();
            broadcastTutorial();
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission!");
            return true;
        }
    }

    public void broadcastTutorial() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Schedule the rest of the titles with incremental delays
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.GOLD + "Welcome to the Telephone Game...", ChatColor.GOLD + "", 1, 60, 1);
            }, 100L); // 5 seconds

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.BLUE + "You may call other contestants", ChatColor.GOLD + "Use the command /call", 1, 60, 1);
            }, 200L); // 10 seconds (relative to the start)

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.RED + "Persuade people to vote for you", ChatColor.DARK_RED + "Most voted person moves on", 1, 60, 1);
            }, 300L); // 15 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.GOLD + "Bribe people to vote for you", ChatColor.AQUA + "Use the command /bribe", 1, 60, 1);
            }, 400L); // 20 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.GOLD + "Good luck!", ChatColor.GOLD + "Make smart decisions...", 1, 60, 1);
            }, 500L); // 25 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.GOLD + "You have 30 seconds to strategize!", ChatColor.GOLD + "You will have 10 minutes!", 1, 60, 1);
            }, 600L); // 30 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.GOLD + "You have 20 seconds left!", ChatColor.GOLD + "You will have 10 minutes!", 1, 60, 1);
            }, 800L); // 40 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.GOLD + "You have 10 seconds left!", ChatColor.GOLD + "You will have 10 minutes!", 1, 60, 1);
            }, 1000L); // 40 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.GOLD + "You have 5 seconds left!", ChatColor.GOLD + "You will have 10 minutes!", 1, 60, 1);
            }, 1100L); // 40 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.GOLD + "The time period has started!", ChatColor.GOLD + "You will have 10 minutes!", 1, 60, 1);
            }, 1200L); // 50 seconds
        }
    }
}
