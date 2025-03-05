package me.elec.telephoneGameSpigot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class BribeManager {

    private final HashMap<UUID, Integer> moneyMap = new HashMap<>();

    public Map<UUID, Integer> getMoneyMap() {
        return moneyMap;
    }

    public void setMoneyMap(UUID playerUUID, int amount) {
        moneyMap.put(playerUUID, amount);
    }

    public BribeManager() {

    }

    public void populateMap() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setMoneyMap(player.getUniqueId(), 100);
        }
    }

    public void processPayment(Player sender, Player receiver, int amount) {
        UUID senderUUID = sender.getUniqueId();
        UUID receiverUUID = receiver.getUniqueId();

        int senderBalance = moneyMap.getOrDefault(senderUUID, 0);
        int receiverBalance = moneyMap.getOrDefault(receiverUUID, 0);

        if (senderBalance >= amount) {
            moneyMap.put(senderUUID, senderBalance - amount);
            moneyMap.put(receiverUUID, receiverBalance + amount);

            receiver.sendMessage(ChatColor.GREEN + "You received " + amount + " from " + sender.getName() + "! Your new balance: " + (receiverBalance + amount));
            sender.sendMessage(ChatColor.GREEN + "You sent " + amount + " to " + receiver.getName() + "! Your new balance: " + (senderBalance - amount));
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have enough money! Your balance: " + senderBalance);
        }
    }

    public void broadcastLeaderboard() {

        // Sort the moneyMap by values in descending order
        LinkedHashMap<UUID, Integer> sortedMap = moneyMap.entrySet()
                .stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Format the sorted data into a leaderboard string
        StringBuilder leaderboard = new StringBuilder(ChatColor.GOLD + "Bribe Leaderboard:\n");
        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : sortedMap.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                leaderboard.append(ChatColor.YELLOW)
                        .append(rank)
                        .append(". ")
                        .append(player.getName())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n");
                rank++;
            }
        }

        // Broadcast the formatted leaderboard to all online players
        Bukkit.broadcastMessage(leaderboard.toString());
    }
}
