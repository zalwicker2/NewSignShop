package com.icanstudios.printbreak;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {
	private static HashMap<Location, Shop> shops;
	private static Economy econ;
	private static YamlConfiguration yml;
	private static File shopFile;
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this,  this);
		shops = new HashMap<Location, Shop>();
		
		// File Management
		this.getDataFolder().mkdirs();
		
		econ = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
		System.out.println("Magma's Sign Shop started.");
	}
	
	@Override
	public void onDisable() {
		System.out.println("Magma's SignShop closed.");
	}
	
	public void loadShops() {
		shopFile = new File(this.getDataFolder(), "shops.yml");
		System.out.println(shopFile.getPath());
		if(!shopFile.exists()) {
			try {
				shopFile.createNewFile();
				System.out.println("Created shop file.");
			} catch(Exception e) {
				System.out.println(e);
			}
		} else {
			System.out.println("Shop file found.");
		}
		yml = YamlConfiguration.loadConfiguration(shopFile);
		for(String s : yml.getKeys(true)) {
			String[] strs = s.split(",");
			Location l = new Location(Bukkit.getServer().getWorld(strs[0]), Double.parseDouble(strs[1]), Double.parseDouble(strs[2]), Double.parseDouble(strs[3]));
			shops.put(l, new Shop(yml.getString(s)));
		}
		try {
			yml.save(shopFile);
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public static Economy getEconomy() {
		return econ;
	}
	
	public static YamlConfiguration getYaml() {
		return yml;
	}
	
	public static File getShopFile() {
		return shopFile;
	}
	
	public void Trade(Player plr, Location spot) {
		if(shops.containsKey(spot)) {
			Shop access = shops.get(spot);
			
		}
	}
}
