package com.doctordark.hcf.eventgame.koth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.koth.argument.KothHelpArgument;
import com.doctordark.hcf.eventgame.koth.argument.KothNextArgument;
import com.doctordark.hcf.eventgame.koth.argument.KothScheduleArgument;
import com.doctordark.hcf.eventgame.koth.argument.KothSetCapDelayArgument;
import com.doctordark.util.command.ArgumentExecutor;
import com.doctordark.util.command.CommandArgument;

public class KothExecutor extends ArgumentExecutor
{
    private final KothScheduleArgument kothScheduleArgument;
    
    public KothExecutor(final HCF plugin) {
        super("koth");
        this.addArgument((CommandArgument)new KothHelpArgument(this));
        this.addArgument((CommandArgument)new KothNextArgument(plugin));
        this.addArgument((CommandArgument)(this.kothScheduleArgument = new KothScheduleArgument(plugin)));
        this.addArgument((CommandArgument)new KothSetCapDelayArgument(plugin));
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            this.kothScheduleArgument.onCommand(sender, command, label, args);
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }
}
