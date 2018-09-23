package net.kaikk.mc.itemrestrict.bukkit;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InvFilter {

    private String invType;

    private Multimap<Material,RestrictedItem> usage = HashMultimap.create();

    public Multimap<Material,RestrictedItem> getUsageBans(){
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
