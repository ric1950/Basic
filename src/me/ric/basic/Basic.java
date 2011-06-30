package me.ric.basic;

//import java.util.ArrayList;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
//import org.bukkit.block.Block;
//import me.ric.teleplus.AimBlock;
//import me.ric.teleplus.TeleHistory;
//import me.ric.teleplus.TelePermissions;
//import me.ric.teleplus.Teleporter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Basic extends JavaPlugin {
//	private Player player;
	public PermissionHandler Permissions = null;
	public Configuration config;
	public int REFRESH_TIMER;
	
	private final BasicPlayerListener playerListener = new BasicPlayerListener(this);
    private final BasicBlockListener blockListener = new BasicBlockListener(this);
    protected static final Logger log = Logger.getLogger("Minecraft");
    protected BukkitScheduler scheduler;
//    public final HashMap<Player, ArrayList<Block>> basicUsers = new HashMap();
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    public Set<String> testList = new HashSet<String>();
    private Set<Player> tpDelayList = new HashSet<Player>();

    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
//    	scheduler.cancelTasks(this); // not needed - they seem to be cancelled automatically
    	log.info("[" + getDescription().getName() + "] " + getDescription().getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {
    	setupPermissions();
    	setupConfigVariables();

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
//        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( "[" + pdfFile.getName() + "] version [" + pdfFile.getVersion() + "] enabled!" );

        scheduler = getServer().getScheduler();
        
    	scheduler.scheduleSyncRepeatingTask(this, new TestTimerTask(), 10, 20 * REFRESH_TIMER);
    	scheduler.scheduleSyncRepeatingTask(this, new SecondTimerTask(), 10, 20 * (REFRESH_TIMER-13));
    }

	public void setupPermissions() {
    	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
    	
    	if (this.Permissions == null) {
    		if (test != null) {
    			this.getServer().getPluginManager().enablePlugin(test);
    			this.Permissions = ((Permissions) test).getHandler();
    		}
    		else {
    			log.info("[" + getDescription().getName() + "] Permissions not detected.");
    		}
    	}
    }

	private void setupConfigVariables() {
    	config = new Configuration(new File(getDataFolder() , "config.yml"));
    	try {
    		config.load();
    	}
    	catch(Exception ex){
    		//Ignore the errors
    	}
    	REFRESH_TIMER = config.getInt("refresh_delay", 20);
    	config.save();
		
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] split = args;
        String commandName = command.getName().toLowerCase();
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
            if (commandName.equalsIgnoreCase("test")) {
                if (sender instanceof ConsoleCommandSender) {
                        System.out.println("test command was used!");
                        return true;
                }
	            else {
	            	if (!check(sender, "test")) {
	        		player.sendMessage("No permission for this command");
	        		return false; 
	            	}
	            	player.sendMessage("you have permission to use test as a command");
	        		if (!testList.contains(player.getName())) {
	        			testList.add(player.getName());
	            		player.sendMessage("name added to List");
	        		} else {
	        			testList.remove(player.getName());
	            		player.sendMessage("Name removed from List");
	        		}
	            }
            }
            if (commandName.equals("tp")) {
                if (split.length == 3 && isNumber(split[0]) && isNumber(split[1]) && isNumber(split[2])) {
            		if (!tpDelayList.contains(player)) {
	                    World currentWorld = player.getWorld();
	                    Location loc = new Location(currentWorld, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), player.getLocation().getYaw(), player.getLocation().getPitch());
	                    Teleporter tp = new Teleporter(loc);
	                    tp.addTeleportee(player);
	                    tp.teleport();
	            		tpDelayList.add(player);
	                	scheduler.scheduleSyncDelayedTask(this, new RemovePlayerFromTPDelayList(player), 20 * (REFRESH_TIMER*3));
            		}
                }
                else {
                    return false;
                }
                return true;
            }
            else if (commandName.equals("plugin")) {
                if (split.length == 2 && split[0].equalsIgnoreCase("check")) {
                	Plugin test = this.getServer().getPluginManager().getPlugin(split[1]);
            		if (test != null) {
    	            	if (this.isEnabled()) {
    	            		player.sendMessage("Plugin " + split[1] + " is enabled");
//    	                    System.out.println( "Plugin is enabled!" );
    		            	}
    		            	else {
        	            		player.sendMessage("Plugin " + split[1] + " is disabled");
//    		                    System.out.println( "Plugin is disabled!" );
    		            	}
            		}
            		else {
	            		player.sendMessage("Plugin " + split[1] + " is not loaded");
            		}
            		return true;

		        }
                else if (split.length == 2 && split[0].equalsIgnoreCase("disable")) {
                	String pluginName = split[1];
                	Plugin test = this.getServer().getPluginManager().getPlugin(pluginName);
            		if (test != null) {
    	            	if (this.isEnabled()) {
    	            		this.getServer().getPluginManager().disablePlugin(test);
    	            		player.sendMessage("Plugin " + pluginName + " has been disabled");
    		            	}
    		            	else {
        	            		player.sendMessage("Plugin " + pluginName + " is not enabled");
    		            	}
            		}
            		else {
	            		player.sendMessage("Plugin " + pluginName + " is not loaded");
            		}
            		return true;
		        }
                else if (split.length == 2 && split[0].equalsIgnoreCase("load")) {
                	Plugin test = this.getServer().getPluginManager().getPlugin(split[1]);
            		if (test != null) {
    	   //         	if (!this.isEnabled()) {
    	            		File f = new File("plugins/" + split[1]);

    	            		try {
								this.getServer().getPluginManager().loadPlugin(f);
							} catch (InvalidPluginException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvalidDescriptionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (UnknownDependencyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    	            		player.sendMessage("Plugin " + split[1] + " has been loaded and enabled");
//		            	}
//		            	else {
//    	            		player.sendMessage("Plugin " + split[1] + " is already enabled");
//    		            }
            		}
            		else {
	            		player.sendMessage("Plugin " + split[1] + " is not loaded");
            		}
            		return true;
		        }
 
                return false;
            }
	        return false;
//        }
//        return false;
    }

	public boolean check(CommandSender sender, String permNode)
    {
    	if (sender instanceof Player) {
    		if (Permissions == null) {
    			return sender.isOp();
    		}
    		else {
    			Player player = (Player) sender;
    			return Permissions.has(player, permNode);
    		}
    	}
    	else if (sender instanceof ConsoleCommandSender) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }

	protected class TestTimerTask implements Runnable
    {
    	protected String name;
    	
    	public TestTimerTask() {
    		this(null);
    	}
    	
    	public TestTimerTask(String name) {
    		this.name = name;
    	}
    	
    	public void run() {
    		if (name == null) {
    	        System.out.println( "Scheduled task has run - name == null" );
//    			updateTestForAll();
    		} else {
//    			Player p = getServer().getPlayer(name);
//    			updateTestForPlayer(p);
    		}
    	}
    }

    protected class SecondTimerTask implements Runnable
    {
    	public void run() {
    	        System.out.println( "Second task has run" );
    	}
    }

    public class RemovePlayerFromTPDelayList implements Runnable {
    	private Player player;
    	RemovePlayerFromTPDelayList(Player player) {
    		this.player = player;
    	}
    	public void run() {
    		tpDelayList.remove(player);
    	}
    }
    
    public static boolean isNumber(String string) {
        try {
            Double.parseDouble(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

}
