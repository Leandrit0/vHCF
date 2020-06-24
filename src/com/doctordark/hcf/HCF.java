package com.doctordark.hcf;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.doctordark.hcf.combatlog.CombatLogListener;
import com.doctordark.hcf.combatlog.CustomEntityRegistration;
import com.doctordark.hcf.command.AngleCommand;
import com.doctordark.hcf.command.CannonCommand;
import com.doctordark.hcf.command.ChatCommand;
import com.doctordark.hcf.command.CraftCommand;
import com.doctordark.hcf.command.CrowbarCommand;
import com.doctordark.hcf.command.EndPlayersCommand;
import com.doctordark.hcf.command.GoppleCommand;
import com.doctordark.hcf.command.HCFCommand;
import com.doctordark.hcf.command.HelpCommand;
import com.doctordark.hcf.command.HelpOpCommand;
import com.doctordark.hcf.command.LocationCommand;
import com.doctordark.hcf.command.LogoutCommand;
import com.doctordark.hcf.command.MapKitCommand;
import com.doctordark.hcf.command.NetherPlayersCommand;
import com.doctordark.hcf.command.PvpTimerCommand;
import com.doctordark.hcf.command.RandomCommand;
import com.doctordark.hcf.command.RefundCommand;
import com.doctordark.hcf.command.SOTWCommand;
import com.doctordark.hcf.command.ServerTimeCommand;
import com.doctordark.hcf.command.SetBorderCommand;
import com.doctordark.hcf.command.SetCommand;
import com.doctordark.hcf.command.SpawnCommand;
import com.doctordark.hcf.command.TLCommand;
import com.doctordark.hcf.command.ToggleCapzoneCommand;
import com.doctordark.hcf.command.ToggleLightningCommand;
import com.doctordark.hcf.command.staff.ModeratorMode;
import com.doctordark.hcf.deathban.Deathban;
import com.doctordark.hcf.deathban.DeathbanListener;
import com.doctordark.hcf.deathban.DeathbanManager;
import com.doctordark.hcf.deathban.FlatFileDeathbanManager;
import com.doctordark.hcf.deathban.lives.LivesExecutor;
import com.doctordark.hcf.economy.EconomyCommand;
import com.doctordark.hcf.economy.EconomyManager;
import com.doctordark.hcf.economy.FlatFileEconomyManager;
import com.doctordark.hcf.economy.PayCommand;
import com.doctordark.hcf.economy.ShopSignListener;
import com.doctordark.hcf.eventgame.CaptureZone;
import com.doctordark.hcf.eventgame.EventExecutor;
import com.doctordark.hcf.eventgame.EventScheduler;
import com.doctordark.hcf.eventgame.conquest.ConquestExecutor;
import com.doctordark.hcf.eventgame.eotw.EotwCommand;
import com.doctordark.hcf.eventgame.eotw.EotwHandler;
import com.doctordark.hcf.eventgame.eotw.EotwListener;
import com.doctordark.hcf.eventgame.faction.CapturableFaction;
import com.doctordark.hcf.eventgame.faction.ConquestFaction;
import com.doctordark.hcf.eventgame.faction.KothFaction;
import com.doctordark.hcf.eventgame.koth.KothExecutor;
import com.doctordark.hcf.faction.FactionExecutor;
import com.doctordark.hcf.faction.FactionManager;
import com.doctordark.hcf.faction.FactionMember;
import com.doctordark.hcf.faction.FlatFileFactionManager;
import com.doctordark.hcf.faction.claim.Claim;
import com.doctordark.hcf.faction.claim.ClaimHandler;
import com.doctordark.hcf.faction.claim.ClaimWandListener;
import com.doctordark.hcf.faction.claim.Subclaim;
import com.doctordark.hcf.faction.type.ClaimableFaction;
import com.doctordark.hcf.faction.type.EndPortalFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.hcf.faction.type.RoadFaction;
import com.doctordark.hcf.faction.type.SpawnFaction;
import com.doctordark.hcf.listener.AutoSmeltOreListener;
import com.doctordark.hcf.listener.BookDeenchantListener;
import com.doctordark.hcf.listener.BorderListener;
import com.doctordark.hcf.listener.BottledExpListener;
import com.doctordark.hcf.listener.ChatListener;
import com.doctordark.hcf.listener.CoreListener;
import com.doctordark.hcf.listener.CrowbarListener;
import com.doctordark.hcf.listener.DeathListener;
import com.doctordark.hcf.listener.DeathMessageListener;
import com.doctordark.hcf.listener.DeathSignListener;
import com.doctordark.hcf.listener.EndListener;
import com.doctordark.hcf.listener.EntityLimitListener;
import com.doctordark.hcf.listener.EventSignListener;
import com.doctordark.hcf.listener.ExpListener;
import com.doctordark.hcf.listener.ExpMultiplierListener;
import com.doctordark.hcf.listener.FactionListener;
import com.doctordark.hcf.listener.FoundDiamondsListener;
import com.doctordark.hcf.listener.FurnaceSmeltSpeederListener;
import com.doctordark.hcf.listener.ItemStatTrackingListener;
import com.doctordark.hcf.listener.KitMapListener;
import com.doctordark.hcf.listener.PortalListener;
import com.doctordark.hcf.listener.ProtectionListener;
import com.doctordark.hcf.listener.SignSubclaimListener;
import com.doctordark.hcf.listener.SkullListener;
import com.doctordark.hcf.listener.WorldListener;
import com.doctordark.hcf.listener.fixes.BeaconStrengthFixListener;
import com.doctordark.hcf.listener.fixes.BlockHitFixListener;
import com.doctordark.hcf.listener.fixes.BlockJumpGlitchFixListener;
import com.doctordark.hcf.listener.fixes.BoatGlitchFixListener;
import com.doctordark.hcf.listener.fixes.EnchantLimitListener;
import com.doctordark.hcf.listener.fixes.EnderChestRemovalListener;
import com.doctordark.hcf.listener.fixes.HungerFixListener;
import com.doctordark.hcf.listener.fixes.InfinityArrowFixListener;
import com.doctordark.hcf.listener.fixes.PearlGlitchListener;
import com.doctordark.hcf.listener.fixes.PhaseListener;
import com.doctordark.hcf.listener.fixes.PotionLimitListener;
import com.doctordark.hcf.listener.fixes.ServerSecurityListener;
import com.doctordark.hcf.listener.fixes.VoidGlitchFixListener;
import com.doctordark.hcf.pvpclass.PvpClassManager;
import com.doctordark.hcf.pvpclass.archer.ArcherClass;
import com.doctordark.hcf.scoreboard.Assemble;
import com.doctordark.hcf.scoreboard.AssembleStyle;
import com.doctordark.hcf.scoreboard.adapter.Adapter;
import com.doctordark.hcf.scoreboard.adapter.TagAdapter;
import com.doctordark.hcf.scoreboard.nametag.Nametag;
import com.doctordark.hcf.timer.TimerExecutor;
import com.doctordark.hcf.timer.TimerManager;
import com.doctordark.hcf.user.FactionUser;
import com.doctordark.hcf.user.UserManager;
import com.doctordark.hcf.visualise.ProtocolLibHook;
import com.doctordark.hcf.visualise.VisualiseHandler;
import com.doctordark.hcf.visualise.WallBorderListener;
import com.doctordark.util.SignHandler;
import com.doctordark.util.itemdb.ItemDb;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lombok.Getter;

public class HCF extends JavaPlugin
{
    private static final long MINUTE;
    private static final long HOUR;
   @Getter private static HCF plugin;
   @Getter public EventScheduler eventScheduler;
   @Getter private Random random;
   @Getter private WorldEditPlugin worldEdit;
   @Getter  private FoundDiamondsListener foundDiamondsListener;
   @Getter  private ClaimHandler claimHandler;
  

   @Getter	private DeathbanManager deathbanManager;
   @Getter  private EconomyManager economyManager;
   @Getter    private EotwHandler eotwHandler;
   @Getter   private FactionManager factionManager;
   @Getter   private PvpClassManager pvpClassManager;
   @Getter   private TimerManager timerManager;
   @Getter   private UserManager userManager;
   @Getter   private VisualiseHandler visualiseHandler;
   @Getter   private ItemDb itemDb;
   @Getter    private SignHandler signHandler;
   

	private File config;
    public File settingsFile;
    @Getter public FileConfiguration settings = null;
      public void ReloadSettings(){
        if (settingsFile == null){
            settingsFile = new File(getDataFolder(),"timers.yml");
        }
        InputStream defaultStream = this.getResource("timers.yml");

        settings = YamlConfiguration.loadConfiguration(settingsFile);
        if (defaultStream != null){
            YamlConfiguration defaultData = YamlConfiguration.loadConfiguration(defaultStream);
             settings.setDefaults(defaultData);
        }
    }

    public FileConfiguration getTimers(){
        if (settings == null){
            this.ReloadSettings();
        }
        return settings;
    }

   public HCF() {
        this.random = new Random();
    }
    
    
    public static String getRemaining(final long millis, final boolean milliseconds) {
        return getRemaining(millis, milliseconds, true);
    }
    
    public static String getRemaining(final long duration, final boolean milliseconds, final boolean trail) {
        if (milliseconds && duration < HCF.MINUTE) {
            return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(duration * 0.001) + 's';
        }
        return DurationFormatUtils.formatDuration(duration, ((duration >= HCF.HOUR) ? "HH:" : "") + "mm:ss");
    }
    
    public void onEnable() {
    	 HCF.plugin = this;	
    	
    	 config = new File(getDataFolder(),File.separator+"config.yml");
    	 
    	ReloadSettings();
         File sf = new File(getDataFolder(), "timers.yml");
         if (!sf.exists()) {
             ReloadSettings();
             saveConfig();
             getTimers().options().copyDefaults(true);
             saveDefaultConfig();
         }
    	 if (!config.exists()){
    		   this.saveDefaultConfig();
    	       this.getConfig().options().copyDefaults(true);
    	       this.saveConfig();
    	}
       
       /** if (!new AdvancedLicense(this.settings.getString("LICENCE"), "https://vailink99.000webhostapp.com/verify.php", this).register()) {
            return;
        }*/
    	 CustomEntityRegistration.registerCustomEntities();
        ProtocolLibHook.hook(this);
       

    	Assemble assemble = new Assemble(this, new Adapter());   	
    	assemble.setTicks(2);   	
    	assemble.setAssembleStyle(AssembleStyle.KOHI);
    	new Nametag(this, new TagAdapter());
    
        final Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
        this.worldEdit = ((wep instanceof WorldEditPlugin && wep.isEnabled()) ? ((WorldEditPlugin)wep) : null);
        this.registerConfiguration();
        this.registerCommands();
        this.registerManagers();
        this.registerListeners();

        Cooldowns.createCooldown("Assassin_item_cooldown");
        Cooldowns.createCooldown("Archer_item_cooldown");
        new BukkitRunnable() {
            public void run() {
                HCF.this.saveData();
            }
        }.runTaskTimerAsynchronously((Plugin)HCF.plugin, TimeUnit.MINUTES.toMillis(20L), TimeUnit.MINUTES.toMillis(20L));
    }
   
    
    public void saveData() {
        this.deathbanManager.saveDeathbanData();
        this.economyManager.saveEconomyData();
        this.factionManager.saveFactionData();
        this.timerManager.saveTimerData();
        this.userManager.saveUserData();
        
    }
    
    public void onDisable() {
        CustomEntityRegistration.unregisterCustomEntities();
        CombatLogListener.removeCombatLoggers();
        this.pvpClassManager.onDisable();
       
        this.foundDiamondsListener.saveConfig();
        this.saveData();
        HCF.plugin = null;
    }
    
    private void registerConfiguration() {
        ConfigurationSerialization.registerClass(CaptureZone.class);
        ConfigurationSerialization.registerClass(Deathban.class);
        ConfigurationSerialization.registerClass(Claim.class);
        ConfigurationSerialization.registerClass(Subclaim.class);
        ConfigurationSerialization.registerClass(Deathban.class);
        ConfigurationSerialization.registerClass(FactionUser.class);
        ConfigurationSerialization.registerClass(ClaimableFaction.class);
        ConfigurationSerialization.registerClass(ConquestFaction.class);
        ConfigurationSerialization.registerClass(CapturableFaction.class);
        ConfigurationSerialization.registerClass(KothFaction.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.class);
        ConfigurationSerialization.registerClass(Faction.class);
        ConfigurationSerialization.registerClass(FactionMember.class);
        ConfigurationSerialization.registerClass(PlayerFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.class);
        ConfigurationSerialization.registerClass(SpawnFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
    }
    
    private void registerListeners() {
        final PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new ModeratorMode(), this);
        manager.registerEvents((Listener)new ArcherClass(this), (Plugin)this);
        manager.registerEvents((Listener)new AutoSmeltOreListener(), (Plugin)this);
        manager.registerEvents((Listener)new BlockHitFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new BlockJumpGlitchFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new BoatGlitchFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new BookDeenchantListener(), (Plugin)this);
        manager.registerEvents((Listener)new BorderListener(), (Plugin)this);
        manager.registerEvents((Listener)new BottledExpListener(), (Plugin)this);
        manager.registerEvents((Listener)new ChatListener(this), (Plugin)this);
        manager.registerEvents((Listener)new ClaimWandListener(this), (Plugin)this);
        manager.registerEvents((Listener)new CombatLogListener(this), (Plugin)this);
        manager.registerEvents((Listener)new CoreListener(this), (Plugin)this);
        manager.registerEvents((Listener)new CrowbarListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathMessageListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathSignListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathbanListener(this), (Plugin)this);
        manager.registerEvents((Listener)new EnchantLimitListener(), (Plugin)this);
        manager.registerEvents((Listener)new EnderChestRemovalListener(), (Plugin)this);
        manager.registerEvents((Listener)new EntityLimitListener(), (Plugin)this);
        manager.registerEvents((Listener)new FlatFileFactionManager(this), (Plugin)this);
        manager.registerEvents((Listener)new EndListener(), (Plugin)this);
        manager.registerEvents((Listener)new EotwListener(this), (Plugin)this);
        manager.registerEvents((Listener)new EventSignListener(), (Plugin)this);
        manager.registerEvents((Listener)new ExpMultiplierListener(), (Plugin)this);
        manager.registerEvents((Listener)new FactionListener(this), (Plugin)this);
        manager.registerEvents((Listener)(this.foundDiamondsListener = new FoundDiamondsListener(this)), (Plugin)this);
        manager.registerEvents((Listener)new FurnaceSmeltSpeederListener(), (Plugin)this);
        manager.registerEvents((Listener)new InfinityArrowFixListener(), (Plugin)this);
      
        manager.registerEvents((Listener)new ItemStatTrackingListener(), (Plugin)this);
        manager.registerEvents((Listener)new KitMapListener(this), (Plugin)this);
        manager.registerEvents((Listener)new ServerSecurityListener(), (Plugin)this);
        manager.registerEvents((Listener)new PhaseListener(), (Plugin)this);
        manager.registerEvents((Listener)new HungerFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new PearlGlitchListener(this), (Plugin)this);
        manager.registerEvents((Listener)new PortalListener(this), (Plugin)this);
        manager.registerEvents((Listener)new PotionLimitListener(), (Plugin)this);
        manager.registerEvents((Listener)new ProtectionListener(this), (Plugin)this);
        manager.registerEvents((Listener)new SignSubclaimListener(this), (Plugin)this);
        manager.registerEvents((Listener)new ShopSignListener(this), (Plugin)this);
        manager.registerEvents((Listener)new SkullListener(), (Plugin)this);
        manager.registerEvents((Listener)new BeaconStrengthFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new VoidGlitchFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new WallBorderListener(this), (Plugin)this);
        manager.registerEvents((Listener)new WorldListener(this), (Plugin)this);
        manager.registerEvents((Listener)new ExpListener(), (Plugin)this);
    }
    
    private void registerCommands() {
    	this.getCommand("craft").setExecutor(new CraftCommand());
    	this.getCommand("mod").setExecutor(new ModeratorMode());
    	this.getCommand("tl").setExecutor(new TLCommand());
    	this.getCommand("helpop").setExecutor(new HelpOpCommand());
    	this.getCommand("set").setExecutor(new SetCommand());
    	this.getCommand("chat").setExecutor(new ChatCommand());
    	this.getCommand("hcf").setExecutor(new HCFCommand());
    	this.getCommand("endplayers").setExecutor(new EndPlayersCommand());
    	this.getCommand("netherplayers").setExecutor(new NetherPlayersCommand());
        this.getCommand("sotw").setExecutor((CommandExecutor)new SOTWCommand());
        this.getCommand("random").setExecutor((CommandExecutor)new RandomCommand(this));
        this.getCommand("angle").setExecutor((CommandExecutor)new AngleCommand());
        this.getCommand("conquest").setExecutor((CommandExecutor)new ConquestExecutor(this));
        this.getCommand("crowbar").setExecutor((CommandExecutor)new CrowbarCommand());
        this.getCommand("economy").setExecutor((CommandExecutor)new EconomyCommand(this));
        this.getCommand("eotw").setExecutor((CommandExecutor)new EotwCommand(this));
        this.getCommand("event").setExecutor((CommandExecutor)new EventExecutor(this));
        this.getCommand("help").setExecutor((CommandExecutor)new HelpCommand());
        this.getCommand("faction").setExecutor((CommandExecutor)new FactionExecutor(this));
        this.getCommand("gopple").setExecutor((CommandExecutor)new GoppleCommand(this));
        this.getCommand("koth").setExecutor((CommandExecutor)new KothExecutor(this));
        this.getCommand("lives").setExecutor((CommandExecutor)new LivesExecutor(this));
        this.getCommand("location").setExecutor((CommandExecutor)new LocationCommand(this));
        this.getCommand("logout").setExecutor((CommandExecutor)new LogoutCommand(this));
        this.getCommand("mapkit").setExecutor((CommandExecutor)new MapKitCommand(this));
        this.getCommand("pay").setExecutor((CommandExecutor)new PayCommand(this));
        this.getCommand("pvptimer").setExecutor((CommandExecutor)new PvpTimerCommand(this));
        this.getCommand("refund").setExecutor((CommandExecutor)new RefundCommand());
        this.getCommand("servertime").setExecutor((CommandExecutor)new ServerTimeCommand());
        this.getCommand("spawn").setExecutor((CommandExecutor)new SpawnCommand(this));
        this.getCommand("cannon").setExecutor((CommandExecutor)new CannonCommand(this));
        this.getCommand("timer").setExecutor((CommandExecutor)new TimerExecutor(this));
        this.getCommand("togglecapzone").setExecutor((CommandExecutor)new ToggleCapzoneCommand(this));
        this.getCommand("togglelightning").setExecutor((CommandExecutor)new ToggleLightningCommand(this));
      
        final Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>)this.getDescription().getCommands();
        for (final Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            final PluginCommand command = this.getCommand((String)entry.getKey());
            command.setPermission("hcf.command." + entry.getKey());
            command.setPermissionMessage(ChatColor.RED + "You do not have permission for this command.");
        }
      
    }
    
    private void registerManagers() {
        this.claimHandler = new ClaimHandler(this);
        this.deathbanManager = new FlatFileDeathbanManager(this);
        this.economyManager = new FlatFileEconomyManager(this);
        this.eotwHandler = new EotwHandler(this);
        this.eventScheduler = new EventScheduler(this);
        this.factionManager = new FlatFileFactionManager(this);
        this.pvpClassManager = new PvpClassManager(this);
        this.timerManager = new TimerManager(this);
        this.userManager = new UserManager(this);
        this.visualiseHandler = new VisualiseHandler();
        this.getCommand("setborder").setExecutor((CommandExecutor)new SetBorderCommand());   
    }
    

    
    static {
        MINUTE = TimeUnit.MINUTES.toMillis(1L);
        HOUR = TimeUnit.HOURS.toMillis(1L);
    }
    

}
