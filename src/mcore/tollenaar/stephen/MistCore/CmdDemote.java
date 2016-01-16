package mcore.tollenaar.stephen.MistCore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CmdDemote implements CommandExecutor{
	MCore plugin;
	DbStuff database;
	Message message;
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		PermissionUser moderator = null;
		String moderatorname;
		int x;
		int y;
		int z;
		String world;
		String reason = null;
		if(sender instanceof Player){
			moderator = PermissionsEx.getUser((Player) sender);
			z = (int) ((Player) sender).getLocation().getZ();
			moderatorname = sender.getName();
			x = (int) ((Player) sender).getLocation().getX();
			y = (int) ((Player) sender).getLocation().getY();
			world = ((Player) sender).getWorld().getName();
		}else{
			moderatorname = "Console";
			z = 0;
			x = 0;
			y = 0;
			world = "CSave";
		}
		if(moderator != null && !moderator.has("MistCore.demote")){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " You don't have permissions for this command!");
			return true;
		}
		if(args.length == 0){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " This command wasn't used correctly. Use it as: /demote <playername> <reason>");
			return true;
		}
		if(args.length == 1){
			reason = "-";
		}else{
			for(int i = 1; i < args.length; i++){
				if(reason == null){
					reason = args[i];
				}else{
					reason = reason + "_" + args[i];
				}
			}
		}
		String playername = args[0];
		
		PermissionUser user = PermissionsEx.getUser(playername);
		String[] groepenarr = user.getGroupsNames();
		StringBuilder groepen =	 new StringBuilder();
		for(String t : groepenarr){
			groepen.append(t + " ");
		}
		database.saveto(playername, moderatorname, reason, 3, x, y, z, 0, System.currentTimeMillis()/1000L, world, groepen.toString());
		user.setGroups(new String[] {plugin.getDemoteRank()});
		if(cmd.getName().equalsIgnoreCase("demote")){
			message.sendmessage(false, playername, "demote", moderatorname, "", reason.replaceAll("_", " "));
		}else{
			message.sendmessage(true, playername, "demote", moderatorname, "", reason.replaceAll("_", " "));
		}
		return true;
	}
	public CmdDemote(MCore instance){
		this.plugin = instance;
		this.database = instance.database;
		this.message = instance.message;
	}
	
}
