package me.elec.telephoneGameSpigot.commands;

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

public class CallCommand implements CommandExecutor {

    private final HashMap<UUID, UUID> callRequests = new HashMap<>();
    private final VoiceChatManager voiceChatManager;
    private final TelephoneGameSpigot plugin;

    public CallCommand(TelephoneGameSpigot plugin,VoiceChatManager voiceChatManager) {
        this.plugin = plugin;
        this.voiceChatManager = voiceChatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /call <player> OR /call accept OR /call deny");
            return true;
        }

        String action = args[0].toLowerCase();

        // Sending a call request
        if (!action.equals("accept") && !action.equals("deny")) {
            Player target = Bukkit.getPlayer(action);

            if (target == null || !target.isOnline()) {
                player.sendMessage(ChatColor.RED + "That player is not online.");
                return true;
            }

            if (callRequests.containsKey(target.getUniqueId())) {
                player.sendMessage(ChatColor.RED + target.getName() + " already has a pending call request.");
                return true;
            }

            callRequests.put(target.getUniqueId(), player.getUniqueId());

            player.sendMessage(ChatColor.GREEN + "You have called " + target.getName() + "!");
            target.sendMessage(ChatColor.YELLOW + player.getName() + " is calling you! Type '/call accept' to accept or '/call deny' to deny.");

            return true;
        }

        // Accepting a call request
        if (action.equals("accept")) {
            if (!callRequests.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You have no pending call requests.");
                return true;
            }

            Player caller = Bukkit.getPlayer(callRequests.get(player.getUniqueId()));

            if (caller == null || !caller.isOnline()) {
                player.sendMessage(ChatColor.RED + "The caller is no longer online.");
                callRequests.remove(player.getUniqueId());
                return true;
            }

            player.sendMessage(ChatColor.GREEN + "You accepted the call from " + caller.getName() + "!");
            caller.sendMessage(ChatColor.GREEN + player.getName() + " has accepted your call!");

            // Add both players to a voice chat group
            voiceChatManager.createVoiceGroup(caller, player);

            callRequests.remove(player.getUniqueId());
            return true;
        }

        // Denying a call request
        if (action.equals("deny")) {
            if (!callRequests.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You have no pending call requests.");
                return true;
            }

            Player caller = Bukkit.getPlayer(callRequests.get(player.getUniqueId()));

            player.sendMessage(ChatColor.RED + "You denied the call request.");
            if (caller != null && caller.isOnline()) {
                caller.sendMessage(ChatColor.RED + player.getName() + " has denied your call request.");
            }

            callRequests.remove(player.getUniqueId());
            return true;
        }

        return true;
    }
}
