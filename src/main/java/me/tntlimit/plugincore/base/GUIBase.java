package me.tntlimit.plugincore.base;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


/**
 * The base class for GUIs.
 * This class handles the inventory creation and event registration.
 * Click/drag events are cancelled by default.
 */
public abstract class GUIBase implements Listener {
	private final int size;
	private final Player viewer;
	protected final Inventory inventory;


	/**
	 * Creates a new GUI
	 *
	 * @param size The size of the inventory in slots <b>(Must be a multiple of 9)</b>
	 * @param viewer The player who will be viewing the GUI
	 * @param title The title of the GUI container (Color codes are supported)
	 */
	public GUIBase(int size, Player viewer, String title) {
		this.size = size;
		this.viewer = viewer;
		this.inventory = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', title));

		PluginBase.INSTANCE.getServer().getPluginManager().registerEvents(this, PluginBase.INSTANCE);
	}

	/**
	 * Displays the GUI to the viewer on the next tick
	 * This is to fix some bugs with the inventory not opening due to other plugins
	 * You may call this method, or call {@link #display()} directly
	 */
	public void displayAsync() {
		Bukkit.getScheduler().runTaskLater(PluginBase.INSTANCE, this::display, 1);
	}

	/**
	 * Called when the GUI is displayed to the viewer
	 * This method should be used to set the contents of the inventory
	 */
	public abstract void display();

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!e.getInventory().equals(inventory)) return;
		e.setCancelled(true);

		ItemStack clickedItem = e.getCurrentItem();
		if (clickedItem == null || clickedItem.getType().isAir()) return;

		this.onInventoryClick(e, (Player) e.getWhoClicked(), e.getRawSlot());
	}

	/**
	 * Called when the viewer clicks on a valid item in the GUI.
	 * <br><br>
	 * Safe to assume that the correct inventory is being clicked on, and that
	 * the item clicked is not null.
	 * <br><br>
	 * The event is cancelled by default, but you can call {@code setCancelled(false)} to override this.
	 *
	 * @param event The event that was fired
	 * @param player The player who clicked the item
	 * @param slot The slot/index the item was in
	 */
	public abstract void onInventoryClick(InventoryClickEvent event, Player player, int slot);

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if (e.getInventory().equals(inventory))
			e.setCancelled(true);
	}

	/**
	 * Get the size of the inventory
	 * @return The size of the inventory (number of slots, <b>not</b> number of rows)
	 */
	public int getSize() {
		return size;
	}

	public Player getViewer() {
		return viewer;
	}

	public Inventory getInventory() {
		return inventory;
	}
}
