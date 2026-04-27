package com.gamingmesh.jobs.hooks.Nexo;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.nexomc.nexo.api.NexoItems;

public class NexoHook {

    private static final boolean enabled = Bukkit.getPluginManager().isPluginEnabled("Nexo");

    public static boolean isEnabled() {
        return enabled;
    }

    public static ItemStack getItem(String nexoId) {
        if (!enabled)
            return null;
        try {
            var builder = NexoItems.itemFromId(nexoId);
            if (builder == null)
                return null;
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDisplayName(String nexoId) {
        if (!enabled)
            return null;
        try {
            ItemStack item = getItem(nexoId);
            if (item == null)
                return null;
            ItemMeta meta = item.getItemMeta();
            if (meta == null)
                return null;
            if (meta.hasDisplayName())
                return meta.getDisplayName();
            if (meta.hasItemName())
                return meta.getItemName();
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
