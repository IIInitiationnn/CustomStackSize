package customstacksize;

import net.minecraft.server.v1_16_R1.Item;
import net.minecraft.server.v1_16_R1.Items;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class main extends JavaPlugin implements Listener {
    private Logger log;
    private Map<Material, Integer> originalStackSizes = new HashMap<>();;

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
        this.setupAllStackSizes();
    }

    // RELOAD
    public void reload() {
        // TODO wipe all pre-set stuff eg. enderpearls = 64, then it's taken out of the config and reloaded
        // all other values will change as necessary but enderpearls stays at 64. need to revert everything to default
        // first. method here, revertStackSizes();
        this.resetAllStackSizes();
        this.reloadConfig();
        this.setupAllStackSizes();
    }

    // STOP
    @java.lang.Override
    public void onDisable() {
        this.log.info("CustomStackSize has been disabled.");
    }

    // SET UP STACK SIZES ON SERVER
    private void setupAllStackSizes() {
        Set<String> items;
        try {
            items = getConfig().getKeys(true);
        } catch (NullPointerException error1) {
            this.log.warning("Unable to retrieve information.");
            return;
        }

        // Verify that the inputs are valid.
        for (String eachItem : items) {
            // Verify item name is valid.
            Material currentMaterial = Material.matchMaterial(eachItem);
            Item currentItem;
            try {
                currentItem = (Item)Items.class.getField(eachItem).get((Object)null);
            } catch (NoSuchFieldException error2) {
                error2.printStackTrace();
                continue;
            }

            if (currentMaterial == null || !currentMaterial.isItem()) {
                this.log.warning("\"" + eachItem + "\" is not a valid item. Skipping.");
                continue;
            }
            String stackSize = getConfig().getString(eachItem);

            // Verify stack size is valid.
            int stackSizeInt;
            try {
                stackSizeInt = Integer.parseInt(stackSize);
                if (stackSizeInt < 1 || stackSizeInt > 64) {
                    this.log.warning("\"" + stackSize + "\" is not a valid stack size (must be an integer between 1 and 64 inclusive). Skipping.");
                    continue;
                }
            } catch (NumberFormatException error3) {
                this.log.warning("\"" + stackSize + "\" is not a valid stack size (must be an integer between 1 and 64 inclusive). Skipping.");
                continue;
            }

            this.setupStackSize(currentMaterial, currentItem, stackSizeInt);
        }
    }

    // SET UP STACK SIZE FOR ITEM ON SERVER
    private void setupStackSize(Material material, Item item, int stackSize) {
        try {
            // Put original stack size into the map containing original stack size values.
            if (!this.originalStackSizes.containsKey(material)) {
                this.originalStackSizes.put(material, material.getMaxStackSize());
            }

            if (stackSize == material.getMaxStackSize()) {
                this.log.warning(item + "already has stack size" + stackSize + ". Skipping.");
            }

            // Modify stack size in Material (Bukkit)
            Field materialField = Material.class.getDeclaredField("maxStack");
            materialField.setAccessible(true);
            materialField.setInt(material, stackSize);

            // Modify stack size in Item (Minecraft)
            Field itemField = Item.class.getDeclaredField("maxStackSize");
            itemField.setAccessible(true);
            itemField.setInt(item, stackSize);
            this.log.info("Setting the stack size for " + item + " to " + stackSize + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // WIPE ALL STACK SIZES FROM MEMORY ON SERVER
    private void resetAllStackSizes() {
        this.log.info("Wiping all custom stack sizes from memory.");
        Set<String> items;
        try {
            items = getConfig().getKeys(true);
        } catch (NullPointerException error1) {
            this.log.warning("Unable to retrieve information.");
            return;
        }

        // Verify that the inputs are valid.
        for (String eachItem : items) {
            // Verify item name is valid.
            Material currentMaterial = Material.matchMaterial(eachItem);
            Item currentItem;
            try {
                currentItem = (Item)Items.class.getField(eachItem).get((Object)null);
            } catch (NoSuchFieldException error2) {
                error2.printStackTrace();
                continue;
            }

            if (currentMaterial == null || !currentMaterial.isItem()) {
                continue;
            }
            String stackSize = getConfig().getString(eachItem);

            this.resetStackSize(currentMaterial, currentItem);
        }
    }

    // WIPE STACK SIZE FOR ITEM ON SERVER
    private void resetStackSize(Material material, Item item) {
        try {
            // Obtain original stack sizes and remove item from map.
            int oldSize = item.getMaxStackSize();
            int newSize = originalStackSizes.remove(material);

            // Modify stack size in Material (Bukkit)
            Field materialField = Material.class.getDeclaredField("maxStack");
            materialField.setAccessible(true);
            materialField.setInt(material, newSize);

            // Modify stack size in Item (Minecraft)
            Field itemField = Item.class.getDeclaredField("maxStackSize");
            itemField.setAccessible(true);
            itemField.setInt(item, newSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DISPLAY PERMISSION DENIED TO CONSOLE
    public void permissionDenied(CommandSender sender) {
        this.log.info(ChatColor.RED + "" + sender.getName() + ChatColor.DARK_RED + " was denied access to command.");
    }

    // DISPLAY LIST OF CUSTOM STACK SIZES: COMMAND "LIST"
    public void list(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "List of items with custom stack sizes:");

        Set<String> items;
        try {
            items = getConfig().getKeys(true);
        } catch (NullPointerException error1) {
            this.log.warning("Unable to retrieve information.");
            return;
        }

        // Verify that the inputs are valid.
        for (String eachItem : items) {
            // Verify item name is valid.
            Material currentMaterial = Material.matchMaterial(eachItem);
            if (currentMaterial == null || !currentMaterial.isItem()) {
                continue;
            }
            String stackSize = getConfig().getString(eachItem);

            // Verify stack size is valid.
            int stackSizeInt;
            try {
                stackSizeInt = Integer.parseInt(stackSize);
                if (stackSizeInt < 1 || stackSizeInt > 64) {
                    continue;
                }
            } catch (NumberFormatException error2) {
                continue;
            }
            sender.sendMessage(ChatColor.GOLD + eachItem + ChatColor.WHITE + ": " + Integer.parseInt(stackSize));
        }
    }

    // MODIFY ITEM'S STACK SIZE IN CONFIG AND LOAD: COMMAND "SET <ITEM> <SIZE>"
    // TODO

    // RESET ITEM'S STACK SIZE IN CONFIG AND LOAD: COMMAND "REVERT <ITEM>"
    // TODO



}