package customstacksize;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.logging.Logger;

public class main extends JavaPlugin implements Listener {
    private Logger log;


    // STARTUP
    @java.lang.Override
    public void onEnable() {
        this.log = this.getLogger();
        this.log.info("Starting CustomStackSize.");
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);

        Iterator it = this.getConfig().getKeys(false).iterator();
        while (it.hasNext()) {
            String itemName = (String)it.next();
            Item itemObject = null;

            // Verify that the item is a valid item.
            try {
                // Check through all MC items to verify if item is valid, then assign to itemField.
                Field itemField = Item.class.getField(itemName);
                // Assign item with corresponding name to itemObject
                itemObject = (Item)itemField.get((Object)null);
            } catch (NoSuchFieldException error1) {
                this.log.warning("\"" + itemName + "\" is not a valid item.");
                continue;
            } catch (IllegalAccessException error2) {
                error2.printStackTrace();
                continue;
            }

            String stackSize = null;

            if (!this.getConfig().isInt(stackSize)) {
                this.log.warning("\"" + stackSize + "\" is not a valid stack size (must be an integer between 1 and x).");
            } else {
                this.setStackSize(itemObject, Integer.parseInt(stackSize));
            }


        }
        /*Iterator var1 = this.getConfig().getKeys(false).iterator();

        while(var1.hasNext()) {
            String var2 = (String)var1.next();
            Item var3 = null;

            try {
                Field var4 = Items.class.getField(var2);
                var3 = (Item)var4.get((Object)null);
            } catch (NoSuchFieldException var5) {
                this.log.warning("`" + var2 + "` is not a valid item");
                continue;
            } catch (IllegalAccessException var6) {
                var6.printStackTrace();
                continue;
            }

            if (!this.getConfig().isInt(var2)) {
                this.log.warning("`" + var2 + "` has property that is not an integer");
            } else {
                this.setStackSize(var3, this.getConfig().getInt(var2));
            }
        }*/



    }
    // STOP
    @java.lang.Override
    public void onDisable() {
        this.log.info("Stopping CustomStackSize.");
    }

    // COMMANDS
    /*public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("customstacksize") || label.equalsIgnoreCase("css")) {
            // Display help prompt with available CSS commands
            if (args.length == 0) {
                //TODO


                return true;
            }

            //

            // RELOAD
            if (args[0].equalsIgnoreCase("reload")) {
                this.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "CustomStackSize has been successfully reloaded.");

            }

            if (!sender.hasPermission("customstacksize.set")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
                //TODO is this handled by pex?

            }

            return false;
        }

        return false;
    }
*/
    // SETTING MAX STACK SIZE
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

}