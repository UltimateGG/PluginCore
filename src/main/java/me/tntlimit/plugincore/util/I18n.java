package me.tntlimit.plugincore.util;

import me.tntlimit.plugincore.base.PluginBase;
import org.bukkit.ChatColor;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * A class for internationalization.
 * This class will automatically load the messages.properties file from the plugin data folder.
 * If the file is not found, it will create it from the messages.properties file inside the jar.
 * <br><br>
 * Includes a cache for 0 arg messages. This is to prevent the overhead of creating a new string every time.
 * <br><br>
 * Includes built in variable support. Variables are defined in the messages.properties file with the format:
 * <code>
 *    var.name=value
 * </code>
 * <br><br>
 * To use a variable, use the format:
 * message=This is a message with a variable: {name}
 * Variable names are <b>case sensitive</b>.
 * The variables will be replaced with their values when the message is retrieved through {@link #format}.
 */
public class I18n {
	private static final HashMap<String, String> CACHE = new HashMap<>();
	private static final HashMap<String, String> VARIABLES = new HashMap<>();
	private static final String FILE = "messages";
	private static ResourceBundle BUNDLE;
	static { init(); }


	private static void init() {
		try { // Load the resource bundle from plugin data dir
			URL[] urls = new URL[] { PluginBase.INSTANCE.getDataFolder().toURI().toURL() };
			ClassLoader loader = new java.net.URLClassLoader(urls);

			BUNDLE = ResourceBundle.getBundle(FILE, Locale.ENGLISH, loader);
		} catch (Exception e) { // Fall back and use default messages.properties from inside the jar
			PluginBase.INSTANCE.saveResource(FILE + ".properties", false);
			BUNDLE = ResourceBundle.getBundle(FILE);
		}

		// Load variables
		for (String key : BUNDLE.keySet())
			if (key.startsWith("var."))
				VARIABLES.put(key.substring(4), ChatColor.translateAlternateColorCodes('&', BUNDLE.getString(key)));
	}

	/**
	 * Get the raw message from the messages.properties file.
	 * This will not apply color codes or variables.
	 * <br><br>
	 * This method is not recommended for use in your plugin.
	 * Use {@link #format} instead.
	 * @param key The key to get the message for
	 * @return The message or the passed in key if the message is not found
	 */
	public static String get(String key) {
		try {
			return BUNDLE.getString(key);
		} catch (Exception e) {
			PluginBase.INSTANCE.getLogger().warning("Missing translation for key: " + key);
			return key;
		}
	}

	/**
	 * Apply color codes and variables to a message.
	 * Use this method if you need to apply color codes or variables to a message
	 * that is not in the messages.properties file (For example from the config.yml).
	 *
	 * @param msg The message to apply color codes and variables to
	 * @return The formatted message
	 */
	public static String colorize(String msg) {
		msg = ChatColor.translateAlternateColorCodes('&', msg);

		// Apply variables
		for (String key : VARIABLES.keySet())
			msg = msg.replace("{" + key + "}", VARIABLES.get(key));

		return msg;
	}

	/**
	 * Get a formatted message from the messages.properties file.
	 * This will apply color codes and variables.
	 * This method will cache messages with no arguments.
	 *
	 * @param key The key to get the message for
	 * @param args The arguments to replace in the message (if any)
	 * @return The formatted message or the passed in key if the message is not found
	 */
	public static String format(String key, Object... args) {
		if (args.length == 0 && CACHE.containsKey(key)) return CACHE.get(key);
		String msg = colorize(get(key));

		for (int i = 0; i < args.length; i++) {
			if (args[i] == null) continue;
			msg = msg.replace("{" + i + "}", args[i].toString());
		}

		if (args.length == 0) CACHE.put(key, msg);
		return msg;
	}
}
