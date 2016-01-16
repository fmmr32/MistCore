package mcore.tollenaar.stephen.MistCore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CmdSummary implements CommandExecutor {
private	MCore plugin;
private	DbStuff database;

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		PermissionUser moderator = null;
		database.checkcon();
		if (sender instanceof Player) {
				moderator = PermissionsEx.getUser((Player) sender);
			} else {
				sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD
						+ "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA
						+ " You must be a player to use this command");
				return true;
			}
			if (!moderator.has("MistCore.summary")) {
				sender.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD
						+ "MistCore" + ChatColor.RED + "]" + ChatColor.AQUA
						+ " You don't have permission to use this command.");
				return true;
			}
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED
						+ "["
						+ ChatColor.GOLD
						+ "MistCore"
						+ ChatColor.RED
						+ "]"
						+ ChatColor.AQUA
						+ " This command wasn't used correctly. Use it as /summary <playername>");
				return true;
			}
			int notes = 0;
			int permbans = 0;
			int tempbans = 0;
			int demotes = 0;
			int promotes = 0;
			int unbans = 0;
			PreparedStatement pst = null;
			ResultSet rs = null;
			String sqlselect = "SELECT * FROM `Mist_Users` WHERE `username` LIKE ? AND `type` = ?;";
			try {
				pst = database.GetCon().prepareStatement(sqlselect);
				Player victim = Bukkit.getPlayer(args[0]);
				UUID playeruuid;
				if (victim == null) {
					@SuppressWarnings("deprecation")
					OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
					playeruuid = off.getUniqueId();
				} else {
					playeruuid = victim.getUniqueId();
				}
				pst.setString(1, playeruuid.toString());
				for (int i = 0; i < 5; i++) {
					pst.setInt(2, i);
					rs = pst.executeQuery();
					while (rs.next()) {
						if (i == 0) {
							notes++;
						}
						if (i == 1) {
							permbans++;
						}
						if (i == 2) {
							tempbans++;
						}
						if (i == 3) {
							demotes++;
						}
						if (i == 4) {
							unbans++;
						}
						if (i == 5) {
							promotes++;
						}
					}
				}
				Inventory myinventory = Bukkit
						.createInventory(null, 9, args[0]);
				plugin.inventorystore.put(sender.getName(),
						myinventory.getName());
				ItemStack unban = new ItemStack(Material.SOUL_SAND, promotes);
				ItemStack note = new ItemStack(Material.GLOWSTONE, notes);
				ItemStack permban = new ItemStack(Material.BEDROCK, permbans);
				ItemStack tempban = new ItemStack(Material.OBSIDIAN, tempbans);
				ItemStack demote = new ItemStack(Material.NETHERRACK, demotes);
				ItemStack promote = new ItemStack(Material.QUARTZ_BLOCK,
						promotes);

				ItemMeta unbanmeta = unban.getItemMeta();
				ItemMeta notemeta = note.getItemMeta();
				ItemMeta permbanmeta = permban.getItemMeta();
				ItemMeta tempbanmeta = tempban.getItemMeta();
				ItemMeta demotemeta = demote.getItemMeta();
				ItemMeta promotemeta = promote.getItemMeta();

				unbanmeta.setDisplayName("Unbans");
				notemeta.setDisplayName("Notes");
				permbanmeta.setDisplayName("Permbans");
				tempbanmeta.setDisplayName("Tempbans");
				demotemeta.setDisplayName("Demotes");
				promotemeta.setDisplayName("Promotes");

				unban.setItemMeta(unbanmeta);
				note.setItemMeta(notemeta);
				permban.setItemMeta(permbanmeta);
				tempban.setItemMeta(tempbanmeta);
				demote.setItemMeta(demotemeta);
				promote.setItemMeta(promotemeta);

				unban.setAmount(unbans);
				note.setAmount(notes);
				permban.setAmount(permbans);
				tempban.setAmount(tempbans);
				demote.setAmount(demotes);
				promote.setAmount(promotes);

				if (unban.getAmount() != 0) {
					myinventory.setItem(1, unban);
				}
				if (note.getAmount() != 0) {
					myinventory.setItem(2, note);
				}
				if (promote.getAmount() != 0) {
					myinventory.setItem(3, promote);
				}
				if (demote.getAmount() != 0) {
					myinventory.setItem(5, demote);
				}
				if (tempban.getAmount() != 0) {
					myinventory.setItem(6, tempban);
				}
				if (permban.getAmount() != 0) {
					myinventory.setItem(7, permban);
				}

				Player player = (Player) sender;
				player.openInventory(myinventory);
			} catch (SQLException e) {
				this.plugin.getLogger().severe(e.getMessage());
				plugin.getLogger().info(
						"There was a error during the lookups of the data to the database: "
								+ e.getMessage());
				try {
					if (pst != null) {
						pst.close();
					}
				} catch (SQLException ex) {
					this.plugin.getLogger().severe(ex.getMessage());
				}
			} finally {
				try {
					if (pst != null) {
						pst.close();
					}
				} catch (SQLException ex) {
					this.plugin.getLogger().severe(ex.getMessage());
				}
			}
			return true;
	}

	public CmdSummary(MCore instance) {
		this.plugin = instance;
		this.database = instance.database;
	}
}
