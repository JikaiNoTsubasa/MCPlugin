package fr.triedge.minecraft.plugin.inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
@Deprecated()
public class InventoryLoader {
	
	public static final String SEPARATOR_ELEMENT				= "#E#";
	public static final String SEPARATOR_LORE					= "#L#";

	public static ArrayList<ItemStack> readInventory(File file) throws FileNotFoundException, JAXBException {
		ArrayList<ItemStack> stacks = new ArrayList<>();
		
		JAXBContext context = JAXBContext.newInstance(XMLInventory.class);
		Unmarshaller um = context.createUnmarshaller();
		XMLInventory inv = (XMLInventory) um.unmarshal(file);
		for (XMLItem item : inv.getItems()) {
			
			Material mat = Material.getMaterial(item.getMaterial().toUpperCase());
			int count = item.getCount();
			String name = item.getMetaName();
			List<String> lore = item.getLore();
			ItemStack stack = new ItemStack(mat, count);
			if (!item.getEnchantments().isEmpty()) {
				for (Entry<String, Integer> e : item.getEnchantments().entrySet()) {
					e.getKey();
					//Enchantment ech = Enchantment.getByKey(new NamespacedKey(null, e.getKey()));
					//Integer level = e.getValue();
					//stack.addEnchantment(ech, level);
				}
			}
			
			stack.getItemMeta().setDisplayName(name);
			stack.getItemMeta().setLore(lore);
			stacks.add(stack);
		}
		
		/*
		Scanner scan = new Scanner(file);
		ArrayList<ItemStack> stacks = new ArrayList<>();
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if (line == null || line.equals("") || line.startsWith("#"))
				continue;
			String[] sline = line.split(SEPARATOR_ELEMENT);
			
			Material mat = Material.getMaterial(sline[0].toUpperCase());
			int count = Integer.parseInt(sline[1]);
			String name = sline[2];
			String[] lore = sline[3].split(SEPARATOR_LORE);
			List<String> slore = new ArrayList<>();
			for (String s : lore)
				slore.add(s);
			
			ItemStack stack = new ItemStack(mat, count);
			stack.getItemMeta().setDisplayName(name);
			stack.getItemMeta().setLore(slore);
			stacks.add(stack);
		}
		scan.close();
		
		*/
		return stacks;
	}
	
	public static void storeInventory(File file, Inventory inventory) throws IOException, JAXBException {
		ItemStack[] stacks = inventory.getContents();
		XMLInventory xmlInv = new XMLInventory();
		for (ItemStack stack : stacks) {
			if (stack == null)
				continue;
			String mat = stack.getType().getKey().getKey();
			int count = stack.getAmount();
			List<String> lore = stack.getItemMeta().getLore();
			String name = stack.getItemMeta().getDisplayName();
			XMLItem item = new XMLItem();
			if (!stack.getEnchantments().isEmpty()) {
				item.setEnchantments(new HashMap<String,Integer>());
				for (Entry<Enchantment, Integer> e : stack.getEnchantments().entrySet()) {
					int level = e.getValue();
					String enchant = e.getKey().getKey().getKey();
					item.getEnchantments().put(enchant, level);
				}
				
			}
			
			item.setMaterial(mat);
			item.setMetaName(name);
			item.setCount(count);
			item.setLore(lore);
			
			xmlInv.getItems().add(item);
		}
		
		JAXBContext context = JAXBContext.newInstance(XMLInventory.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(xmlInv, file);
		/*
		FileWriter w = new FileWriter(file);
		for (ItemStack stack : stacks) {
			if (stack == null)
				continue;
			String mat = stack.getType().getKey().getKey();
			int count = stack.getAmount();
			List<String> lore = stack.getItemMeta().getLore();
			String name = stack.getItemMeta().getDisplayName();
			
			StringBuilder tmp = new StringBuilder();
			tmp
			.append(mat).append(SEPARATOR_ELEMENT)
			.append(count).append(SEPARATOR_ELEMENT)
			.append(name).append(SEPARATOR_ELEMENT);
			if (lore != null) {
				for (String line : lore) {
					tmp.append(line).append(SEPARATOR_LORE);
				}
			}
			w.write(tmp.toString()+"\r\n");
			w.flush();
		}
		w.close();
		*/
	}
}
