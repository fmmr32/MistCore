package mcore.tollenaar.stephen.MistCore;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CmdCount implements CommandExecutor{
	MCore plugin;
	HashMap<Boolean, Integer> temp = new HashMap<Boolean, Integer>();
	ArrayList<Boolean> next = new ArrayList<Boolean>();
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		boolean pass = true;
		if(sender instanceof Player){
			Player player = (Player) sender;
			PermissionUser ser = PermissionsEx.getUser(player);
			if(!ser.has("Ysir.restart")){
				pass = false;
			}
		}
		
		if(pass){
			comm();
		}else{
			return false;
		}
		return true;
	}
	
	public void comm(){
		temp.put(true, 1);
		next.add(false);
		if(!next.get(0)){
		final int id =Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				String time;
				if(temp.keySet().contains(true)){
					time = "1 minute";
					temp.remove(true);
					temp.put(false, 50);
				}else{
					time = temp.get(false) + " second(s)";
					temp.put(false, temp.get(false)-10);
				}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), ChatColor.RED +  "say restart in " + time + ".");
			}
		}, 0, 10*20L);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run(){
				Bukkit.getScheduler().cancelTask(id);
				temp.remove(false);
				temp.put(true, 10);
				next.set(0, true);
				comm();
			}
		}, 50*20L);
		}
		if(next.get(0)){
		final int id2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				String time;
				if(temp.keySet().contains(true)){
					time = "10 seconds";
					temp.remove(true);
					temp.put(false, 9);
				}else{
					time = temp.get(false) + " second(s)";
					temp.put(false, temp.get(false) - 1);
				}
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), ChatColor.RED +  "say restart in " + time + ".");

			}
		}, 0, 20L);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run(){
				Bukkit.getScheduler().cancelTask(id2);
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-all");
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "stop");
			}
		}, 20L*10);
		}
	}
	public CmdCount(MCore instance){
		this.plugin = instance;
	}
}
