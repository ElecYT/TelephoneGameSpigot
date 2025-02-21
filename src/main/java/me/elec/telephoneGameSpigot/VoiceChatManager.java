package me.elec.telephoneGameSpigot;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import org.bukkit.entity.Player;

import java.util.UUID;

public class VoiceChatManager implements VoicechatPlugin {

    private final TelephoneGameSpigot plugin;
    private VoicechatApi voicechatApi;
    private VoicechatServerApi voiceChatServerApi;

    public VoiceChatManager(TelephoneGameSpigot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize(VoicechatApi api) {
        this.voicechatApi = api;
        plugin.getLogger().info("Voicechat API initialized in VoiceChatManager.");
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(PlayerConnectedEvent.class, event -> {
            this.voiceChatServerApi = event.getVoicechat();
            plugin.getLogger().info("VoicechatServerApi obtained from VoicechatServerStartedEvent: " + voiceChatServerApi);
        });
    }

    @Override
    public String getPluginId() {
        return "telephonegame";
    }

    public void createVoiceGroup(Player player1, Player player2) {
        if (voiceChatServerApi == null) {
            plugin.getLogger().warning("VoiceChatServerApi is null, cannot create group.");
            return;
        }

        String groupName = "Call-" + UUID.randomUUID().toString().substring(0, 6);
        Group group = voiceChatServerApi.createGroup(groupName, null);
        if (group == null) {
            plugin.getLogger().warning("Failed to create voice chat group.");
            return;
        }

        VoicechatConnection connection1 = voiceChatServerApi.getConnectionOf(player1.getUniqueId());
        if (connection1 == null) {
            player1.sendMessage("§cYou are not connected to voice chat.");
            return;
        }

        VoicechatConnection connection2 = voiceChatServerApi.getConnectionOf(player2.getUniqueId());
        if (connection2 == null) {
            player2.sendMessage("§cYou are not connected to voice chat.");
            return;
        }

        connection1.setGroup(group);
        connection2.setGroup(group);

        player1.sendMessage("§aYou have been added to a voice chat group with " + player2.getName() + "!");
        player2.sendMessage("§aYou have been added to a voice chat group with " + player1.getName() + "!");
    }
}
