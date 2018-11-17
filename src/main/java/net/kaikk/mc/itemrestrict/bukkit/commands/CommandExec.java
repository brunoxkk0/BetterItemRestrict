package net.kaikk.mc.itemrestrict.bukkit.commands;

import net.kaikk.mc.itemrestrict.bukkit.BetterItemRestrict;
import net.kaikk.mc.itemrestrict.bukkit.Config;
import net.kaikk.mc.itemrestrict.bukkit.restrictdata.RestrictedItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CommandExec implements CommandExecutor {
	public BetterItemRestrict instance;

	public CommandExec(BetterItemRestrict instance) {
		this.instance = instance;
	}

	public static boolean getInvName(CommandSender sender){

		if ( !(sender instanceof Player)){
			sender.sendMessage("Apenas jogadores físicos podem usar esse comando.");
			return true;
		}

		Player player = (Player) sender;

		sender.sendMessage("§aAbra algum inventário para saber seu nome! (Você tem 3 segundos)");

		new BukkitRunnable() {
			@Override
			public void run() {
				InventoryView inventoryView = player.getOpenInventory();
				if (inventoryView == null){
					sender.sendMessage("§cVocê não possui nenhum inventário aberto no momento....");
					return;
				}

				Inventory inventory = inventoryView.getTopInventory();
				if (inventory == null){
					sender.sendMessage("§cVocê não possui nenhum inventário aberto no momento....");
					return;
				}

				sender.sendMessage("Nome do Inventário: " + inventory.getHolder().toString());
				sender.sendMessage("Nome do Inventário: " + inventory.getHolder().getClass().getName());
				BetterItemRestrict.instance.getLogger().info("(" + player.getName() + ") Inventory View " + inventory.getTitle());
			}
		}.runTaskLater(BetterItemRestrict.instance,60);

		return true;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("betteritemrestrict")) {
			if (!sender.hasPermission("betteritemrestrict.manage")) {
				sender.sendMessage(ChatColor.RED + "Permission denied");
				return false;
			}

			if (args.length==0) {
				sender.sendMessage("Usage: /"+label+" [<reload/addHand/getInvTypeClass>] [exact]");
				return false;
			}

			switch(args[0].toLowerCase()) {
				case "getinvname": {
					return getInvName(sender);
				}
				case "reload": {
					Config.initialize(instance);
					sender.sendMessage("§aLoaded §e" + Config.usage.size() + "§a usage, §e" + Config.ownership.size() + "§a ownership, and §e" + Config.world.size() + "§a world restrictions.");
					sender.sendMessage("§aAnd §e" + Config.invsFilters.size() + "§a invFilters with §e" + Config.bannedItemsOnInvs + "§a bannedUsages");

					sender.sendMessage("Plugin reloaded.");
					break;
				}
				case "addhand": {

					if ( !(sender instanceof Player)){
						sender.sendMessage("Apenas jogadores físicos podem usar esse comando.");
						return true;
					}

					Player player = (Player) sender;

					if (player.getItemInHand() == null){
						sender.sendMessage("§cVocê precisa estar segurando um item!");
						return true;
					}

					ItemStack itemStack = player.getItemInHand();

					String itemBukktiName = itemStack.getType().name();
					int itemDamage = itemStack.getDurability();

					String resultString;
					if (itemDamage > 0 || (args.length > 1 && args[1].equalsIgnoreCase("exact")) ){
						sender.sendMessage("§a§l(+)§r§2 " + itemBukktiName + ":" + itemDamage);
						resultString = itemBukktiName + "," + itemDamage + "|" + itemBukktiName + "|" + "Item banido";
					}else {
						resultString = itemBukktiName + "|" + itemBukktiName + "|" + "Item banido";
						sender.sendMessage("§a§l(+)§r§2 " + itemBukktiName);
					}


					List<String> list = instance.getConfig().getStringList("Ownership");
					if (!list.contains(resultString)){
						list.add(resultString);
						instance.getConfig().set("Ownership",list);
						instance.saveConfig();
					}
					break;
				}
				default:
					sender.sendMessage("Wrong parameter "+args[0]);
					break;
			}
			return true;
		} else if (cmd.getName().equals("banneditems")) {
			if (!sender.hasPermission("betteritemrestrict.list")) {
				sender.sendMessage(ChatColor.RED+"Permission denied");
				return false;
			}

			sender.sendMessage(ChatColor.GOLD+"--- BetterItemRestrict Banned Items List ---");

			StringBuilder sb = new StringBuilder();
			boolean sw = true;
			for (RestrictedItem ri : Config.ownership.values()) {
				if (ri.label != null && !ri.label.isEmpty()) {
					sb.append(sw ? ChatColor.GREEN : ChatColor.DARK_GREEN);
					sw = !sw;
					sb.append(ri.label);
					sb.append(ChatColor.WHITE);
					sb.append(" - ");
					sb.append(ChatColor.RED);
					sb.append(ri.reason);
					sb.append(", ");
				}
			}
			if (sb.length()>1) {
				sb.setLength(sb.length()-2);
			}
			sender.sendMessage(ChatColor.GOLD+"Ownership: "+sb.toString());

			sb.setLength(0);
			sw = true;
			for (RestrictedItem ri : Config.usage.values()) {
				if (ri.label != null && !ri.label.isEmpty()) {
					sb.append(sw ? ChatColor.GREEN : ChatColor.DARK_GREEN);
					sw = !sw;
					sb.append(ri.label);
					sb.append(ChatColor.WHITE);
					sb.append(" - ");
					sb.append(ChatColor.RED);
					sb.append(ri.reason);
					sb.append(", ");
				}
			}
			if (sb.length()>1) {
				sb.setLength(sb.length()-2);
			}
			sender.sendMessage(ChatColor.GOLD+"Use/Place: "+sb.toString());
			return true;
		}
		return false;
	}
}
