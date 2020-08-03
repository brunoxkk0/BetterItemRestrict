package br.com.finalcraft.betteritemrestrict.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import br.com.finalcraft.betteritemrestrict.BetterItemRestrict;
import br.com.finalcraft.betteritemrestrict.restrictdata.InvFilter;
import br.com.finalcraft.betteritemrestrict.restrictdata.RestrictedItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ConfigManager {
	public static Multimap<Material, RestrictedItem> usage = HashMultimap.create();
	public static Multimap<Material,RestrictedItem> ownership = HashMultimap.create();
	public static Multimap<Material,RestrictedItem> world = HashMultimap.create();
	//Inv Filters
	public static List<InvFilter> invsFilters = new ArrayList<InvFilter>();
	public static int bannedItemsOnInvs;

	public static void initialize(JavaPlugin instance){

		FileConfiguration config = instance.getConfig();
		instance.reloadConfig();

		usage.clear();
		ownership.clear();
		world.clear();
		invsFilters.clear();

		// load config
		copyAsset(instance, "config.yml");
		instance.reloadConfig();

		for(String bannedItem : config.getStringList("Ownership")) {
			try {
				RestrictedItem ri = RestrictedItem.deserialize(bannedItem);
				ownership.put(ri.material, ri);
			} catch (IllegalArgumentException e) {
				instance.getLogger().warning(e.getMessage());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		for(String bannedItem : config.getStringList("Usage")) {
			try {
				RestrictedItem ri = RestrictedItem.deserialize(bannedItem);
				if (!ownership.get(ri.material).contains(ri)) {
					usage.put(ri.material, ri);
				}
			} catch (IllegalArgumentException e) {
				instance.getLogger().warning(e.getMessage());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}


		for(String bannedItem : config.getStringList("World")) {
			try {
				RestrictedItem ri = RestrictedItem.deserialize(bannedItem);
				world.put(ri.material, ri);
			} catch (IllegalArgumentException e) {
				instance.getLogger().warning(e.getMessage());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		bannedItemsOnInvs = 0;
		if (BetterItemRestrict.invCheckCanBeDone){
			if (config.contains("InvFilter")){
				for(String aInvName : config.getConfigurationSection("InvFilter").getKeys(false)) {

					String invType = config.getString("InvFilter." + aInvName + ".inventoryClassPath");
					InvFilter anInvFilter = new InvFilter(aInvName,invType);

					for(String bannedItem : config.getStringList("InvFilter." + aInvName + ".BanUsage")) {
						try {
							RestrictedItem ri = RestrictedItem.deserialize(bannedItem);
							anInvFilter.banUsage(ri.material, ri);
							bannedItemsOnInvs++;
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}

					if (anInvFilter.getUsageBans().values().size() == 0){
						BetterItemRestrict.instance.getLogger().info( aInvName+ " has no BannedItems on it, so, it will not be added! Fix your config! Serach for the errors!");
					}else {
						invsFilters.add(anInvFilter);
					}
				}
			}
		}


		instance.getLogger().info("Loaded " + usage.size() + " usage, " + ownership.size() + " ownership, and " + world.size() + " world restrictions.");
		instance.getLogger().info("And " + invsFilters.size() + " invFilters with " + bannedItemsOnInvs + " bannedUsages");
	}

	public static File copyAsset(JavaPlugin instance, String assetName) {
		File file = new File(instance.getDataFolder(), assetName);
		file.getParentFile().mkdirs();
		if (!file.exists()) {
			try {
				Files.copy(getAsset(instance, assetName), file.getAbsoluteFile().toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
			} catch (IOException var4) {
				throw new RuntimeException(var4);
			}
		}

		return file;
	}

	public static InputStream getAsset(JavaPlugin instance, String assetName) {
		return instance.getResource("assets/" + instance.getName().toLowerCase() + "/" + assetName);
	}

}
