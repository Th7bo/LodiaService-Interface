package net.lodia.service.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class MessageConverter {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component convert(String input) {
        return miniMessage.deserialize(input);
    }
}