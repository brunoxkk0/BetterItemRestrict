package net.kaikk.mc.itemrestrict.bukkit.events;

import modfixng.nms.utils.NMSUtilsAccess;
import net.kaikk.mc.itemrestrict.bukkit.BetterItemRestrict;
import net.kaikk.mc.itemrestrict.bukkit.Config;
import net.kaikk.mc.itemrestrict.bukkit.restrictdata.InvFilter;
import net.kaikk.mc.itemrestrict.bukkit.restrictdata.RestrictedItem;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
	private BetterItemRestrict instance;

	public InventoryListener(BetterItemRestrict instance) {
		this.instance = instance;
	}

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event){

        if (event.getWhoClicked() == null || !(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null){
            return;
        }

        String invTypeClass =  NMSUtilsAccess.getNMSUtils().getOpenInventoryName(player);

        InvFilter invFilter =  null;
        for (InvFilter anInvFilter : Config.invsFilters){
            if (invTypeClass.equals(anInvFilter.getInvTypeClass())){
                invFilter = anInvFilter;
                break;
            }
        }

        //If it is not a registered inv, return;
        if (invFilter == null){
            return;
        }

        RestrictedItem restrictedItem = invFilter.getUsageBans().getOrDefault(itemStack.getType(),null);
        //If null, then, the item is not banned
        if (restrictedItem == null){
            return;
        }

        //If different, then, the item is not banned
        if (restrictedItem.dv != null && restrictedItem.dv != itemStack.getDurability()){
            return;
        }

        event.setCancelled(true);
        player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1F, 1F);
        player.sendMessage("   §c[Você não pode usar esse item nessa interface!]");
        BetterItemRestrict.instance.notify(player,restrictedItem);
        player.closeInventory();

        instance.inventoryCheck(player);
    }

}
