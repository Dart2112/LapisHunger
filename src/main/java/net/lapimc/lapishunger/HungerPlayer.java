/*
 * Copyright 2018 Benjamin Martin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.lapimc.lapishunger;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HungerPlayer {

    public boolean sprinting = false;
    public boolean jumping = false;
    public boolean swimming = false;
    private Double health = 20.0;
    private Double food = 20.0;
    private Player p;

    HungerPlayer(LapisHunger plugin, UUID uuid) {
        p = Bukkit.getPlayer(uuid);
        int tickTime = plugin.getConfig().getInt("FoodTickTime", 1);
        Runnable hungerTickTask = () -> {
            if (food == 0.0) {
                subtractHealth(plugin.getConfig().getDouble("StarvingHealth", 0.5));
            }
            double regenFood = plugin.getConfig().getDouble("RegenFoodRequirement", 18.0);
            if (food > regenFood) {
                addHealth(plugin.getConfig().getDouble("RegenAmount", 2));
                subtractFood(plugin.getConfig().getDouble("RegenCost", 1.5));
            }
            Double hungerToRemove = 0.0;
            if (sprinting) {
                sprinting = false;
                hungerToRemove += plugin.getConfig().getDouble("FoodCosts.Sprinting", 0.1);
            }
            if (jumping) {
                jumping = false;
                hungerToRemove += plugin.getConfig().getDouble("FoodCosts.Jumping", 0.05);
            }
            if (swimming) {
                swimming = false;
                hungerToRemove += plugin.getConfig().getDouble("FoodCosts.Swimming", 0.01);
            }
            Biome biome = p.getWorld().getBiome(p.getLocation().getBlockX(), p.getLocation().getBlockY());
            if (biome == Biome.DESERT) {
                hungerToRemove += plugin.getConfig().getDouble("FoodCosts.BiomeDesert");
            } else {
                if (p.getWorld().hasStorm()) {
                    if (p.getWorld().getHighestBlockAt(p.getLocation()).getY() < p.getLocation().getY()) {
                        hungerToRemove += plugin.getConfig().getDouble("FoodCosts.WeatherExposure");
                    }
                }
            }
            subtractFood(hungerToRemove);
            update();
        };
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, hungerTickTask, tickTime, tickTime);
    }

    public UUID getUUID() {
        return p.getUniqueId();
    }

    public double getHealth() {
        return health;
    }

    private void addHealth(double amount) {
        this.health = Math.min(20.0, health + amount);
    }

    public void subtractHealth(double amount) {
        this.health = Math.max(0.0, health - amount);
    }

    public double getFood() {
        return food;
    }

    public void setFood(Double amount) {
        food = amount;
    }

    public void addFood(Double amount) {
        food = Math.min(20.0, food + amount);
    }

    public void subtractFood(Double amount) {
        food = Math.max(0.0, food - amount);
    }

    public void update() {
        p.setHealth(health);
        p.setFoodLevel(food.intValue());
    }
}
