package fr.triedge.minecraft.plugin;


public class ConfigSaverTask implements Runnable{
	
	private final MCPlugin plugin;
	
	public ConfigSaverTask(MCPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if (plugin.magic.getConfig() != null) {
			plugin.magic.saveConfig();
			plugin.getLogger().info("SCHEDULER: Config Magic saved.");
		}
	}

}
