package fr.triedge.minecraft.plugin.magic;

import java.util.HashMap;

import org.bukkit.event.Listener;

import fr.triedge.minecraft.plugin.ConfigurationHandler;
import fr.triedge.minecraft.plugin.MCPlugin;

public class MagicListener implements Listener{

	private HashMap<String, MagicData> magics = new HashMap<String, MagicData>();
	private MCPlugin plugin;
	private ConfigurationHandler config;
	
	public MagicListener(MCPlugin plugin) {
		super();
		this.setPlugin(plugin);
	}
	

	public HashMap<String, MagicData> getMagics() {
		return magics;
	}

	public void setMagics(HashMap<String, MagicData> magics) {
		this.magics = magics;
	}

	public MCPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(MCPlugin plugin) {
		this.plugin = plugin;
	}

	public ConfigurationHandler getConfig() {
		return config;
	}

	public void setConfig(ConfigurationHandler config) {
		this.config = config;
	}
}
