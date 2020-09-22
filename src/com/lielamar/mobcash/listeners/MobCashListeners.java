package com.lielamar.mobcash.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import com.lielamar.mobcash.Main;
import com.lielamar.mobcash.utils.PacketHandler;

public class MobCashListeners implements Listener {

	private Main main;
	public MobCashListeners(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onMobDeath(EntityDamageByEntityEvent e) {
		if(!this.main.getMobManager().getValues().containsKey(e.getEntity().getType())) return;
		
		double value = this.main.getMobManager().getValues().get(e.getEntity().getType());
		if(value == 0) return;
		
		if(!(e.getEntity() instanceof LivingEntity)) return;
		
		if(!(e.getDamager() instanceof Player)) return;
		
		if(e.getFinalDamage() == 0) return;
		
		if(e.isCancelled()) return;
		
		if(((LivingEntity)e.getEntity()).getHealth()-e.getDamage() > 0) return;
		
		value = this.main.getMobManager().getCoins(value);
		
		if(!this.main.getMobManager().fancyMoneyGiving || this.main.getMobManager().fancyMoneyGivingItem == null) {
			this.main.getMobManager().econ.depositPlayer((Player)e.getDamager(), value);
			
			if(!this.main.getMobManager().receivedCashMessage.equalsIgnoreCase("none"))
				((Player)e.getDamager()).sendMessage(this.main.getMobManager().receivedCashMessage.replace("%value%", value + "").replace("%type%", this.main.getMobManager().niceName(e.getEntity().getType() + "")));
			
			if(!this.main.getMobManager().receivedCashActionbarMessage.equalsIgnoreCase("none"))
				PacketHandler.getInstance().getNMSHandler().sendActionBar((Player)e.getDamager(), this.main.getMobManager().receivedCashActionbarMessage.replace("%value%", value + "").replace("%type%", this.main.getMobManager().niceName(e.getEntity().getType() + "")));
		} else {
			try {
				e.getDamager().getWorld().dropItemNaturally(e.getEntity().getLocation(), this.main.getMobManager().getFancyItem((Player)e.getDamager(), e.getEntity().getType(), value));
			} catch(Exception ex) {
				((Player)e.getDamager()).sendMessage(ChatColor.RED + "An error accourd and we couldn't give you money for killing the entity. Please contact the server staff.");
				return;
			}
		}
	}
	
	@EventHandler
	public void onPickup(EntityPickupItemEvent e) {
		if(e.getItem() == null) return;
		if(e.getItem().getItemStack().getType() == Material.AIR) return;
		
		if(e.getItem().getItemStack().getType() != this.main.getMobManager().fancyMoneyGivingItem) return;
		
		if(!e.getItem().getItemStack().hasItemMeta()) return;
		if(!e.getItem().getItemStack().getItemMeta().hasDisplayName()) return;
		if(!e.getItem().getItemStack().getItemMeta().getDisplayName().startsWith(ChatColor.YELLOW + "Cash;")) return;
		if(!(e.getEntity() instanceof Player)) return;
		
		Player p = (Player)e.getEntity();
		
		String player = e.getItem().getItemStack().getItemMeta().getDisplayName().split(";")[1];
		EntityType type = EntityType.valueOf(e.getItem().getItemStack().getItemMeta().getDisplayName().split(";")[2]);
		double value = this.main.getMobManager().getCoins(Double.parseDouble(e.getItem().getItemStack().getItemMeta().getDisplayName().split(";")[3]));
		
		if(!p.getName().equalsIgnoreCase(player)) return;
		this.main.getMobManager().econ.depositPlayer(p, value);
		
		if(!this.main.getMobManager().receivedCashMessage.equalsIgnoreCase("none"))
			p.sendMessage(this.main.getMobManager().receivedCashMessage.replace("%value%", value + "").replace("%type%", this.main.getMobManager().niceName(type + "")));
		
		if(!this.main.getMobManager().receivedCashActionbarMessage.equalsIgnoreCase("none"))
			PacketHandler.getInstance().getNMSHandler().sendActionBar(p, this.main.getMobManager().receivedCashActionbarMessage.replace("%value%", value + "").replace("%type%", this.main.getMobManager().niceName(type + "")));
		
		e.setCancelled(true);
		e.getItem().remove();
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		Player p = (Player) e.getPlayer();
		
		for(ItemStack item : e.getInventory().getContents()) {
			if(item == null) return;
			if(item.getType() == Material.AIR) return;
			
			if(item.getType() != this.main.getMobManager().fancyMoneyGivingItem) return;
			
			if(!item.hasItemMeta()) return;
			if(!item.getItemMeta().hasDisplayName()) return;
			if(!item.getItemMeta().getDisplayName().startsWith(ChatColor.YELLOW + "Cash;")) return;

			EntityType type = EntityType.valueOf(item.getItemMeta().getDisplayName().split(";")[2]);
			double value = this.main.getMobManager().getCoins(Double.parseDouble(item.getItemMeta().getDisplayName().split(";")[3]));
			
			for(int i = 0; i < item.getAmount(); i++) {
				this.main.getMobManager().econ.depositPlayer(p, value);
				if(!this.main.getMobManager().receivedCashMessage.equalsIgnoreCase("none"))
					p.sendMessage(this.main.getMobManager().receivedCashMessage.replace("%value%", value + "").replace("%type%", this.main.getMobManager().niceName(type + "")));
				
				if(!this.main.getMobManager().receivedCashActionbarMessage.equalsIgnoreCase("none"))
					PacketHandler.getInstance().getNMSHandler().sendActionBar(p, this.main.getMobManager().receivedCashActionbarMessage.replace("%value%", value + "").replace("%type%", this.main.getMobManager().niceName(type + "")));
				e.setCancelled(true);
			}
			item.setType(Material.AIR);
		}
	}
}
