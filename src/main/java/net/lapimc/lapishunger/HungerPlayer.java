package net.lapimc.lapishunger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HungerPlayer {

    private Double health = 20.0;
    private Double food = 20.0;
    private Player p;

    HungerPlayer(LapisHunger plugin, UUID uuid) {
        p = Bukkit.getPlayer(uuid);
        //TODO: get actual values for how often it should tick
        Runnable hungerTickTask = () -> {
            Double hungerToRemove = 0.0;
            if (p.isSprinting()) {
                hungerToRemove += plugin.getConfig().getDouble("FoodCosts.Sprinting", 0.1);
            }
            subtractFood(hungerToRemove);
            update();
        };
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, hungerTickTask, 1, 1);
    }

    public UUID getUUID() {
        return p.getUniqueId();
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public int getFood() {
        return food.intValue();
    }

    public void addFood(Double amount) {
        food += amount;
    }

    public void subtractFood(Double amount) {
        food -= amount;
    }

    public void update() {
        p.setHealth(health);
        p.setFoodLevel(food.intValue());
    }
}
