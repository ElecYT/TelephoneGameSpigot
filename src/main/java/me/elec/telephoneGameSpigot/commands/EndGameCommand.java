package me.elec.telephoneGameSpigot.commands;


import me.elec.telephoneGameSpigot.BribeManager;
import me.elec.telephoneGameSpigot.TelephoneGameSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EndGameCommand implements CommandExecutor {

    private final BribeManager bribeManager;
    private final TelephoneGameSpigot plugin;

    public EndGameCommand(BribeManager bribeManager, TelephoneGameSpigot plugin) {
        this.bribeManager = bribeManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("telephonegamespigot.endgame")) {
            sender.sendMessage("Broadcasting end messages...");
            broadcastEndMessagesl();
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission!");
            return true;
        }
    }

    public void broadcastEndMessagesl() {
        plugin.gameStarted = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Schedule the rest of the titles with incremental delays
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.GOLD + "The game has ended!", ChatColor.GOLD + "", 1, 60, 1);
            }, 100L); // 5 seconds

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendTitle(ChatColor.BLUE + "The coins leaderboard will be", ChatColor.GOLD + "sent in the chat!", 1, 60, 1);
            }, 200L); // 10 seconds (relative to the start)
            bribeManager.broadcastLeaderboard();
        }
    }
}
