package mcore.tollenaar.stephen.MistCore;

import java.util.HashMap;





import mcore.tollenaar.stephen.Admin.Storage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;


public class MCore extends JavaPlugin {
	protected MCore plugin;
	protected DbStuff database;
	protected Message message;
	protected Adminchat ac;
	protected Note note;
	public HashMap<String, HashMap<Integer, Integer>> lookuplist = new HashMap<String, HashMap<Integer, Integer>>();
	public HashMap<String, String> inventorystore = new HashMap<String, String>();
	private String announcer = ChatColor.GOLD + "[" + ChatColor.RED + "MAdmin" + ChatColor.GOLD + "] " + ChatColor.RED;
	public Storage storage;
	public FileWriters fw;
	
	public void onEnable() {
		final FileConfiguration config = this.getConfig();

		config.options().copyDefaults(true);
		saveConfig();
		getConfig().options().copyDefaults(true);
		plugin = this;
		fw = new FileWriters(this);
		database = new DbStuff(this);
		message = new Message(this);
		ac = new Adminchat(this);
		note = new Note(this);
		database.intvar();
		int poging = 0;
		while (database.GetCon() == null) {
			database.OpenConnect();
			if (database.GetCon() == null) {
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
		if (database.GetCon() != null) {

			getLogger().info("Databse connection has succeed");
			database.TableCreate();
			database.closecon();
			database.fw.loadall();

			for (Player player : Bukkit.getOnlinePlayers()) {
				database.updateonlinestatus(player.getUniqueId().toString(),
						true);
			}

		} else {
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
		getServer().getPluginManager()
				.registerEvents(new JoinBlock(this), this);
		getServer().getPluginManager().registerEvents(
				new SummaryListener(this), this);
		getServer().getPluginManager().registerEvents(ac, this);
	}

	public void onDisable() {
		database.onshutdown();
	
	}
	
	protected String getDemoteRank(){
		return getConfig().getString("demoterank");
	}
	
	
	public String getAnnouncer(){
		return announcer;
	}
	

}
