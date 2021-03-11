package com.icanstudios.newsignshop;

import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

public class Utilities {
	public static OfflinePlayer getOfflinePlayer(Player p) {
		return p.getServer().getOfflinePlayer(p.getUniqueId());
	}
	public static String beautifyItemName(String s) {
        String[] words = s.split("_");
        String toReturn = "";
        for(String word : words) {
            toReturn+=(word.substring(0, 1) + word.toLowerCase().substring(1) + " ");
        }
        return toReturn;
    }
}
