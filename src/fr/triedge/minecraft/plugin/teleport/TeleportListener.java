package fr.triedge.minecraft.plugin.teleport;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import fr.triedge.minecraft.plugin.ConfigurationHandler;
import fr.triedge.minecraft.plugin.Custom;
import fr.triedge.minecraft.plugin.MCPlugin;

// 20200706.3
public class TeleportListener implements Listener{
	
	public static final String WARP_LOCATION			= ".location";
	public static final String WARP_WORLD				= ".world";
	public static final String WARP_GROUP				= ".group";
	
	public static final String FILE_TELEPORT			= "teleport";
	
	private MCPlugin plugin;
	private HashMap<String, Warp> warps = new HashMap<>();
	private ConfigurationHandler config;
	
	public TeleportListener(MCPlugin plugin) {
		super();
		this.setPlugin(plugin);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event == null)
			return;
		
		// Right click block
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			// If WALL Sign
			Block block = event.getClickedBlock();
			if (block != null && (block.getType() == Material.ACACIA_WALL_SIGN || block.getType() == Material.BIRCH_WALL_SIGN || block.getType() == Material.DARK_OAK_WALL_SIGN || block.getType() == Material.JUNGLE_WALL_SIGN || block.getType() == Material.OAK_WALL_SIGN || block.getType() == Material.SPRUCE_WALL_SIGN))
			{
				Player player = event.getPlayer();
				String[] lines = readSign(block);
				if (lines != null) {
					parseSign(lines[0], player);
				}
			}

		}
	}
	
	public void loadWarps() {
		// Load config
		config = new ConfigurationHandler(getPlugin(), FILE_TELEPORT);
		config.saveDefaultConfig();
		if (config == null) {
			getPlugin().getLogger().info("Config Teleport is null!");
		}else {
			getPlugin().getLogger().info("Loaded Teleport Config");
		}
		FileConfiguration conf = getConfig().getConfig();
		List<String> tpList = conf.getStringList("tp");
		for (String tpName : tpList) {
			Warp warp = new Warp();
			warp.name = tpName;
			warp.location = conf.getVector(tpName + WARP_LOCATION);
			warp.group = conf.getString(tpName + WARP_GROUP);
			warp.world = conf.getString(tpName + WARP_WORLD);
			warps.put(tpName, warp);
		}
		
	}
	
	public void saveWarps() {
		for (Entry<String,Warp> e : warps.entrySet()) {
			String tpName = "tp."+e.getValue().name;
			FileConfiguration conf = getConfig().getConfig();
			// Check if config file contains TP
			if (conf.contains(tpName)) {
				// Remove current TP from config file
				conf.set(tpName, null);
			}
			// Save TP into config file
			conf.set(tpName+WARP_LOCATION, e.getValue().location);
			conf.set(tpName+WARP_WORLD, e.getValue().world);
			conf.set(tpName+WARP_GROUP, e.getValue().group);
			getConfig().saveConfig();
		}
	}
	
	public void removeWarp(CommandSender sender, String name) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (doesWarpExist(name)) {
				//getConfig().set(name, null);
				warps.remove(name);
				player.sendMessage(ChatColor.RED+"Suppression de "+name+" faite.");
				saveWarps();
			}
		}
	}
	
	private String[] readSign(Block block) {
		Sign sign = (Sign) block.getState();
		String[] lines = sign.getLines();
		if (lines.length != 0)
			return lines;
		return null;
	}
	
	private void parseSign(String line, Player player) {
		String[] sp = line.split(":");
		switch (sp[0]) {
		case "TP":
			if (sp.length >=2)
				actionTP(sp[1],player);
			break;
		case "ULT":
			if (sp.length >=2)
				actionULT(sp[1],player);
			break;

		default:
			break;
		}
	}
	
	private void actionTP(String name, Player player) {
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		getPlugin().getLogger().info("### Action TP ###");
		getPlugin().getLogger().info("# "+player.getName());
		//getLogger().info(block.getLocation().getX()+"/"+block.getLocation().getY()+"/"+block.getLocation().getZ());
		//getLogger().info("Block Type: "+block.getType());
		//getLogger().info("Diamond: "+Material.DIAMOND_BLOCK);

		if (block.getType().equals(Material.DIAMOND_BLOCK)) {
			warpTo(player, name);
		}else {
			player.sendMessage("Vous devez etre sur un block de diamant pour cette commande.");
		}
	}
	
	public void warpTo(CommandSender sender, String target) {
		if (sender instanceof Player) {
			if (doesWarpExist(target)) {

				// Get teleport target
				Warp warp = warps.get(target);
				Vector vector = warp.location;
				String world_str = warp.world;
				//String group = warp.group;
				
				Player player = (Player) sender;

				float pitch = player.getLocation().getPitch();
				float yaw = player.getLocation().getYaw();
				
				World world = Bukkit.getWorld(world_str);
				world.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
				Location target_loc = new Location(world, vector.getX(),vector.getY(),vector.getZ());
				target_loc.setPitch(pitch);
				target_loc.setYaw(yaw);

				Block block = target_loc.getBlock().getRelative(BlockFace.DOWN);
				if (block != null && block.getType() == Material.DIAMOND_BLOCK) {
					player.teleport(target_loc);
					world.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
					getPlugin().getLogger().info("# "+player.getName()+" warped to "+target);
				}else {
					player.sendMessage(ChatColor.RED+"La destination n'est pas un block de diamant");
				}

			}else {
				sender.sendMessage("La destination n'existe pas!");
			}
		}
	}
	
	public void registerWarp(CommandSender sender, String name) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
			//getLogger().info(block.getLocation().getX()+"/"+block.getLocation().getY()+"/"+block.getLocation().getZ());
			//getLogger().info("Block Type: "+block.getType());
			//getLogger().info("Diamond: "+Material.DIAMOND_BLOCK);

			if (block.getType().equals(Material.DIAMOND_BLOCK)) {
				Vector vector = player.getLocation().toVector();
				String world = player.getWorld().getName();
				Warp warp = new Warp();
				warp.name = name;
				warp.location = vector;
				warp.world = world;
				warp.group = "none";
				warps.put(name, warp);
				
				//getConfig().set(name+".location", vector); 		// Saves the location as a vector
				//getConfig().set(name+".world", world);			//Saves the world name
				//sender.sendMessage("Warp["+name+"][World: "+world+" X:"+vector.getX()+" Y:"+vector.getY()+" Z"+vector.getZ()+"]");
				sender.sendMessage("Cible de teleportation sauvegardé: "+name);
				getPlugin().getLogger().info("# REGISTER TP: "+name+"["+vector.getBlockX()+"/"+vector.getBlockY()+"/"+vector.getBlockZ()+"]");
				saveWarps();
			}else {
				sender.sendMessage("Vous devez etre sur un block de diamant pour cette commande.");
			}
		}
	}
	
	private boolean doesWarpExist(String name) {
		return (warps.containsKey(name));
		
		/*
		Set<String> list = getConfig().getKeys(false);
		for (String name2 : list) {
			if (name.equals(name2))
				return true;
		}
		return false;
		*/
	}
	
	private void actionULT(String name, Player player) {
		switch (name) {
		case "STICK":
			player.getInventory().addItem(Custom.createUtlStick());
			player.sendMessage(ChatColor.GOLD+"Added STICK");
			break;
		case "WATER":
			player.getInventory().addItem(Custom.createUtlBottle());
			player.sendMessage(ChatColor.GOLD+"Added WATER");
			break;
		case "G.PAXE":
			player.getInventory().addItem(Custom.createImprovedGoldPickaxe());
			player.sendMessage(ChatColor.GOLD+"Added Gold Pickaxe");
			break;
		case "G.AXE":
			player.getInventory().addItem(Custom.createImprovedGoldAxe());
			player.sendMessage(ChatColor.GOLD+"Added Gold Axe");
			break;
		case "G.SHO":
			player.getInventory().addItem(Custom.createImprovedGoldShovel());
			player.sendMessage(ChatColor.GOLD+"Added Gold Shovel");
			break;
		case "D.PAXE":
			player.getInventory().addItem(Custom.createImprovedDiamondPickaxe());
			player.sendMessage(ChatColor.GOLD+"Added Diamond Pickaxe");
			break;
		case "S.WAND":
			player.getInventory().addItem(Custom.createSnowWand());
			player.sendMessage(ChatColor.GOLD+"Added Sonw Wand");
			break;
		case "F.WAND":
			player.getInventory().addItem(Custom.createFireWand());
			player.sendMessage(ChatColor.GOLD+"Added Fire Wand");
			break;
		case "POP.INV":
			player.getInventory().addItem(Custom.createInventoryPotion());
			player.sendMessage(ChatColor.GOLD+"Added Inventory Potion");
			break;
		case "GRE":
			player.getInventory().addItem(Custom.createGrenade(10));
			player.sendMessage(ChatColor.GOLD+"Added 10 Grenades");
			break;
		default:
			break;
		}
	}

	public MCPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(MCPlugin plugin) {
		this.plugin = plugin;
	}

	public HashMap<String, Warp> getWarps() {
		return warps;
	}

	public void setWarps(HashMap<String, Warp> warps) {
		this.warps = warps;
	}

	public ConfigurationHandler getConfig() {
		return config;
	}

	public void setConfig(ConfigurationHandler config) {
		this.config = config;
	}
}
