package me.ric.basic;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
* Handle events for all Player related events
* @author Dinnerbone
*/
public class BasicPlayerListener extends PlayerListener {
    private final Basic plugin;

    public BasicPlayerListener(Basic instance) {
        plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println(event.getPlayer().getName() + " joined the server! :D");
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        System.out.println(event.getPlayer().getName() + " left the server! :'(");
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (Math.floor(from.getX()) != Math.floor(to.getX() ) ) {
        	event.getPlayer().sendMessage(String.format("From %.2f,%.2f,%.2f to %.2f,%.2f,%.2f", from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ()));
        }
    }
}
