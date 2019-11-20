package fr.triedge.minecraft.plugin.inventory;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class XMLItem {

	private String material,metaName;
	private int count, position;
	private List<String> lore;
	private short durability;
	private HashMap<String, Integer> enchantments;

	public String getMaterial() {
		return material;
	}

	@XmlElement(name = "Material")
	public void setMaterial(String material) {
		this.material = material;
	}

	public int getCount() {
		return count;
	}

	@XmlAttribute(name = "Count")
	public void setCount(int count) {
		this.count = count;
	}

	public List<String> getLore() {
		return lore;
	}

	@XmlElement(name = "Lore")
	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public String getMetaName() {
		return metaName;
	}

	@XmlElement(name = "MetaName")
	public void setMetaName(String metaName) {
		this.metaName = metaName;
	}

	public int getPosition() {
		return position;
	}

	@XmlAttribute(name = "Position")
	public void setPosition(int position) {
		this.position = position;
	}

	public short getDurability() {
		return durability;
	}

	@XmlElement(name = "Durability")
	public void setDurability(short durability) {
		this.durability = durability;
	}

	public HashMap<String, Integer> getEnchantments() {
		return enchantments;
	}

	@XmlElement(name = "Enchantments")
	public void setEnchantments(HashMap<String, Integer> enchantments) {
		this.enchantments = enchantments;
	}
}
