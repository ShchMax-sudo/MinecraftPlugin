package com.shchmax.app;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public class App extends JavaPlugin implements Listener {
    private HashSet<Player> online = new HashSet<Player>();
    private HashSet<Player> awake = new HashSet<Player>();
    private HashSet<Player> asleep = new HashSet<Player>();
    private double needToBlind = 1;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if (cmd.getName().equalsIgnoreCase("sleepingplayers")) {
            if (asleep.size() == 0) {
                sender.sendMessage("Nobody is sleeping now");
                return true;
            }
            StringBuffer sb = new StringBuffer("Now sleeping:");
            for (Player p : asleep) {
                sb.append(" ");
                sb.append(p.getName());
            }
            sender.sendMessage(sb.toString());
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("awakeplayers")) {
            if (awake.size() == 0) {
                sender.sendMessage("Everyone are sleeping now");
                return true;
            }
            StringBuffer sb = new StringBuffer("Not sleeping now:");
            for (Player p : awake) {
                sb.append(" ");
                sb.append(p.getName());
            }
            sender.sendMessage(sb.toString());
            return true;
        }
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        online.add(p);
        awake.add(p);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        Player p = event.getPlayer();
        if (awake.contains(p)) {
            awake.remove(p);
        }
        asleep.add(p);
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        Player p = event.getPlayer();
        if (asleep.contains(p)) {
            asleep.remove(p);
        }
        awake.add(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws Exception {
        Player p = event.getPlayer();
        if (awake.contains(p)) {
            awake.remove(p);
        }
        if (asleep.contains(p)) {
            asleep.remove(p);
        }
        if (!online.contains(p)) {
            throw new Exception("Not online player exit the game");
        } else {
            online.remove(p);
        }
    }
}
