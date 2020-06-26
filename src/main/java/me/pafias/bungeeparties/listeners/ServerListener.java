package me.pafias.bungeeparties.listeners;

import me.pafias.bungeeparties.partymanagement.Party;
import me.pafias.bungeeparties.partymanagement.PartyManager;
import me.pafias.bungeeparties.usermanagement.User;
import me.pafias.bungeeparties.usermanagement.Users;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerListener implements Listener {

    @EventHandler
    public void onServerSwitch(ServerConnectEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInParty()) {
            Party party = PartyManager.getParty(user);
            if (party.getOwner() != user && event.getReason() != ServerConnectEvent.Reason.PLUGIN) {
                event.setCancelled(true);
                user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "Only the party owner can change servers!"));
            } else {
                party.teleport(event.getTarget());
            }
        }
    }

}
