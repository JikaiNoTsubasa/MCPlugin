package fr.triedge.minecraft.plugin.inventory;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Inventory")
public class XMLInventory {

	private ArrayList<XMLItem> items = new ArrayList<>();

	public ArrayList<XMLItem> getItems() {
		return items;
	}

	@XmlElement(name = "ItemStack")
	public void setItems(ArrayList<XMLItem> items) {
		this.items = items;
	}
}
