/**
 * 
 */
package com.technicassassin.RandomTeleport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
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
		
		lowerboundx = config.getDouble("x.lowerbound");
		higherboundx = config.getDouble("x.higherbound");
		lowerboundz = config.getDouble("z.lowerbound");
		higherboundz = config.getDouble("z.higherbound");
		
	}
	
	public void onDisable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		sender.sendMessage("ONCOMMAND DEBUG");
		
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
	
	public boolean teleport(Player p) {
		
		p.sendMessage("TELEPORT DEBUG");
		
		Location[] locs = randLoc();
		
		while(true){
		
			while(
				locs[0].getBlock().isLiquid() ||
				locs[1].getBlock().isLiquid() ||
				locs[2].getBlock().isLiquid() ||
				locs[1].getBlock().getType() == Material.FIRE ||
				locs[0].getBlock().getType() == Material.FIRE ||
				locs[2].getBlock().getType() == Material.FIRE
			){
				locs = randLoc();
			}
			
			while(
				locs[1].getBlock().getType() == Material.AIR ||
				locs[0].getBlock().getType() != Material.AIR ||
				locs[2].getBlock().getType() != Material.AIR
			){
				
				if(locs[1].getBlockY() > 96){
					
					locs = randLoc();
					continue;
				}
				
				locs[0].setY(locs[1].getY() + 2);
				
				updateLocs(locs);
				
			}
			
			if(
				!locs[0].getBlock().isLiquid() &&
				!locs[1].getBlock().isLiquid() &&
				!locs[2].getBlock().isLiquid() &&
				locs[1].getBlock().getType() != Material.FIRE &&
				locs[0].getBlock().getType() != Material.FIRE &&
				locs[2].getBlock().getType() != Material.FIRE &&
				locs[1].getBlock().getType() != Material.AIR &&
				locs[0].getBlock().getType() == Material.AIR &&
				locs[2].getBlock().getType() == Material.AIR
			){
				
				return p.teleport(locs[0]);
			}
		}
	}
	
	public Location[] randLoc() {
		
		/*  
		 * 0 = lower body level
		 * 1 = below feet
		 * 2 = head level
		 */
		
		this.getLogger().log(Level.WARNING, "RANDLOC DEBUG");
		
		Location[] locs = new Location[3];
		
		locs[0] = new Location(
				w,
				Math.round((float)(lowerboundx + (higherboundx - lowerboundx) * Math.random())),
				63,
				Math.round((float)(lowerboundz + (higherboundz - lowerboundz) * Math.random()))
		);
		
		locs[1] = new Location(
				w,
				locs[0].getX(),
				locs[0].getY() - 1,
				locs[0].getZ()
		);
		
		locs[2] = new Location(
				w,
				locs[0].getX(),
				locs[0].getY() + 1,
				locs[0].getZ()
		);
		
		return locs;
	}
	
	public Location[] updateLocs(Location[] locs){
		
		locs[1] = locs[0];
		locs[2] = locs[0];
		
		locs[1].setY(locs[0].getY() - 1);
		locs[2].setY(locs[0].getY() + 1);
		
		return locs;
	}
}
