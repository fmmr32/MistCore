package mcore.tollenaar.stephen.MistCore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class CmdPromote implements CommandExecutor {
	private MCore plugin;
	private DbStuff database;
	private Message message;
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		PermissionUser moderator = null;
		String moderatorname;
		int x;
		int y;
		int z;
		String world;
		if(sender instanceof Player){
			moderator = PermissionsEx.getUser((Player) sender);
			x = (int) ((Player) sender).getLocation().getX();
			z = (int) ((Player) sender).getLocation().getZ();
			y = (int) ((Player) sender).getLocation().getY();
			world = ((Player) sender).getWorld().getName();
			moderatorname = sender.getName();
		}else{
			x = 0;
			z = 0;
			y = 0;
			world = "CSave";
			moderatorname = "Console";
		}
		if(moderator != null && !moderator.has("MistCore.promote")){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " You don't have permissions for this command!");
			return true;
		}
		String reason = null;
		int type = 5;
		if(args.length == 0){
			sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA + " This command wasn't used correctly. Use it as /promote <playername>");
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
		PermissionUser subject = PermissionsEx.getUser(args[0]);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sqlsel = "SELECT * FROM `Mist_Users` WHERE `username` LIKE ? AND (`type` = 1 OR `type` = 2 OR `type` = 3) ORDER BY `id` DESC LIMIT 1";
		try{
			pst = database.GetCon().prepareStatement(sqlsel);
			Player victim = Bukkit.getPlayer(playername);
			UUID playeruuid;
			if(victim == null){
				OfflinePlayer off = Bukkit.getOfflinePlayer(playername);
				playeruuid = off.getUniqueId();
			}else{
				playeruuid = victim.getUniqueId();
			}
			pst.setString(1, playeruuid.toString());
			rs = pst.executeQuery();
			if(rs.last()){
				StringToRang(rs.getString("groepen"), subject);
			}
			String[] groepenarr = subject.getGroupsNames();
			StringBuilder groepen =	 new StringBuilder();
			for(String t : groepenarr){
				groepen.append(t + " ");
			}
			database.saveto(playername, moderatorname, reason, type, x, y, z, 0, System.currentTimeMillis()/1000L, world, groepen.toString());
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
		if(cmd.getName().equalsIgnoreCase("promote")){
			message.sendmessage(false, playername, "promote", moderatorname, "", reason.replaceAll("_", " "));
		}else{
			message.sendmessage(true, playername, "promote", moderatorname, "", reason.replaceAll("_", " "));
		}
	
		return true;
	}
	  @SuppressWarnings("deprecation")
	public void StringToRang(String rangen, PermissionUser user)
	  {
	    user.setGroups(rangen.split(":"));
	  }
	  public CmdPromote(MCore instance){
		  this.plugin = instance;
		  this.database = instance.database;
		  this.message = instance.message;
	  }
}
