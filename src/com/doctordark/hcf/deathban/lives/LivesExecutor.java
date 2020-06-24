package com.doctordark.hcf.deathban.lives;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.deathban.lives.argument.LivesCheckArgument;
import com.doctordark.hcf.deathban.lives.argument.LivesCheckDeathbanArgument;
import com.doctordark.hcf.deathban.lives.argument.LivesClearDeathbansArgument;
import com.doctordark.hcf.deathban.lives.argument.LivesGiveArgument;
import com.doctordark.hcf.deathban.lives.argument.LivesReviveArgument;
import com.doctordark.hcf.deathban.lives.argument.LivesSetArgument;
import com.doctordark.hcf.deathban.lives.argument.LivesSetDeathbanTimeArgument;
import com.doctordark.util.command.ArgumentExecutor;
import com.doctordark.util.command.CommandArgument;

public class LivesExecutor extends ArgumentExecutor
{
    public LivesExecutor(final HCF plugin) {
        super("lives");
        this.addArgument((CommandArgument)new LivesCheckArgument(plugin));
        this.addArgument((CommandArgument)new LivesCheckDeathbanArgument(plugin));
        this.addArgument((CommandArgument)new LivesClearDeathbansArgument(plugin));
        this.addArgument((CommandArgument)new LivesGiveArgument(plugin));
        this.addArgument((CommandArgument)new LivesReviveArgument(plugin));
        this.addArgument((CommandArgument)new LivesSetArgument(plugin));
        this.addArgument((CommandArgument)new LivesSetDeathbanTimeArgument());
    }
}
