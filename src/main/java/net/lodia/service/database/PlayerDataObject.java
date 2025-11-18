package net.lodia.service.database;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Setter 
@Getter 
@Accessors(fluent = true)
public abstract class PlayerDataObject implements IPlayerDataObject {
    private UUID uuid;

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public void uuid(UUID uuid) {
        this.uuid = uuid;
    }
}
