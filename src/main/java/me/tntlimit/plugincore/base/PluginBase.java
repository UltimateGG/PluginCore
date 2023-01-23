package me.tntlimit.plugincore.base;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * @author TNTLimit
 * <br><br>
 *
 * The base class your plugin should extend and use as an entry point.
 * Nothing special here, just adds a static instance and opens up {@link #config()}.
 * <br><br>
 * Your base plugin should be:<br>
 * Main class extending this class<br>
 * Resources:<br>
 * plugin.yml<br>
 * <b>messages.properties</b> - must at least have the "no-permission" key.
 *
 * @see CommandBase
 * @see GUIBase
 * @see ItemBase
 */
public abstract class PluginBase extends JavaPlugin {
	public static PluginBase INSTANCE;


	public PluginBase() {
		INSTANCE = this;
	}

	/**
	 * Called when the plugin is enabled.
	 * This is where you should register commands, listeners, etc.
	 * You're usually going to want to call {@link #saveDefaultConfig()} here.
	 */
	public abstract void onEnable();

	public abstract void onDisable();

	public static FileConfiguration config() {
		return INSTANCE.getConfig();
	}
}
