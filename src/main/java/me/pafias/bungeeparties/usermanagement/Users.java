package me.pafias.bungeeparties.usermanagement;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Users {

    private static Map<UUID, User> users = new HashMap<>();

    public static Map<UUID, User> getUsers() {
        return users;
    }

    public static User getUser(ProxiedPlayer player) {
        return users.get(player.getUniqueId());
    }

    public static User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public static void addUser(ProxiedPlayer player) {
        if (!users.containsKey(player.getUniqueId()))
            users.put(player.getUniqueId(), new User(player));
    }

    public static void removeUser(ProxiedPlayer player) {
        users.remove(player);
    }

}
