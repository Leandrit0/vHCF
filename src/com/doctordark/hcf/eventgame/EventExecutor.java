package com.doctordark.hcf.eventgame;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.argument.EventCancelArgument;
import com.doctordark.hcf.eventgame.argument.EventCreateArgument;
import com.doctordark.hcf.eventgame.argument.EventDeleteArgument;
import com.doctordark.hcf.eventgame.argument.EventRenameArgument;
import com.doctordark.hcf.eventgame.argument.EventSetAreaArgument;
import com.doctordark.hcf.eventgame.argument.EventSetCapzoneArgument;
import com.doctordark.hcf.eventgame.argument.EventStartArgument;
import com.doctordark.hcf.eventgame.argument.EventUptimeArgument;
import com.doctordark.util.command.ArgumentExecutor;
import com.doctordark.util.command.CommandArgument;

public class EventExecutor extends ArgumentExecutor
{
    public EventExecutor(final HCF plugin) {
        super("event");
        this.addArgument((CommandArgument)new EventCancelArgument(plugin));
        this.addArgument((CommandArgument)new EventCreateArgument(plugin));
        this.addArgument((CommandArgument)new EventDeleteArgument(plugin));
        this.addArgument((CommandArgument)new EventRenameArgument(plugin));
        this.addArgument((CommandArgument)new EventSetAreaArgument(plugin));
        this.addArgument((CommandArgument)new EventSetCapzoneArgument(plugin));
        this.addArgument((CommandArgument)new EventStartArgument(plugin));
        this.addArgument((CommandArgument)new EventUptimeArgument(plugin));
    }
}
