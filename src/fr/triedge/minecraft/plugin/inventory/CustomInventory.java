package fr.triedge.minecraft.plugin.inventory;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.triedge.minecraft.plugin.ConfigurationHandler;
import fr.triedge.minecraft.plugin.MCPlugin;
import fr.triedge.minecraft.plugin.utils.Utils;

public class CustomInventory {

	private ConfigurationHandler config;
	private MCPlugin plugin;
	private HashMap<String, Inventory> inventories = new HashMap<>();

	public CustomInventory(ConfigurationHandler config, MCPlugin plugin) {
		super();
		this.config = config;
		this.plugin = plugin;
	}
	
	private String getInvKey(Player player, int inventoryId) {
		return player.getName()+".inv-"+inventoryId;
	}
	
	private String getInvKey(String player, int inventoryId) {
		return player+".inv-"+inventoryId;
	}
	
	public Inventory getInventory(Player player, int id) {
		String invKey = getInvKey(player, id);
		if (!inventories.containsKey(invKey))
			return null;
		return inventories.get(invKey);
	}
	
	public void loadAllInventories() {
		if (getConfig() == null || getConfig().getConfig() == null) {
			getPlugin().getLogger().log(Level.SEVERE, "Cannot load inventories, something null");
			return;
		}
		Set<String> players = getConfig().getConfig().getKeys(false);
		for (String player : players) {
			Set<String> usrInvnetories = getConfig().getConfig().getConfigurationSection(player).getKeys(false);
			for (String usrInv : usrInvnetories) {
				Inventory inv = loadInventoryFromFile(player, usrInv);
				if (inv !=null) {
					inventories.put(player+"."+usrInv, inv);
				}
			}
		}
	}
	
	private Inventory loadInventoryFromFile(String player, String inventoryName) {
		String num = inventoryName.split("-")[1];
		if (num == null)
			return null;
		return loadInventoryFromFile(player, Integer.parseInt(num));
	}
	
	private Inventory loadInventoryFromFile(String playerName, int id) {
		if (playerName == null || getConfig() == null || getConfig().getConfig() == null) {
			getPlugin().getLogger().log(Level.SEVERE, "Cannot load inventories, something null");
			return null;
		}
		String invKey = getInvKey(playerName, id);
		Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST);
		if (getConfig().getConfig().isSet(invKey)) {
			Set<String> list = getConfig().getConfig().getConfigurationSection(invKey).getKeys(false);
			for (String el : list) {
				ItemStack stack = getConfig().getConfig().getItemStack(invKey+"."+el);
				if (stack != null) {
					inv.addItem(stack);
				}
			}
			return inv;
		}
		return null;
	}
	
	public void storeAllInventories() {
		for (Entry<String,Inventory> e : inventories.entrySet()) {
			if (e.getKey() !=null) {
				Utils.log(getPlugin(), "Storing inventory for: "+e.getKey());
				String player = e.getKey().split("\\.")[0];
				int id = Integer.parseInt(e.getKey().split("\\.")[1].split("-")[1]);
				storeInventory(player, id);
				
			}else {
				Utils.log(getPlugin(), "NULL inventory for: "+e.getKey());
			}
		}
	}
	
	public void storeInventory (String player, int id) {
		if (inventories.containsKey(player+".inv-"+id)) {
			Inventory inv = inventories.get(player+".inv-"+id);
			storeInventory(player, inv, id);
		}
	}
	
	private void storeInventory (String player, Inventory inventory, int inventoryId) {
		if (player == null || inventory == null || getConfig() == null || getConfig().getConfig() == null) {
			getPlugin().getLogger().log(Level.SEVERE, "Cannot store inventories, something null");
			return;
		}
		String invKey = getInvKey(player, inventoryId);
		
		// Clear Stored inventory
		if (getConfig().getConfig().isSet(invKey)) {
			getConfig().getConfig().set(invKey, null);
		}
		
		ItemStack[] stacks = inventory.getContents();
		for (int idx = 0; idx < stacks.length; ++idx) {
			ItemStack stack = stacks[idx];
			if (stack == null)
				continue;
			String key = invKey+"."+idx;
			getConfig().getConfig().set(key, stack);
		}
		getConfig().saveConfig();
	}

	public ConfigurationHandler getConfig() {
		return config;
	}

	public void setConfig(ConfigurationHandler config) {
		this.config = config;
	}

	public MCPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(MCPlugin plugin) {
		this.plugin = plugin;
	}

	public HashMap<String, Inventory> getInventories() {
		return inventories;
	}

	public void setInventories(HashMap<String, Inventory> inventories) {
		this.inventories = inventories;
	}

	public void createEmptyInv(Player p, int id) {
		String defaultInv = getInvKey(p, id);
		if (!inventories.containsKey(defaultInv)) {
			inventories.put(defaultInv, Bukkit.createInventory(p, InventoryType.CHEST));
			storeInventory(p.getName(), id);
		}
	}
}
