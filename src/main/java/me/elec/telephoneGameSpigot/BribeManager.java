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

    public void setupMap() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            moneyMap.put(player, 100);
        }
    }

    public void processPayment(Player sender, Player reciever, int amount) {
        int newBalance = moneyMap.get(sender) - amount;
        if (newBalance >= 0) {
            setMoneyMap(newBalance, sender);
            reciever.sendMessage(ChatColor.GREEN + "You recieved " + amount + " from " + sender.getName() + "!");
            sender.sendMessage(ChatColor.GREEN + "You sent " + amount + " to " + reciever.getName() +"!");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have enough money! Your balance: " + getMoneyMap().get(sender));
        }
    }
}
