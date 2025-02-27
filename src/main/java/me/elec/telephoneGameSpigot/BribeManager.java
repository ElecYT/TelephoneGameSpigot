package me.elec.telephoneGameSpigot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BribeManager {

    public HashMap<Player, Integer> moneyMap = new HashMap<>();

    public HashMap<Player, Integer> getMoneyMap() {
        return moneyMap;
    }

    public void setMoneyMap(int amount, Player player) {
        moneyMap.put(player, amount);
    }

    public BribeManager() {

    }

    public void populateMap() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            moneyMap.put(player, 100);
        }
    }

    public void processPayment(Player sender, Player receiver, int amount) {
        // Retrieve the sender's balance, defaulting to 0 if not present.
        Integer currentBalance = moneyMap.getOrDefault(sender, 0);
        Integer recieverCurrentBalance = moneyMap.getOrDefault(receiver, 0);
        int newBalance = currentBalance - amount;
        int recieverNewBalance = recieverCurrentBalance + amount;
        if (newBalance >= 0) {
            setMoneyMap(newBalance, sender);

            receiver.sendMessage(ChatColor.GREEN + "You received " + amount + " from " + sender.getName() + "! You now have: " + recieverNewBalance);
            sender.sendMessage(ChatColor.GREEN + "You sent " + amount + " to " + receiver.getName() + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have enough money! Your balance: " + currentBalance);
        }
    }
}
