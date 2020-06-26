package me.pafias.bungeeparties.listeners;

import me.pafias.bungeeparties.partymanagement.Party;
import me.pafias.bungeeparties.partymanagement.PartyManager;
import me.pafias.bungeeparties.usermanagement.User;
import me.pafias.bungeeparties.usermanagement.Users;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!event.getMessage().startsWith("/")) {
            User user = Users.getUser((ProxiedPlayer) event.getSender());
            if (user.isInParty()) {
                if (user.isInPartyChat()) {
                    event.setCancelled(true);
                    handle(user, event.getMessage());
                } else {
                    if (event.getMessage().startsWith("@")) {
                        event.setCancelled(true);
                        handle(user, event.getMessage().replaceFirst("@", ""));
                    }
                }
            }
        }
    }

    private void handle(User user, String message) {
        Party party = PartyManager.getParty(user);
        party.broadcastPartyChat(user, message);
    }

}
