package br.com.finalcraft.betteritemrestrict.events;

import br.com.finalcraft.evernifecore.version.MCVersion;
import br.com.finalcraft.betteritemrestrict.BetterItemRestrict;
import br.com.finalcraft.betteritemrestrict.restrictdata.RestrictedItem;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class EventListener implements Listener {
	private BetterItemRestrict instance;

	public EventListener(BetterItemRestrict instance) {
		this.instance = instance;
	}

	private ItemStack getPlayersHeldItem(HumanEntity player) {
		return MCVersion.isLegacy() ? player.getItemInHand() : player.getInventory().getItemInMainHand();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer()==null) {
			return;
		}

		if (instance.check(event.getPlayer(), event.getBlock(), event.getItemInHand())) {
			event.setCancelled(true);
			event.setBuild(false);
			instance.inventoryCheck(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof HumanEntity)) {
			return;
		}

		if (instance.check((HumanEntity) event.getDamager(), getPlayersHeldItem((HumanEntity) event.getDamager())) != null) {
			event.setCancelled(true);
			instance.inventoryCheck((HumanEntity) event.getDamager());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getPlayer()==null) {
			return;
		}

		if (instance.check(event.getPlayer(), event.getItem()) != null) {
			event.setCancelled(true);
			instance.inventoryCheck(event.getPlayer());
			return;
		}

		RestrictedItem restrictedItem = null;
		if (event.getAction()!=Action.PHYSICAL) {
			if ((restrictedItem = instance.check(event.getPlayer(), getPlayersHeldItem(event.getPlayer()))) != null) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				if (!restrictedItem.keepOnInteract){
					event.getPlayer().getItemInHand().setType(Material.AIR);
				}
			} else if ( (restrictedItem = instance.check(event.getPlayer(), event.getClickedBlock())) != null ) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				if (!restrictedItem.keepOnInteract){
					event.getClickedBlock().setType(Material.AIR);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCraftItem(CraftItemEvent event) {
		if (instance.ownershipCheck(event.getWhoClicked(), event.getCurrentItem())) {
			event.setResult(Result.DENY);
			instance.inventoryCheck(event.getWhoClicked());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		instance.inventoryCheck((HumanEntity) event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (instance.ownershipCheck(event.getPlayer(), event.getItem().getItemStack())) {
			event.setCancelled(true);
			event.getItem().remove();
			instance.inventoryCheck(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPickupItem(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			if (instance.ownershipCheck(player, event.getItem().getItemStack())) {
				event.setCancelled(true);
				event.getItem().remove();
				instance.inventoryCheck(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		if (instance.ownershipCheck(event.getPlayer(), event.getItemDrop().getItemStack())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					event.getItemDrop().remove();
				}
			}.runTaskLater(instance, 1L);
			instance.inventoryCheck(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getPlayer() instanceof HumanEntity) {
			instance.inventoryCheck((HumanEntity) event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof HumanEntity) {
			instance.inventoryCheck((HumanEntity) event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		if (instance.check(event.getPlayer(), event.getPlayer().getInventory().getItem(event.getNewSlot())) != null) {
			instance.inventoryCheck(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChunkLoad(ChunkLoadEvent event) {
		instance.checkChunk(event.getChunk());
	}

}
