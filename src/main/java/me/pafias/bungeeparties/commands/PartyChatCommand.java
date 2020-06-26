package me.pafias.bungeeparties.commands;

import me.pafias.bungeeparties.partymanagement.Party;
import me.pafias.bungeeparties.partymanagement.PartyManager;
import me.pafias.bungeeparties.usermanagement.User;
import me.pafias.bungeeparties.usermanagement.Users;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyChatCommand extends Command {

    public PartyChatCommand() {
        super("pchat", "party", "partychat", "pc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Only players!"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        User user = Users.getUser(player);
        if (args.length == 0) {
            if (!user.isInParty()) {
                user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You are not in a party!"));
                return;
            }
            user.setInPartyChat(!user.isInPartyChat());
        } else {
            if (!user.isInParty()) {
                user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You are not in a party!"));
                return;
            }
            Party party = PartyManager.getParty(user);
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++)
                sb.append(args[i]).append(" ");
            party.broadcastPartyChat(user, sb.toString());
        }
    }

}
