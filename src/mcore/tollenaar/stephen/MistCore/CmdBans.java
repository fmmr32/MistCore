package mcore.tollenaar.stephen.MistCore;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CmdBans implements CommandExecutor {
	DbStuff database;
	MCore plugin;
	Message message;
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		PermissionUser user = null;
		PermissionUser moderator = null;
		String moderatorname;
		int x;
		int y;
		int z;
		String world;
		if(sender instanceof Player){
			moderatorname = ((Player) sender).getPlayer().getName();
		moderator = PermissionsEx.getUser(((Player) sender).getPlayer());
			x=(int) ((Player) sender).getLocation().getX();
			y=(int) ((Player) sender).getLocation().getY();
			z=(int) ((Player) sender).getLocation().getZ();
			world=((Player) sender).getWorld().getName();
		}else{
			moderatorname = "Console";
			x=0;
			z=0;
			y=0;
			world = "Csave";
		}
		if(moderator != null &&!moderator.has("MistCore.ban")){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " You don't have permissions for this command!");
			return true;
		}
		String playername = null;
		
		String reason = null;
		
	
		if(cmd.getName().equalsIgnoreCase("ban") || cmd.getName().equalsIgnoreCase("qban")){
			if(args.length < 1){
				sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " This command wasn't used correctly. Use it as: /ban <playername> <reason>");
				return true;
			}else{
				
			playername = args[0];
			user = PermissionsEx.getUser(playername);
		String[] groepenarr = user.getGroupsNames();
		StringBuilder groepen =	 new StringBuilder();
		for(String t : groepenarr){
			groepen.append(t + " ");
		}
		if(args.length == 1){
			reason = "-";
		}else{
			for(int k = 1; k < args.length; k++){
				if(reason == null){
					reason = args[k];
				}else{
					reason = reason +"_" +args[k];
				}
			}
		}
			database.saveto(playername, moderatorname, reason, 1, x, y, z, 0, System.currentTimeMillis()/1000l, world, groepen.toString());
			Player player = Bukkit.getPlayer(playername);
			if(player != null){
				player.kickPlayer(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " You're banned from this server. Check the website for more information.");
			}
			user.setGroups(new String[] {plugin.getDemoteRank()});
			}
			if(cmd.getName().equalsIgnoreCase("ban")){
				message.sendmessage(false, playername, "permban", moderatorname, "", reason.replaceAll("_", " "));
			}else{
				message.sendmessage(true, playername, "permban", moderatorname, "", reason.replaceAll("_", " "));
			}
			return true;
		}else if(cmd.getName().equalsIgnoreCase("tempban") || cmd.getName().equalsIgnoreCase("qtempban")){
			if(args.length <2){
				sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " This command wasn't used correctly. Use it as: /ban <playername> <time> <reason>");
				
				return true;
			}else{
				playername = args[0];
			user = PermissionsEx.getUser(playername);
			String[] groepenarr = user.getGroupsNames();
			StringBuilder groepen =	 new StringBuilder();
			for(String t : groepenarr){
				groepen.append(t + " ");
			}
			if(args.length == 2){
				reason = "-";
			}else{
				for(int k = 2; k < args.length; k++){
					if(reason == null){
						reason = args[k];
					}else{
						reason = reason + "_" + args[k];
					}
				}
			}
			long seconds = parseDateDiff(args[1], true) / 1000L;
			database.saveto(playername, moderatorname, reason, 2, x, y, z, seconds, System.currentTimeMillis()/1000L, world, groepen.toString());
			Player player = Bukkit.getPlayer(playername);
			if(player != null){
				String tijd = calcTime(seconds - System.currentTimeMillis() / 1000L);
				player.kickPlayer(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " You're tempory banned from this server. You are " + tijd + " banned");		
				}
			user.setGroups(new String[] {plugin.getDemoteRank()});
			String tijd = calcTime(seconds - System.currentTimeMillis() / 1000L);
			if(cmd.getName().equalsIgnoreCase("tempban")){
				message.sendmessage(false, playername, "tempban", moderatorname, tijd, reason.replaceAll("_", " "));
			}else{
				message.sendmessage(true, playername, "tempban", moderatorname, tijd, reason.replaceAll("_", " "));
			}
			return true;
		}
		
		}
		return false;
	}
	public CmdBans(MCore instance){
		this.plugin = instance;
		this.database = instance.database;
		this.message = instance.message;
	}
	
	 public long parseDateDiff(String time, boolean future)
	  {
	    Pattern timePattern = Pattern.compile(
	      "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 
	      




	      2);
	    Matcher m = timePattern.matcher(time);
	    int years = 0;
	    int months = 0;
	    int weeks = 0;
	    int days = 0;
	    int hours = 0;
	    int minutes = 0;
	    int seconds = 0;
	    boolean found = false;
	    while (m.find()) {
	      if ((m.group() != null) && (!m.group().isEmpty()))
	      {
	        for (int i = 0; i < m.groupCount(); i++) {
	          if ((m.group(i) != null) && (!m.group(i).isEmpty()))
	          {
	            found = true;
	            break;
	          }
	        }
	        if (found)
	        {
	          if ((m.group(1) != null) && (!m.group(1).isEmpty())) {
	            years = Integer.parseInt(m.group(1));
	          }
	          if ((m.group(2) != null) && (!m.group(2).isEmpty())) {
	            months = Integer.parseInt(m.group(2));
	          }
	          if ((m.group(3) != null) && (!m.group(3).isEmpty())) {
	            weeks = Integer.parseInt(m.group(3));
	          }
	          if ((m.group(4) != null) && (!m.group(4).isEmpty())) {
	            days = Integer.parseInt(m.group(4));
	          }
	          if ((m.group(5) != null) && (!m.group(5).isEmpty())) {
	            hours = Integer.parseInt(m.group(5));
	          }
	          if ((m.group(6) != null) && (!m.group(6).isEmpty())) {
	            minutes = Integer.parseInt(m.group(6));
	          }
	          if ((m.group(7) == null) || (m.group(7).isEmpty())) {
	            break;
	          }
	          seconds = Integer.parseInt(m.group(7));
	          
	          break;
	        }
	      }
	    }
	    if (!found) {
	      return -1L;
	    }
	    Calendar c = new GregorianCalendar();
	    if (years > 0) {
	      c.add(1, years * (future ? 1 : -1));
	    }
	    if (months > 0) {
	      c.add(2, months * (future ? 1 : -1));
	    }
	    if (weeks > 0) {
	      c.add(3, weeks * (future ? 1 : -1));
	    }
	    if (days > 0) {
	      c.add(5, days * (future ? 1 : -1));
	    }
	    if (hours > 0) {
	      c.add(11, hours * (future ? 1 : -1));
	    }
	    if (minutes > 0) {
	      c.add(12, minutes * (future ? 1 : -1));
	    }
	    if (seconds > 0) {
	      c.add(13, seconds * (future ? 1 : -1));
	    }
	    return c.getTimeInMillis();
	  }
	 public String calcTime(double secondsleft)
	  {
	    String message = "";
	    if (86400.0D < secondsleft)
	    {
	      double daysleft = secondsleft / 86400.0D;
	      secondsleft = (daysleft - Math.floor(daysleft)) * 86400.0D;
	      if (Math.floor(daysleft) == 1.0D) {
	        message = message + "1 day, ";
	      } else {
	        message = message + (int)Math.floor(daysleft) + " days, ";
	      }
	    }
	    if (3600.0D < secondsleft)
	    {
	      double hoursleft = secondsleft / 3600.0D;
	      secondsleft = (hoursleft - Math.floor(hoursleft)) * 3600.0D;
	      if (Math.floor(hoursleft) == 1.0D) {
	        message = message + "1 hour, ";
	      } else {
	        message = message + (int)Math.floor(hoursleft) + " hours, ";
	      }
	    }
	    if (60.0D < secondsleft)
	    {
	      double minutesleft = secondsleft / 60.0D;
	      secondsleft = (minutesleft - Math.floor(minutesleft)) * 60.0D;
	      if (Math.floor(minutesleft) == 1.0D) {
	        message = message + "1 minute and ";
	      } else {
	        message = message + (int)Math.floor(minutesleft) + " minutes and ";
	      }
	    }
	    if (Math.floor(secondsleft) == 1.0D) {
	      message = message + "1 second ";
	    } else {
	      message = message + (int)Math.floor(secondsleft) + " seconds ";
	    }
	    return message;
	  }
	  
	  public String calcTimeShort(double secondsleft)
	  {
	    String message = "";
	    if (86400.0D < secondsleft)
	    {
	      double daysleft = secondsleft / 86400.0D;
	      secondsleft = (daysleft - Math.floor(daysleft)) * 86400.0D;
	      message = message + (int)Math.floor(daysleft) + "d";
	    }
	    if (3600.0D < secondsleft)
	    {
	      double hoursleft = secondsleft / 3600.0D;
	      secondsleft = (hoursleft - Math.floor(hoursleft)) * 3600.0D;
	      message = message + (int)Math.floor(hoursleft) + "u";
	    }
	    if (60.0D < secondsleft)
	    {
	      double minutesleft = secondsleft / 60.0D;
	      secondsleft = (minutesleft - Math.floor(minutesleft)) * 60.0D;
	      message = message + (int)Math.floor(minutesleft) + "m";
	    }
	    message = message + (int)Math.floor(secondsleft) + "s";
	    return message;
	  }
}

