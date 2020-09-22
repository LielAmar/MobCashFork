package com.lielamar.mobcash.utils.versions;

import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.lielamar.mobcash.utils.PacketVersion;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;

public class NMS_v1_8_R1 implements PacketVersion {
	
	/**
	 * Send an action bar to the player using nms
	 * 
	 * @param p - Player we want to send the actionbar to
	 * @param message - message
	 */
	public void sendActionBar(Player p, String message) {
		IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(icbc, (byte)2);
		
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
}
