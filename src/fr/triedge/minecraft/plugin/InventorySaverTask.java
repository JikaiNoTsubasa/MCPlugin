package fr.triedge.minecraft.plugin;

public class InventorySaverTask implements Runnable{

private final MCPlugin plugin;
	
	public InventorySaverTask(MCPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if (plugin.inventoryManager != null) {
			plugin.inventoryManager.storeAllInventories();
			plugin.getLogger().info("SCHEDULER: Inventories saved.");
		}
	}
}
