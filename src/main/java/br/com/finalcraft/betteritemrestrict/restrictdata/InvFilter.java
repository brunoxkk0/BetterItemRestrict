package br.com.finalcraft.betteritemrestrict.restrictdata;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

@Deprecated //No need for this anymore, fixing the mod itself with mixin is better!
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
