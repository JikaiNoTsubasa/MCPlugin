package fr.triedge.minecraft.plugin.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Material;

import fr.triedge.minecraft.plugin.build.Bloc;
import fr.triedge.minecraft.plugin.build.Schema;

public class Builder {

	public static Schema buildHouse() {
		Schema schema = new Schema();
		for (int x = 0; x < 10; x++) {
			for (int z = 0; z < 10; z++) {
				for (int y = 0; y < 10; y++) {
					Bloc b = new Bloc();
					b.x = x;
					b.y = y;
					b.z = z;
					b.material = Material.DIAMOND_AXE;
					schema.getBlocs().add(b);
				}
			}
		}
		return schema;
	}
	
	public static void main(String[] args) {
		Schema sh = Builder.buildHouse();
		try {
			sh.store(new File("schemas/house.jk"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Schema sh2 = new Schema();
		try {
			sh2.load(new File("schemas/house.jk"));
			System.out.println(sh2.getBlocs().get(10).toStore());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
