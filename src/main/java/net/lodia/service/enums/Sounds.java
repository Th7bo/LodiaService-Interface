package net.lodia.service.enums;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Sound;

@Getter
@Accessors(fluent = true)
public enum Sounds {

    SUCCESS(Sound.ENTITY_PLAYER_LEVELUP),
    ERROR(Sound.ENTITY_VILLAGER_NO),
    CLICK(Sound.UI_BUTTON_CLICK),
    COUNTDOWN(Sound.BLOCK_NOTE_BLOCK_HAT),
    TELEPORT(Sound.ENTITY_ENDERMAN_TELEPORT);

    private final Sound sound;

    Sounds(Sound sound) {
        this.sound = sound;
    }
}