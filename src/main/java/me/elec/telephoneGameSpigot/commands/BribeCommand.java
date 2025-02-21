package me.elec.telephoneGameSpigot.commands;

import me.elec.telephoneGameSpigot.BribeManager;
import me.elec.telephoneGameSpigot.TelephoneGameSpigot;
import me.elec.telephoneGameSpigot.VoiceChatManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BribeCommand implements CommandExecutor {

    private final HashMap<UUID, UUID> callRequests = new HashMap<>();
    private final BribeManager bribeManager;
    private final TelephoneGameSpigot plugin;

    public BribeCommand(TelephoneGameSpigot plugin, BribeManager bribeManager) {
        this.plugin = plugin;
        this.bribeManager = bribeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /bribe [amount] <player>)");
            return true;
        }

        int amount = Integer.valueOf(args[1]);
        String target = args[1];
        Player targetPlayer = Bukkit.getPlayer(target);

        if (targetPlayer == null || !player.isOnline()) {
            player.sendMessage(ChatColor.RED + "Invalid Player.");
        } else {
            bribeManager.processPayment(player, targetPlayer, amount);
            return true;
        }

        return true;
    }
}
