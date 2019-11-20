package fr.triedge.minecraft.plugin.magic;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class Spell {

	protected Player player;
	protected FileConfiguration config;
	protected Plugin plugin;
	public abstract float getDamageFactor();
	
	public Spell(Player player, FileConfiguration config, Plugin plugin) {
		super();
		this.player = player;
		this.config = config;
		this.plugin = plugin;
	}
	public abstract String getName();
	public abstract String getDisplayName();
	public abstract void launch();
	public int addXP() {
		int xp = 1;
		if (!configPropertyExists(config, player.getName()+"."+getName()+".level")) {
			config.set(player.getName()+"."+getName()+".level", 1);
		}
		if (!configPropertyExists(config, player.getName()+"."+getName()+".xp")) {
			config.set(player.getName()+"."+getName()+".xp", xp);
		}else {
			xp += config.getInt(player.getName()+"."+getName()+".xp");
			config.set(player.getName()+"."+getName()+".xp", xp);
			if (xp % 50 == 0) {
				// level up every 50 xp
				int level = config.getInt(player.getName()+"."+getName()+".level");
				level++;
				config.set(player.getName()+"."+getName()+".level", level);
				float player_xp_add = level*2.5f;
				player.setTotalExperience(player.getTotalExperience() + (int)player_xp_add);
				player.sendMessage(ChatColor.GREEN+"Level UP: "+getDisplayName()+" lvl "+level);
				player.sendMessage(ChatColor.GREEN+"Personnage XP +"+player_xp_add);
			}
		}
		return xp;
	}
	public float getDamage() {
		if (!configPropertyExists(config, player.getName()+"."+getName()+".level")) {
			config.set(player.getName()+"."+getName()+".level", 1);
		}
		int level = config.getInt(player.getName()+"."+getName()+".level");
		return getDamageFactor()*level;
	}
	public boolean configPropertyExists(FileConfiguration config, String prop) {
		return (config.get(prop)==null)?false:true;
	}
}
