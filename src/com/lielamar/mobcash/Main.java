package com.lielamar.mobcash;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private MobCashManager mobManager;
	
	@Override
	public void onEnable() {
		this.mobManager = new MobCashManager(this);
	}
	
	public MobCashManager getMobManager() { return this.mobManager; }
}
