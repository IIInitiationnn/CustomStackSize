package customstacksize;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class commands implements CommandExecutor {
    // PLUGIN INSTANCE
    private main pluginInstance;

    commands(main pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("customstacksize") || label.equalsIgnoreCase("css")) {
            // Display help prompt with available CSS commands
            if (args.length == 0) {
                if (!sender.hasPermission("customstacksize.reload") && !sender.hasPermission("customstacksize.set") && !sender.hasPermission("customstacksize.view")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "CustomStackSize commands you have access to:");
                if (sender.hasPermission("customstacksize.reload")) {
                    sender.sendMessage(ChatColor.GOLD + "/" + label + " reload" + ChatColor.WHITE + ": reloads the config and plugin.");
                }
                if (sender.hasPermission("customstacksize.set")) {
                    sender.sendMessage(ChatColor.GOLD + "/" + label + " set <item> <size>" + ChatColor.WHITE + ": sets the stack size for the item to an integer from 1 to 64.");
                }
                if (sender.hasPermission("customstacksize.view")) {
                    sender.sendMessage(ChatColor.GOLD + "/" + label + " display <item>" + ChatColor.WHITE + ": displays custom stack size of specified item.");
                    sender.sendMessage(ChatColor.GOLD + "/" + label + " list" + ChatColor.WHITE + ": displays all items with custom stack sizes.");
                }
                return true;
            }

            // TODO: commands: revert <item>

            // RELOAD
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("customstacksize.reload")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return true;
                }
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " reload");
                    return true;
                }
                pluginInstance.reload();
                sender.sendMessage(ChatColor.GREEN + "CustomStackSize has been successfully reloaded.");
                return false;
            }

            // SET ITEM STACKSIZE
            // TODO include original value in the message displaying ITEM: stack size changed from OLD to NEW
            if (args[0].equalsIgnoreCase("set")) {
                if (!sender.hasPermission("customstacksize.set")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return true;
                }

                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " set <item> <size>");
                    return true;
                }

                sender.sendMessage(ChatColor.GOLD + "TO BE IMPLEMENTED");
                // TODO need to load each item into memory (use setStackSize)
                return false;
            }

            // DISPLAY
            if (args[0].equalsIgnoreCase("display")) {
                if (!sender.hasPermission("customstacksize.view")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return true;
                }

                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " display <item>");
                    return true;
                }

                return pluginInstance.display(sender, args[1]);
            }

            // LIST
            if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("customstacksize.view")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return true;
                }

                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " list");
                    return true;
                }

                pluginInstance.list(sender);
                return false;
            }


            return false;
        }

        return false;
    }

}
