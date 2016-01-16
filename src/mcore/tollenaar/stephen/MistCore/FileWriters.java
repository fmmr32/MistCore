package mcore.tollenaar.stephen.MistCore;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;




import mcore.tollenaar.stephen.Admin.Active;
import mcore.tollenaar.stephen.Admin.Storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FileWriters {
	private MCore plugin;
	
	private File direct;
	private Storage storage;
	public FileWriters(MCore instance){
		this.plugin = instance;
		filecheck();
	}
	
	private File directory;
	private int lastadded = 0;
	private File current;
	private FileConfiguration currentconfig;
	
	
	public void filecheck(){
		directory = new File(Bukkit.getServer().getPluginManager().getPlugin("MistCore").getDataFolder(), "offline");
		if(!directory.exists()){
			directory.mkdir();
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		current  = new File(directory, dateFormat.format(date).replace("/", "-") + ".yml");
		if(!current.exists()){
			try {
				current.createNewFile();
				currentconfig = YamlConfiguration.loadConfiguration(current);
				saveyml(currentconfig,  current);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		direct = new File(plugin.getDataFolder(), "active");
		if(!direct.exists()){
			direct.mkdir();
		}
	}
	

	
	
	public void savePlayerData(UUID player){
		Active ac = storage.getActive(player);
		File pl = new File(direct, player + ".yml");
		if(!pl.exists()){
			try {
				pl.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileConfiguration config=  YamlConfiguration.loadConfiguration(pl);
		config.set("X", ac.getLocation().getX());
		config.set("Y", ac.getLocation().getY());
		config.set("Z", ac.getLocation().getZ());
		config.set("World", ac.getLocation().getWorld().getName());
		
		for(int i = 0; i < ac.getContents().length; i ++){
			config.set("Inventory." + i, ac.getContents()[i]);
		}
		for(int i = 0; i < ac.getArmor().length; i++){
			config.set("Armor." + i, ac.getArmor()[i]);
		}
		try {
			config.save(pl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		storage.removePlayer(player);
	}
	
	public void loadPlayerData(Player player){
		UUID playeruuid = player.getUniqueId();
		File pl = new File(direct, playeruuid + ".yml");
		if(!pl.exists()){
			return;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(pl);
		Location l = new Location(Bukkit.getWorld(config.getString("World")),config.getDouble("X"), config.getDouble("Y"), config.getDouble("Z"));
		ItemStack[] content = new ItemStack[player.getInventory().getSize()];
		for(String in : config.getConfigurationSection("Inventory").getKeys(false)){
			content[Integer.parseInt(in)] = config.getItemStack("Inventory."  + in);
		}
		ItemStack[] armor = new ItemStack[player.getInventory().getArmorContents().length];
		for(String in : config.getConfigurationSection("Armor").getKeys(false)){
			armor[Integer.parseInt(in)] = config.getItemStack("Armor." + in);
		}

		Active ac = new Active(playeruuid, content, l, armor);
 		storage.loadActive(ac, player);
	}
	public void deletePlayerData(UUID player){
		File f = new File(direct, player + ".yml");
		if(f.exists()){
			f.delete();
		}
	}
	
	public void disableSafe(){
		for(UUID player : storage.getActiveAdmins()){
			savePlayerData(player);
		}
	}
	
	
	public void saveyml(FileConfiguration config, File file){
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addline(String username, String moderatorname, String reason, int type, int x, int y, int z, long tijd, long datum, String wereld, String groepen){
		
		currentconfig.set(lastadded + "." +username + ".moderator", moderatorname);
		currentconfig.set(lastadded + "." +username + ".reason",reason);
		currentconfig.set(lastadded + "." +username +".type", type);
		currentconfig.set(lastadded + "." +username + ".x", x);
		currentconfig.set(lastadded + "." +username +".y", y);
		currentconfig.set(lastadded + "." +username+ ".z", z);
		currentconfig.set(lastadded + "." +username +".wereld", wereld);
		currentconfig.set(lastadded + "." +username +".tijd", tijd);
		currentconfig.set(lastadded + "." +username + ".datum", datum);
		currentconfig.set(lastadded + "." +username + ".groepen", groepen);
		saveyml(currentconfig, current);
		lastadded++;
	}


	
	public void loadall(){
		for(File in : directory.listFiles()){
			FileConfiguration tempconfig = YamlConfiguration.loadConfiguration(in);
			for(int i = 0; i < tempconfig.getKeys(false).size(); i++){
				for(String keys : tempconfig.getConfigurationSection(Integer.toString(i)).getKeys(false)){
				String username = keys;
				String moderatorname = tempconfig.getString(i + "." +keys + ".moderator");
				String reason = tempconfig.getString(i + "." +keys + ".reason");
				int type = tempconfig.getInt(i + "." +keys + ".type");
				int x = tempconfig.getInt(i + "." +keys + ".x");
				int y = tempconfig.getInt(i + "." +keys + ".y");
				int z = tempconfig.getInt(i + "." +keys + ".z");
				String wereld = tempconfig.getString(i + "." +keys + ".wereld");
				long tijd = tempconfig.getLong(i + "." +keys + ".tijd");
				long datum = tempconfig.getLong(i + "." +keys + ".datum");
				String groepen = tempconfig.getString(i + "." +keys + ".groepen");
				plugin.database.saveto(username, moderatorname, reason, type, x, y, z, tijd, datum, wereld, groepen);
			}
			}
			in.delete();
		}
		filecheck();
	}

}
