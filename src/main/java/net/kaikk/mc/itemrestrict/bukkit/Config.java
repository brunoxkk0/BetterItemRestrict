package net.kaikk.mc.itemrestrict.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Config {
	private JavaPlugin instance;
	public static Multimap<Material,RestrictedItem> usage = HashMultimap.create();
	public static Multimap<Material,RestrictedItem> ownership = HashMultimap.create();
	public static Multimap<Material,RestrictedItem> world = HashMultimap.create();
	public static Multimap<Material,RestrictedItem> invs = HashMultimap.create();

	Config(JavaPlugin instance) {
		this.instance = instance;

		File iresFolder = new File(instance.getDataFolder().getParentFile(), "ItemRestrict");
		File iresConfigFile = new File(iresFolder, "config.yml");
		File configFile = new File(instance.getDataFolder(), "config.yml");
		FileConfiguration config = instance.getConfig();

		if (!configFile.exists() && iresConfigFile.exists()) {

			usage.clear();
			ownership.clear();
			world.clear();
			invs.clear();

			// load config
			copyAsset(instance, "config.yml");
			instance.reloadConfig();

			for(String s : config.getStringList("Ownership")) {
				try {
					RestrictedItem ri = RestrictedItem.deserialize(s);
					ownership.put(ri.material, ri);
				} catch (IllegalArgumentException e) {
					instance.getLogger().warning(e.getMessage());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

			for(String s : config.getStringList("Usage")) {
				try {
					RestrictedItem ri = RestrictedItem.deserialize(s);
					if (!ownership.get(ri.material).contains(ri)) {
						usage.put(ri.material, ri);
					}
				} catch (IllegalArgumentException e) {
					instance.getLogger().warning(e.getMessage());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}


			for(String s : config.getStringList("World")) {
				try {
					RestrictedItem ri = RestrictedItem.deserialize(s);
					world.put(ri.material, ri);
				} catch (IllegalArgumentException e) {
					instance.getLogger().warning(e.getMessage());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}


		}

		instance.getLogger().info("Loaded "+usage.size()+" usage, "+ownership.size()+" ownership, and "+world.size()+" world restrictions.");
	}


	private void iresImportMaterials(String importName, Multimap<Material,RestrictedItem> map, List<String> list) {
		for(String s : list) {
			try {
				RestrictedItem ri = RestrictedItem.fromItemRestrict(s);
				map.put(ri.material, ri);
			} catch (Throwable e) {
				instance.getLogger().warning("Error during ItemRestrict config file import "+importName+": "+s+" - Error: "+e.getMessage());
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
				instance.getLogger().warning("Error during ItemRestrict config file import Messages.labels: "+key+" - Error: "+e.getMessage());
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
				instance.getLogger().warning("Error during ItemRestrict config file import Messages.reasons: "+key+" - Error: "+e.getMessage());
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
