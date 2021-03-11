package com.icanstudios.newsignshop;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
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
		
		if (!setupEconomy() ) {
            System.out.println(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
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
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
        	System.out.println("no vault");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
        	System.out.println("no rsp");
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
