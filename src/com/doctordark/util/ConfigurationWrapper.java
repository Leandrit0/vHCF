package com.doctordark.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteStreams;

public class ConfigurationWrapper {
	private File file;
	private String name;
	private YamlConfiguration config;
	private JavaPlugin plugin;

	public ConfigurationWrapper(String name, JavaPlugin plugin) {
		this.plugin = plugin;
		this.name = name;
		plugin.getDataFolder().mkdir();
		this.file = new File(plugin.getDataFolder(), name);
		this.saveDefault();
		this.reloadConfig();
	}

	public YamlConfiguration getConfig() {
		return this.config;
	}

	public File getFile(){
		return file;
	}
	public void saveDefault() {
		if (!this.file.exists()) {
			InputStream defConfigStream = this.plugin.getResource(this.name);
			FileOutputStream stream = null;

			try {
				stream = new FileOutputStream(this.file);
				ByteStreams.copy(defConfigStream, stream);
			} catch (IOException var12) {
				var12.printStackTrace();
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException var11) {
						var11.printStackTrace();
					}
				}

			}
		}

	}

	public void reloadConfig() {
		this.config = YamlConfiguration.loadConfiguration(this.file);
		/*
		InputStream defConfigStream = this.plugin.getResource(this.name);
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8));
			this.config.setDefaults(defConfig);
		}
		*/
	}
}
