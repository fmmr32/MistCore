package mcore.tollenaar.stephen.MistCore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;






import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CmdLookup implements CommandExecutor {
	private MCore plugin;
	private DbStuff database;
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		database.checkcon();
		PermissionUser moderator = null;
				if(sender instanceof Player){
					moderator = PermissionsEx.getUser((Player) sender);
				}
		if(moderator != null &&!moderator.has("MistCore.lookup")){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " You don't have permissions for this command!");
			return true;
		}
		if(args.length == 0){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " This command wasn't used correctly. Use it as /lookup <playername>");
			return true;
		}
		HashMap<Integer, Integer> lookupmap = new HashMap<Integer, Integer>();
		String startmessage = ChatColor.GOLD + "========" + ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.GOLD + "========";
		String totalentries = ChatColor.GOLD + "Total notes found: ";
		String latest = ChatColor.GOLD + "Showing the last 5 of user " + args[0];
		String ent1 = ChatColor.RED + "1. ";
		String ent2 = ChatColor.RED +  "2. ";
		String ent3 = ChatColor.RED +  "3. ";
		String ent4 = ChatColor.RED +  "4. ";
		String ent5 = ChatColor.RED +  "5. ";
		String website = ChatColor.AQUA + "Want to see more of this player visit: ysir.eu/mistcore/" + args[0];
		String type = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int notes = 0;
		String sqllimit = "SELECT * FROM `Mist_Users` WHERE `username` LIKE ? ORDER BY `id` DESC LIMIT 5";
		try{
			
			Player victim = Bukkit.getPlayer(args[0]);
			UUID playeruuid;
			if(victim == null){
				@SuppressWarnings("deprecation")
				OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
				playeruuid = off.getUniqueId();
			}else{
				playeruuid = victim.getUniqueId();
			}
			
			pst = database.GetCon().prepareStatement(sqllimit);
			pst.setString(1, playeruuid.toString());
			
			rs = pst.executeQuery();
	
			while(rs.next()){
				notes++;
			} 
			pst.close();
			totalentries = totalentries + notes;
			pst = database.GetCon().prepareStatement(sqllimit);

			pst.setString(1, playeruuid.toString());
			rs = pst.executeQuery();
			long currentnixtime = System.currentTimeMillis() / 1000L;
			int rowcount = 1;
			while(rs.next()){
				
				if(rs.getInt("type") == 1){
					type = ChatColor.GOLD + "Ban";
				}
				if(rs.getInt("type") == 2){
					type = ChatColor.GOLD + "Tempban";
					 if (rs.getLong("tijd") >= currentnixtime) {
				            type = type + ", still " + calcTimeShort(rs.getLong("tijd") - currentnixtime);
				          } else {
				            type = type + ", ended";
				          }
				      }
				if(rs.getInt("type") == 3){
					type = ChatColor.GOLD + "Demote";
				}
				if(rs.getInt("type") == 5){
					type = ChatColor.GOLD + "Promote";
				}
				if(rs.getInt("type") == 0){
					type = ChatColor.GOLD + "Note";
				}
				if(rs.getInt("type") == 4){
					type = ChatColor.GOLD + "Unban";
				}
				
			if(rs.getString("reason").equalsIgnoreCase("")){
				switch(rowcount){
				case 1:
					ent1 = ent1 + type +  " Reason: Not defined.";
					break;
				case 2:
					ent2 = ent2 + type +  " Reason: Not defined.";
					break;
				case 3: 
					ent3 = ent3 + type +  " Reason: Not defined.";
					break;
				case 4:
					ent4 = ent4 + type +  " Reason: Not defined.";
					break;
				case 5:
					ent5 = ent5 + type +  " Reason: Not defined.";
					break;
				}
			}else{
				switch(rowcount){
			case 1:
				ent1 = ent1 + type + " Reason: " + rs.getString("reason");
				break;
			case 2:
				ent2 = ent2 + type + " Reason: " +   rs.getString("reason");
				break;
			case 3: 
				ent3 = ent3 + type +   " Reason: " + rs.getString("reason");
				break;
			case 4:
				ent4 = ent4 + type +  " Reason: " +  rs.getString("reason");
				break;
			case 5:
				ent5 = ent5 + type +   " Reason: " + rs.getString("reason");
				break;
			}
			}
			lookupmap.put(rowcount, rs.getInt("id"));
			rowcount++;
			}
		}  catch (SQLException e)
	    {
		      this.plugin.getLogger().severe(e.getMessage());
		      sender.sendMessage("An error occurd when retrieving the data: " + e.getMessage());
		      try
		      {
		        if (pst != null) {
		          pst.close();
		        }
		      }
		      catch (SQLException ex)
		      {
		        this.plugin.getLogger().severe(ex.getMessage());
		      }
		    }
		    finally
		    {
		      try
		      {
		        if (pst != null) {
		          pst.close();
		        }
		      }
		      catch (SQLException ex)
		      {
		        this.plugin.getLogger().severe(ex.getMessage());
		      }
		    }
		if(this.plugin.lookuplist.containsKey(sender.getName())){
			this.plugin.lookuplist.remove(sender.getName());
		}
		this.plugin.lookuplist.put(sender.getName(), lookupmap);
		String []returnmess = {startmessage, totalentries, latest, ent1.replace("_", " "), ent2.replace("_", " "), ent3.replace("_", " "), ent4.replace("_", " "), ent5.replace("_", " "), website};
		if(notes == 0){
			sender.sendMessage(startmessage);
			sender.sendMessage(ChatColor.GOLD + "No notes found of this player");
			return true;
		}else{
			for(String mess : returnmess){
				sender.sendMessage(mess);
			}
		}

		
		
		
		
		database.closecon();
		return true;
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
	public CmdLookup(MCore instance){
		this.plugin = instance;
		this.database = instance.database;
	}
}
