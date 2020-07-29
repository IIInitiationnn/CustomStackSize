package customstacksize;

import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
    private Logger log;



    @java.lang.Override
    public void onEnable() {
        this.log.info("Starting CustomStackSize.");
    }

    @java.lang.Override
    public void onDisable() {
        this.log.info("Stopping CustomStackSize.");
    }
}
