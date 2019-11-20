package fr.triedge.minecraft.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationHandler {

	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	 
	private MCPlugin plugin;
	private String name;
	 
	public ConfigurationHandler(MCPlugin plugin, String name) {
	    this.plugin = plugin;
	    this.name = name;
	}
	 
	public void reloadCustomConfig() {
	    if(customConfigFile == null)
	        customConfigFile = new File(plugin.getDataFolder(), name + ".yml");
	    customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	 
	    InputStream defConfigStream = plugin.getResource(name + ".yml");
	    if(defConfigStream != null) {
	    	Reader reader = new InputStreamReader(defConfigStream);
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
	        customConfig.setDefaults(defConfig);
	    }
	}
	 
	public FileConfiguration getConfig() {
	    if(customConfig == null)
	        reloadCustomConfig();
	    return customConfig;
	}
	 
	public void saveConfig() {
	    if(customConfig == null || customConfigFile == null)
	        return;
	    try {
	         getConfig().save(customConfigFile);
	    } catch(IOException ex) {
	        plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
	    }
	}
	 
	public void saveDefaultConfig() {
	    if(customConfigFile == null) 
	        customConfigFile = new File(plugin.getDataFolder(), name + ".yml");
	 
	    if(!customConfigFile.exists())
	        plugin.saveResource(name + ".yml", false);
	    
	}
}
