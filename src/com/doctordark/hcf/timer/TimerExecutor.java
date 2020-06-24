package com.doctordark.hcf.timer;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.timer.argument.TimerCheckArgument;
import com.doctordark.hcf.timer.argument.TimerSetArgument;
import com.doctordark.util.command.ArgumentExecutor;
import com.doctordark.util.command.CommandArgument;

public class TimerExecutor extends ArgumentExecutor
{
    public TimerExecutor(final HCF plugin) {
        super("timer");
        this.addArgument((CommandArgument)new TimerCheckArgument(plugin));
        this.addArgument((CommandArgument)new TimerSetArgument(plugin));
    }
}
