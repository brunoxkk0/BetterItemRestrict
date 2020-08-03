package br.com.finalcraft.betteritemrestrict.events;

import br.com.finalcraft.betteritemrestrict.config.ConfigManager;
import br.com.finalcraft.evernifecore.nms.util.NMSUtils;
import br.com.finalcraft.evernifecore.version.MCVersion;
import br.com.finalcraft.betteritemrestrict.BetterItemRestrict;
import br.com.finalcraft.betteritemrestrict.restrictdata.InvFilter;
import br.com.finalcraft.betteritemrestrict.restrictdata.RestrictedItem;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
	private BetterItemRestrict instance;

	private final Sound SOUND_ANVIL_BREAK;

	public InventoryListener(BetterItemRestrict instance) {
		this.instance = instance;
		if (MCVersion.isHigherEquals(MCVersion.v1_8_R1)){
            SOUND_ANVIL_BREAK = Sound.BLOCK_ANVIL_BREAK;
        }else {
		    SOUND_ANVIL_BREAK = Sound.valueOf("ANVIL_BREAK");
        }
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

        String invTypeClass =  NMSUtils.get().getOpenInventoryName(player);

        InvFilter invFilter =  null;
        for (InvFilter anInvFilter : ConfigManager.invsFilters){
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
        player.playSound(player.getLocation(), SOUND_ANVIL_BREAK, 1F, 1F);
        player.sendMessage("   §c[Você não pode usar esse item nessa interface!]");
        BetterItemRestrict.instance.notify(player,restrictedItem);
        player.closeInventory();
        instance.inventoryCheck(player);
    }

}
