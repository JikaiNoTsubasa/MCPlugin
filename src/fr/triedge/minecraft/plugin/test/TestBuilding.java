package fr.triedge.minecraft.plugin.test;

import org.bukkit.Location;

import fr.triedge.minecraft.plugin.build.StructureBuilder;

public class TestBuilding {

	public static void main(String[] args) {
		Location startPos = new Location(null, 10, 64, 20);
		StructureBuilder.buildPyramid(null, null, startPos);
	}
}
