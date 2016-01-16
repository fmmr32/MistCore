package mcore.tollenaar.stephen.Admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import mcore.tollenaar.stephen.MistCore.MCore;

public class Storage {
	private MCore plugin;

	private Map<UUID, Active> ACTIVE_ADMINS = new HashMap<>();

	public Storage(MCore instance) {
		this.plugin = instance;
	}

	public Set<UUID> getActiveAdmins() {
		return ACTIVE_ADMINS.keySet();
	}

	public void loadActive(Active ac, Player pl) {
		ACTIVE_ADMINS.put(pl.getUniqueId(), ac);
		pl.sendMessage(plugin.getAnnouncer()
				+ "You are still registered as an Active Admin. Please turn this mode off or finish your business.");
	}

	public void addActive(Player player) {
		Location l = player.getLocation();
		Active ac = new Active(player.getUniqueId(), player.getInventory()
				.getContents().clone(), l, player.getInventory()
				.getArmorContents().clone());
		ACTIVE_ADMINS.put(player.getUniqueId(), ac);
		player.getInventory().clear();
		player.setGameMode(GameMode.CREATIVE);
		player.sendMessage(plugin.getAnnouncer()
				+ "You have enabled Admin mode. Your items and location are stored");
		
		PermissionUser user = PermissionsEx.getUser(player);
		user.addGroup("«Admin»");
		for (String cmd : plugin.getConfig().getStringList("adminmodecmd")) {
			Bukkit.dispatchCommand(player, cmd);
		}


		@SuppressWarnings("deprecation")
		PermissionGroup[] groups = user.getGroups();
		PermissionGroup group = null;
		for (PermissionGroup i : groups) {
			if (i.getName().equals("Admin")) {
				group = i;
			}
		}
		if (group == null) {
			return;
		}
		for (PermissionUser online : group.getActiveUsers()) {
			if (online != null && online.getPlayer() != null) {
				if (online.getPlayer().getUniqueId() != player.getUniqueId()) {
					online.getPlayer().sendMessage(
							plugin.getAnnouncer() + player.getName()
									+ " has enabled Admin Mode.");
				}
			}
		}
	}

	public void restoreAdmin(Player player) {
		Active ac = ACTIVE_ADMINS.get(player.getUniqueId());
		player.teleport(ac.getLocation());
		player.getInventory().clear();
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().setContents(ac.getContents());
		player.getInventory().setArmorContents(ac.getArmor());

		player.sendMessage(plugin.getAnnouncer()
				+ "You have disabled Admin mode. Your items and location are restored.");
		ACTIVE_ADMINS.remove(player.getUniqueId());
		plugin.fw.deletePlayerData(player.getUniqueId());
		PermissionUser user = PermissionsEx.getUser(player);
		user.removeGroup("«Admin»");

		@SuppressWarnings("deprecation")
		PermissionGroup[] groups = user.getGroups();
		PermissionGroup group = null;
		for (PermissionGroup i : groups) {
			if (i.getName().equals("Admin")) {
				group = i;
			}
		}
		if (group == null) {
			return;
		}
		for (PermissionUser online : group.getActiveUsers()) {
			if (online != null && online.getPlayer() != null) {
				if (online.getPlayer().getUniqueId() != player.getUniqueId()) {
					online.getPlayer().sendMessage(
							plugin.getAnnouncer() + player.getName()
									+ " has disabled Admin Mode.");
				}
			}
		}
	}

	public Active getActive(UUID player) {
		return ACTIVE_ADMINS.get(player);
	}

	public void removePlayer(UUID player) {
		ACTIVE_ADMINS.remove(player);
	}
}
