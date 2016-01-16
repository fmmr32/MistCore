package mcore.tollenaar.stephen.MistCore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SummaryListener implements Listener {
private	MCore plugin;
private	DbStuff database;
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		Inventory inventory = event.getInventory();
		Player player = (Player) event.getWhoClicked();
		if(plugin.inventorystore.get(player.getName()) != null && plugin.inventorystore.get(player.getName()).equals(inventory.getName())){
			if(inventory.getName().contains("all")){
				event.setCancelled(true);
			}else{
			int type = 0;
			ItemStack clicked = event.getCurrentItem();
			if(clicked.getType() == Material.BEDROCK){
				type = 1;
			}else
			if(clicked.getType() == Material.OBSIDIAN){
				type = 2;
			}else
			if(clicked.getType() == Material.SOUL_SAND){
				type = 3;
			}else
			if(clicked.getType() == Material.QUARTZ_BLOCK){
				type = 5;
			}else
			if(clicked.getType() == Material.GLOWSTONE){
				type = 0;
			}else
			if(clicked.getType() == Material.NETHERRACK){
				type = 4;
			}else{
				event.setCancelled(true);
			}
			String sqlselect = "SELECT * FROM `Mist_Users` WHERE `username` LIKE ? AND `type` = " + type;
			PreparedStatement pst = null;
			ResultSet rs = null;
			String playername = inventory.getName();
			event.setCancelled(true);
			
			String types = null;
			switch(type){
			case 1:
				types = "permbans";
				break;
			case 2:
				types = "tempbans";
				break;
			case 3:
				types = "demotes";
				break;
			case 4:
				types = "unbans";
				break;
			case 5:
				types = " promotes";
				break;
			case 0:
				types = "notes";
				break;
			}
			Material[] blocktype = new Material[]{Material.GLOWSTONE, Material.BEDROCK, Material.OBSIDIAN, Material.NETHERRACK, Material.SOUL_SAND, Material.QUARTZ_BLOCK};
			try{
				pst = database.GetCon().prepareStatement(sqlselect);
				Player victim = Bukkit.getPlayer(playername);
				UUID playeruuid;
				if(victim == null){
					@SuppressWarnings("deprecation")
					OfflinePlayer off = Bukkit.getOfflinePlayer(playername);
					playeruuid = off.getUniqueId();
				}else{
					playeruuid = victim.getUniqueId();
				}
				pst.setString(1, playeruuid.toString());
				int rowcount = 0;
				rs = pst.executeQuery();
				while(rs.next()){
					rowcount++;
				}
				while(rowcount % 9 != 0){
					rowcount++;
				}
				Inventory allnotes = Bukkit.createInventory(null, rowcount, playername + " all " + types);
				plugin.inventorystore.put(player.getName(), allnotes.getName());
				rs = pst.executeQuery();
				int place = 0;
				while(rs.next()){
					ItemStack item = new ItemStack(blocktype[type], 1);
					ItemMeta itemmeta = item.getItemMeta();
					itemmeta.setDisplayName(types);
					String reason  = ChatColor.GOLD + rs.getString("reason").replaceAll("_", " ") + " by " + rs.getString("moderatorname");
					ArrayList<String> reasonlist = new ArrayList<String>();
					reasonlist.add(reason);
					itemmeta.setLore(reasonlist);
					item.setItemMeta(itemmeta);
					allnotes.setItem(place, item);
					place++;
				}
				player.closeInventory();
				player.openInventory(allnotes);
			} catch (SQLException e)
		    {
			      this.plugin.getLogger().severe(e.getMessage());
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

		}
		}
	}
	
	public SummaryListener(MCore instance){
		this.plugin = instance;
		this.database = instance.database;
	}
}
