package com.icanstudios.printbreak;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.Economy;

public class Shop {
	ShopType shopType;
	ItemStack trading;
	int cost;
	
	private Economy econ;
	private YamlConfiguration yml;
	private File shopFile;
	
	public Shop(ShopType s, ItemStack selling, int price) {
		shopType = s;
		trading = selling;
		cost = price;
		econ = Main.getEconomy();
		yml = Main.getYaml();
		shopFile = Main.getShopFile();
	}
	public Shop(String s) {
		this(ShopType.valueOf(s.split(",")[0]), new ItemStack(Material.getMaterial(s.split(",")[1]), Integer.parseInt(s.split(",")[2])), Integer.parseInt(s.split(",")[3]));
	}
	public ItemStack getTrading() {
		return trading;
	}
	public int getCost() {
		return cost;
	}
	public int getAmount() {
		return trading.getAmount();
	}
	
	public boolean attemptPurchase(Player plr) {
		OfflinePlayer customer = Utilities.getOfflinePlayer(plr);
		String action = "failed";
		switch(shopType) {
		case NONE:
			break;
		case BUY:
			if(econ.has(customer, cost)) {
				if(plr.getInventory().firstEmpty() == -1) {
					plr.sendMessage(ChatColor.RED + "No inventory space! Transaction cancelled.");
					return false;
				}
				plr.getInventory().addItem(trading);
				econ.withdrawPlayer(customer, cost);
				action = "bought";
			}
			break;
		case SELL:
			if(plr.getInventory().containsAtLeast(trading, cost)) {
				plr.getInventory().removeItem(trading);
				econ.depositPlayer(customer, cost);
				action = "sold";
			}
			break;
		}
		if(action.equals("failed")) {
			plr.sendMessage(ChatColor.RED + "Transaction failed.");
		} else {
			plr.sendMessage(ChatColor.GOLD + "You have " + action + " " + cost + " " + Utilities.beautifyItemName(trading.getType().toString()) + "for $" + cost + ". " + ChatColor.YELLOW + "Balance: " + econ.getBalance(plr));
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Trading " + trading.getAmount() + trading.toString() + " at $" + cost;
	}
	public ShopType getType() {
		return shopType;
	}
	public String toFormattedString() {
		return shopType + "," + trading.getType().toString() + "," + trading.getAmount() + "," + cost;
	}
}
