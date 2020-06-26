package me.pafias.bungeeparties.listeners;

import me.pafias.bungeeparties.partymanagement.Party;
import me.pafias.bungeeparties.partymanagement.PartyManager;
import me.pafias.bungeeparties.usermanagement.User;
import me.pafias.bungeeparties.usermanagement.Users;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Random;

public class JoinAndQuitListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        Users.addUser(event.getPlayer());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInParty()) {
            Party party = PartyManager.getParty(user);
            if (party.getOwner() == user)
                party.setOwner(party.getMembers().get(new Random().nextInt(party.getMembers().size())));
            user.getPlayer().chat("/party leave");
        }
        Users.removeUser(event.getPlayer());
    }

}
