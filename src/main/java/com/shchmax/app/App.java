package com.shchmax.app;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;

public class App extends JavaPlugin implements Listener {
    private final HashSet<Player> online = new HashSet<>();
    private final HashSet<Player> awake = new HashSet<>();
    private final HashSet<Player> asleep = new HashSet<>();
    private double needToBlind = 1;
    private PotionEffect[] blinds;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        blinds = new PotionEffect[2];
        int blindDuration = 200;
        blinds[0] = new PotionEffect(PotionEffectType.SLOW_DIGGING, blindDuration, 2, false, false, false);
        blinds[1] = new PotionEffect(PotionEffectType.BLINDNESS, blindDuration, 1, false, false, false);
        new BukkitRunnable(){
            @Override
            public void run(){
                onAlways();
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    private void tooManyArgs(CommandSender sender) {
        sender.sendMessage("Too many arguments");
    }

    private void tooLittleArgs(CommandSender sender) {
        sender.sendMessage("Too little arguments");
    }

    private void blind(Player p) {
        p.addPotionEffects(Arrays.asList(blinds));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if (cmd.getName().equalsIgnoreCase("sleepingplayers")) {
            if (args.length > 0) {
                tooManyArgs(sender);
                return true;
            }
            if (asleep.size() == 0) {
                sender.sendMessage("Nobody is sleeping now");
                return true;
            }
            StringBuilder sb = new StringBuilder("Now sleeping:");
            for (Player p : asleep) {
                sb.append(" ");
                sb.append(p.getName());
            }
            sender.sendMessage(sb.toString());
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("awakeplayers")) {
            if (args.length > 0) {
                tooManyArgs(sender);
                return true;
            }
            if (awake.size() == 0) {
                sender.sendMessage("Everyone are sleeping now");
                return true;
            }
            StringBuilder sb = new StringBuilder("Not sleeping now:");
            for (Player p : awake) {
                sb.append(" ");
                sb.append(p.getName());
            }
            sender.sendMessage(sb.toString());
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("issleeping")) {
            if (args.length > 1) {
                tooManyArgs(sender);
                return true;
            } else if (args.length < 1) {
                tooLittleArgs(sender);
                return true;
            }
            String name = args[0];
            for (Player p : awake) {
                if (p.getName().equals(name)) {
                    sender.sendMessage(name + " is awake");
                    return true;
                }
            }
            for (Player p : asleep) {
                if (p.getName().equals(name)) {
                    sender.sendMessage(name + " is sleep");
                    return true;
                }
            }
            sender.sendMessage(name + " is not online");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("autoblurryeyes")) {
            if (args.length == 0) {
                sender.sendMessage((needToBlind * 100) + "%");
            } else if (args.length == 1) {
                try {
                   double pred = Double.parseDouble(args[0]) / 100;
                   if (pred < 0 || pred > 1) {
                       sender.sendMessage("Incorrect part");
                   } else {
                       needToBlind = pred;
                   }
                } catch (NumberFormatException e) {
                    sender.sendMessage("Not a number");
                }
            } else {
                tooManyArgs(sender);
            }
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
        awake.remove(p);
        asleep.add(p);
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        Player p = event.getPlayer();
        asleep.remove(p);
        awake.add(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws Exception {
        Player p = event.getPlayer();
        if (awake.contains(p)) {
            awake.remove(p);
        }
        asleep.remove(p);
        if (!online.contains(p)) {
            throw new Exception("Not online player exit the game");
        } else {
            online.remove(p);
        }
    }

    public void onAlways() {
        if (online.size() == 0) {
            return;
        }
        Player q = null;
        for (Player qq : online) {
            q = qq;
            break;
        }
        long time = q.getWorld().getTime();
        getLogger().info(time + "");
        if (time >= 13000L) {
            if ((int) (needToBlind * online.size()) <= asleep.size()) {
                for (Player p : awake) {
                    blind(p);
                }
            }
        }
    }
}
