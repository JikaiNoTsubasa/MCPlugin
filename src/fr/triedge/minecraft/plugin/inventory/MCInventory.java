package fr.triedge.minecraft.plugin.inventory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.triedge.minecraft.plugin.MCPlugin;
import fr.triedge.minecraft.plugin.utils.Utils;
@Deprecated()
public class MCInventory implements Listener{

	private MCPlugin plugin;
	private HashMap<String, Inventory> inventories = new HashMap<>();
	private String folder;
	
	@EventHandler
	public void onPlayerReceiveItem(EntityPickupItemEvent event) {
		if (event == null)
			return;
		/*
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (player.getInventory().firstEmpty() == -1) {
				inventories.get(player.getName()).addItem(event.getItem().getItemStack());
				player.sendMessage(ChatColor.GRAY+event.getItem().getItemStack().getItemMeta().getDisplayName()+" ajouté à votre deuxième inventaire");
			}
		}
		*/
	}
	
	public void openPlayerInventory(Player player) throws FileNotFoundException, JAXBException {
		if (!inventories.containsKey(player.getName())) {
			loadInventory(player);
		}
		Inventory inv = inventories.get(player.getName());
		if (inv == null) {
			player.sendMessage(ChatColor.RED+"Aucun inventaire enregistré");
			return;
		}
		player.openInventory(inv);
	}
	
	public void loadInventory(Player player) throws FileNotFoundException, JAXBException {
		if (inventories.containsKey(player.getName()))
			return;
		ArrayList<ItemStack> list = InventoryLoader.readInventory(getFileLocation(player));
		Inventory inv = Bukkit.createInventory(player, InventoryType.CHEST);
		for (ItemStack stack : list) {
			inv.addItem(stack);
		}
		getInventories().put(player.getName(), inv);
	}
	
	public void loadAllInventories() throws FileNotFoundException, JAXBException {
		File f = new File(getFolder());
		if (f != null) {
			File[] list = f.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File file) {
					return file.getName().endsWith(".inv");
				}
			});
			for (File file : list) {
				ArrayList<ItemStack> array = InventoryLoader.readInventory(file);
				String name = file.getName().replaceAll(".inv", "");
				Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST);
				for (ItemStack stack : array) {
					if (stack != null)
						inv.addItem(stack);
				}
				inventories.put(name, inv);
				Utils.syslog(getPlugin(), "Loaded inventory: "+name);
			}
		}
	}
	
	public void init() {
		File f = new File(getFolder());
		if (!f.exists()) {
			f.mkdirs();
			Utils.syslog(getPlugin(), "Folder created: "+f.getAbsolutePath());
		}
	}
	
	public void saveAllInventories() throws IOException, JAXBException {
		for (Entry<String, Inventory> e : getInventories().entrySet()) {
				InventoryLoader.storeInventory(getFileLocation(e.getKey()), e.getValue());
				Utils.syslog(getPlugin(), "Saved inventory: "+e.getKey());
		}
	}
	
	public void checkForInventory(Player player) throws IOException, JAXBException {
		if (inventories.containsKey(player.getName()))
			return;
		Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST);
		inventories.put(player.getName(), inv);
		saveInventory(player);
	}
	
	private File getFileLocation(Player player) {
		return new File(getFolder()+player.getName()+".inv");
	}
	
	private File getFileLocation(String player) {
		return new File(getFolder()+player+".inv");
	}

	public MCInventory(MCPlugin plugin, String folder) {
		super();
		this.plugin = plugin;
		this.folder = folder;
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

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public void saveInventory(Player player) throws IOException, JAXBException {
		if (inventories.containsKey(player.getName())) {
			Inventory inv = inventories.get(player.getName());
			InventoryLoader.storeInventory(getFileLocation(player), inv);
		}
	}
	
	

}
