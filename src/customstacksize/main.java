package customstacksize;

import net.minecraft.server.v1_16_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

public class main extends JavaPlugin implements Listener {
    private Logger log;
    private Map<Material, Integer> originalStackSizes = new HashMap<>();;

    // TODO: IS THERE ANY OTHER PROPERTY EG IN INVENTORY WE CAN CHANGE TO MINIMISE THE FIDDLYNESS?
    // TODO: handle stacks on the ground?

    // STARTUP
    @java.lang.Override
    public void onEnable() {
        this.log = this.getLogger();
        this.log.info("Initialising CustomStackSize and validating stack sizes.");
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("customstacksize").setExecutor(new commands(this));
        this.getCommand("customstacksize").setTabCompleter(new tabcomplete(this));
        this.setupAllStackSizes();
    }

    // RELOAD
    public void reload() {
        this.getCommand("customstacksize").setExecutor(new commands(this));
        this.getCommand("customstacksize").setTabCompleter(new tabcomplete(this));
        this.resetAllStackSizes();
        this.reloadConfig();
        this.setupAllStackSizes();
    }

    // STOP
    @java.lang.Override
    public void onDisable() {
        this.resetAllStackSizes();
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
                currentItem = (Item)Class.forName("org.bukkit.craftbukkit." + this.getServer().getClass().getPackage().getName().split("\\.")[3] + ".util.CraftMagicNumbers").getDeclaredMethod("getItem", Material.class).invoke(null, currentMaterial);;
            } catch (Exception error2) {
                this.log.warning(eachItem + " is not a valid item. Skipping.");
                continue;
            }

            if (currentMaterial == null) {
                this.log.warning(eachItem + " is not a valid item. Skipping.");
                continue;
            }
            String stackSize = getConfig().getString(eachItem);

            // Verify stack size is valid.
            int stackSizeInt;
            try {
                stackSizeInt = Integer.parseInt(stackSize);
                if (stackSizeInt < 1 || stackSizeInt > 64) {
                    this.log.warning(stackSize + " is not a valid stack size (must be an integer between 1 and 64 inclusive). Skipping.");
                    continue;
                }
            } catch (NumberFormatException error3) {
                this.log.warning(stackSize + " is not a valid stack size (must be an integer between 1 and 64 inclusive). Skipping.");
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
                this.log.warning(item + " already has stack size " + stackSize + ". Skipping.");
                this.originalStackSizes.remove(material);
                return;
            }

            // Modify stack size in Material (Bukkit).
            Field materialField = Material.class.getDeclaredField("maxStack");
            materialField.setAccessible(true);
            materialField.setInt(material, stackSize);

            // Modify stack size in Item (Minecraft).
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
                currentItem = (Item)Class.forName("org.bukkit.craftbukkit." + this.getServer().getClass().getPackage().getName().split("\\.")[3] + ".util.CraftMagicNumbers").getDeclaredMethod("getItem", Material.class).invoke(null, currentMaterial);;
            } catch (Exception error2) {
                continue;
            }

            if (currentMaterial == null) {
                continue;
            }
            String stackSize = getConfig().getString(eachItem);

            this.resetStackSize(currentMaterial, currentItem);
        }
    }

    // WIPE STACK SIZE FOR ITEM ON SERVER
    private int resetStackSize(Material material, Item item) {
        try {
            // Obtain original stack sizes and remove item from map.
            int vanillaSize = originalStackSizes.remove(material);

            // Modify stack size in Material (Bukkit).
            Field materialField = Material.class.getDeclaredField("maxStack");
            materialField.setAccessible(true);
            materialField.setInt(material, vanillaSize);

            // Modify stack size in Item (Minecraft).
            Field itemField = Item.class.getDeclaredField("maxStackSize");
            itemField.setAccessible(true);
            itemField.setInt(item, vanillaSize);
            return vanillaSize;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // DISPLAY PERMISSION DENIED TO CONSOLE
    public void permissionDenied(CommandSender sender) {
        this.log.info(ChatColor.RED + "" + sender.getName() + ChatColor.DARK_RED + " was denied access to command.");
    }

    // DISPLAY CUSTOM STACK SIZE OF ITEM: COMMAND "DISPLAY <ITEM>"
    public boolean display(CommandSender sender, String item) {
        Set<String> items;
        try {
            items = getConfig().getKeys(true);
        } catch (NullPointerException error1) {
            this.log.warning("Unable to retrieve information.");
            return true;
        }

        for (String eachItem : items) {
            if (!item.equalsIgnoreCase(eachItem)) {
                continue;
            }
            Material currentMaterial = Material.matchMaterial(eachItem);
            if (currentMaterial == null) {
                continue;
            }
            String stackSize = getConfig().getString(eachItem);

            sender.sendMessage(ChatColor.GOLD + eachItem + ChatColor.WHITE + " has custom stack size: " + Integer.parseInt(stackSize));
            return false;
        }

        Material material = Material.matchMaterial(item);
        if (material != null) {
            sender.sendMessage(ChatColor.GOLD + item + ChatColor.WHITE + " has Vanilla stack size: " + material.getMaxStackSize());
            return false;
        }

        sender.sendMessage(ChatColor.RED + item + " does not have a custom stack size.");
        return true;
    }

    // DISPLAY LIST OF CUSTOM STACK SIZES: COMMAND "LIST"
    public void list(CommandSender sender) {

        Set<String> items;
        try {
            items = getConfig().getKeys(true);
        } catch (NullPointerException error1) {
            this.log.warning("Unable to retrieve information.");
            return;
        }

        if (items.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "There are currently no items with custom stack sizes.");
        } else {
            sender.sendMessage(ChatColor.GOLD + "All items with custom stack sizes:");
        }

        // Verify that the inputs are valid.
        for (String eachItem : items) {
            // Verify item name is valid.
            Material currentMaterial = Material.matchMaterial(eachItem);
            if (currentMaterial == null) {
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
    public boolean setStackCommand(CommandSender sender, String itemName, String size) {
        int newSize;
        try {
            newSize = Integer.parseInt(size);
            if (newSize < 1 || newSize > 64) {
                sender.sendMessage(ChatColor.RED + size + " is not a valid stack size (must be an integer between 1 and 64 inclusive).");
                return true;
            }
        } catch (Exception error) {
            sender.sendMessage(ChatColor.RED + size + " is not a valid stack size (must be an integer between 1 and 64 inclusive).");
            return true;
        }

        switch (itemName.toLowerCase()) {
            case "all_dye":
            case "all_wool":
            case "all_carpet":
            case "all_terracotta":
            case "all_glazed_terracotta":
            case "all_glass":
            case "all_glass_pane":
            case "all_concrete":
            case "all_concrete_powder":
            case "all_bed":
            case "all_banner":
            case "all_shulker_box":
                return setBulkColoured(sender, itemName.replaceAll("all_", ""), size);
            case "all_planks":
            case "all_slab":
            case "all_stairs":
            case "all_sapling":
            case "all_log":
            case "all_wood":
            case "all_stripped_log":
            case "all_stripped_wood":
            case "all_leaves":
            case "all_fence":
            case "all_fence_gate":
            case "all_sign":
            case "all_boat":
            case "all_door":
            case "all_trapdoor":
            case "all_button":
            case "all_pressure_plate":
                return setBulkWooden(sender, itemName.replaceAll("all_", ""), size);
            default:
                return executeSet(sender, itemName.toUpperCase(), newSize);
        }
    }

    // SET COLLECTION OF COLOURED ITEMS TO THE SAME STACK SIZE
    public boolean setBulkColoured(CommandSender sender, String category, String size) {
        String[] colours = {"red", "lime", "pink", "blue", "cyan", "gray", "white", "black", "brown", "green", "orange", "purple", "yellow", "magenta", "light_blue", "light_gray"};
        int newSize;
        try {
            newSize = Integer.parseInt(size);
            if (newSize < 1 || newSize > 64) {
                sender.sendMessage(ChatColor.RED + size + " is not a valid stack size (must be an integer between 1 and 64 inclusive).");
                return true;
            }
        } catch (Exception error) {
            sender.sendMessage(ChatColor.RED + size + " is not a valid stack size (must be an integer between 1 and 64 inclusive).");
            return true;
        }

        for (String colour : colours) {
            String itemName = category.equalsIgnoreCase("glass") || category.equalsIgnoreCase("glass_pane") ? (colour + "_stained_" + category).toUpperCase() : (colour + "_" + category).toUpperCase();
            executeSet(sender, itemName, newSize);
        }

        if (category.equalsIgnoreCase("glass")) {
            executeSet(sender, "GLASS", newSize);
        } else if (category.equalsIgnoreCase("glass_pane")) {
            executeSet(sender, "GLASS_PANE", newSize);
        } else if (category.equalsIgnoreCase("terracotta")) {
            executeSet(sender, "TERRACOTTA", newSize);
        } else if (category.equalsIgnoreCase("shulker_box")) {
            executeSet(sender, "SHULKER_BOX", newSize);
        }

        return false;
    }

    // SET COLLECTION OF WOODEN ITEMS TO THE SAME STACK SIZE
    public boolean setBulkWooden(CommandSender sender, String category, String size) {
        String[] woods = {"oak", "birch", "acacia", "spruce", "jungle", "dark_oak"};
        String[] fungi = {"warped", "crimson"};

        int newSize;
        try {
            newSize = Integer.parseInt(size);
            if (newSize < 1 || newSize > 64) {
                sender.sendMessage(ChatColor.RED + size + " is not a valid stack size (must be an integer between 1 and 64 inclusive).");
                return true;
            }
        } catch (Exception error) {
            sender.sendMessage(ChatColor.RED + size + " is not a valid stack size (must be an integer between 1 and 64 inclusive).");
            return true;
        }

        List <String> items = new ArrayList<String>();
        for (String wood : woods) {
            switch (category) {
                case "stripped_log":
                    items.add(("stripped_" + wood + "_log").toUpperCase());
                    break;
                case "stripped_wood":
                    items.add(("stripped_" + wood + "_wood").toUpperCase());
                    break;
                default:
                    items.add((wood + "_" + category).toUpperCase());
                    break;
            }
        }
        for (String fungus : fungi) {
            switch (category) {
                case "boat":
                case "leaves":
                    break;
                case "sapling":
                    items.add((fungus + "_fungus").toUpperCase());
                    break;
                case "log":
                    items.add((fungus + "_stem").toUpperCase());
                    break;
                case "wood":
                    items.add((fungus + "_hyphae").toUpperCase());
                    break;
                case "stripped_log":
                    items.add(("stripped_" + fungus + "_stem").toUpperCase());
                    break;
                case "stripped_wood":
                    items.add(("stripped_" + fungus + "_hyphae").toUpperCase());
                    break;
                default:
                    items.add((fungus + "_" + category).toUpperCase());
                    break;
            }
        }
        switch (category) {
            case "door":
            case "trapdoor":
                items.add(("iron_" + category).toUpperCase());
                break;
            case "pressure_plate":
                items.add(("light_weighted_" + category).toUpperCase());
                items.add(("heavy_weighted_" + category).toUpperCase());
            case "button":
                items.add(("stone_" + category).toUpperCase());
                items.add(("polished_blackstone_" + category).toUpperCase());
                break;
            default:
                break;
        }

        for (String item : items) {
            executeSet(sender, item, newSize);
        }

        return false;
    }

    // EXECUTING THE SET COMMAND
    public boolean executeSet(CommandSender sender, String itemName, int newSize) {
        Material material = Material.matchMaterial(itemName);
        Item item;
        try {
            item = (Item)Class.forName("org.bukkit.craftbukkit." + this.getServer().getClass().getPackage().getName().split("\\.")[3] + ".util.CraftMagicNumbers").getDeclaredMethod("getItem", Material.class).invoke(null, material);;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + itemName + " is not a valid item.");
            return true;
        }

        if (material == null) {
            sender.sendMessage(ChatColor.RED + itemName + " is not a valid item.");
            return true;
        }

        int oldSize = material.getMaxStackSize();
        if (oldSize == newSize) {
            sender.sendMessage(ChatColor.RED + itemName + " already has stack size " + newSize + ".");
            return true;
        } else if (this.originalStackSizes.containsKey(material) && this.originalStackSizes.get(material) == newSize) {
            sender.sendMessage(ChatColor.RED + String.valueOf(newSize) + " is the Vanilla stack size for " + itemName + ". Try using /css reset " + itemName.toLowerCase() + ".");
            return true;
        }

        this.getConfig().set(itemName, newSize);
        this.saveConfig();
        setupStackSize(material, item, newSize);

        sender.sendMessage(ChatColor.GREEN + "Set stack size for " + ChatColor.GOLD + itemName + ChatColor.GREEN + " from " + ChatColor.GOLD + oldSize + ChatColor.GREEN + " to " + ChatColor.GOLD + newSize + ChatColor.GREEN + ".");
        return false;
    }

    // RESET ITEM'S STACK SIZE IN CONFIG AND LOAD: COMMAND "RESET <ITEM>"
    public boolean resetStackCommand(CommandSender sender, String itemName) {
        Material material = Material.matchMaterial(itemName);
        Item item;
        try {
            item = (Item)Class.forName("org.bukkit.craftbukkit." + this.getServer().getClass().getPackage().getName().split("\\.")[3] + ".util.CraftMagicNumbers").getDeclaredMethod("getItem", Material.class).invoke(null, material);;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + itemName.toUpperCase() + " is not a valid item.");
            return true;
        }

        if (material == null) {
            sender.sendMessage(ChatColor.RED + itemName.toUpperCase() + " is not a valid item.");
            return true;
        }

        int customSize = material.getMaxStackSize();
        boolean hasVanillaSize = !originalStackSizes.containsKey(material);
        if (hasVanillaSize) {
            sender.sendMessage(ChatColor.RED + itemName.toUpperCase() + " already has its Vanilla stack size " + customSize + ".");
            return true;
        }

        this.getConfig().set(itemName.toUpperCase(), null);
        this.saveConfig();
        int vanillaSize = resetStackSize(material, item);
        sender.sendMessage(ChatColor.GREEN + "Reset stack size for " + ChatColor.GOLD + itemName.toUpperCase() + ChatColor.GREEN + " from " + ChatColor.GOLD + customSize + ChatColor.GREEN + " to " + ChatColor.GOLD + vanillaSize + ChatColor.GREEN + ".");
        this.log.info("Resetting the stack size for " + itemName + " to " + vanillaSize + ".");
        return false;
    }

    // PLAYERS EMPTYING BUCKETS
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.valueOf("CREATIVE"))) {
            return;
        }

        ItemStack filledBucket = event.getPlayer().getInventory().getItemInMainHand();
        // Only one bucket, Minecraft will handle.
        if (filledBucket.getAmount() - 1 == 0) {
            return;
        } else {
            filledBucket.setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
        }

        // Cancel event, empty bucket manually.
        String bucketContents = event.getBucket().name();
        bucketContents = bucketContents.replaceAll("_BUCKET", "");
        if (!bucketContents.equalsIgnoreCase("LAVA")) {
            // Need to place water
            event.getBlock().setType(Material.WATER);
            if (!bucketContents.equalsIgnoreCase("WATER")) {
                // Need to place fish
                event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.valueOf(bucketContents));
            }
        } else {
            // Need to place lava.
            event.getBlock().setType(Material.LAVA);
        }
        event.setCancelled(true);

        // TODO handle player statistics

        ItemStack bucket = new ItemStack(Material.BUCKET);

        // Determines if player has space for buckets in inventory.
        int numBuckets = 0;
        int numStacks = 0;
        for (ItemStack emptyBuckets : event.getPlayer().getInventory().getContents()) {
            if (emptyBuckets != null && emptyBuckets.getType().equals(Material.BUCKET)) {
                numBuckets += emptyBuckets.getAmount();
                numStacks += 1;
            }
        }
        boolean spaceForEmptyBuckets = numBuckets < (numStacks * Material.BUCKET.getMaxStackSize());

        if (spaceForEmptyBuckets) {
            // Inventory has a bucket stack to which an extra bucket can be added.
            event.getPlayer().getInventory().addItem(bucket);
        } else if (event.getPlayer().getInventory().firstEmpty() == -1) {
            // Inventory is completely full.
            event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), bucket);
        } else {
            // Inventory has space.
            event.getPlayer().getInventory().addItem(bucket);
        }
    }

    // PLAYERS DRINKING MILK OR STEWS
    @EventHandler
    // TODO FIND SOURCE OF LOOP USING LOG.INFO
    public void onPlayerConsumeFood(PlayerItemConsumeEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.valueOf("CREATIVE"))) {
            return;
        }
        ItemStack foodItemStack = event.getPlayer().getInventory().getItemInMainHand();
        String foodName = foodItemStack.getType().name();

        // Determine which container to return. Terminate if not an appropriate food item.
        ItemStack container;
        Material container2;
        if (foodName.equalsIgnoreCase("MUSHROOM_STEW") || foodName.equalsIgnoreCase("RABBIT_STEW") ||
                foodName.equalsIgnoreCase("SUSPICIOUS_STEW") || foodName.equalsIgnoreCase("BEETROOT_SOUP")) {
            container2 = Material.BOWL;
            container = new ItemStack(Material.BOWL);
        } else if (foodName.equalsIgnoreCase("MILK_BUCKET")) {
            container2 = Material.BUCKET;
            container = new ItemStack(Material.BUCKET);
        } else {
            return;
        }

        // Only one foodstuff, Minecraft will handle.
        if (foodItemStack.getAmount() - 1 == 0) {
            return;
        } else {
            foodItemStack.setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
        }

        // Cancel event, feed player manually.
        Item foodItem;
        try {
            foodItem = (Item)Items.class.getField(foodName).get((Object)null);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int oldHunger = event.getPlayer().getFoodLevel();
        int newHunger = Math.min(foodItem.getFoodInfo().getNutrition() + oldHunger, 20);
        float oldSaturation = event.getPlayer().getSaturation();
        float newSaturation = Math.min(foodItem.getFoodInfo().getSaturationModifier() + oldSaturation, 5.0F);
        event.getPlayer().setFoodLevel(newHunger);
        // TODO apply effect of milk / sus stew
        event.setCancelled(true);


        // TODO handle player statistics

        // Determines if player has space for containers in inventory.
        int numContainers = 0;
        int numStacks = 0;
        for (ItemStack emptyContainers : event.getPlayer().getInventory().getContents()) {
            if (emptyContainers != null && emptyContainers.getType().equals(container2)) {
                numContainers += emptyContainers.getAmount();
                numStacks += 1;
            }
        }
        boolean spaceForEmptyContainers = numContainers < (numStacks * container2.getMaxStackSize());

        if (spaceForEmptyContainers) {
            // Inventory has a container stack which can be added to
            event.getPlayer().getInventory().addItem(container);
        } else if (event.getPlayer().getInventory().firstEmpty() == -1) {
            // Inventory is completely full
            event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), container);
        } else {
            // Inventory has space
            event.getPlayer().getInventory().addItem(container);
        }

    }

}