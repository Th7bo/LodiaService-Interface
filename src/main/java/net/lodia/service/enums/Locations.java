package net.lodia.service.enums;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Getter
@Accessors(fluent = true)
public enum Locations {

    SPAWN(new Location(Bukkit.getWorld("spawn"), 0.5, 100, 0.5, -90, 0)),
    AFK(new Location(Bukkit.getWorld("afk"), 0.5, 100, 0.5, -90, 0));

    private final Location location;

    Locations(Location location) {
        this.location = location;
    }

    public static boolean isProtectedWorld(String worldName) {
        for (Locations loc : values()) {
            var world = loc.location.getWorld();
            if (world != null && world.getName().equalsIgnoreCase(worldName)) {
                return true;
            }
        }
        return false;
    }
}