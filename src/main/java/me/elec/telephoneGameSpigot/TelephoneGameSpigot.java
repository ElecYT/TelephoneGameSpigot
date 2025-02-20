package me.elec.telephoneGameSpigot;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import me.elec.telephoneGameSpigot.commands.CallCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public final class TelephoneGameSpigot extends JavaPlugin {
    private static TelephoneGameSpigot plugin;

    // Single instance for both API registration and command handling.
    @Nullable
    private VoiceChatManager voiceChatManager;

    @Override
    public void onEnable() {
        plugin = this;

        // Delay lookup slightly if necessary (optional)
        Bukkit.getScheduler().runTaskLater(this, () -> {
            BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
            if (service != null) {
                voiceChatManager = new VoiceChatManager(this);
                service.registerPlugin(voiceChatManager);
                getLogger().info("Simple Voice Chat API hooked in!");
            } else {
                getLogger().warning("Simple Voice Chat API not found. Voice features will be disabled.");
            }
        }, 20L); // 20 ticks delay

        // IMPORTANT: Use the same instance even if it might not be ready immediately.
        // You may also delay command registration if needed.
        getCommand("call").setExecutor(new CallCommand(this, voiceChatManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
