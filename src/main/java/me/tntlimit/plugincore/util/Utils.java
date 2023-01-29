package me.tntlimit.plugincore.util;

import me.tntlimit.plugincore.base.PluginBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Utils {
	public static final List<String> EMPTY_LIST = Collections.emptyList();
	private static ItemStack FILLER;
	private static ItemStack INVALID;


	/**
	 * This method makes sure a resource exists, if so it will return the file contents,
	 * if not it will create the file and return the default contents from the jar.
	 *
	 * @param path The path to the resource
	 * @return The contents of the resource or null if it doesn't exist in the jar file
	 */
	public static String getOrCreateResource(String path) {
		try { // Try to read the file contents from disk in plugin data folder
			return inputStreamToString(Files.newInputStream(new File(PluginBase.INSTANCE.getDataFolder(), path).toPath()));
		} catch (Exception e) {
			PluginBase.INSTANCE.saveResource(path, false); // Will write default file to disk if it doesn't exist

			try {
				return inputStreamToString(PluginBase.INSTANCE.getResource(path)); // Get internal version from jar or newly written file
			} catch (Exception e1) {
				PluginBase.INSTANCE.getLogger().warning("Failed to load default " + path + "file: " + e1.getMessage());
				return null;
			}
		}
	}

	/**
	 * Read the UTF-8 contents of an input stream
	 * Should only be used for small files
	 *
	 * @param stream The input stream to read
	 * @return The contents of the stream or null if an error occurred
	 */
	public static String inputStreamToString(InputStream stream) {
		try {
			StringBuilder textBuilder = new StringBuilder();
			try (Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
				int c;
				while ((c = reader.read()) != -1) textBuilder.append((char) c);
			}

			return textBuilder.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get an item from the config
	 * Display name and lore are automatically colorized via {@link I18n#colorize(String)}
	 * <br><br>
	 * Example config item section:
	 * <code>
	 * item:<br>
	 *   material: GOLD_BLOCK<br>
	 *   name: '&amp;c&amp;lAdvertising'<br>
	 *   lore:<br>
	 *     - '&amp;7Click to kick the player for advertising'<br>
	 * </code>
	 * <br><br>
	 * Amount is optional and defaults to 1<br>
	 * Data is optional and defaults to 0
	 *
	 * @param key The path to the item in the config
	 * @param def The default item to return if the config is invalid
	 * @return The item or the default item if the config is invalid
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack getItemFromConfig(String key, ItemStack def) {
		FileConfiguration config = PluginBase.INSTANCE.getConfig();
		int amount = config.getInt(key + ".amount", 1);
		int data = config.getInt(key + ".data", 0);
		String material = config.getString(key + ".material");
		String name = config.getString(key + ".name");
		ArrayList<String> lore = (ArrayList<String>) config.getStringList(key + ".lore");

		try {
			ItemStack itemStack = new ItemStack(Material.valueOf(material), amount, (short) data);
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (itemMeta == null) throw new NullPointerException("ItemMeta is null");

			if (name != null) itemMeta.setDisplayName(I18n.colorize(name));

			ArrayList<String> coloredLore = new ArrayList<>();
			for (String s : lore) coloredLore.add(I18n.colorize(s));

			itemMeta.setLore(coloredLore);
			itemStack.setItemMeta(itemMeta);
			return itemStack;
		} catch (Exception e) {
			PluginBase.INSTANCE.getLogger().warning("Invalid item config for " + key + ": " + e.getMessage());
			return def;
		}
	}

	/**
	 * Get an item from the config
	 * Display name and lore are automatically colorized via {@link I18n#colorize(String)}
	 * @see #getItemFromConfig(String, ItemStack)
	 *
	 * @param key The path to the item in the config
	 * @return The item or a dirt block if the config is invalid
	 */
	public static ItemStack getItemFromConfig(String key) {
		if (INVALID == null) {
			INVALID = new ItemStack(Material.DIRT);
			setItemName(INVALID, "&cInvalid item config");
			setItemLore(INVALID, "&7Please check your config.yml");
		}

		return getItemFromConfig(key, INVALID);
	}

	/**
	 * Returns a blank black stained-glass pane with a name and lore of &7
	 * @return The filler item
	 */
	public static ItemStack getDefaultFiller() {
		if (FILLER != null) return FILLER;

		FILLER = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		setItemName(FILLER, "&7");
		setItemLore(FILLER,"&7");

		return FILLER;
	}

	/**
	 * Set the display name of an item
	 * Color codes are automatically translated via {@link ChatColor#translateAlternateColorCodes(char, String)}
	 * @param item The item to set the name of
	 * @param s The name to set
	 */
	public static void setItemName(ItemStack item, String s) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null || s == null) return;
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', s));
		item.setItemMeta(meta);
	}

	/**
	 * Set the lore of an item
	 * Color codes are automatically translated via {@link ChatColor#translateAlternateColorCodes(char, String)}
	 * @param item The item to set the lore of
	 * @param lore The lore to set
	 */
	public static void setItemLore(ItemStack item, String... lore) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return;

		ArrayList<String> coloredLore = new ArrayList<>();
		for (String s : lore)
			if (s != null)
				coloredLore.add(ChatColor.translateAlternateColorCodes('&', s));

		meta.setLore(coloredLore);
		item.setItemMeta(meta);
	}
}
