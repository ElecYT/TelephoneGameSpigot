package me.elec.telephoneGameSpigot;

import de.maxhenkel.voicechat.api.*;
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
    private VoicechatServerApi voiceChatServerApi;

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
            //Set voiceChatServerApi variable
            voiceChatServerApi = event.getVoicechat();
        });
    }

    @Override
    public String getPluginId() {
        return "telephonegame";
    }

    // Creates a voice group for two players using the stored connections.
    public void createVoiceGroup(Player player1, Player player2) {

        // Create a unique group name.
        String groupName = "Call-" + UUID.randomUUID().toString().substring(0, 6);

        Group group = voiceChatServerApi.createGroup(groupName, null);
        VoicechatConnection playerOneConnection = voiceChatServerApi.getConnectionOf(player1.getUniqueId());

        if (playerOneConnection == null) {
            return; // Player does not exist
        }

        playerOneConnection.setGroup(group);

        VoicechatConnection playerTwoConnection = voiceChatServerApi.getConnectionOf(player1.getUniqueId());

        if (playerTwoConnection == null) {
            return; // Player does not exist
        }

        playerTwoConnection.setGroup(group);

        player1.sendMessage("§aYou have been added to a voice chat group with " + player2.getName() + "!");
        player2.sendMessage("§aYou have been added to a voice chat group with " + player1.getName() + "!");
    }
}
