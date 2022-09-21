package me.gameisntover.killstreakcounter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public final class KillStreakCounter extends JavaPlugin implements Listener {
    public HashMap<UUID,Integer> killMap = new HashMap<>();
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this,this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerKillEvent(PlayerDeathEvent e){
        Player player = e.getEntity();
        if (e.getEntity().getKiller() != null) {
            Player killer = player.getKiller();
            if (!killMap.containsKey(killer.getUniqueId())) killMap.put(killer.getUniqueId(),0);
            killMap.put(killer.getUniqueId(),killMap.get(killer.getUniqueId()) +1);
            int kills = killMap.get(killer.getUniqueId());
            Optional<String> title = Optional.ofNullable(getConfig().getString("killstreak.title."+ kills));
            Optional<String> subtitle = Optional.ofNullable(getConfig().getString("killstreak.subtitle."+ kills));
            Optional<String> message = Optional.ofNullable(getConfig().getString("killstreak.message."+ kills));
            Optional<String> sound = Optional.ofNullable(getConfig().getString("killstreak.sound."+ kills));
            for (Player p : Bukkit.getOnlinePlayers()){
                message.ifPresent(s -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("%playername%", killer.getName()))));
                if (subtitle.isPresent() && title.isPresent())
                    p.sendTitle(ChatColor.translateAlternateColorCodes('&',title.get()),ChatColor.translateAlternateColorCodes('&',subtitle.get()),20,20,20);
                if (sound.isPresent()) {
                    String[] args = sound.get().split(":");
                    String sn = args[0];
                    float volume = Float.parseFloat(args[1]);
                    float pitch = Float.parseFloat(args[2]);
                    p.playSound(p.getLocation(), Sound.valueOf(sn),volume,pitch);

                }
            }
        }
        killMap.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        killMap.remove(e.getPlayer().getUniqueId());
    }
}
