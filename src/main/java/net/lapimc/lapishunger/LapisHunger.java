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

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class LapisHunger extends JavaPlugin {

    private HashMap<UUID, HungerPlayer> hungerPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        new LapisHungerHandler(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void addPlayer(UUID uuid, HungerPlayer player) {
        hungerPlayers.put(uuid, player);
    }

    public void removePlayer(UUID uuid) {
        hungerPlayers.remove(uuid);
    }

    public HungerPlayer getPlayer(UUID uuid) {
        return hungerPlayers.get(uuid);
    }
}
