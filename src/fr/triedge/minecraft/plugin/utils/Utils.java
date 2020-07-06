package fr.triedge.minecraft.plugin.utils;

import java.util.logging.Level;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class Utils {
	
	public static final String SYSLOG					= "SYSTEM: ";
	public static final String INFO						= "INFO: ";
	
	public static void log(JavaPlugin plugin, String message) {
		plugin.getLogger().log(Level.INFO, INFO+message);
	}
	
	public static void syslog(JavaPlugin plugin, Level level, String message) {
		plugin.getLogger().log(level, SYSLOG+message);
	}
	
	public static void syslog(JavaPlugin plugin, String message) {
		syslog(plugin, Level.INFO, message);
	}
	
	public static void playSound(Player player, Sound sound) {
		player.getWorld().playSound(player.getLocation(), sound, 10, 1);
	}
	
	public static int random(int min, int max) {
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	public static boolean percent(int percent) {
		int rnd = random(0,100);
		return percent <= rnd;
	}
	
	public static void decreaseItemFromInventory(String name, Player player) {
		PlayerInventory inv = player.getInventory();
		for (ItemStack stack : inv.getContents()) {
			if (stack == null)
				continue;
			if (stack.getItemMeta().getDisplayName().equals(name)) {
				int count = stack.getAmount();
				if (count <= 1) {
					inv.remove(stack);
				}else {
					stack.setAmount(count - 1);
				}
				break;
			}
		}
	}
	
	public static void decreaseItemFromInventory(ItemStack stack, Player player) {
		if (stack == null || player == null)
			return;
		PlayerInventory inv = player.getInventory();
		int count = stack.getAmount();
		if (count <= 1) {
			inv.remove(stack);
		}else {
			stack.setAmount(count - 1);
		}
	}
	
	public static long secondsToTicks(int seconds) {
		return (long) seconds * 20L;
	}

	/**
	 * https://www.spigotmc.org/threads/how-to-make-blocks-glow-no-enchantment.336667/
	 * @param plugin
	 * @param p
	 * @param loc
	 * @param lifetime
	 */
	/* NEEDS 1.8 java
	public static void sendGlowingBlock(JavaPlugin plugin, Player p, Location loc, long lifetime){
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
            //Scoreboard nmsScoreBoard = ((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle();
            EntityShulker shulker = new EntityShulker(((CraftWorld) loc.getWorld()).getHandle());
            shulker.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
            shulker.setFlag(6, true); //Glow
            shulker.setFlag(5, true); //Invisibility

            PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(shulker);
            connection.sendPacket(spawnPacket);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(shulker.getId());
                connection.sendPacket(destroyPacket);
            }, lifetime + (long) ((Math.random() + 1) * 100));
        }, (long) ((Math.random() + 1) * 40));
    }
    */
}
