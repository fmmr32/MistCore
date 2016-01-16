package mcore.tollenaar.stephen.MistCore;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Note implements CommandExecutor{
	MCore plugin;
	DbStuff database;
	Message message;
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("note")){
		Player psender = null;
		PermissionUser psenderu = null;
		String moderatorname;
		String world;
		int x;
		int y;
		int z;
		int type = 0;
		if(sender instanceof Player){
		psender = (Player) sender;
		psenderu = PermissionsEx.getUser(psender);
		moderatorname = psender.getName();
		z = (int) psender.getLocation().getZ();
		y = (int) psender.getLocation().getY();
		x = (int) psender.getLocation().getX();
		world = psender.getWorld().getName();
		}else{
			moderatorname = "Console";
			z = 0;
			y = 0;
			x = 0;
			world = "Csave";
		}
		if(psenderu != null && !psenderu.has("MistCore.note")){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " You don't have permissions for this command!");
			return true;
		}else{
			if(args.length < 2){
				psender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " This command wasn't used correctly. Use it as: /note <playername> <reason>");
				return true;
			}else{
		String playername = args[0];
		String reason = null;
		for(int k = 1; k < args.length; k++){
			if(reason == null){
				reason = args[k];
			}else{
				reason = reason +"_" + args[k];
			}
		}
		
		PermissionUser subject = PermissionsEx.getUser(playername);
		String[] groepenarr = subject.getGroupsNames();
		StringBuilder groepen =	 new StringBuilder();
		for(String t : groepenarr){
			groepen.append(t + " ");
		}

			database.saveto(playername, moderatorname, reason, type, x, y, z, 0, System.currentTimeMillis()/1000L, world, groepen.toString());
			message.sendmessage(true, playername, "note", moderatorname, "0", reason);
			return true;
			}
		
	}
		}
		return false;
	}
	
	
	public Note(MCore instance){
		this.plugin = instance;
		this.database = instance.database;
		this.message = instance.message;
	}
}
