package com.lielamar.mobcash;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class MobCashManager {

	private Main main;
	
	private Random rnd = null;
	
	private Map<EntityType, Double> values;
	
	public Economy econ = null;
	
	public String noPermissionsMessage;
	public String wrongUsageMessage;
	public String reloadConfigMessage;
	
	public String receivedCashMessage;
	public String receivedCashActionbarMessage;
	
	public boolean fancyMoneyGiving = false;
	public Material fancyMoneyGivingItem = null;
	public boolean randomCoins = false;
	
	public MobCashManager(Main main) {
		this.main = main;
		
	    rnd = new Random();
		RegisteredServiceProvider<Economy> economyProvider = this.main.getServer().getServicesManager().getRegistration(Economy.class);
	    if (economyProvider != null)
	        econ = economyProvider.getProvider();
	    
	    loadValues(this.main.getConfig());
	}
	
	/**
	 * Loads all values from the config
	 * 
	 * @param config      Config to load data from
	 */
	public void loadValues(FileConfiguration config) {
		this.values = new HashMap<EntityType, Double>();
		
		if(config.contains("NoPermissionsMessage")) 
			this.noPermissionsMessage = ChatColor.translateAlternateColorCodes('&', config.getString("NoPermissionsMessage"));

		if(config.contains("WrongUsageMessage")) 
			this.wrongUsageMessage = ChatColor.translateAlternateColorCodes('&', config.getString("WrongUsageMessage"));
		
		if(config.contains("ReloadConfigMessage")) 
			this.reloadConfigMessage = ChatColor.translateAlternateColorCodes('&', config.getString("ReloadConfigMessage"));

		
		if(config.contains("ReceivedCashMessage")) 
			this.receivedCashMessage = ChatColor.translateAlternateColorCodes('&', config.getString("ReceivedCashMessage"));

		if(config.contains("HotbarMessage")) 
			this.receivedCashActionbarMessage = ChatColor.translateAlternateColorCodes('&', config.getString("ReceivedCashActionbarMessage"));
		
		
		if(config.contains("FancyMoneyGiving")) 
			this.fancyMoneyGiving = config.getBoolean("FancyMoneyGiving");

		if(config.contains("FancyMoneyGivingItem")) 
			this.fancyMoneyGivingItem = Material.valueOf(config.getString("FancyMoneyGivingItem"));

		if(config.contains("RandomCoins"))
			randomCoins = config.getBoolean("RandomCoins");
		
		
		boolean changed = false;
		for(EntityType type : EntityType.values()) {
			if(!type.isAlive()) continue;
			
			if(!config.contains("Mobs." + type)) {
				config.set("Mobs." + type, 10.0);
				values.put(type, 10.0);
				changed = true;
			} else
				values.put(type, config.getDouble("Mobs." + type));
		}
		
		if(changed) this.main.saveConfig();;
	}
	
	/**
	 * @param coins    Coins to calculate
	 * @return         Amount of coins to give
	 */
	public double getCoins(double coins) {
		if(!randomCoins) return coins;
		
		return ((double) rnd.nextInt((int)coins + 0 + 1));
	}

	/**
	 * @param p        Player to assing the item to
	 * @param type     Type of the entity
	 * @param value    Value of the entity
	 * @return         Custom Item
	 */
	public ItemStack getFancyItem(Player p, EntityType type, double value) {
		if(fancyMoneyGivingItem == null) return null;
		ItemStack item = new ItemStack(fancyMoneyGivingItem, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Cash;" + p.getName() + ";" + type + ";" + value);
		item.setItemMeta(meta);
		
		return item;
	}
	
	/**
	 * @param name        Name of the mob
	 * @return            A nicer name of the mob
	 */
	public String niceName(String name) {
		String newName = "";
		
		String tmp = new String(name);
		
		for(String s : tmp.split("_")) {
			s = s.toLowerCase();
			s = (s.charAt(0) + "").toUpperCase() + s.substring(1, s.length());
			newName += s + " ";
		}
		
		if(newName.charAt(newName.length()-1) == ' ')
			newName = newName.substring(0, newName.length()-1);
		
		return newName;
	}
	
	public Map<EntityType, Double> getValues() {
		return values;
	}
}
