package me.pafias.bungeeparties.usermanagement;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private ProxiedPlayer player;
    private List<UUID> invites;

    private boolean inParty;
    private boolean inPartyChat;

    public User(ProxiedPlayer player) {
        this.player = player;
        this.invites = new ArrayList<UUID>();
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public List<UUID> getInvites() {
        return invites;
    }

    public void setInvites(List<UUID> list) {
        this.invites = list;
    }

    public boolean isInParty() {
        return inParty;
    }

    public void setInParty(boolean inParty) {
        this.inParty = inParty;
        if (!inParty)
            inPartyChat = false;
    }

    public boolean isInPartyChat() {
        return inPartyChat;
    }

    public void setInPartyChat(boolean inPartyChat) {
        this.inPartyChat = inPartyChat;
        player.sendMessage(new TextComponent(ChatColor.GOLD + "Party chat is now " + (inPartyChat ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled")));
    }

}
