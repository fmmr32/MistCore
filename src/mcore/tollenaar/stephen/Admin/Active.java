package mcore.tollenaar.stephen.Admin;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Active {
	
	private final UUID player;
	private final ItemStack[] contents;
	private final ItemStack[] armor;
	private final Location loc;
	
	public Active(UUID player, ItemStack[] content, Location loc, ItemStack[] armor){
		this.player = player;
		this.contents = content;
		this.armor = armor;
		this.loc = loc;
	}

	public UUID getPlayer() {
		return player;
	}

	public ItemStack[] getArmor() {
		return armor;
	}

	public ItemStack[] getContents() {
		return contents;
	}

	public Location getLocation() {
		return loc;
	}


	
}
