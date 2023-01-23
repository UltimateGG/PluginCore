package me.tntlimit.plugincore.base;

import me.tntlimit.plugincore.util.I18n;
import me.tntlimit.plugincore.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.List;


/**
 * The base class for all commands.
 * Any command you create should extend this class.
 * It will automatically register the command and tab complete.
 */
public abstract class CommandBase {
	private final String name;
	private final boolean allowedInConsole;
	private final String permission; // null if no permission required


	/**
	 * Creates a new command.
	 * The command will <b>not</b> be allowed to be run from the console and will <b>not</b> require a permission.
	 *
	 * @param name The name of the command from plugin.yml
	 */
	public CommandBase(String name) {
		this(name, false, null);
	}

	/**
	 *
	 * @param name The name of the command from plugin.yml
	 * @param allowedInConsole Whether the command can be run from the console. If false, your class will not receive
	 *                         the {@link #execute} method if the sender
	 *                         was not a player and a default message will be sent to the sender.
	 * @param permission The permission required to run the command. If null, no permission is required.
	 */
	public CommandBase(String name, boolean allowedInConsole, String permission) {
		this.name = name;
		this.allowedInConsole = allowedInConsole;
		this.permission = permission;

		PluginCommand cmd = PluginBase.INSTANCE.getCommand(name);
		if (cmd == null) throw new IllegalArgumentException("Command " + name + " not found in plugin.yml");

		cmd.setExecutor((sender, command, label, args) -> {
			if (!allowedInConsole && !(sender instanceof Player)) {
				sender.sendMessage("You must be a player to use this command");
				return true;
			}

			if (permission != null && !sender.hasPermission(permission)) {
				sender.sendMessage(tl("no-permission"));
				return true;
			}

			execute(sender, command, label, args);
			return true;
		});

		// Require permission to tab complete
		cmd.setTabCompleter((sender, command, alias, args) -> {
			if (!allowedInConsole && !(sender instanceof Player)) return Utils.EMPTY_LIST;
			if (permission != null && !sender.hasPermission(permission)) return Utils.EMPTY_LIST;

			return tabComplete(sender, command, args);
		});
	}

	/**
	 * Called when the command is executed.
	 * Only called after checks pass (console, permission)
	 *
	 * @param sender The sender of the command (player or console)
	 * @param command The command object
	 * @param label The alias used to execute the command
	 * @param args The arguments passed to the command
	 */
	public abstract void execute(CommandSender sender, Command command, String label, String[] args);

	/**
	 * Called when the command is tab completed.
	 * Only called after checks pass (console, permission)
	 * Note: By default, the first argument is tab completed as a username, and the rest are blank, unless overridden.
	 * Note: Bukkit counts arguments weirdly, be careful.
	 *
	 * @param sender The sender who tab completed the command (player or console)
	 * @param command The command object
	 * @param args The arguments passed to the command so far
	 * @return A list of possible tab completions, or null to use the default Bukkit tab completion (username)
	 */
	public List<String> tabComplete(CommandSender sender, Command command, String[] args) {
		if (args.length == 1) return null; // By default, tab complete first arg as username unless overridden
		return Utils.EMPTY_LIST;
	}

	/**
	 * Translates a key from the language file.
	 * Shorthand for {@link I18n#format}
	 *
	 * @param key The key to translate
	 * @param args The arguments to pass to the translation
	 * @return The translated string
	 * @see I18n
	 */
	protected String tl(String key, Object... args) {
		return I18n.format(key, args);
	}

	public String getName() {
		return name;
	}

	public boolean isAllowedInConsole() {
		return allowedInConsole;
	}

	public String getPermission() {
		return permission;
	}
}
