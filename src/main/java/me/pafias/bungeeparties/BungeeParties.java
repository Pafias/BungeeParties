package me.pafias.bungeeparties;

import me.pafias.bungeeparties.commands.PartyChatCommand;
import me.pafias.bungeeparties.commands.PartyCommand;
import me.pafias.bungeeparties.listeners.ChatListener;
import me.pafias.bungeeparties.listeners.JoinAndQuitListener;
import me.pafias.bungeeparties.listeners.ServerListener;
import me.pafias.bungeeparties.usermanagement.Users;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class BungeeParties extends Plugin {

    private static BungeeParties instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        for (ProxiedPlayer p : getProxy().getPlayers())
            Users.addUser(p);
        registerCommands();
        registerListeners();
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void registerCommands() {
        getProxy().getPluginManager().registerCommand(this, new PartyCommand());
        getProxy().getPluginManager().registerCommand(this, new PartyChatCommand());
    }

    private void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new JoinAndQuitListener());
        getProxy().getPluginManager().registerListener(this, new ServerListener());
        getProxy().getPluginManager().registerListener(this, new ChatListener());
    }

    public static BungeeParties getInstance() {
        return instance;
    }

}
