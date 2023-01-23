package me.tntlimit.plugincore;

import org.bukkit.plugin.java.JavaPlugin;


public class PluginBase extends JavaPlugin {
	private static PluginBase INSTANCE;


	public PluginBase() {
		INSTANCE = this;
		System.out.println("PluginBase constructor CALLED");
	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}

	public static PluginBase getInstance() {
		return INSTANCE;
	}
}
