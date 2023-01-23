package me.tntlimit.plugincore.base;

import me.tntlimit.plugincore.util.I18n;
import me.tntlimit.plugincore.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;


/**
 * <b>UNDER CONSTRUCTION</b><br>
 * Base class for custom items in the plugin.
 * <p>
 *     This class provides a few methods that are called when the item is used.
 *     The {@link #onUse(PlayerInteractEvent)} method is called when the item is right-clicked.
 *     The {@link #onRightClickPlayer(PlayerInteractAtEntityEvent)} method is called when the item is right-clicked on a player.
 * </p>
 */
@Deprecated
public abstract class ItemBase {
	private final ItemStack item;
	private final int slot;
	private final String permission;
	private boolean enabled;


	public ItemBase(String key, int defaultSlot) {
		this.item = Utils.getItemFromConfig(key + ".item");

		int slot = PluginBase.config().getInt(key + "item.slot", defaultSlot);
		if (slot < 0 || slot > 8) {
			PluginBase.INSTANCE.getLogger().warning("Invalid slot for " + key + ": " + slot);
			slot = defaultSlot;
		}

		this.slot = slot;
		this.permission = /*"staffmode." + */key; //todo
		this.enabled = PluginBase.config().getBoolean(key + ".enabled", true);
	}

	public void onRightClick(PlayerInteractEvent event) {
		if (event.getItem() == null || !isItemEqual(event.getItem())) return;
		event.setCancelled(true);

		if (isValid(event.getPlayer(), event.getItem())) this.onUse(event);
	}

	public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND || !(event.getRightClicked() instanceof Player)) return;

		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (!this.isValid(event.getPlayer(), item)) return;
		event.setCancelled(true);

		this.onRightClickPlayer(event);
	}

	protected boolean isValid(Player player, ItemStack item) {
		if (item == null || !isItemEqual(item)) return false;
		if (!this.isEnabled()) {
			player.sendMessage(I18n.format("item.disabled"));
			return false;
		}

		if (!player.hasPermission(this.permission)) {
			player.sendMessage(I18n.format("item.no-permission"));
			return false;
		}

		return true;
	}

	public boolean isItemEqual(ItemStack item) {
		if (item == null || item.getItemMeta() == null) return false;
		if (item.getAmount() != this.item.getAmount()) return false;
		if (item.getMaxStackSize() != this.item.getMaxStackSize()) return false;
		return item.isSimilar(this.item);
	}

	public abstract void onUse(PlayerInteractEvent event);

	public void onRightClickPlayer(PlayerInteractAtEntityEvent event) { }

	public ItemStack getItem() {
		return this.item.clone();
	}

	public int getSlot() {
		return this.slot;
	}

	public String getPermission() {
		return this.permission;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	protected void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
