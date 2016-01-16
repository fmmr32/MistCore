package mcore.tollenaar.stephen.MistCore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class TpNote implements CommandExecutor {
private	MCore plugin;
private	DbStuff database;
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		PermissionUser moderator = null;
		int x;
		int y;
		int z;
		String world;
		if(sender instanceof Player){
			moderator = PermissionsEx.getUser((Player) sender);
		}else{
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " You can't tp from console to note!");
			return true;
		}
		if(!moderator.has("MistCore.tpnote")){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " You don't have permissions for this command!");
			return true;
		}
		HashMap<Integer, Integer> lookupdata = new HashMap<Integer, Integer>();
		if(plugin.lookuplist.get(sender.getName()) == null){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " You need to lookup the playersdata first.");
			return true;
		}
		lookupdata = plugin.lookuplist.get(sender.getName());
		if(args.length != 1){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " This command was not used correctly. Use it as: /tpnote <lookupid>");
			return true;
		}
		int lookupid;
		try{
			lookupid = Integer.parseInt(args[0]);
		} catch (NumberFormatException e)
	    {
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " This isn't a number. Please check you argument.");
			return true;
	    }
		if(lookupdata.get(lookupid) == null){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " This lookupid doesn't exist. please fill in a correct one");
			return true;
		}
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sqlget = "SELECT * FROM `Mist_Users` WHERE `id` = ?;";
		try{
			pst = database.GetCon().prepareStatement(sqlget);
			pst.setInt(1, lookupdata.get(lookupid));
			rs = pst.executeQuery();
			if(rs.last()){
				if(rs.getString("wereld").equals("CSave")){
					sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " Sorry this note was saved by the console.");
					return true;
				}
				x = rs.getInt("x");
				z = rs.getInt("z");
				y = rs.getInt("y");
				world = rs.getString("wereld");
				sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " Teleporting to x:" + x + " z:" + z + " y:" + y + " in the world:" + world);
				Player player = (Player) sender;
				World wereld = Bukkit.getServer().getWorld(world);
				Location loc = new Location(wereld, x, y, z);
				player.teleport(loc);
			}
		}catch (SQLException e)
	    {
		      this.plugin.getLogger().severe(e.getMessage());
		      sender.sendMessage("An error occurd when storing the data: " + e.getMessage());
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
		return true;
	}

	
	
	public TpNote(MCore instance){
		this.plugin = instance;
		this.database = instance.database;
	}
}
