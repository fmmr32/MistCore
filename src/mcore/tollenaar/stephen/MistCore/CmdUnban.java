package mcore.tollenaar.stephen.MistCore;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CmdUnban implements CommandExecutor {
	private DbStuff database;
	private Message message;

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		PermissionUser moderator = null;
		String moderatorname = null;
		int x;
		int y;
		int z;
		String world;
		if (sender instanceof Player) {
			moderatorname = sender.getName();
			x = (int) ((Player) sender).getLocation().getX();
			y = (int) ((Player) sender).getLocation().getY();
			z = (int) ((Player) sender).getLocation().getZ();
			world = ((Player) sender).getWorld().getName();
			moderator = PermissionsEx.getUser((Player) sender);
			if (moderator != null & !moderator.has("MistCore.unban")) {
				sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD
						+ "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA
						+ " You don't have permissions for this command!");
				return true;
			}
		} else {
			moderatorname = "Console";
			x = 0;
			z = 0;
			y = 0;
			world = "CSave";
		}

		String playername;
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED
					+ "["
					+ ChatColor.GOLD
					+ "MistCore"
					+ ChatColor.RED
					+ "]"
					+ ChatColor.AQUA
					+ " This command wasn't used correctly. Use it as /unban <playername> <reason>");
			return true;
		}
		String reason = null;
		if (args.length == 1) {
			reason = "-";
		} else {
			for (int k = 1; k < args.length; k++) {
				if (reason == null) {
					reason = args[k];
				} else {
					reason = reason + "_" + args[k];
				}
			}

		}
		playername = args[0];
		int type = 4;
		PermissionUser user = PermissionsEx.getUser(playername);
		String[] groepenarr = user.getGroupsNames();
		StringBuilder groepen = new StringBuilder();
		for (String t : groepenarr) {
			groepen.append(t + " ");
		}
		database.saveto(playername, moderatorname, reason, type, x, y, z, 0,
				System.currentTimeMillis() / 1000L, world, groepen.toString());
		if (cmd.getName().equalsIgnoreCase("unban")) {
			message.sendmessage(false, playername, "unban", moderatorname,
					null, reason.replaceAll("_", " "));
		} else {
			message.sendmessage(true, playername, "unban", moderatorname, null,
					reason.replaceAll("_", " "));
		}
		return true;
	}

	public CmdUnban(MCore instance) {
		this.database = instance.database;
		this.message = instance.message;
	}
}
