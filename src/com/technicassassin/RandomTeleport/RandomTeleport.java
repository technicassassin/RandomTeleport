package com.technicassassin.RandomTeleport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Dan
 *
 */
public class RandomTeleport extends JavaPlugin {
	
	public double lowerboundx;
	public double higherboundx;
	public double lowerboundz;
	public double higherboundz;
	
	public File configfile;
	public YamlConfiguration config;
	
	public volatile World w; 
	
	public void onEnable() {
		
		//Get location range from the config.
		
		config = new YamlConfiguration();
		try {
			configfile = new File(this.getDataFolder().getAbsolutePath() + "/config.yml");
			config.load(configfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			return;
		}
		
		lowerboundx = config.getDouble("lowerboundx");
		higherboundx = config.getDouble("higherboundx");
		lowerboundz = config.getDouble("lowerboundz");
		higherboundz = config.getDouble("higherboundz");
		
	}
	
	public void onDisable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("rtp")) {
			
			sender.sendMessage("RTP DEBUG");
			
			w = ((Player)sender).getWorld();
			
			if(teleport((Player)sender)) {
				
				sender.sendMessage("Teleportation complete.");
				
			} else {
				
				sender.sendMessage("Teleportation failed.");
			}
		}
		
		return true;
	}
	
	public boolean teleport (Player p) {
		
		boolean invalid = true;
		Location loc;
		int breakout = 0;
		int count;
		
		while (invalid && breakout < 10) {
			
			breakout++;
			loc = randLoc();
			count = 0;
			
			while (count < 2){
				
				if (loc.getY() > 96) break;
				
				if (loc.getBlock().isEmpty()) {
					
					count++;
					
				} else count = 0;
				
				loc.setY(loc.getY() + 1);
			}
			
			if (count == 2) {
				
				invalid = false;
				
				loc.setY(loc.getY() + 1);
				return p.teleport(loc);
			}
		}
		
		return false;
	}
	
	public Location randLoc(){
		
		return new Location(
				w,
				(lowerboundx + (higherboundx - lowerboundx) * Math.random()),
				63,
				(lowerboundz + (higherboundz - lowerboundz) * Math.random())
		);
	}
}
