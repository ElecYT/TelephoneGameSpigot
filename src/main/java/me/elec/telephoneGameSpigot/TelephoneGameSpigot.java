package me.elec.telephoneGameSpigot;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import me.elec.telephoneGameSpigot.commands.BribeCommand;
import me.elec.telephoneGameSpigot.commands.CallCommand;
import me.elec.telephoneGameSpigot.commands.EndGameCommand;
import me.elec.telephoneGameSpigot.commands.StartGameCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;

public final class TelephoneGameSpigot extends JavaPlugin {
    private static TelephoneGameSpigot plugin;

    // Single instance for both API registration and command handling.
    @Nullable
    private VoiceChatManager voiceChatManager;

    private BribeManager bribeManager;
    private TeleportManager teleportManager;
    public boolean gameStarted = false;



    @Override
    public void onEnable() {
        plugin = this;
        teleportManager = new TeleportManager();
        voiceChatManager = new VoiceChatManager(plugin, teleportManager);
        bribeManager = new BribeManager();



        // Log the "voicechat" plugin instance from the plugin manager.
        Bukkit.getLogger().info("Voicechat plugin instance: " + Bukkit.getPluginManager().getPlugin("voicechat"));

        Bukkit.getLogger().info("Checking all registrations for BukkitVoicechatService...");
        for (RegisteredServiceProvider<BukkitVoicechatService> reg : getServer().getServicesManager().getRegistrations(BukkitVoicechatService.class)) {
            Bukkit.getLogger().info("Found registration: " + reg + ", provider: " + reg.getProvider() +
                    ", provider class: " + reg.getProvider().getClass() +
                    ", ClassLoader: " + reg.getProvider().getClass().getClassLoader());
        }

        // Use an array to hold the task so we can access its task ID inside the Runnable.
        final BukkitTask[] taskHolder = new BukkitTask[1];
        taskHolder[0] = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            int attempts = 0;

            @Override
            public void run() {
                attempts++;
                int currentTaskId = taskHolder[0].getTaskId();
                Bukkit.getLogger().info("Attempt " + attempts + " to lookup BukkitVoicechatService...");

                var registration = getServer().getServicesManager().getRegistration(BukkitVoicechatService.class);
                if (registration != null) {
                    BukkitVoicechatService service = registration.getProvider();
                    Bukkit.getLogger().info("Found BukkitVoicechatService: " + service);
                    Bukkit.getLogger().info("Service classloader: " + service.getClass().getClassLoader());


                    // Create and register your VoiceChatManager using the found service.

                    service.registerPlugin(voiceChatManager);
                    getLogger().info("Simple Voice Chat API hooked in!");

                    // Cancel further attempts.
                    Bukkit.getScheduler().cancelTask(currentTaskId);
                } else {
                    Bukkit.getLogger().info("No registration found for BukkitVoicechatService.");
                    if (attempts >= 10) { // After 10 attempts (~10 seconds)
                        Bukkit.getLogger().warning("BukkitVoicechatService did not become available after " + attempts + " attempts.");
                        Bukkit.getScheduler().cancelTask(currentTaskId);
                    }
                }
            }
        }, 0L, 20L); // Start after 20 ticks, repeat every 20 ticks.

        // Register the command executor using the shared instance.
        // Note: This instance might be null until the service is found.
        getCommand("call").setExecutor(new CallCommand(this, voiceChatManager));
        getCommand("bribe").setExecutor(new BribeCommand(this, bribeManager));
        getCommand("startgame").setExecutor(new StartGameCommand(bribeManager, this));
        getCommand("endgame").setExecutor(new EndGameCommand(bribeManager, this));

        //Populate Location Map
        teleportManager.populateLocationMap();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
