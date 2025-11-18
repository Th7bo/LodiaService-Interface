package net.lodia.service.database;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.Optional;

public interface DataRepository<T extends IPlayerDataObject> {

    Map<UUID, T> cache();

    void save(T data);

    T load(Player player);

    default Optional<T> get(UUID uuid) {
        return Optional.ofNullable(cache().get(uuid));
    }

    default T getOrLoad(Player player) {
        UUID uuid = player.getUniqueId();
        T data = cache().get(uuid);
        if (data != null) {
            return data;
        }
        return load(player);
    }

    default T removeFromCache(UUID uuid) {
        return cache().remove(uuid);
    }

    default boolean isCached(UUID uuid) {
        return cache().containsKey(uuid);
    }

    default void clearCache() {
        cache().clear();
    }

    default int cacheSize() {
        return cache().size();
    }
}
