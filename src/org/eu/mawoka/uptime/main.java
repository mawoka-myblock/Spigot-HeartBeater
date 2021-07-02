package org.eu.mawoka.uptime;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.Bukkit;

public class main extends JavaPlugin {

    private File customConfigFile;
    private FileConfiguration customConfig;


    @Override
    public void onEnable() {
        System.out.println("Succesfully started HeartBeater by https://mawoka.eu.org");
        createCustomConfig();
        System.out.println(customConfig.getBoolean("Hello"));
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                HttpClient client = HttpClient.newHttpClient();
                var request = HttpRequest.newBuilder(
                        URI.create(Objects.requireNonNull(customConfig.getString("Url"))))
                        .header("accept", "*/*")
                        .build();
                HttpResponse<Void> response = null;
                try {
                    response = client.send(request, HttpResponse.BodyHandlers.discarding());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (response.statusCode() != customConfig.getInt("ExpectedHttpCode")) {
                    System.out.println("The request didn't return an 200!");
                    System.out.println("The status-code was: " + response.statusCode());
                }


            }

        }, 0L, customConfig.getInt("Time") * 60 *20L);
    }

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "HeartBeat.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("HeartBeat.yml", false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onDisable() {

    }



}
