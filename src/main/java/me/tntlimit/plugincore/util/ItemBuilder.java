package me.tntlimit.plugincore.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;


public class ItemBuilder {
	private final ItemStack item;


	public ItemBuilder(Material material) {
		this(material, 1);
	}

	public ItemBuilder(Material material, int amount) {
		item = new ItemStack(material, amount);
	}

	/**
	 * Set the display name of an item
	 * Color codes are automatically translated via {@link ChatColor#translateAlternateColorCodes(char, String)}
	 *
	 * @param s The name to set
	 */
	public ItemBuilder setDisplayName(String s) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null || s == null) return this;

		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', s));
		item.setItemMeta(meta);

		return this;
	}

	/**
	 * Set the lore of an item
	 * Color codes are automatically translated via {@link ChatColor#translateAlternateColorCodes(char, String)}
	 *
	 * @param lore The lore to set
	 */
	public ItemBuilder setLore(String... lore) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return this;

		ArrayList<String> coloredLore = new ArrayList<>();
		for (String s : lore)
			if (s != null)
				coloredLore.add(ChatColor.translateAlternateColorCodes('&', s));

		meta.setLore(coloredLore);
		item.setItemMeta(meta);

		return this;
	}

	/**
	 * Set name and lore to a blank string
	 */
	public ItemBuilder blank() {
		return setDisplayName(" ").setLore(" ");
	}

	public ItemStack build() {
		return item;
	}
}
