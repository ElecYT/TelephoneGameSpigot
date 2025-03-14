package me.elec.telephoneGameSpigot.commands;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
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
            player.sendMessage(ChatColor.RED + "Usage: /call <player> OR /call accept OR /call deny OR /call end");
            return true;
        }

        String action = args[0].toLowerCase();
        if (plugin.gameStarted) {
            // Sending a call request
            if (!action.equals("accept") && !action.equals("deny") && !action.equals("end")) {
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
                voiceChatManager.addCallPairing(player.getUniqueId(), caller.getUniqueId());

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

            if (action.equals("end")) {
                UUID partnerUUID = voiceChatManager.getCallPartner(player.getUniqueId());
                if (partnerUUID == null) {
                    player.sendMessage("You are not in a call.");
                    return true;
                }
                VoicechatServerApi serverApi = voiceChatManager.getVoiceChatServerApi();
                if (serverApi == null) {
                    player.sendMessage("Voice chat server API is not available.");
                    return true;
                }

                // Get the voice chat connection for both players.
                VoicechatConnection connection1 = serverApi.getConnectionOf(player.getUniqueId());
                VoicechatConnection connection2 = serverApi.getConnectionOf(partnerUUID);

                if (connection1 != null) {
                    connection1.setGroup(null);
                    player.sendMessage("Call ended.");
                } else {
                    player.sendMessage("Could not end your call because you are not connected to voice chat.");
                }
                Player partner = Bukkit.getPlayer(partnerUUID);
                if (partner != null && connection2 != null) {
                    connection2.setGroup(null);
                    partner.sendMessage("Call ended.");
                }

                // Remove the pairing from your manager.
                voiceChatManager.removeCallPairing(player.getUniqueId());

                return true;
            }
        } else {
            player.sendMessage(ChatColor.RED + "The game hasn't started yet!");
        }

        return true;

    }
}
