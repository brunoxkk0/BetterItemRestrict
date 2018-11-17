package net.kaikk.mc.itemrestrict.bukkit.restrictdata;

import net.kaikk.mc.itemrestrict.bukkit.BetterItemRestrict;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;

public class InvFilter {

    private String invName;
    private String invType;

    private Map<Material,RestrictedItem> usage = new HashMap<Material, RestrictedItem>();

    public Map<Material,RestrictedItem> getUsageBans(){
        return this.usage;
    }

    public void banUsage(Material material, RestrictedItem restrictedItem){
        this.usage.put(material,restrictedItem);
    }

    public InvFilter(String invName, String invType){
        this.invName = invName;
        this.invType = invType;
    }

    public String getInvTypeClass() {
        return invType;
    }

    public String getInvName() {
        return invName;
    }
}
