package com.icanstudios.newsignshop;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	private static HashMap<Location, Shop> shops;
	private static Economy econ;
	private static YamlConfiguration yml;
	private static FileConfiguration config;
	private static File shopFile;
	
	@Override
	public void onEnable() {
		Logger log = Bukkit.getLogger();
		
		shops = new HashMap<Location, Shop>();
		loadShops();
		
		config = this.getConfig();
		
		System.out.println(new Location(this.getServer().getWorlds().get(0), 0, 0, 0).toString());
		
		Material shopBreaker = Material.GOLDEN_AXE;
		try {
			shopBreaker = Material.valueOf(config.getString("sign_breaker"));
		} catch(NullPointerException e) {
			log.log(Level.WARNING, "Missing sign_breaker line in config! Defaulting to GOLDEN_AXE");
		} catch(IllegalArgumentException e) {
			log.log(Level.WARNING, "sign_breaker is not a valid item! Defaulting to GOLDEN_AXE");
		}
		// File Management
		this.getDataFolder().mkdirs();
		
		SignManager signListener = new SignManager(this, shops, econ, shopBreaker);
		getServer().getPluginManager().registerEvents(signListener,  this);
		
		econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
		
		log.log(Level.INFO, "New Sign Shop started.");
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
			if(l.getWorld().getBlockAt(l).getType().toString().contains("WALL_SIGN")) {
				shops.put(l, new Shop(l, yml.getString(s)));
			}
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
}
