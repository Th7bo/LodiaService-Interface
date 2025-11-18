package net.lodia.service;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.lodia.service.database.DataSaveHandler;
import net.lodia.service.database.DatabaseHandler;
import net.lodia.service.database.repository.CrateDataRepository;
import net.lodia.service.database.repository.PlayerDataRepository;
import net.lodia.service.enums.PipelineMode;
import net.lodia.service.listeners.PlayerJoinListener;
import net.lodia.service.listeners.PlayerQuitListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Getter
@Accessors(fluent = true)
public final class LodiaService extends JavaPlugin {

    @Getter
    @Accessors(fluent = true)
    private static LodiaService service;

    private DatabaseHandler databaseHandler;
    private DataSaveHandler dataSaveHandler;

    private PlayerDataRepository playerDataRepository;
    private CrateDataRepository crateDataRepository;

    @Override
    public void onEnable() {
        service = this;

        databaseHandler = new DatabaseHandler(this);
        dataSaveHandler = new DataSaveHandler(this);

        playerDataRepository = new PlayerDataRepository(this);
        crateDataRepository = new CrateDataRepository(this);

        loadWorlds();
        registerListeners();
    }

    @Override
    public void onDisable() {
        saveAllDataAndWait();
        databaseHandler.closeConnection();
    }

    public void pipelineHandler(Player player, PipelineMode mode) {
        Runnable task = () -> {
            UUID uuid = player.getUniqueId();

            switch (mode) {
                case LOAD -> {
                    var playerData = playerDataRepository.load(player);
                    var crateData = crateDataRepository.load(player);

                    if (playerData != null && crateData != null) {
                        playerDataRepository.cache().put(uuid, playerData);
                        crateDataRepository.cache().put(uuid, crateData);
                    }
                }

                case SAVE -> {
                    var playerData = playerDataRepository.cache().get(uuid);
                    var crateData = crateDataRepository.cache().get(uuid);

                    if (playerData != null) playerDataRepository.save(playerData);
                    if (crateData != null) crateDataRepository.save(crateData);
                }
            }
        };

        databaseHandler.executor().submit(task);
    }
    private void saveAllDataAndWait() {
        var playerFutures = playerDataRepository.cache().values().stream()
                .map(data -> CompletableFuture.runAsync(() -> playerDataRepository.save(data), databaseHandler.executor()))
                .toList();

        var crateFutures = crateDataRepository.cache().values().stream()
                .map(data -> CompletableFuture.runAsync(() -> crateDataRepository.save(data), databaseHandler.executor()))
                .toList();

        CompletableFuture.allOf(
                Stream.concat(playerFutures.stream(), crateFutures.stream())
                        .toArray(CompletableFuture[]::new)
        ).join();
    }
    private void loadWorlds() {}
    private void registerListeners() {
        var pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerQuitListener(this), this);
    }
}