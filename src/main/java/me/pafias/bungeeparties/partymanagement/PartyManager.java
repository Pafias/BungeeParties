package me.pafias.bungeeparties.partymanagement;

import me.pafias.bungeeparties.usermanagement.User;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class PartyManager {

    private static List<Party> parties = new ArrayList<>();

    public static List<Party> getParties() {
        return parties;
    }

    public static Party getParty(User user) {
        for (Party p : parties)
            if (p.getMembers().contains(user) || p.getOwner() == user)
                return p;
        return null;
    }

    public static void createParty(User owner, User invited) {
        parties.add(new Party(owner, invited));
    }

    public static void disbandParty(Party party) {
        party.broadcast(ChatColor.GOLD + "The party has been disbanded!");
        List<User> members = party.getMembers();
        for (User u : members)
            u.setInParty(false);
        members.clear();
        party.setMembers(members);
        party.getOwner().setInParty(false);
        party.setOwner(null);
        parties.remove(party);
    }

}
