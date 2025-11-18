package net.lodia.service;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.lodia.service.api.CombatAPI;
import net.lodia.service.api.TeleportAPI;
import net.lodia.service.commands.SpawnCommand;
import net.lodia.service.database.DataRepository;
import net.lodia.service.database.DataSaveHandler;
import net.lodia.service.database.DatabaseHandler;
import net.lodia.service.database.IPlayerDataObject;
import net.lodia.service.database.repository.CrateDataRepository;
import net.lodia.service.database.repository.PlayerDataRepository;
import net.lodia.service.enums.PipelineMode;
import net.lodia.service.listeners.PlayerJoinListener;
import net.lodia.service.listeners.PlayerQuitListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Getter
@Accessors(fluent = true)
public final class LodiaService extends JavaPlugin {

    @Getter
    @Accessors(fluent = true)
    private static LodiaService service;

    private final String prefix = "<dark_gray>[<#FC004D>Lodia<dark_gray>] <gray>";
    private final String mainColor = "<#FC004D>";

    private DatabaseHandler databaseHandler;
    private DataSaveHandler dataSaveHandler;

    private PlayerDataRepository playerDataRepository;
    private CrateDataRepository crateDataRepository;

    private TeleportAPI teleportAPI;
    private CombatAPI combatAPI;

    private final List<DataRepository<?>> dataRepositories = new ArrayList<>();

    @Override
    public void onEnable() {
        service = this;

        databaseHandler = new DatabaseHandler(this);
        dataSaveHandler = new DataSaveHandler(this);

        playerDataRepository = new PlayerDataRepository(this);
        crateDataRepository = new CrateDataRepository(this);

        dataRepositories.add(playerDataRepository);
        dataRepositories.add(crateDataRepository);

        teleportAPI = new TeleportAPI(this);
        combatAPI = new CombatAPI(this);

        loadWorlds();
        registerListeners();
        registerCommands();
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
                    for (DataRepository<?> repository : dataRepositories) {
                        repository.load(player); // already puts it in cache
                    }
                }

                case SAVE -> {
                    for (DataRepository<?> repository : dataRepositories) {
                        var data = repository.cache().get(uuid);
                        if (data != null) {
                            saveData(repository, data);
                        }
                    }
                }

                case QUIT_SAVE -> {
                    for (DataRepository<?> repository : dataRepositories) {
                        var data = repository.cache().get(uuid);
                        if (data != null) {
                            saveData(repository, data);
                            repository.cache().remove(uuid);
                        }
                    }
                }
            }
        };

        databaseHandler.executor().submit(task);
    }

    @SuppressWarnings("unchecked")
    private <T extends IPlayerDataObject> void saveData(
            DataRepository<?> repository, T data) {
        ((DataRepository<T>) repository).save(data);
    }
    private void saveAllDataAndWait() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (var repository : dataRepositories) {
            futures.addAll(repository.cache().values().stream()
                    .map(data -> CompletableFuture.runAsync(() -> saveData(repository, data), databaseHandler.executor()))
                    .toList());
        }

        CompletableFuture.allOf(
                futures.toArray(CompletableFuture[]::new)
        ).join();
    }
    private void loadWorlds() {}

    private void registerListeners() {
        var pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerQuitListener(this), this);
    }
    private void registerCommands() {

        registerCommand("spawn", new SpawnCommand());
    }
}