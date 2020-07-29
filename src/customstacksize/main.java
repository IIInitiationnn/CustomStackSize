package customstacksize;

import net.minecraft.server.v1_16_R1.Item;
import net.minecraft.server.v1_16_R1.Items;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.logging.Logger;

public class main extends JavaPlugin implements Listener {
    private Logger log;

    // TODO: IS THERE ANY OTHER PROPERTY EG IN INVENTORY WE CAN CHANGE TO MINIMISE THE FIDDLYNESS?

    // STARTUP
    @java.lang.Override
    public void onEnable() {
        this.log = this.getLogger();
        this.log.info("Initialising CustomStackSize and validating stack sizes.");
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("customstacksize").setExecutor(new commands(this));
        this.getCommand("customstacksize").setTabCompleter(new tabcomplete());
    }

    // RELOAD
    public void reload() {
        // TODO wipe all pre-set stuff eg. enderpearls = 64, then it's taken out of the config and reloaded
        // all other values will change as necessary but enderpearls stays at 64. need to revert everything to default
        // first. method here, revertStackSizes();
        this.reloadConfig();
        this.setupStackSizes();
    }

    // STOP
    @java.lang.Override
    public void onDisable() {
        this.log.info("CustomStackSize has been disabled.");
    }

    // SET UP STACK SIZES
    private void setupStackSizes() {
        Iterator it = this.getConfig().getKeys(true).iterator();
        while (it.hasNext()) {
            String itemName = (String)it.next();
            String stackSize = this.getConfig().getString(itemName);
            Item itemObject = null;

            // Verify that the item is a valid item.
            try {
                // Check through all MC items to verify if item is valid, then assign to itemField.
                Field itemField = Items.class.getField(itemName);
                // Assign item with corresponding name to itemObject
                itemObject = (Item)itemField.get((Object)null);
            } catch (NoSuchFieldException error1) {
                this.log.warning("\"" + itemName + "\" is not a valid item. Skipping.");
                continue;
            } catch (IllegalAccessException error2) {
                error2.printStackTrace();
                continue;
            }

            try {
                Integer stackSizeInt = Integer.parseInt(stackSize);
                if (stackSizeInt < 1 || stackSizeInt > 64) {
                    this.log.warning("\"" + stackSize + "\" is not a valid stack size (must be an integer between 1 and 64 inclusive). Skipping.");
                    continue;
                }
            } catch (NumberFormatException error3) {
                this.log.warning("\"" + stackSize + "\" is not a valid stack size (must be an integer between 1 and 64 inclusive). Skipping.");
                continue;
            }

            this.setStackSize(itemObject, Integer.parseInt(stackSize));

        }
    }

    // SET UP STACK SIZE FOR ITEM
    private void setStackSize(Item item, int stackSize) {
        try {
            Field itemField = Item.class.getDeclaredField("maxStackSize");
            itemField.setAccessible(true);
            itemField.setInt(item, stackSize);
            this.log.info("Setting the stack size for " + item + " to " + stackSize + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // DISPLAY PERMISSION DENIED TO CONSOLE
    public void permissionDenied(CommandSender sender) {
        this.log.info(ChatColor.RED + "" + sender + ChatColor.DARK_RED + " was denied access to command.");
    }

    // DISPLAY LIST OF CUSTOM STACK SIZES
    public void list(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "ITEM"+ ChatColor.WHITE + ": SIZE");
        Iterator it = this.getConfig().getKeys(true).iterator();
        while (it.hasNext()) {
            String itemName = (String)it.next();
            String stackSize = this.getConfig().getString(itemName);
            Item itemObject = null;

            try {
                Field itemField = Items.class.getField(itemName);
                itemObject = (Item)itemField.get((Object)null);
            } catch (NoSuchFieldException error1) {
                continue;
            } catch (IllegalAccessException error2) {
                continue;
            }

            try {
                Integer stackSizeInt = Integer.parseInt(stackSize);
                if (stackSizeInt < 1 || stackSizeInt > 64) {
                    continue;
                }
            } catch (NumberFormatException error3) {
                continue;
            }

            sender.sendMessage(ChatColor.GOLD + itemName + ChatColor.WHITE + ": " + Integer.parseInt(stackSize));
        }
    }


}