package com.example.exampleplugin;

import io.papermc.lib.PaperLib;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        getLogger().info("ExamplePlugin has been enabled!");
    }

}
