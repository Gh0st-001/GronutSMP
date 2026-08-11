package com.donutclone.economy.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private final Map<UUID, GuiSession> sessions = new HashMap<>();

    public GuiSession get(UUID uuid) {
        return sessions.computeIfAbsent(uuid, u -> new GuiSession());
    }

    public void clear(UUID uuid) {
        sessions.remove(uuid);
    }
}
