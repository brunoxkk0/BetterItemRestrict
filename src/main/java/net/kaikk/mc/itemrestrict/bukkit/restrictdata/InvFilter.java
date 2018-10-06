package net.kaikk.mc.itemrestrict.bukkit.restrictdata;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;

public class InvFilter {

    private String invType;

    private Map<Material,RestrictedItem> usage = new HashMap<Material, RestrictedItem>();

    public Map<Material,RestrictedItem> getUsageBans(){
        return this.usage;
    }

    public InvFilter(String invType){
        this.invType = invType;
    }

    public String getInvName() {
        return invType;
    }

    public InventoryType getIventoryType() {
        return InventoryType.valueOf(invType);
    }
}
