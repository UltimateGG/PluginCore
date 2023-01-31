package me.tntlimit.plugincore.util;

import me.tntlimit.plugincore.base.GUIBase;
import me.tntlimit.plugincore.base.PluginBase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


public abstract class PaginatedGUI<T> extends GUIBase {
	private final ItemStack nextButton;
	private final ItemStack backButton;
	private final ItemStack filler;
	private final T[] items;
	private final int maxPages;
	private int page;


	/**
	 * Creates the GUI with the default next/back buttons and filler
	 */
	public PaginatedGUI(int size, Player player, String title, T[] items) {
		this(size, player, title, items,
				new ItemBuilder(Material.ARROW)
					.setDisplayName("&aNext Page")
					.setLore("&7Click to go to the next page")
					.build(),
				new ItemBuilder(Material.ARROW)
					.setDisplayName("&aPrevious Page")
					.setLore("&7Click to go to the previous page")
					.build()
				, Utils.getDefaultFiller()
		);
	}

	public PaginatedGUI(int size, Player player, String title, T[] items, ItemStack nextButton, ItemStack backButton, ItemStack filler) {
		super(size, player, title);

		this.items = items;
		this.nextButton = nextButton;
		this.backButton = backButton;
		this.filler = filler;
		this.maxPages = (int) Math.max(1, Math.ceil(items.length / (size - 9.0)));
		this.page = 0;
	}

	@Override
	public void displayAsync() {
		Bukkit.getScheduler().runTaskLater(PluginBase.INSTANCE, () -> displayPage(0), 1);
	}

	/**
	 * Displays page 0
	 */
	@Override
	public void display() {
		displayPage(0);
	}

	/**
	 * Displays the specified page
	 * The GUI will be cleared before displaying the page
	 * This method will populate the bottom row with the next/back buttons and filler
	 * and then display to the viewer
	 *
	 * @param page The page to display (0-indexed)
	 */
	public void displayPage(int page) {
		this.page = page;
		this.inventory.clear();

		for (int i = 0; i < this.getSize(); i++) {
			if (i == this.getSize() - 9 && page != 0) {
				inventory.setItem(i, backButton);
				continue;
			} else if (i == this.getSize() - 1 && page != maxPages - 1) {
				inventory.setItem(i, nextButton);
				break;
			} else if (i >= this.getSize() - 9) {
				inventory.setItem(i, filler);
				continue;
			}

			int index = page * (this.getSize() - 9) + i;
			if (index >= items.length) continue;

			inventory.setItem(i, this.getItem(items[index]));
		}

		this.getViewer().openInventory(inventory);
	}

	/**
	 * Called to get the item to display for the specified item
	 * This method is called for every item in the GUI
	 *
	 * @param item The corresponding item in the array passed to the constructor
	 * @return The item to display in the GUI or null to not display anything
	 */
	public abstract ItemStack getItem(T item);

	@Override
	public void onInventoryClick(InventoryClickEvent e, Player p, int slot) {
		if (slot == this.getSize() - 9 && page != 0) {
			displayPage(page - 1);
		} else if (slot == this.getSize() - 1 && page != maxPages - 1) {
			displayPage(page + 1);
		} else if (slot < this.getSize() - 9) {
			int index = page * (this.getSize() - 9) + slot;
			if (index >= items.length) return;

			onInventoryClick(e, p, slot, items[index]);
		}
	}

	/**
	 * Called when an item in the GUI is clicked
	 *
	 * @param e The InventoryClickEvent which was already cancelled
	 * @param p The player who clicked the item
	 * @param slot The slot the item was clicked in (Relative to this page)
	 * @param item The corresponding item in the array passed to the constructor
	 */
	public abstract void onInventoryClick(InventoryClickEvent e, Player p, int slot, T item);
}
