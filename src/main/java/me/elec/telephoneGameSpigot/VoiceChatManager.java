package me.elec.telephoneGameSpigot;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.ClientVoicechatConnectionEvent;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoiceChatManager implements VoicechatPlugin {

    private final TelephoneGameSpigot plugin;
    private VoicechatApi voicechatApi;

    // Maintain a map of player UUIDs to their voice chat connections.
    private final Map<UUID, VoicechatConnection> connectionMap = new HashMap<>();

    public VoiceChatManager(TelephoneGameSpigot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize(VoicechatApi api) {
        this.voicechatApi = api;
        plugin.getLogger().info("Voicechat API has been initialized.");
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        // Register the ClientVoicechatConnectionEvent listener.
        registration.registerEvent(MicrophonePacketEvent.class, event -> {
            // Instead of getConnection(), we call connection() per your API version.
            // (If your Javadoc uses a different name, adjust accordingly.)
            VoicechatConnection conn = event.getSenderConnection();
            UUID playerUUID = event.getSenderConnection().getPlayer().getUuid();
            if (event.getSenderConnection().isConnected()) {
                connectionMap.put(playerUUID, conn);
                plugin.getLogger().info("Stored connection for player: " + playerUUID);
            } else {
                connectionMap.remove(playerUUID);
                plugin.getLogger().info("Removed connection for player: " + playerUUID);
            }
        });
    }

    @Override
    public String getPluginId() {
        return "telephonegame";
    }

    // Creates a voice group for two players using the stored connections.
    public void createVoiceGroup(Player player1, Player player2) {
        if (voicechatApi == null) {
            player1.sendMessage("§cVoice chat API not available.");
            player2.sendMessage("§cVoice chat API not available.");
            return;
        }

        VoicechatConnection connection1 = connectionMap.get(player1.getUniqueId());
        VoicechatConnection connection2 = connectionMap.get(player2.getUniqueId());

        if (connection1 == null || connection2 == null) {
            player1.sendMessage("§cOne of you is not connected to voice chat.");
            player2.sendMessage("§cOne of you is not connected to voice chat.");
            return;
        }

        // Create a unique group name.
        String groupName = "Call-" + UUID.randomUUID().toString().substring(0, 6);
        var group = new Group.Builder() {
            @Override
            public Group.Builder setId(@Nullable UUID uuid) {
                return null;
            }

            @Override
            public Group.Builder setName(String s) {
                return null;
            }

            @Override
            public Group.Builder setPassword(@Nullable String s) {
                return null;
            }

            @Override
            public Group.Builder setPersistent(boolean b) {
                return null;
            }

            @Override
            public Group.Builder setHidden(boolean b) {
                return null;
            }

            @Override
            public Group.Builder setType(Group.Type type) {
                return null;
            }

            @Override
            public Group build() {
                return null;
            }
        }
                .setName(groupName)
                .setPersistent(false)  // Temporary group
                .setPassword(null)       // No password required
                .build();

        if (group == null) {
            player1.sendMessage("§cFailed to create a voice chat group.");
            player2.sendMessage("§cFailed to create a voice chat group.");
            return;
        }

        // Assign both players to the new group.
        connection1.setGroup(group);
        connection2.setGroup(group);

        player1.sendMessage("§aYou have been added to a voice chat group with " + player2.getName() + "!");
        player2.sendMessage("§aYou have been added to a voice chat group with " + player1.getName() + "!");
    }
}
