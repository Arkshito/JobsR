package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class payouts implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            CMIMessages.sendMessage(sender, LC.info_Ingame);
            return null;
        }

        if (args.length < 1)
            return false;

        Player player = (Player) sender;

        Job job = Jobs.getJob(args[0]);
        if (job == null) {
            Language.sendMessage(sender, "general.error.job");
            return true;
        }

        if (Jobs.getGCManager().hideJobsInfoWithoutPermission && !Jobs.getCommandManager().hasJobPermission(player, job)) {
            CMIMessages.sendMessage(player, LC.info_NoPermission);
            return true;
        }

        plugin.getGUIManager().openJobsGUI(player, job);
        return true;
    }
}
