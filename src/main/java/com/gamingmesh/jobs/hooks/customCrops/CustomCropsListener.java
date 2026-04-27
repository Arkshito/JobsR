package com.gamingmesh.jobs.hooks.customCrops;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.CustomCropsInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;

import com.flowpowered.nbt.Tag;

import net.momirealms.customcrops.api.core.block.BreakReason;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import net.momirealms.customcrops.api.event.PotFillEvent;

public class CustomCropsListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCropPlant(CropPlantEvent event) {
        Player player = event.getPlayer();

        if (!Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
            return;

        if (!JobsPaymentListener.payIfCreative(player))
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getWorld().getName()))
            return;

        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle()
            && !player.getVehicle().getType().toString().contains("BOAT"))
            return;

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player),
            new CustomCropsInfo(event.cropConfig().id(), ActionType.CUSTOMCROPSPLANT));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCropBreak(CropBreakEvent event) {
        Entity breaker = event.entityBreaker();
        if (!(breaker instanceof Player))
            return;

        Player player = (Player) breaker;

        BreakReason reason = event.reason();
        if (reason != BreakReason.BREAK && reason != BreakReason.ACTION)
            return;

        if (!Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
            return;

        if (!JobsPaymentListener.payIfCreative(player))
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getWorld().getName()))
            return;

        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle()
            && !player.getVehicle().getType().toString().contains("BOAT"))
            return;

        CropConfig config = event.cropConfig();

        // Solo recompensar si el cultivo está completamente maduro
        if (getGrowthPoints(event.blockState()) < config.maxPoints())
            return;

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player),
            new CustomCropsInfo(config.id(), ActionType.CUSTOMCROPSHARVEST));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotFill(PotFillEvent event) {
        Player player = event.getPlayer();

        if (!Jobs.getGCManager().canPerformActionInWorld(player.getWorld()))
            return;

        if (!JobsPaymentListener.payIfCreative(player))
            return;

        if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getWorld().getName()))
            return;

        if (Jobs.getGCManager().disablePaymentIfRiding && player.isInsideVehicle()
            && !player.getVehicle().getType().toString().contains("BOAT"))
            return;

        PotConfig potConfig = event.potConfig();

        // Solo recompensar si el pote necesitaba agua
        if (getPotWaterLevel(event.blockState()) >= potConfig.storage())
            return;

        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player),
            new CustomCropsInfo(potConfig.id(), ActionType.CUSTOMCROPSWATER));
    }

    private int getGrowthPoints(CustomCropsBlockState state) {
        try {
            Tag<?> tag = state.get("point");
            if (tag == null)
                return 0;
            Object value = tag.getValue();
            return value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private int getPotWaterLevel(CustomCropsBlockState state) {
        try {
            Tag<?> tag = state.get("water");
            if (tag == null)
                return 0;
            Object value = tag.getValue();
            return value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }
}
