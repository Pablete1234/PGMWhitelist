package me.pablete1234.pgmwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.command.graph.CommandExecutor;
import tc.oc.pgm.lib.app.ashcon.intake.Command;
import tc.oc.pgm.lib.app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;

public class PGMWhitelist extends JavaPlugin {

    @Override
    public void onEnable() {
        BasicBukkitCommandGraph g = new BasicBukkitCommandGraph();
        g.getRootDispatcherNode().registerNode("wl").registerCommands(new WlCommands());

        new CommandExecutor(this, g).register();
    }

    public static class WlCommands {
        @Command(aliases = "all", desc = "Whitelists all online players", perms = "wl.all")
        public void all(CommandSender sender) {
            sender.sendMessage("Added " + Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(pl -> !pl.isWhitelisted())
                    .peek(pl -> pl.setWhitelisted(true))
                    .count() + " players to the whitelist");
        }

        @Command(aliases = "clear", desc = "Whitelists all online players", perms = "wl.all")
        public void clear(CommandSender sender) {
            sender.sendMessage("Removed " + Bukkit.getWhitelistedPlayers()
                    .stream()
                    .peek(pl -> pl.setWhitelisted(false))
                    .count() + " players from the whitelist");
        }

        @Command(aliases = "kick", desc = "Kicks all players not on the whitelist", perms = "wl.all")
        public void kick(CommandSender sender) {
            sender.sendMessage("Kicked " + Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(pl -> !pl.isWhitelisted() && !pl.isOp() && !pl.hasPermission("wl.bypass"))
                    .peek(pl -> pl.kickPlayer(ChatColor.RED + "You are not whitelisted"))
                    .count() + " players who weren't on the whitelist");
        }

        @Command(aliases = "team", desc = "Clears the whitelist", perms = "wl.all")
        public void team(CommandSender sender) {
            Match match = PGM.get().getMatchManager().getMatch(sender);
            if (match == null) {
                sender.sendMessage(ChatColor.RED + "You are not in a match!");
                return;
            }
            sender.sendMessage("Added " + match.getCompetitors()
                    .stream()
                    .flatMap(c -> c.getPlayers().stream())
                    .map(MatchPlayer::getBukkit)
                    .filter(p -> !p.isWhitelisted())
                    .peek(p -> p.setWhitelisted(true))
                    .count() + " players who were on a team to the whitelist");
        }

    }

}
