package mcore.tollenaar.stephen.MistCore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import code.husky.mysql.MySQL;

public class DbStuff {
	private Connection con = null;
	private String mysqlpass;
	private String mysqluser;
	private String mysqldb;
	private String mysqlpot;
	private String mysqlhost;
	private MCore plugin;
	private MySQL MySQl;
	private int scheduler;
	public FileWriters fw;
	private int timer = 0;

	public void closecon() {
		int timeout = 10;

		int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
				new Runnable() {
					public void run() {
						PreparedStatement pst;
						try {
							pst = con
									.prepareStatement("SELECT id FROM `Mist_Users` LIMIT 1;");
							pst.execute();
						} catch (SQLException ex) {
							opencon();
							intvar();
							OpenConnect();
							closecon();
						}
					}
				}, 0L, timeout * 20L);
		scheduler = id;
	}

	public void opencon() {
		Bukkit.getScheduler().cancelTask(scheduler);
	}

	public void saveto(String username, String moderatorname, String reason,
			int type, int x, int y, int z, long tijd, long date, String world,
			String groepen) {
		if (con != null) {
			PreparedStatement pst = null;
			opencon();
			try {
				Player victim = Bukkit.getPlayer(username);
				UUID playeruuid;
				if (victim == null) {
					@SuppressWarnings("deprecation")
					OfflinePlayer off = Bukkit.getOfflinePlayer(username);
					playeruuid = off.getUniqueId();
				} else {
					playeruuid = victim.getUniqueId();
				}
				pst = con
						.prepareStatement("INSERT INTO `Mist_Users` (`id`, `username`, `moderatorname`, `reason`, `type`, `x`, `y`, `z`, `tijd`, `datum`, `wereld`, `groepen`)"
								+ "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				pst.setString(1, playeruuid.toString());
				pst.setString(2, moderatorname);
				pst.setString(3, reason);
				pst.setInt(4, type);
				pst.setInt(5, x);
				pst.setInt(6, y);
				pst.setInt(7, z);
				pst.setLong(8, tijd);
				pst.setLong(9, date);
				pst.setString(10, world);
				pst.setString(11, groepen);
				pst.execute();
			} catch (SQLException e) {
				this.plugin.getLogger().severe(e.getMessage());
				plugin.getLogger().info(
						"There was an error during the savings of the data to the database: "
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
			closecon();
		} else {
			fw.addline(username, moderatorname, reason, type, x, y, z, tijd,
					date, world, groepen);
		}
	}

	public void saveuuid(String playeruuid, String playername) {
		String insert = "INSERT INTO Mist_playeruuid (" + "`useruuid`,"
				+ "`username`, `isonline`) VALUES (?,?,?);";

		String update = "UPDATE Mist_playeruuid SET"
				+ "`username`=? WHERE `useruuid`=?;";

		String test = "SELECT * FROM Mist_playeruuid WHERE `useruuid`=?";
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(test);
			pst.setString(1, playeruuid);
			ResultSet rs = pst.executeQuery();
			if (rs.next() == false) {
				pst.close();
				pst = con.prepareStatement(insert);
				pst.setString(1, playeruuid);
				pst.setString(2, playername);
				pst.setInt(3, 0);
				pst.execute();
			} else if (!rs.getString("username").equals(playername)) {
				pst.close();
				pst = con.prepareStatement(update);
				pst.setString(1, playername);
				pst.setString(2, playeruuid);

				pst.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
	}

	public void updateonlinestatus(String playeruuid, boolean isonline) {
		String setonline = "UPDATE Mist_playeruuid SET "
				+ "`isonline`=? WHERE `useruuid`=?;";

		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(setonline);
			if (isonline) {
				pst.setInt(1, 1);
			} else {
				pst.setInt(1, 0);
			}
			pst.setString(2, playeruuid);
			
			pst.execute();
			
		}catch (SQLException e) {
			e.printStackTrace();
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

	}
	
	public void onshutdown(){
		String select = "UPDATE Mist_playeruuid SET `isonline`=0 WHERE `isonline`=1;";
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(select);
			pst.execute();
		}catch (SQLException e) {
			e.printStackTrace();
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
		
	}

	public void TableCreate() {
		Statement statement;
		try {
			statement = con.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS Mist_Users ("
					+ "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
					+ "username VARCHAR(45) NOT NULL, "
					+ "moderatorname VARCHAR(45) NOT NULL, "
					+ "reason VARCHAR(45) NOT NULL, "
					+ "type INTEGER NOT NULL, " + "x INTEGER NOT NULL, "
					+ "y INTEGER NOT NULL, " + "z INTEGER NOT NULL, "
					+ "tijd INTEGER NOT NULL, " + "datum INTEGER NOT NULL, "
					+ "wereld VARCHAR(45) NOT NULL, "
					+ "groepen VARCHAR(45) NOT NULL);");

			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS Mist_playeruuid ("
							+ "useruuid VARCHAR(50) PRIMARY KEY,"
							+ "username VARCHAR(50) NOT NULL,"
							+ "isonline TINYINT(1) NOT NULL);");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void intvar() {
		mysqlpass = plugin.getConfig().getString("mysqlpass");
		mysqluser = plugin.getConfig().getString("mysqluser");
		mysqldb = plugin.getConfig().getString("mysqldb");
		mysqlpot = plugin.getConfig().getString("mysqlport");
		mysqlhost = plugin.getConfig().getString("mysqlhost");
		MySQl = new MySQL(plugin, mysqlhost, mysqlpot, mysqldb, mysqluser,
				mysqlpass);
	}

	public DbStuff(MCore instance) {
		this.plugin = instance;
		this.fw = instance.fw;
	}

	public void setcon(Connection connect) {
		if (connect != null) {
			con = connect;
		}
	}
	
	public Connection GetCon(){
		return con;
	}
	public void checkcon(){
		try {
			if(con.isClosed()){
				opencon();
				intvar();
				OpenConnect();
			}
		} catch (SQLException | NullPointerException e) {
			opencon();
			intvar();
			OpenConnect();
		}
	}
	
	public void OpenConnect(){
		setcon(MySQl.openConnection());
	}
	
	public void specialclock() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				intvar();
				con = MySQl.openConnection();

				if (con != null) {
					Bukkit.getScheduler().cancelTasks(plugin);
					fw.loadall();
					TableCreate();
					closecon();
					fw.loadall();
					if (!Bukkit.getPluginManager().getPlugin("MistsOfYsir")
							.isEnabled()) {
						Bukkit.getPluginManager().enablePlugin(
								Bukkit.getPluginManager().getPlugin(
										"MistsOfYsir"));
					}
				} else {
					timer++;
					if (timer == 30) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
								"countdown");
						return;
					}
					con = null;
					MySQl = null;
				}
			}
		}, 60 * 2 * 20L, 60 * 2 * 20L);
	}

	public void cancelspeical(int id) {
		Bukkit.getScheduler().cancelTask(id);
	}

}
