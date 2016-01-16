package mcore.tollenaar.stephen.MistCore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Message {
MCore plugin;
	public Message(MCore instance){
		this.plugin = instance;
	}
	public void sendmessage(boolean quiet, String subject, String sort, String moderator, String tijd, String reason){
		if(quiet == true){
			String message = null;
			if(sort.equals("tempban")){
				 message = ChatColor.RED + "[" + ChatColor.GOLD + "MistCore quiet" + ChatColor.RED + "] " + ChatColor.AQUA + moderator + " temporay banned "  + subject + " for " + tijd + " reason  " + reason+ ".";
			}
				if(sort.equals("permban")){
					message = ChatColor.RED + "[" + ChatColor.GOLD + "MistCore quiet" + ChatColor.RED + "] " + ChatColor.AQUA + moderator + " banned " +  subject + " reason " + reason + ".";
				}
				if(sort.equals("demote")){
					message = ChatColor.RED + "[" + ChatColor.GOLD + "MistCore quiet" + ChatColor.RED + "] " + ChatColor.AQUA + moderator +  " demoted " + subject+ ".";
				}
				if(sort.equals("promote")){
					message = ChatColor.RED + "[" + ChatColor.GOLD + "MistCore quiet" + ChatColor.RED + "] " + ChatColor.AQUA + moderator + " promoted "+ subject+ ".";
				}
				if(sort.equals("unban")){
					message = ChatColor.RED + "[" + ChatColor.GOLD + "MistCore quiet" + ChatColor.RED + "] " + ChatColor.AQUA + moderator + " unbanned "+ subject+ ".";
				}
				if(sort.equals("note")){
					message = ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "] " + ChatColor.AQUA + moderator + " has noted " + subject+  " " + reason.replace("_", " ") + ".";
				}
				for(Player players : Bukkit.getOnlinePlayers()){
					PermissionUser user = PermissionsEx.getUser(players);
					if(user.has("MistCore.quiet")){
					players.sendMessage(message);
				}
				}
		}else{
			String message = null;
			if(sort.equals("tempban")){
				 message = ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "] " + ChatColor.AQUA + moderator + " temporary banned "  + subject + " for " + tijd + " reason  " + reason+ ".";
			}
				if(sort.equals("permban")){
					message = ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "] " + ChatColor.AQUA + moderator + " banned " +  subject + " reason " + reason+ ".";
				}
				if(sort.equals("demote")){
					message = ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "] " + ChatColor.AQUA + moderator +  " demoted " + subject+ ".";
				}
				if(sort.equals("promote")){
					message = ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "] " + ChatColor.AQUA + moderator + " promoted "+ subject+ ".";
				}
				if(sort.equals("unban")){
					message = ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "] " + ChatColor.AQUA + moderator + " unbanned "+ subject+ ".";
				}
			for(Player players : Bukkit.getOnlinePlayers()){
				players.sendMessage(message);
			}
		}
	}
	
}
