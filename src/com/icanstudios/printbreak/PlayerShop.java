package com.icanstudios.printbreak;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerShop extends Shop {
	
	private Inventory inv;
	
	public PlayerShop(ShopType s, ItemStack selling, int price, Inventory items) {
		super(s, selling, price);
		this.inv = items;
	}
	
	@Override
	public boolean attemptPurchase(Player p) {
		if(inv.contains(super.trading)) {
			boolean purchase = super.attemptPurchase(p);
			if(purchase) {
				inv.remove(super.trading);
			}
			return true;
		}
		return false;
	}
}
