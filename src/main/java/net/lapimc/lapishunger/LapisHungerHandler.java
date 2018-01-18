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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LapisHungerHandler implements Listener {

    private LapisHunger plugin;

    LapisHungerHandler(LapisHunger p) {
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, p);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        HungerPlayer player = new HungerPlayer(plugin, e.getPlayer().getUniqueId());
        plugin.addPlayer(player.getUUID(), player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.removePlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onHungerChangeEvent(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            HungerPlayer hunger = plugin.getPlayer(p.getUniqueId());
            hunger.setFood((double) e.getFoodLevel());
        }
    }

    @EventHandler
    public void onItemConsumeEvent(PlayerItemConsumeEvent e) {
        foodItem itemEaten = foodItem.NULL;
        for (foodItem item : foodItem.values()) {
            if (e.getItem().getType().toString().equalsIgnoreCase(item.getName())) {
                itemEaten = item;
                break;
            }
        }
        if (itemEaten == foodItem.NULL) {
            plugin.getLogger().info("Player " + e.getPlayer() + " Consumed an item that isn't detected by this plugin: "
                    + e.getItem().getType().name());
            return;
        }
        Player p = e.getPlayer();
        HungerPlayer hunger = plugin.getPlayer(p.getUniqueId());
        Double toAdd = plugin.getConfig().getDouble("FoodValues." + itemEaten.getName(), itemEaten.getValue());
        Double difference = toAdd - itemEaten.getValue();
        hunger.addFood(difference);
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player && e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            Player p = (Player) e.getEntity();
            HungerPlayer hunger = plugin.getPlayer(p.getUniqueId());
            double newHealth = e.getAmount() + p.getHealth();
            boolean isCorrectHealth = newHealth == hunger.getHealth();
            if (!isCorrectHealth) {
                e.setCancelled(true);
                hunger.update();
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            plugin.getPlayer(p.getUniqueId()).subtractHealth(e.getFinalDamage());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        HungerPlayer hunger = plugin.getPlayer(p.getUniqueId());
        if (p.isSprinting()) {
            hunger.sprinting = true;
        }
        if (p.getVelocity().getY() > 0) {
            hunger.jumping = true;
        }
        if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
            hunger.swimming = true;
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            HungerPlayer hunger = plugin.getPlayer(p.getUniqueId());
            hunger.subtractFood(plugin.getConfig().getDouble("FoodCosts.AttackEntity", 0.1));
            hunger.update();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        HungerPlayer hunger = plugin.getPlayer(e.getPlayer().getUniqueId());
        hunger.subtractFood(plugin.getConfig().getDouble("FoodCosts.BlockBreak", 0.005));
        hunger.update();
    }

    @SuppressWarnings("unused")
    private enum foodItem {
        NULL(0, "NULL"), Apple(4, "APPLE"), BakedPotato(5, "BAKED_POTATO"), Beetroot(1, "BEETROOT"), BeetrootSoup(6, "BEETROOT_SOUP"),
        Bread(5, "BREAD"), CakeSlice(2, "CAKE"), CakeWhole(14, "CAKE_BLOCK"), Carrot(3, "CARROT"),
        ChorusFruit(4, "CHORUS_FRUIT"), Clownfish(1, "CLOWNFISH"), CookedChicken(6, "COOKED_CHICKEN"),
        CookedFish(5, "COOKED_FISH"), CookedMutton(6, "COOKED_MUTTON"), CookedPorkchop(8, "COOKED_PORKCHOP"),
        CookedRabbit(5, "COOKED_RABBIT"), CookedSalmon(6, "COOKED_SALMON"), Cookie(2, "COOKIE"),
        GoldenApple(4, "GOLDEN_APPLE"), GoldenCarrot(6, "GOLDEN_CARROT"), Melon(2, "MELON"),
        MushroomStew(6, "MUSHROOM_STEW"), PoisonousPotato(2, "POISONOUS_POTATO"), Potato(1, "POTATO"),
        Pufferfish(1, "PUFFERFISH"), PumpkinPie(8, "PUMPKIN_PIE"), RabbitStew(10, "RABBIT_STEW"),
        RawBeef(3, "RAW_BEEF"), RawChicken(2, "RWA_CHICKEN"), RawFish(2, "RAW_FISH"), RawMutton(2, "RAW_MUTTON"),
        RawPorkchop(3, "RAW_PORKCHOP"), RawRabbit(3, "RAW_RABBIT"), RawSalmon(2, "RAW_SALMON"),
        RottenFlesh(4, "ROTTEN_FLESH"), SpiderEye(2, "SPIDER_EYE"), Steak(8, "STEAK");

        private int value;

        private String name;

        foodItem(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

    }

}
