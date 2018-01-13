package net.lapimc.lapishunger;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class LapisHunger extends JavaPlugin {

    private HashMap<UUID, HungerPlayer> hungerPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        new LapisHungerHandler(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void addPlayer(UUID uuid, HungerPlayer player){
        hungerPlayers.put(uuid, player);
    }

    public void removePlayer(UUID uuid){
        hungerPlayers.remove(uuid);
    }

    public HungerPlayer getPlayer(UUID uuid){
        return hungerPlayers.get(uuid);
    }
}
