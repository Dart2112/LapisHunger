package net.lapimc.lapishunger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
            boolean isCorrectHunger = e.getFoodLevel() == hunger.getFood();
            if (!isCorrectHunger) {
                e.setCancelled(true);
                hunger.update();
            }
        }
    }

    @EventHandler
    public void onItemConsumeEvent(PlayerItemConsumeEvent e) {
        foodItem itemEaten = foodItem.Beetroot;
        for (foodItem item : foodItem.values()) {
            if (e.getItem().getType().toString().equalsIgnoreCase(item.getName())) {
                itemEaten = item;
                break;
            }
        }
        Player p = e.getPlayer();
        HungerPlayer hunger = plugin.getPlayer(p.getUniqueId());
        int toAdd = plugin.getConfig().getInt("FoodValues." + itemEaten.getName(), itemEaten.getValue());
        hunger.addFood((double) toAdd);
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
            plugin.getPlayer(p.getUniqueId()).setHealth(p.getHealth() - e.getFinalDamage());
        }
    }

    private enum foodItem {
        Apple(4, "APPLE"), BakedPotato(5, "BAKED_POTATO"), Beetroot(1, "BEETROOT"), BeetrootSoup(6, "BEETROOT_SOUP"),
        Bread(5, "BREAD"), CakeSlice(2, "CAKE_SLICE"), CakeWhole(14, "CAKE"), Carrot(3, "CARROT"),
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
