package fr.triedge.minecraft.plugin.build;

import org.bukkit.Material;

public class Bloc {

	public int x,y,z;
	public Material material;
	
	public String toStore() {
		return x+"::"+y+"::"+z+"::"+material.getKey().getKey();
	}
}
