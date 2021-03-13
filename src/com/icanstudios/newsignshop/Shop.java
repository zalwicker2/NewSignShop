package com.icanstudios.newsignshop;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.Economy;

public class Shop {
	ShopType shopType;
	ItemStack trading;
	int cost;
	WallSign sign;
	private Location loc;
	private Inventory stock;
	private boolean infinite;
	
	private Economy econ;
	
	public Shop(Location l, ShopType s, ItemStack selling, int price) {
		this.loc = l;
		this.sign = (WallSign) l.getWorld().getBlockAt(l).getBlockData();
		this.stock = getAttachedChest();
		this.infinite = stock == null;
		shopType = s;
		trading = selling;
		cost = price;
		econ = Main.getEconomy();
	}
	public Shop(Location l, String s) {
		this(l, ShopType.valueOf(s.split(",")[0]), new ItemStack(Material.getMaterial(s.split(",")[1]), Integer.parseInt(s.split(",")[2])), Integer.parseInt(s.split(",")[3]));
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
		getAttachedChest();
		OfflinePlayer customer = Utilities.getOfflinePlayer(plr);
		boolean success = false;
		String action = "failed";
		switch(shopType) {
		case NONE:
			break;
		case BUY:
			success = buyItem(customer);
			action = "bought";
			break;
		case SELL:
			success = sellItem(customer);
			action = "sold";
			break;
		}
		if(!success) {
			plr.sendMessage(ChatColor.RED + "Transaction failed.");
		} else {
			plr.sendMessage(ChatColor.GOLD + "You have " + action + " " + trading.getAmount() + " " + Utilities.beautifyItemName(trading.getType().toString()) + "for $" + cost + ". " + ChatColor.YELLOW + "Balance: " + econ.getBalance(plr));
		}
		return false;
	}
	
	private boolean buyItem(OfflinePlayer customer) {
		Player plr = customer.getPlayer();
		if(econ.has(customer, cost)) {
			if(!infinite) {
				if(stock.containsAtLeast(trading, trading.getAmount())) {
					stock.removeItem(trading);
				} else {
					plr.sendMessage(ChatColor.RED + "No more stock! Transaction cancelled.");
					return false;
				}
			}
			if(plr.getInventory().firstEmpty() == -1) {
				plr.sendMessage(ChatColor.RED + "No inventory space! Transaction cancelled.");
				return false;
			}
			plr.getInventory().addItem(trading);
			econ.withdrawPlayer(customer, cost);
			return true;
		}
		return false;
	}
	
	private boolean sellItem(OfflinePlayer customer) {
		Player plr = customer.getPlayer();
		if(plr.getInventory().containsAtLeast(trading, trading.getAmount())) {
			if(!infinite) {
				if(stock.firstEmpty() == -1) {
					plr.sendMessage(ChatColor.RED + "Stock is full! Transaction cancelled.");
					return false;
				} else {
					stock.setItem(stock.firstEmpty(), trading);
				}
			}
			plr.getInventory().removeItem(trading);
			
			econ.depositPlayer(customer, cost);
			return true;
		}
		return false;
	}
	
	private Inventory getAttachedChest() {
		Block b = loc.getWorld().getBlockAt(loc).getRelative(sign.getFacing().getOppositeFace());
		if(b.getType() == Material.CHEST) {
			Chest c = (Chest) b.getState();
			return c.getInventory();
		}
		return null;
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
