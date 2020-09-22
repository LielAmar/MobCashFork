package com.lielamar.mobcash.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.lielamar.mobcash.Main;

public class MobCash implements CommandExecutor {

	private Main main;
	
	public MobCash(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String cmdLabel, String[] args) {
		if(!cs.hasPermission("mobcash.reload")) {
			cs.sendMessage(this.main.getMobManager().noPermissionsMessage);
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("mobcash")) {	
			if(args.length == 0) {
				cs.sendMessage(this.main.getMobManager().wrongUsageMessage);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload")) {
				this.main.reloadConfig();
				this.main.getMobManager().loadValues(this.main.getConfig());;
				cs.sendMessage(this.main.getMobManager().reloadConfigMessage);
				return true;
			}
		}
		return true;
	}
	
}
