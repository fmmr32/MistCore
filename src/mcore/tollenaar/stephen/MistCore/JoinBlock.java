package mcore.tollenaar.stephen.MistCore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinBlock implements Listener {
private 	MCore plugin;
private	DbStuff database;

	public JoinBlock(MCore instance) {
		this.plugin = instance;
		this.database = instance.database;
	}
	
	
	@EventHandler
	public void onPlayerquit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		database.updateonlinestatus(player.getUniqueId().toString(), false);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerlogin(PlayerLoginEvent event) throws SQLException {
		Player player = event.getPlayer();
		String sql = "SELECT * FROM `Mist_Users` WHERE `username` LIKE ? ORDER BY `id` DESC LIMIT 1;";
		PreparedStatement pst = null;
		ResultSet result = null;
		boolean comeson  =true;
		try {
			pst = database.GetCon().prepareStatement(sql);
			Player victim = Bukkit.getPlayer(player.getName());
			UUID playeruuid;
			if (victim == null) {
				@SuppressWarnings("deprecation")
				OfflinePlayer off = Bukkit.getOfflinePlayer(player.getName());
				playeruuid = off.getUniqueId();
			} else {
				playeruuid = victim.getUniqueId();
			}
			pst.setString(1, playeruuid.toString());
			result = pst.executeQuery();
			result.last();
			int rowCount = result.getRow();
			if (rowCount != 0) {
				if (result.getInt("type") == 1) {
					String reason = result.getString("reason").replaceAll("_",
							" ");
					event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
							ChatColor.RED + "[" + ChatColor.GOLD + "MistCore"
									+ ChatColor.RED + "] " + ChatColor.AQUA
									+ reason
									+ ". Go to the website for an unbanticket.");
					comeson = false;
				}
				if (result.getInt("type") == 2) {
					String reason = result.getString("reason").replaceAll("_",
							" ");
					long currentnixtime = System.currentTimeMillis() / 1000L;
					long bannixtime = result.getLong("tijd");
					if (bannixtime > currentnixtime) {
						double secondsleft = bannixtime - currentnixtime;
						String message = ChatColor.RED + "[" + ChatColor.GOLD
								+ "MistCore" + ChatColor.RED + "] "
								+ ChatColor.AQUA + reason + ". You are still "
								+ calcTime(secondsleft) + " banned.";
						event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
								message);
						comeson = false;
					}
				}
			}
			database.saveuuid(playeruuid.toString(), player.getName());
			if(comeson){
			database.updateonlinestatus(player.getUniqueId().toString(), true);
			}
		} catch (SQLException ex) {
			this.plugin.getLogger().severe(ex.getMessage());
			try {
				if (result != null) {
					result.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException exx) {
				this.plugin.getLogger().severe(exx.getMessage());
			}
		}catch(NullPointerException ex){
			
		}
		finally {
			try {
				if (result != null) {
					result.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				this.plugin.getLogger().severe(ex.getMessage());
			}
		}

	}

	public String calcTime(double secondsleft) {
		String message = "";
		if (86400.0D < secondsleft) {
			double daysleft = secondsleft / 86400.0D;
			secondsleft = (daysleft - Math.floor(daysleft)) * 86400.0D;
			if (Math.floor(daysleft) == 1.0D) {
				message = message + "1 day, ";
			} else {
				message = message + (int) Math.floor(daysleft) + " days, ";
			}
		}
		if (3600.0D < secondsleft) {
			double hoursleft = secondsleft / 3600.0D;
			secondsleft = (hoursleft - Math.floor(hoursleft)) * 3600.0D;
			if (Math.floor(hoursleft) == 1.0D) {
				message = message + "1 hour, ";
			} else {
				message = message + (int) Math.floor(hoursleft) + " hours, ";
			}
		}
		if (60.0D < secondsleft) {
			double minutesleft = secondsleft / 60.0D;
			secondsleft = (minutesleft - Math.floor(minutesleft)) * 60.0D;
			if (Math.floor(minutesleft) == 1.0D) {
				message = message + "1 minute and ";
			} else {
				message = message + (int) Math.floor(minutesleft)
						+ " minutes and ";
			}
		}
		if (Math.floor(secondsleft) == 1.0D) {
			message = message + "1 second ";
		} else {
			message = message + (int) Math.floor(secondsleft) + " seconds ";
		}
		return message;
	}
}
