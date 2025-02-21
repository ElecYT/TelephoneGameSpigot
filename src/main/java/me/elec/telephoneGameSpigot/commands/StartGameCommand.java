package me.elec.telephoneGameSpigot.commands;


import me.elec.telephoneGameSpigot.BribeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StartGameCommand implements CommandExecutor {

    private final BribeManager bribeManager;

    public StartGameCommand(BribeManager bribeManager) {
        this.bribeManager = bribeManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        bribeManager.setupMap();
        return true;
    }
}
