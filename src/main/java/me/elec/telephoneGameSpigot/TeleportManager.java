package me.elec.telephoneGameSpigot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TeleportManager {
    // Map of room numbers to teleport locations.
    public HashMap<Integer, Location> locationHashMap;
    // Map of room numbers to busy status (true if occupied).
    public HashMap<Integer, Boolean> busyRoomMap;

    public TeleportManager() {
        locationHashMap = new HashMap<>();
        busyRoomMap = new HashMap<>();
    }

    // Prepopulate a starting room.
    public void populateLocationMap() {
        // Room 1 at (0, 80, 0) in world "world".
        locationHashMap.put(1, new Location(Bukkit.getWorld("world"), 0, 80, 0));
        busyRoomMap.put(1, false);
    }

    // Teleport two players to a unique room.
    public void teleportPlayers(Player player1, Player player2) {
        Integer availableRoom = null;

        // Look for a room that is not busy.
        for (Integer room : busyRoomMap.keySet()) {
            if (!busyRoomMap.get(room)) {
                availableRoom = room;
                break;
            }
        }

        // If no free room is found, create a new one.
        if (availableRoom == null) {
            availableRoom = locationHashMap.size() + 1;
            // For example, new rooms are offset 100 blocks apart on the x-axis.
            Location newLoc = new Location(Bukkit.getWorld("world"), (availableRoom - 1) * 100, 80, 0);
            locationHashMap.put(availableRoom, newLoc);
            busyRoomMap.put(availableRoom, false);
        }

        // Mark the room as busy.
        busyRoomMap.put(availableRoom, true);
        // Retrieve the room's location.
        Location roomLoc = locationHashMap.get(availableRoom);

        // Teleport both players to the room's location.
        player1.teleport(roomLoc);
        player2.teleport(roomLoc);

        // Notify the players.
        player1.sendMessage("§aTeleported to room " + availableRoom + ".");
        player2.sendMessage("§aTeleported to room " + availableRoom + ".");
    }
}
