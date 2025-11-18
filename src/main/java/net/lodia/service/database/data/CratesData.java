package net.lodia.service.database.data;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.lodia.service.database.PlayerDataObject;

@Getter
@Accessors(fluent = true)
public class CratesData extends PlayerDataObject {

    private int allCreate;
    private int basicCreate;
    private int rareCrate;
    private int ultraCrate;

    public CratesData() {
        this.allCreate = 0;
        this.basicCreate = 0;
        this.rareCrate = 0;
        this.ultraCrate = 0;
    }

    public void addAllCreate(int amount) {
        this.allCreate += amount;
    }
    public void removeAllCrate(int amount) {
        this.allCreate -= amount;
    }

    public void addBasicCreate(int amount) {
        this.basicCreate += amount;
    }
    public void removeBasicCrate(int amount) {
        this.basicCreate -= amount;
    }

    public void addRareCreate(int amount) {
        this.rareCrate += amount;
    }
    public void removeRareCrate(int amount) {
        this.rareCrate -= amount;
    }

    public void addUltraCreate(int amount) {
        this.ultraCrate += amount;
    }
    public void removeUltraCrate(int amount) {
        this.ultraCrate -= amount;
    }
}