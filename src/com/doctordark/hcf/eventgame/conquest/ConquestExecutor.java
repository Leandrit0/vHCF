package com.doctordark.hcf.eventgame.conquest;

import com.doctordark.hcf.HCF;
import com.doctordark.util.command.ArgumentExecutor;
import com.doctordark.util.command.CommandArgument;

public class ConquestExecutor extends ArgumentExecutor
{
    public ConquestExecutor(final HCF plugin) {
        super("conquest");
        this.addArgument((CommandArgument)new ConquestSetpointsArgument(plugin));
    }
}
