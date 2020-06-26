package me.pafias.bungeeparties.partymanagement;

import me.pafias.bungeeparties.usermanagement.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.ArrayList;
import java.util.List;

public class Party {

    private User owner;
    private List<User> members;
    private ServerInfo server;

    public Party(User owner, User invited) {
        owner.setInParty(true);
        invited.setInParty(true);
        this.owner = owner;
        this.members = new ArrayList<>();
        members.add(invited);
        this.server = owner.getPlayer().getServer().getInfo();
        invited.getPlayer().connect(server, ServerConnectEvent.Reason.PLUGIN);
    }

    public void broadcast(String message) {
        owner.getPlayer().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
        for (User u : members)
            u.getPlayer().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public void broadcastPartyChat(User user, String message) {
        String msg = ChatColor.LIGHT_PURPLE + "[Party] " + ChatColor.RESET + user.getPlayer().getName() + ": " + ChatColor.translateAlternateColorCodes('&', message);
        owner.getPlayer().sendMessage(new TextComponent(msg));
        for (User u : members)
            u.getPlayer().sendMessage(new TextComponent(msg));
    }

    public void teleport(ServerInfo server) {
        for (User u : members)
            u.getPlayer().connect(server, ServerConnectEvent.Reason.PLUGIN);
        this.server = server;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        members.remove(owner);
        members.add(this.owner);
        this.owner = owner;
        broadcast(ChatColor.GOLD + "The owner of the party has been changed to " + ChatColor.GRAY + owner.getPlayer().getName());
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public ServerInfo getServer() {
        return server;
    }

}
