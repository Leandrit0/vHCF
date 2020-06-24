package com.doctordark.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config extends YamlConfiguration
{
    private final String fileName;
    private final JavaPlugin plugin;
    
    public Config(final JavaPlugin plugin, final String fileName) {
        this(plugin, fileName, ".yml");
    }
    
    public Config(final JavaPlugin plugin, final String fileName, final String fileExtension) {
        this.plugin = plugin;
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        this.createFile();
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public JavaPlugin getPlugin() {
        return this.plugin;
    }
    
    private void createFile() {
        final File folder = this.plugin.getDataFolder();
        try {
            final File file = new File(folder, this.fileName);
            if (!file.exists()) {
                if (this.plugin.getResource(this.fileName) != null) {
                    this.plugin.saveResource(this.fileName, false);
                }
                else {
                    this.save(file);
                }
            }
            else {
                this.load(file);
                this.save(file);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void save() {
        final File folder = this.plugin.getDataFolder();
        try {
            this.save(new File(folder, this.fileName));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Config)) {
            return false;
        }
        final Config config = (Config)o;
        Label_0054: {
            if (this.fileName != null) {
                if (this.fileName.equals(config.fileName)) {
                    break Label_0054;
                }
            }
            else if (config.fileName == null) {
                break Label_0054;
            }
            return false;
        }
        if (this.plugin != null) {
            return this.plugin.equals((Object)config.plugin);
        }
        return config.plugin == null;
    }
    
    public int hashCode() {
        int result = (this.fileName != null) ? this.fileName.hashCode() : 0;
        result = 31 * result + ((this.plugin != null) ? this.plugin.hashCode() : 0);
        return result;
    }
    public static List<String> replace(List<String> text, String oldChar, String newChar) {
        List < String > messages = new ArrayList < String > ();
        for (String string: text) {
            messages.add(string.replaceAll(oldChar, newChar));
        }
        return messages;
    }

}
