package me.pafias.bungeeparties.commands;

import me.pafias.bungeeparties.BungeeParties;
import me.pafias.bungeeparties.PartiesConfig;
import me.pafias.bungeeparties.partymanagement.Party;
import me.pafias.bungeeparties.partymanagement.PartyManager;
import me.pafias.bungeeparties.usermanagement.User;
import me.pafias.bungeeparties.usermanagement.Users;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PartyCommand extends Command {

    public PartyCommand() {
        super("party", "party", "p");
    }

    private void help(CommandSender sender) {
        for (String s : PartiesConfig.getConfig().getStringList("help"))
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', s)));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0)
            help(sender);
        else {
            if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Only players!"));
                return;
            }
            ProxiedPlayer player = (ProxiedPlayer) sender;
            User user = Users.getUser(player);
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("invites")) {
                    List<UUID> invites = user.getInvites();
                    if (invites.isEmpty())
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You currently have no party invites!"));
                    else {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&9------- &e&lParty Invitations &r&9-------")));
                        for (UUID uuid : invites)
                            if (BungeeParties.getInstance().getProxy().getPlayer(uuid) != null) {
                                ProxiedPlayer p = BungeeParties.getInstance().getProxy().getPlayer(uuid);
                                user.getPlayer().sendMessage(
                                        new ComponentBuilder(ChatColor.GREEN + p.getName() + ChatColor.GRAY + " - ")
                                                .append(new ComponentBuilder(ChatColor.GREEN + "ACCEPT")
                                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p accept " + p.getName()))
                                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&aClick to &a&lACCEPT &r&athe party request.")).create()))
                                                        .create())
                                                .append(new ComponentBuilder(ChatColor.GRAY + " " + ChatColor.BOLD + "| ").create())
                                                .append(new ComponentBuilder(ChatColor.RED + "DENY")
                                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p deny " + p.getName()))
                                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&aClick to &c&lDENY &r&athe party request.")).create()))
                                                        .create())
                                                .create());
                            }
                    }
                } else if (args[0].equalsIgnoreCase("chat")) {
                    if (!user.isInParty()) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You are not in a party!"));
                        return;
                    }
                    user.setInPartyChat(!user.isInPartyChat());
                } else if (args[0].equalsIgnoreCase("status")) {
                    if (!user.isInParty()) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You are not in a party!"));
                        return;
                    }
                    Party party = PartyManager.getParty(user);
                    user.getPlayer().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&9------- &e&lParty Status &r&9-------")));
                    user.getPlayer().sendMessage(new TextComponent(ChatColor.GOLD + "Owner: " + ChatColor.GREEN + party.getOwner().getPlayer().getName()));
                    user.getPlayer().sendMessage(new TextComponent(ChatColor.GOLD + "Members:"));
                    for (User u : party.getMembers()) {
                        ComponentBuilder msg = new ComponentBuilder(ChatColor.GREEN + u.getPlayer().getName());
                        if (party.getOwner() == user)
                            msg.append(new ComponentBuilder(ChatColor.GRAY + " " + ChatColor.BOLD + "| ")
                                    .append(new ComponentBuilder(ChatColor.RED + "KICK")
                                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p kick " + u.getPlayer().getName()))
                                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&aClick to &c&lKICK &r&athis player")).create()))
                                            .create())
                                    .create());
                        user.getPlayer().sendMessage(msg.create());
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (!user.isInParty()) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You are not in a party!"));
                        return;
                    }
                    Party party = PartyManager.getParty(user);
                    if (party.getOwner() == user) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "The party owner cannot leave the party!"));
                    } else {
                        List<User> members = party.getMembers();
                        members.remove(user);
                        party.setMembers(members);
                        user.setInParty(false);
                        party.broadcast(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.RED + " left the party!");
                    }
                } else if (args[0].equalsIgnoreCase("disband")) {
                    if (!user.isInParty()) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You are not in a party!"));
                        return;
                    }
                    Party party = PartyManager.getParty(user);
                    if (party.getOwner() != user) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "Only the party owner is allowed to disband the party!"));
                    } else {
                        PartyManager.disbandParty(party);
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.GOLD + "You disbanded the party."));
                    }
                }
            } else {
                if (args[0].equalsIgnoreCase("invite")) {
                    if (user.isInParty()) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You are already in a party!"));
                    } else {
                        ProxiedPlayer t = BungeeParties.getInstance().getProxy().getPlayer(args[1]);
                        if (t == user.getPlayer()) {
                            user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You can't invite yourself!"));
                            return;
                        }
                        if (t == null) {
                            user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "Player not found."));
                        } else {
                            User target = Users.getUser(t);
                            List<UUID> invites = target.getInvites();
                            if (invites.contains(user.getPlayer().getUniqueId())) {
                                user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You have already sent a party request to that player."));
                            } else {
                                invites.add(user.getPlayer().getUniqueId());
                                target.setInvites(invites);
                                target.getPlayer().sendMessage(new TextComponent(ChatColor.GOLD + "You have received a party request from " + ChatColor.GRAY + user.getPlayer().getName() + ChatColor.GOLD + "!"));
                                target.getPlayer().sendMessage(new ComponentBuilder(ChatColor.GREEN + "" + ChatColor.BOLD + "ACCEPT")
                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p accept " + user.getPlayer().getName()))
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&aClick to &a&lACCEPT &r&athis party request.")).create()))
                                        .append(new ComponentBuilder(ChatColor.GRAY + " | ").create())
                                        .append(new ComponentBuilder(ChatColor.RED + "" + ChatColor.BOLD + "DENY")
                                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p deny " + user.getPlayer().getName()))
                                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&aClick to &c&lDENY &r&athis party request.")).create()))
                                                .create())
                                        .create());
                                user.getPlayer().sendMessage(new TextComponent(ChatColor.GOLD + "You sent a party request to " + ChatColor.GRAY + target.getPlayer().getName() + ChatColor.GOLD + "."));
                                user.getPlayer().sendMessage(new TextComponent(ChatColor.GOLD + "They have 30 seconds to accept it."));
                                BungeeParties.getInstance().getProxy().getScheduler().schedule(BungeeParties.getInstance(), () -> {
                                    List<UUID> list = target.getInvites();
                                    list.remove(user.getPlayer().getUniqueId());
                                    target.setInvites(list);
                                }, 30, TimeUnit.SECONDS);
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("accept")) {
                    if (user.isInParty()) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You are already in a party!"));
                    } else {
                        if (BungeeParties.getInstance().getProxy().getPlayer(args[1]) == null) {
                            user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "That player is not online."));
                        } else {
                            ProxiedPlayer t = BungeeParties.getInstance().getProxy().getPlayer(args[1]);
                            List<UUID> invites = user.getInvites();
                            if (!invites.contains(t.getUniqueId())) {
                                user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You don't have a party request from that player."));
                            } else {
                                User target = Users.getUser(t);
                                invites.remove(target.getPlayer().getUniqueId());
                                user.setInvites(invites);
                                PartyManager.createParty(target, user);
                                target.getPlayer().sendMessage(new TextComponent(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.GOLD + " joined your party!"));
                                user.getPlayer().sendMessage(new TextComponent(ChatColor.GOLD + "You joined " + ChatColor.GRAY + user.getPlayer().getName() + ChatColor.GOLD + (user.getPlayer().getName().endsWith("s") ? "' " : "'s ") + "party!"));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("deny")) {
                    if (user.isInParty()) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You are already in a party!"));
                    } else {
                        if (BungeeParties.getInstance().getProxy().getPlayer(args[1]) == null) {
                            user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "That player is not online."));
                        } else {
                            ProxiedPlayer t = BungeeParties.getInstance().getProxy().getPlayer(args[1]);
                            List<UUID> invites = user.getInvites();
                            if (!invites.contains(t.getUniqueId())) {
                                user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You don't have a party request from that player."));
                            } else {
                                User target = Users.getUser(t);
                                invites.remove(target.getPlayer().getUniqueId());
                                user.setInvites(invites);
                                target.getPlayer().sendMessage(new TextComponent(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.RED + " denied your party request!"));
                                user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You denied " + ChatColor.GRAY + user.getPlayer().getName() + ChatColor.RED + (user.getPlayer().getName().endsWith("s") ? "' " : "'s ") + "party request!"));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("kick")) {
                    if (!user.isInParty()) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You are not in a party!"));
                        return;
                    }
                    Party party = PartyManager.getParty(user);
                    if (party.getOwner() != user) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "Only the party owner is allowed to kick players from the party!"));
                    } else {
                        ProxiedPlayer t = BungeeParties.getInstance().getProxy().getPlayer(args[1]);
                        if (t == null) {
                            user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "Player not found."));
                            return;
                        }
                        if (t == user.getPlayer()) {
                            user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You cannot kick yourself!"));
                            return;
                        }
                        User target = Users.getUser(t);
                        if (!target.isInParty() || (target.isInParty() && PartyManager.getParty(target) != party)) {
                            user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "That player is not in your party."));
                            return;
                        }
                        List<User> members = party.getMembers();
                        members.remove(target);
                        party.setMembers(members);
                        target.setInParty(false);
                        party.broadcast(ChatColor.GRAY + target.getPlayer().getName() + ChatColor.RED + " was kicked from the party!");
                        target.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You got kicked from the party."));
                    }
                } else if (args[0].equalsIgnoreCase("setowner")) {
                    if (!user.isInParty()) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "You are not in a party!"));
                        return;
                    }
                    Party party = PartyManager.getParty(user);
                    if (party.getOwner() != user) {
                        user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "Only the party owner is allowed to change the owner of the party!"));
                    } else {
                        ProxiedPlayer t = BungeeParties.getInstance().getProxy().getPlayer(args[1]);
                        if (t == null) {
                            user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "Player not found."));
                            return;
                        }
                        User target = Users.getUser(t);
                        if (!party.getMembers().contains(target)) {
                            user.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "That player is not in your party!"));
                            return;
                        }
                        party.setOwner(target);
                    }
                } else if (args[0].equalsIgnoreCase("chat")) {
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
    }

}
