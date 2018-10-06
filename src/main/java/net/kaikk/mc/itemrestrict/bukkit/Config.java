package net.kaikk.mc.itemrestrict.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import net.kaikk.mc.itemrestrict.bukkit.restrictdata.InvFilter;
import net.kaikk.mc.itemrestrict.bukkit.restrictdata.RestrictedItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Config {
	public static Multimap<Material, RestrictedItem> usage = HashMultimap.create();
	public static Multimap<Material,RestrictedItem> ownership = HashMultimap.create();
	public static Multimap<Material,RestrictedItem> world = HashMultimap.create();


	//Inv Filters
	public static Map<String, InvFilter> invsFilters = new HashMap<String, InvFilter>();

	public static void initialize(JavaPlugin instance){

		FileConfiguration config = instance.getConfig();

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

		int bannedItemsOnInvs = 0;
		for(String aInvName : config.getStringList("InvFilter")) {

			InvFilter anInvFilter = new InvFilter(aInvName);
			if (anInvFilter.getIventoryType() == null){
				BetterItemRestrict.instance.getLogger().info("There is no InvType called \"" + aInvName + "\"... So, it's items will not be loaded on config!" );
				continue;
			}
			invsFilters.put(aInvName,anInvFilter);

			for(String bannedItem : config.getStringList("InvFilter." + aInvName + ".Usage")) {
				try {
					RestrictedItem ri = RestrictedItem.deserialize(bannedItem);
					anInvFilter.getUsageBans().put(ri.material, ri);
					bannedItemsOnInvs++;
				} catch (IllegalArgumentException e) {
					instance.getLogger().warning(e.getMessage());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}

		instance.getLogger().info("Loaded " + usage.size() + " usage, " + ownership.size() + " ownership, and " + world.size() + " world restrictions.");
		instance.getLogger().info("And " + invsFilters.size() + " invFilters with " + bannedItemsOnInvs + " bannedUsages");
	}

	private void iresImportMaterials(String importName, Multimap<Material,RestrictedItem> map, List<String> list) {
		for(String s : list) {
			try {
				RestrictedItem ri = RestrictedItem.fromItemRestrict(s);
				map.put(ri.material, ri);
			} catch (Throwable e) {
				BetterItemRestrict.instance.getLogger().warning("Error during ItemRestrict config file import "+importName+": "+s+" - Error: "+e.getMessage());
			}
		}
	}

	private void iresProcessMessages(FileConfiguration iresConfig) {
		for (String key : iresConfig.getConfigurationSection("Messages.labels").getKeys(false)) {
			try {
				String label = iresConfig.getString("Messages.labels."+key);
				RestrictedItem tempri = RestrictedItem.fromItemRestrict(key);
				Collection<RestrictedItem> ric = usage.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.label = label;
						}
					}
				}

				ric = ownership.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.label = label;
						}
					}
				}

				ric = world.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.label = label;
						}
					}
				}
			} catch (Throwable e) {
				BetterItemRestrict.instance.getLogger().warning("Error during ItemRestrict config file import Messages.labels: "+key+" - Error: "+e.getMessage());
			}
		}

		for (String key : iresConfig.getConfigurationSection("Messages.reasons").getKeys(false)) {
			try {
				String reason = iresConfig.getString("Messages.reasons."+key);
				RestrictedItem tempri = RestrictedItem.fromItemRestrict(key);
				Collection<RestrictedItem> ric = usage.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.reason = reason;
						}
					}
				}

				ric = ownership.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.reason = reason;
						}
					}
				}

				ric = world.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.reason = reason;
						}
					}
				}
			} catch (Throwable e) {
				BetterItemRestrict.instance.getLogger().warning("Error during ItemRestrict config file import Messages.reasons: "+key+" - Error: "+e.getMessage());
			}
		}
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
