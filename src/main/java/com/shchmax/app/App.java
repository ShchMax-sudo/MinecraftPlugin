package com.shchmax.app;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class App extends JavaPlugin {
    @Override
    public void onEnable() {
        Logger log = getLogger();
        log.info("Your plugin has been enabled.");
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if (cmd.getName().equalsIgnoreCase("basic")) {
            Logger log = getLogger();
            log.info("Hello");
            return true;
        }
        return false;
    }
}
