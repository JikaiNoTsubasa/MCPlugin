package fr.triedge.minecraft.plugin.build;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import fr.triedge.minecraft.plugin.MCPlugin;
import fr.triedge.minecraft.plugin.utils.Utils;

public class StructureBuilder {

	public static void buildPyramid(MCPlugin plugin, World world, Location startPos) {
		ArrayList<String[]> pyr = new ArrayList<>();
		String[] layer0 = {
				"xxxxxxxxx",
				"xooooooox",
				"xooooooox",
				"xooooooox",
				"xooooooox",
				"xooooooox",
				"xooooooox",
				"xooooooox",
				"xxxxxxxxx"
		};
		String[] layer1 = {
				"ooooooooo",
				"oxxxxxxxo",
				"oxoooooxo",
				"oxoooooxo",
				"oxoooooxo",
				"oxoooooxo",
				"oxoooooxo",
				"oxxxxxxxo",
				"ooooooooo"
		};
		String[] layer2 = {
				"ooooooooo",
				"ooooooooo",
				"ooxxxxxoo",
				"ooxoooxoo",
				"ooxoooxoo",
				"ooxoooxoo",
				"ooxxxxxoo",
				"ooooooooo",
				"ooooooooo"
		};
		String[] layer3 = {
				"ooooooooo",
				"ooooooooo",
				"ooooooooo",
				"oooxxxooo",
				"oooxoxooo",
				"oooxxxooo",
				"ooooooooo",
				"ooooooooo",
				"ooooooooo"
		};
		String[] layer4 = {
				"ooooooooo",
				"ooooooooo",
				"ooooooooo",
				"ooooooooo",
				"ooooxoooo",
				"ooooooooo",
				"ooooooooo",
				"ooooooooo",
				"ooooooooo"
		};
		
		pyr.add(0,layer0);
		pyr.add(1,layer1);
		pyr.add(2,layer2);
		pyr.add(3,layer3);
		pyr.add(4,layer4);
		
		// Generate all blocks
		ArrayList<Bloc> allBlocks = new ArrayList<>();
		int y = 0;
		for (String[] layer : pyr) {
			int z = 0;
			for (String s : layer) {
				char[] charList = s.toCharArray();
				int x = 0;
				for (char c : charList) {
					Bloc b = new Bloc();
					b.x = x;
					b.z = z;
					b.y = y;
					if (c == 'o')
						b.material = Material.AIR;
					else if (c == 'x')
						b.material = Material.STONE;
					allBlocks.add(b);
					++x;
				}
				++z;
			}
			++y;
		}
		
		// Apply offset
		for (Bloc b : allBlocks) {
			b.x += startPos.getBlockX();
			b.y += startPos.getBlockY();
			b.z += startPos.getBlockZ();
			
			Location loc = new Location(world, b.x, b.y, b.z);
			loc.getBlock().setType(b.material);
			Utils.syslog(plugin, "Changed: "+b.toStore());
		}
		
	}
	
	public static BlockPlaceEvent PlaceBlock(MCPlugin plugin, Block clickedBlock, Player p, Material newType, byte newData) {
		  BlockState state = clickedBlock.getState();
		  state.setType(Material.DIAMOND_BLOCK);
		  state.getData().setData((byte)0);
		  Block placedBlock = state.getBlock();
		  BlockPlaceEvent event = new BlockPlaceEvent(placedBlock, state, clickedBlock, p.getItemInHand(), p, true);
		  plugin.getServer().getPluginManager().callEvent(event);
		  return event;
	}
	
	public static void placeDoor(MCPlugin plugin, Location loc, Material mat) {
		Block doorBottomBlock = loc.getBlock().getRelative(BlockFace.UP);
        Block doorUpBlock = doorBottomBlock.getRelative(BlockFace.UP);
        BlockData blockData = plugin.getServer().createBlockData(mat);
        Bisected bisected = (Bisected) blockData;

        bisected.setHalf(Bisected.Half.BOTTOM);
        doorBottomBlock.setBlockData(bisected, false);

        bisected.setHalf(Bisected.Half.TOP);
        doorUpBlock.setBlockData(bisected, false);
	}
}
