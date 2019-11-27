package fr.triedge.minecraft.plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import fr.triedge.minecraft.plugin.inventory.CustomInventory;
import fr.triedge.minecraft.plugin.magic.Spell;
import fr.triedge.minecraft.plugin.magic.SpellFireBall;
import fr.triedge.minecraft.plugin.magic.SpellSnowBall;
import fr.triedge.minecraft.plugin.utils.Utils;


/**
 * Plugin features:
 * [x] v1.0 Build teleports [TEST OK]
 * [x] v1.1 New gold/diamond pickaxe with more durability [TEST OK]
 * [x] v1.2 Ultimate stick/bottle [TEST OK]
 * [x] v1.2 Display title at login [TEST OK]
 * [x] v1.2 Snow Wand [TEST OK]
 * [x] v1.2 Get more ore with custom pickaxe [TEST OK]
 * [X] v1.3 Update recipe to allow all LOGs for custom pickaxes
 * [x] v1.3 Creepers drop 1 Emerald [TEST OK]
 * [x] v1.3 Store more items [TO TEST]
 * [x] v1.3 Grenade -> no drop [TO TEST]
 * [x] v1.3 Added popo to prevent inventory drop when death occurs [TO TEST]
 * [x] v1.3 Fire Wand -> no xp [TO TEST]
 * [x] v1.3 Command save inventories / when connect check if inv already loaded / save inv when delogg
 * [x] v1.4 Custom Axe and Shovel GOLD [TO TEST]
 * [ ] v1.4 Bigger jump
 * [x] v1.4 Hidden TP with h_ [TO TEST]
 * [x] v1.4 Spawn mob Pack [TO TEST]
 * [ ] Custom mob drop emerald (BOSS)
 * [ ] Detector in different directions
 * [ ] Custom mob spawn (obscurity)
 * [ ] Popo to teleport from anywhere - custom craft
 * [ ] Popo REZ
 * [ ] Popo speed with scheduler
 * [ ] Spawn emerald chests
 * 
 * [ ] Add glow effects to detector blocks
 * 
 * Bug:
 * [x] v1.2 NullPointer - When break block with no item in hand
 * [x] v1.2 NullPointer - Magic config not loading
 * [x] v1.3 Double Snow Ball - Firing 2 snow balls instead of 1
 * [x] v1.3 Solved a bug about xp given to player when level up Magic
 * [x] v1.3 ULT STICK breaks block but no loot from block
 * [x] v1.3 ULT WATER fill up water in bottle and changes it's name with right click
 * [x] v1.3 Teleport could happen event if destination is not on diamond block
 * 
 * Client:
 * [ ] Create laboratory
 * [ ] Terrasse double arches
 * [ ] Bouton fontaine
 * [x] Appart in mountain
 * [ ] Underwater house
 * 
 * @author steph
 *
 */
public class MCPlugin extends JavaPlugin implements Listener{

	// https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/spigotmc/spigot-api/1.14.4-R0.1-SNAPSHOT/
	
	public static final String ULT_STICK 	= "ULTIMATE STICK";
	public static final String VERSION		= "v1.4";
	public static final String VERSION_SUB	= "Into Darkness";
	public static final String INV_FOLDER	= "inventories/";

	//public FileConfiguration cfgMagic;
	public ConfigurationHandler magic;
	public ConfigurationHandler inventoryConfig;
	public CustomInventory inventoryManager;
	
	private HTTPDashboard dashboard;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender != null) {
			if (command.getName().equalsIgnoreCase("wregister")) {
				if (args.length>=1) {
					String name = args[0];
					registerWarp(sender, name);
					return true;
				}
			}else if (command.getName().equalsIgnoreCase("whelp")) {
				showHelp(sender);
				return true;
			}else if (command.getName().equalsIgnoreCase("wremove")) {
				if (args.length>=1) {
					removeWarp(sender, args[0]);
					return true;
				}else {
					return false;
				}
			}else if (command.getName().equalsIgnoreCase("wlist")) {
				listWarp(sender);
				return true;
			}else if (command.getName().equalsIgnoreCase("detector")) {
				actionDetector(sender);
				return true;
			}else if (command.getName().equalsIgnoreCase("inv")) {
				if (args.length>=1) {
					int id = Integer.parseInt(args[0]);
					actionOpenInventory(sender, id);
				}else {
					actionOpenInventory(sender, 0);
				}
				return true;
			}else if (command.getName().equalsIgnoreCase("saveinv")) {
				if (args.length>=1) {
					int id = Integer.parseInt(args[0]);
					actionSaveInventory(sender, id);
				}else {
					actionSaveInventory(sender, 0);
				}
				return true;
			}else if (command.getName().equalsIgnoreCase("warp")) {
				if (args.length>=1) {
					String name = args[0];
					opWarp(sender, name);
					return true;
				}
				return true;
			}
		}
		return false;
	}

	private void actionSaveInventory(CommandSender sender, int id) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			inventoryManager.storeInventory(player.getName(), id);
			player.sendMessage(ChatColor.GOLD+"Inventaire sauvegardé");
			Utils.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL);
		}
	}

	private void opWarp(CommandSender sender, String name) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (doesWarpExist(name)) {
				if (player.hasPermission("server.op")) {
					warpTo(sender, name);
				}
			}
		}
	}

	@EventHandler
	public void onBlockDamageEvent(BlockDamageEvent event) {
		Player player = event.getPlayer();
		if (player.getInventory().getItemInMainHand() != null && 
				player.getInventory().getItemInMainHand().getType() == Material.STICK &&
				player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(Custom.ULT_STICK)) {
			Block block = event.getBlock();
			if (block != null)
				block.breakNaturally();
			//player.getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType()));
		}
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null)
			return;
		if (item.getItemMeta() == null)
			return;
		String name = item.getItemMeta().getDisplayName();
		if (name.equals(Custom.IMP_GOLD_PICKAXE) ||
				name.equals(Custom.IMP_DIAMOND_PICKAXE)) {
			// Manage durability
			int durability = Integer.valueOf(item.getItemMeta().getLore().get(0));
			durability--;
			if (durability <= 0) {
				player.getInventory().remove(item);
				player.sendMessage(ChatColor.RED+item.getItemMeta().getDisplayName()+" est cassé!");
			}else {
				Custom.decreaseDurability(item);
			}

			// Manage custom drop for custom pickaxe
			Block block = event.getBlock();
			if (block == null)
				return;
			if (block.getType() == Material.DIAMOND_ORE ||
					block.getType() == Material.GOLD_ORE ||
					block.getType() == Material.EMERALD_ORE ||
					block.getType() == Material.IRON_ORE ||
					block.getType() == Material.COAL_ORE) {
				if (!block.getDrops().isEmpty()) {
					ItemStack stack = new ItemStack(block.getDrops().iterator().next().getType(), 4);
					block.getLocation().getWorld().dropItemNaturally(block.getLocation(), stack);

				}
			}
		}


	}

	@EventHandler
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		// Slime appearance
		if (event.getEntityType() == EntityType.SLIME) {
			Slime slime = (Slime) event.getEntity();
			Bukkit.broadcastMessage(ChatColor.ITALIC+""+ChatColor.DARK_PURPLE+"Un Slime est apparu: x:"+slime.getLocation().getX()+" y:"+slime.getLocation().getY()+" z:"+slime.getLocation().getZ());
		}
		
		/*
		if (event.getEntityType() == EntityType.ZOMBIE) {
			if (Utils.percent(20)) {
				for (int i = 0; i < 4; ++i) {
					event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.ZOMBIE);
				}
				Bukkit.broadcastMessage(ChatColor.ITALIC+""+ChatColor.RED+"Un groupe de zombie est apparu!");
			}
		}
		*/
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event == null)
			return;

		//getLogger().info("Entity damaged");

		// skills damage
		if (event.getDamager() instanceof Snowball) {
			Snowball snow = (Snowball)event.getDamager();
			List<MetadataValue> list_val = snow.getMetadata("player");
			if (list_val != null && !list_val.isEmpty()) {
				String playerName = list_val.get(0).asString();
				Player player = getPlayer(playerName);
				if (magic.getConfig() == null) {
					getLogger().info("/!\\ cfgMagic config is NULL");
					return;
				}
				getLogger().info("Snow skill touched, sent by player: "+player.getName());
				setDamageForSpell(player, event, new SpellSnowBall(player, magic.getConfig(), this));
			}
		}else if (event.getDamager() instanceof SmallFireball) {
			SmallFireball fire = (SmallFireball)event.getDamager();
			List<MetadataValue> list_val = fire.getMetadata("player");
			if (list_val != null && !list_val.isEmpty()) {
				String playerName = list_val.get(0).asString();
				Player player = getPlayer(playerName);
				if (magic.getConfig() == null) {
					getLogger().info("/!\\ cfgMagic config is NULL");
					return;
				}
				getLogger().info("Fire skill touched, sent by player: "+player.getName());
				setDamageForSpell(player, event, new SpellFireBall(player, magic.getConfig(), this));
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event == null)
			return;
		// Custom loot on creepers
		if (event.getEntityType() == EntityType.CREEPER) {
			event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.EMERALD));
		}

		// Check player dead
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player)event.getEntity();
			ItemStack inventoryPotion = checkInventoryPotion(player);
			if (inventoryPotion != null) {
				player.sendMessage(ChatColor.GOLD+"Votre potion d'inventaire fait effet!");
				Utils.decreaseItemFromInventory(Custom.INVENTORY_POTION, player);
				event.getDrops().clear();
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event == null)
			return;

		// Executes for main hand
		if (event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND) && event.getPlayer().getInventory().getItemInMainHand() != null) {
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
			onPlayerUseCustomItem(event, event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName());
		}

	}

	private void onPlayerUseCustomItem(PlayerInteractEvent event, String name) {
		// Grenade
		if (name.equals(Custom.GRENADE) && event.getAction() == Action.RIGHT_CLICK_AIR) {
			launchGrenade(event.getPlayer(), 2.0F, Utils.secondsToTicks(3)); // 3s
			event.setCancelled(true);
		// Nuke
		}else if (name.equals(Custom.NUKE) && event.getAction() == Action.RIGHT_CLICK_AIR) {
			launchGrenade(event.getPlayer(), 10.0F, Utils.secondsToTicks(5)); // 5s
			event.setCancelled(true);
		// SnowBall
		}else if (name.equals(Custom.WAND_SNOW) && event.getAction() == Action.LEFT_CLICK_AIR) {
			Spell spell = new SpellSnowBall(event.getPlayer(), magic.getConfig(), this);
			spell.launch();
		// FireBall
		}else if (name.equals(Custom.WAND_FIRE) && event.getAction() == Action.LEFT_CLICK_AIR) {
			Spell spell = new SpellFireBall(event.getPlayer(), magic.getConfig(), this);
			spell.launch();
		// Ultimate Water
		}else if (name.equals(Custom.ULT_WATER) && event.getAction() == Action.LEFT_CLICK_BLOCK) {
			event.getClickedBlock().setType(Material.WATER);
			event.getPlayer().getWorld().spawnParticle(Particle.WATER_BUBBLE, event.getClickedBlock().getLocation(), 10);
		// Ultimate Water Cancel
		}else if (name.equals(Custom.ULT_WATER) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			event.setCancelled(true);
		}
	
	}

	private ItemStack checkInventoryPotion(Player player) {
		ItemStack[] items = player.getInventory().getContents();
		for (int i = 0; i < items.length; ++i) {
			ItemStack item = items[i];
			if (item == null)
				continue;
			if (item.getItemMeta().getDisplayName()!=null && item.getItemMeta().getDisplayName().equals(Custom.INVENTORY_POTION)) {
				return item;
			}
		}
		return null;
	}

	private void setDamageForSpell(Player player, EntityDamageByEntityEvent event, Spell spell) {
		if (player != null) {
			if (player.getInventory().getItemInMainHand().getType() == Material.STICK) {
				event.setDamage(spell.getDamage());
				int xp = spell.addXP();
				player.sendMessage("XP "+spell.getDisplayName()+": "+xp);
				getLogger().info(player.getName()+" deals "+spell.getDamage()+" damages to "+event.getEntity().getName());
			}
		}
	}

	private void actionOpenInventory(CommandSender sender, int id) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Inventory inv = inventoryManager.getInventory(player, id);
			if (inv == null) {
				player.sendMessage(ChatColor.RED+"Aucun inventaire disponible pour l'id: "+id);
				// Create Custom Inventory if not exist
				inventoryManager.createEmptyInv(player, id);
				player.sendMessage(ChatColor.RED+"Inventaire créé pour l'id: "+id);
				Utils.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL);
				inv = inventoryManager.getInventory(player, id);
			}
			player.openInventory(inv);
			Utils.playSound(player, Sound.BLOCK_CHEST_OPEN);
		}
	}

	private void actionDetector(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			HashMap<Material, Integer> resources = new HashMap<>();
			int maxDist = 50;
			Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
			Location loc = block.getLocation();
			player.sendMessage(ChatColor.GOLD+"Recherche avec le detecteur:");
			for (int i = 0; i <= maxDist; ++i) {
				Material type = loc.getBlock().getType();
				if (
						type == Material.DIAMOND_ORE ||
						type == Material.IRON_ORE ||
						type == Material.GOLD_ORE ||
						type == Material.COAL_ORE ||
						type == Material.EMERALD_ORE ||
						type == Material.LAPIS_ORE) {
					if (resources.containsKey(type)) {
						Integer val = resources.get(type);
						resources.put(type, val+1);
					}else {
						resources.put(type, 1);
					}
				}
				loc.setY(loc.getY()-1);
			}// end loop
			if (resources.isEmpty())
				player.sendMessage(ChatColor.DARK_PURPLE+"Rien trouvé");
			else {
				for (Entry<Material, Integer> e : resources.entrySet()) {
					player.sendMessage(ChatColor.GREEN+e.getKey().toString()+" -> "+e.getValue());
				}
			}
		}
	}

	private void listWarp(CommandSender sender) {
		Set<String> list = getConfig().getKeys(false);
		if (list.isEmpty()) {
			sender.sendMessage("List vide!");
			return;
		}
		StringBuilder tmp = new StringBuilder();
		for (String name : list) {
			if (!name.startsWith("h_")) {
				tmp.append(name);
				tmp.append(", ");
			}
		}
		sender.sendMessage(tmp.toString());
	}

	private boolean doesWarpExist(String name) {
		Set<String> list = getConfig().getKeys(false);
		for (String name2 : list) {
			if (name.equals(name2))
				return true;
		}
		return false;
	}

	private void removeWarp(CommandSender sender, String name) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (doesWarpExist(name)) {
				getConfig().set(name, null);
				player.sendMessage(ChatColor.RED+"Suppression de "+name+" faite.");
				saveConfig();
			}
		}
	}

	private void launchGrenade(Player player, float power, long ticks ) {
		// must be player's inventory item
		ItemStack stack = player.getInventory().getItemInMainHand();
		final Item item = player.getWorld().dropItem(player.getEyeLocation(), stack);
		item.setVelocity(player.getEyeLocation().getDirection());
		Utils.decreaseItemFromInventory(item.getItemStack().getItemMeta().getDisplayName(), player);
		//player.getInventory().removeItem(item.getItemStack());
		final float pow = power;
		// After 3sec
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				item.getWorld().playEffect(item.getLocation(), Effect.SMOKE, 5);
				item.getWorld().createExplosion(item.getLocation(), pow);
				item.remove();
				item.playEffect(EntityEffect.WOLF_SMOKE);
			}
		}, ticks);
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
		case "D.AXE":
			player.getInventory().addItem(Custom.createImprovedDiamondPickaxe());
			player.sendMessage(ChatColor.GOLD+"Added Diamond Axe");
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

	private void actionTP(String name, Player player) {
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		getLogger().info("### Action TP ###");
		getLogger().info("# "+player.getName());
		//getLogger().info(block.getLocation().getX()+"/"+block.getLocation().getY()+"/"+block.getLocation().getZ());
		//getLogger().info("Block Type: "+block.getType());
		//getLogger().info("Diamond: "+Material.DIAMOND_BLOCK);

		if (block.getType().equals(Material.DIAMOND_BLOCK)) {
			warpTo(player, name);
		}else {
			player.sendMessage("Vous devez etre sur un block de diamant pour cette commande.");
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		p.sendMessage(ChatColor.GREEN + "MCPlugin[1.14] "+VERSION);
		p.sendMessage(ChatColor.GREEN + "Les info sur triedge.fr/minecraft");
		Custom.displayTitle(p, ChatColor.AQUA+"Bienvenue "+p.getName(), VERSION_SUB);

		// Create Custom Inventory if not exist
		inventoryManager.createEmptyInv(p, 0);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		inventoryManager.storeInventory(p.getName(), 0);
	}

	public String[] readSign(Block block) {
		Sign sign = (Sign) block.getState();
		String[] lines = sign.getLines();
		if (lines.length != 0) 
			return lines;
		return null;
	}

	private void warpTo(CommandSender sender, String target) {
		if (sender instanceof Player) {
			if (doesWarpExist(target)) {
				Player player = (Player) sender;
				
				
				float pitch = player.getLocation().getPitch();
				float yaw = player.getLocation().getYaw();
				Vector vector = getConfig().getVector(target+".location");
				String world_str = getConfig().getString(target+".world");
				World world = Bukkit.getWorld(world_str);
				world.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
				Location target_loc = new Location(world, vector.getX(),vector.getY(),vector.getZ());
				target_loc.setPitch(pitch);
				target_loc.setYaw(yaw);
				
				Block block = target_loc.getBlock().getRelative(BlockFace.DOWN);
				if (block != null && block.getType() == Material.DIAMOND_BLOCK) {
					player.teleport(target_loc);
					world.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
					getLogger().info("# "+player.getName()+" warped to "+target);
				}else {
					player.sendMessage(ChatColor.RED+"La destination n'est pas un block de diamant");
				}
				
			}else {
				sender.sendMessage("La destination n'existe pas!");
			}
		}
	}

	private void showHelp(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage(ChatColor.GOLD+"/wregister <NomDuTp> Permet d'enregistrer un teleporteur");
			player.sendMessage(ChatColor.GOLD+"/wremove <NomDuTp> Permet de supprimer un teleporteur");
			player.sendMessage(ChatColor.GOLD+"/wlist Permet de lister tous les teleporteurs");
		}
	}

	private void registerWarp(CommandSender sender, String name) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
			//getLogger().info(block.getLocation().getX()+"/"+block.getLocation().getY()+"/"+block.getLocation().getZ());
			//getLogger().info("Block Type: "+block.getType());
			//getLogger().info("Diamond: "+Material.DIAMOND_BLOCK);

			if (block.getType().equals(Material.DIAMOND_BLOCK)) {
				Vector vector = player.getLocation().toVector();
				String world = player.getWorld().getName();
				getConfig().set(name+".location", vector); 		// Saves the location as a vector
				getConfig().set(name+".world", world);			//Saves the world name
				//sender.sendMessage("Warp["+name+"][World: "+world+" X:"+vector.getX()+" Y:"+vector.getY()+" Z"+vector.getZ()+"]");
				sender.sendMessage("Cible de teleportation sauvegardé: "+name);
				getLogger().info("# REGISTER TP: "+name+"["+vector.getBlockX()+"/"+vector.getBlockY()+"/"+vector.getBlockZ()+"]");
				saveConfig();
			}else {
				sender.sendMessage("Vous devez etre sur un block de diamant pour cette commande.");
			}
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
		getServer().getScheduler().cancelTasks(this);
		//getDashboard().setRunning(false);
	}

	@Override
	public void onEnable() {
		super.onEnable();

		// Create Inventory Manager
		inventoryConfig = new ConfigurationHandler(this, "inventory");
		inventoryConfig.saveDefaultConfig();
		if (inventoryConfig.getConfig() == null) {
			getLogger().log(Level.SEVERE,"Config Magic is null!");
		}else {
			getLogger().info("Loaded Custom Inventory Config");
		}
		inventoryManager = new CustomInventory(inventoryConfig, this);
		inventoryManager.loadAllInventories();
		
		// Register this plugin
		getServer().getPluginManager().registerEvents(this, this);

		// Load config
		magic = new ConfigurationHandler(this, "magic");
		magic.saveDefaultConfig();
		//this.loadConfig(cfgMagic, Magic.FILE_MAGIC);
		if (magic.getConfig() == null) {
			getLogger().info("Config Magic is null!");
		}else {
			getLogger().info("Loaded Custom Config");
		}

		// Start scheduled task
		BukkitScheduler scheduler = getServer().getScheduler();
		int res = scheduler.scheduleSyncRepeatingTask(this, new ConfigSaverTask(this), 0L, 6000L);
		if (res == -1)
			getLogger().log(Level.SEVERE, "Cannot schedule ConfigSaverTask");
		int res2 = scheduler.scheduleSyncRepeatingTask(this, new InventorySaverTask(this), 0L, 6000L);
		if (res2 == -1)
			getLogger().log(Level.SEVERE, "Cannot schedule InventorySaverTask");
		int res3 = scheduler.scheduleSyncRepeatingTask(this, new MetricSaverTask(this), 0L, 3000L);
		if (res3 == -1)
			getLogger().log(Level.SEVERE, "Cannot schedule MetricSaverTask");
		
		// Create custom recipes
		getServer().addRecipe(Custom.createImprovedGoldPickaxeRecipe(this, Material.ACACIA_LOG));
		getServer().addRecipe(Custom.createImprovedGoldPickaxeRecipe(this, Material.BIRCH_LOG));
		getServer().addRecipe(Custom.createImprovedGoldPickaxeRecipe(this, Material.DARK_OAK_LOG));
		getServer().addRecipe(Custom.createImprovedGoldPickaxeRecipe(this, Material.JUNGLE_LOG));
		getServer().addRecipe(Custom.createImprovedGoldPickaxeRecipe(this, Material.OAK_LOG));
		getServer().addRecipe(Custom.createImprovedGoldPickaxeRecipe(this, Material.SPRUCE_LOG));
		
		getServer().addRecipe(Custom.createImprovedGoldAxeRecipe(this, Material.ACACIA_LOG));
		getServer().addRecipe(Custom.createImprovedGoldAxeRecipe(this, Material.BIRCH_LOG));
		getServer().addRecipe(Custom.createImprovedGoldAxeRecipe(this, Material.DARK_OAK_LOG));
		getServer().addRecipe(Custom.createImprovedGoldAxeRecipe(this, Material.JUNGLE_LOG));
		getServer().addRecipe(Custom.createImprovedGoldAxeRecipe(this, Material.OAK_LOG));
		getServer().addRecipe(Custom.createImprovedGoldAxeRecipe(this, Material.SPRUCE_LOG));
		
		getServer().addRecipe(Custom.createImprovedGoldShovelRecipe(this, Material.ACACIA_LOG));
		getServer().addRecipe(Custom.createImprovedGoldShovelRecipe(this, Material.BIRCH_LOG));
		getServer().addRecipe(Custom.createImprovedGoldShovelRecipe(this, Material.DARK_OAK_LOG));
		getServer().addRecipe(Custom.createImprovedGoldShovelRecipe(this, Material.JUNGLE_LOG));
		getServer().addRecipe(Custom.createImprovedGoldShovelRecipe(this, Material.OAK_LOG));
		getServer().addRecipe(Custom.createImprovedGoldShovelRecipe(this, Material.SPRUCE_LOG));

		getServer().addRecipe(Custom.createImprovedDiamondPickaxeRecipe(this, Material.ACACIA_LOG));
		getServer().addRecipe(Custom.createImprovedDiamondPickaxeRecipe(this, Material.BIRCH_LOG));
		getServer().addRecipe(Custom.createImprovedDiamondPickaxeRecipe(this, Material.DARK_OAK_LOG));
		getServer().addRecipe(Custom.createImprovedDiamondPickaxeRecipe(this, Material.JUNGLE_LOG));
		getServer().addRecipe(Custom.createImprovedDiamondPickaxeRecipe(this, Material.OAK_LOG));
		getServer().addRecipe(Custom.createImprovedDiamondPickaxeRecipe(this, Material.SPRUCE_LOG));

		getServer().addRecipe(Custom.createUltimateBottleRecipe(this));
		getServer().addRecipe(Custom.createSnowWandRecipe(this));
		getServer().addRecipe(Custom.createFireWandRecipe(this));
		getServer().addRecipe(Custom.createGrenadeRecipe(this));
		getServer().addRecipe(Custom.createNukeRecipe(this));
		getServer().addRecipe(Custom.createInventoryPotionRecipe(this));
		
		// Start Dashboard web server
		/*
		setDashboard(new HTTPDashboard(8888));
		Thread th = new Thread(getDashboard(), "MCDashboard");
		getLogger().info("Started dashboard on port: 8888");
		th.start();
		*/

		// Update Max stack
		/*
		for (int i = 1; i<=256; i++) {
			try {
				String itemName = net.minecraft.server.v1_13_R2.Item.getById(i).getName();
				boolean isChanged = Custom.setMaxStackSize(i, 256);
				if (isChanged)
					Utils.syslog(this, "Item "+itemName+"["+i+"] increased stack to: 256");
				else
					Utils.syslog(this, "Cannot increase stack to 256 for: "+itemName);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				getLogger().log(Level.SEVERE, "Cannot update max size for object id: "+i, e);
			}
		}
		 */

		/*
		try {
			getLogger().info("=============== ID: "+Material.COBBLESTONE.getId());
			//getLogger().info("=============== Item: "+Item.getById(4).getName());
			Custom.setMaxStackSize(Material.COBBLESTONE, 1);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		 */
	}

	/*
	private void loadConfig(FileConfiguration conf, String path) {
		File fmmo = new File(getDataFolder(),path);
		getLogger().info("Data folder: "+getDataFolder().getAbsolutePath());
		if (!fmmo.exists()) {
			FileWriter w;
			try {
				w = new FileWriter(fmmo);
				w.write("# Creation\r\n");
				w.flush();
				w.close();
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Cannot write into "+fmmo.getAbsolutePath(), e);
			}

		}
		conf = YamlConfiguration.loadConfiguration(fmmo);

	}
	 */

	@Override
	public void onLoad() {
		super.onLoad();



		reloadConfig();
	}

	public void saveConfig(FileConfiguration conf, String name) {
		File file = new File(getDataFolder(), name);
		try {
			conf.save(file);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Cannot write into "+file.getAbsolutePath(), e);
		}
	}

	public boolean configExists(FileConfiguration conf, String name) {
		return conf.get(name)==null?false:true;
	}

	public Player getPlayer(String name) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equals(name))
				return p;
		}
		return null;
	}

	public double round(double val) {
		return (double)Math.round(val*100)/100;
	}

	public HTTPDashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(HTTPDashboard dashboard) {
		this.dashboard = dashboard;
	}
	
	public static void main(String[] args) {
		
	}
}
