package com.icanstudios.newsignshop;

import java.io.File;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.Economy;

public class SignManager implements Listener {
	private Plugin creator;
	private HashMap<Location, Shop> shops;
	private Economy econ;
	private YamlConfiguration yml;
	private Material destructo = Material.GOLDEN_AXE;
	private File shopFile;
	
	public SignManager(Plugin creator, HashMap<Location, Shop> shops, Economy econ, Material breakableItem) {
		this.shops = shops;
		this.econ = econ;
		this.creator = creator;
		this.destructo = breakableItem;
		econ = Main.getEconomy();
		yml = Main.getYaml();
		shopFile = Main.getShopFile();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent b) {
		Block block = b.getBlock();
		String mat = block.getBlockData().getMaterial().toString();
		System.out.println("new sign");
		if(!shops.containsKey(block.getLocation())) {
			if(mat.length() > 9) {
				if(mat.substring(mat.length() - 9, mat.length()).equals("WALL_SIGN")) {
					Sign sign = (Sign) block.getState();
					ShopType type = ShopType.NONE;
					if(b.getLine(0).indexOf("buy") != -1) {
						type = ShopType.BUY;
					} else if (b.getLine(0).indexOf("sell") != -1) {
						type = ShopType.SELL;
					}
					if(type == ShopType.NONE) {
						return;
					}
					int cost = 0;
					try {
						cost = Integer.parseInt(b.getLine(3));
					} catch(NumberFormatException e) {
						System.out.println("Line 4 not a price.");
					}
					ItemStack trans;
					try {
						trans = b.getPlayer().getInventory().getItemInOffHand();
					} catch(Exception e) {
						System.out.println("Player is trying to sell their hand. Cancelling attempt.");
						return;
					}
					if(trans.getType() == Material.AIR) {
						return;
					}
					Shop newShop = new Shop(block.getLocation(), type, trans, cost);
					shops.put(block.getLocation(), newShop);
					sign.setLine(0, "[" + ChatColor.DARK_GREEN + (type == ShopType.BUY ? "Buy" : "Sell") + ChatColor.BLACK + "]");
					sign.setLine(1, "" + trans.getAmount());
					if(b.getLine(2).equals("")) {
						sign.setLine(2, Utilities.beautifyItemName(trans.getType().toString()));
					} else {
						sign.setLine(2, b.getLine(2));
					}
					sign.setLine(3, "$" + ChatColor.GREEN + cost);
					sign.update();
					Location loc = block.getLocation();
					try {
						yml.set(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ(), newShop.toFormattedString());
						yml.save(shopFile);
					} catch(Exception e) {
						System.out.println(e);
					}
					block.setMetadata("isShop", new FixedMetadataValue(creator, Boolean.valueOf(true)));
					block.setMetadata("owner", new FixedMetadataValue(creator, b.getPlayer().getUniqueId()));
					b.getPlayer().sendMessage(ChatColor.GREEN + "Shop successfully created. Use a gold axe to remove it.");
					b.setCancelled(true);
				}
			}
		} else {
			System.out.println(b.getPlayer().getInventory().getItemInMainHand().getType().toString());
			if(!b.getPlayer().getInventory().getItemInMainHand().getType().toString().equals("GOLDEN_AXE")) {
				b.setCancelled(true);
			} else {
				shops.remove(block.getLocation());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent b) {
		Block block = b.getBlock();
		BlockFace[] facesToCheck = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}; 
		for(BlockFace near : facesToCheck) {
			if(shops.containsKey(block.getRelative(near).getLocation())) {
				b.setCancelled(true);
				return;
			}
		}
		if(shops.containsKey(block.getLocation())) {
			if(b.getPlayer().getInventory().getItemInMainHand().getType().equals(destructo) && b.getPlayer().hasPermission("newsignshop.destroyany")) {
				Location s = block.getLocation();
				shops.remove(s);
				yml.set(s.getWorld().getName() + "," + s.getBlockX() + "," + s.getBlockY() + "," + s.getBlockZ(), null);
				try {
					yml.save(shopFile);
				} catch(Exception e) {
					System.out.println(e);
				}
				b.getPlayer().sendMessage(ChatColor.RED + "Shop successfully destroyed.");
			} else {
				b.setCancelled(true);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockClicked(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			if(block.getMetadata("isShop").size() > 0 && block.getMetadata("isShop").get(0).asBoolean() == true) {
				shops.get(block.getLocation()).attemptPurchase(e.getPlayer());
			}
		}
	}
}
