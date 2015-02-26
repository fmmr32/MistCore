package com.tollenaar.stephen.MistCore;

import java.sql.Connection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;




import code.husky.mysql.MySQL;

public class MCore extends JavaPlugin {
	Connection con = null;
	MCore plugin;
	DbStuff database;
	Message message;
	Adminchat ac;
	Note note;
	public HashMap<String, HashMap<Integer, Integer>> lookuplist = new HashMap<String, HashMap<Integer, Integer>>();
	public HashMap<String, String> inventorystore = new HashMap<String, String>();

	public void onEnable() {
		final FileConfiguration config = this.getConfig();

		config.options().copyDefaults(true);
		saveConfig();
		getConfig().options().copyDefaults(true);
		plugin = this;
		database = new DbStuff(this);
		message = new Message(this);
		ac = new Adminchat(this);
		note = new Note(this);
		database.intvar();
		MySQL MySQl = database.MySQl;
		int poging = 0;
		while (con == null) {
			con = MySQl.openConnection();
			if (con == null) {
				poging++;
				getLogger()
						.info("Database connection lost. Reconection will be started");

			}
			if (poging == 2) {
				getLogger()
						.info(Ansi.ansi().fg(Ansi.Color.RED)
								+ "No Connection to Database. Plugin is going in offline mode"
								+ Ansi.ansi().fg(Ansi.Color.WHITE));
				break;
			}

		}
			database.setcon(con);
			if(con != null){
				
			getLogger().info("Databse connection has succeed");
			database.TableCreate();
			database.closecon();
			database.fw.loadall();
			
			for(Player player : Bukkit.getOnlinePlayers()){
				database.updateonlinestatus(player.getUniqueId().toString(), true);
			}
			
			}else{
				database.specialclock();
			}
			getCommand("ban").setExecutor(new CmdBans(this));
			getCommand("tempban").setExecutor(new CmdBans(this));
			getCommand("qban").setExecutor(new CmdBans(this));
			getCommand("qtempban").setExecutor(new CmdBans(this));
			getCommand("note").setExecutor(note);
			getCommand("lookup").setExecutor(new CmdLookup(this));
			getCommand("demote").setExecutor(new CmdDemote(this));
			getCommand("promote").setExecutor(new CmdPromote(this));
			getCommand("qdemote").setExecutor(new CmdDemote(this));
			getCommand("qpromote").setExecutor(new CmdPromote(this));
			getCommand("tpnote").setExecutor(new TpNote(this));
			getCommand("summary").setExecutor(new CmdSummary(this));
			getCommand("unban").setExecutor(new CmdUnban(this));
			getCommand("qunban").setExecutor(new CmdUnban(this));
			getCommand("staff").setExecutor(ac);
			getCommand("countdown").setExecutor(new CmdCount(this));
			getServer().getPluginManager().registerEvents(new JoinBlock(this),
					this);
			getServer().getPluginManager().registerEvents(
					new SummaryListener(this), this);
			getServer().getPluginManager().registerEvents(ac, this);
	}

	public void onDisable() {
		database.onshutdown();
	}

}
