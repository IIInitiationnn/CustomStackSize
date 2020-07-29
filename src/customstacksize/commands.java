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
                if (!sender.hasPermission("customstacksize.reload") && !sender.hasPermission("customstacksize.set") && !sender.hasPermission("customstacksize.list")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "CustomStackSize commands you have access to:");
                if (sender.hasPermission("customstacksize.reload")) {
                    sender.sendMessage(ChatColor.GOLD + "/" + label + " reload" + ChatColor.WHITE + ": reloads the config and plugin.");
                }
                if (sender.hasPermission("customstacksize.set")) {
                    sender.sendMessage(ChatColor.GOLD + "/" + label + " set <item> <1-64>" + ChatColor.WHITE + ": sets the stack size for the item to an integer from 1 to 64.");
                }
                if (sender.hasPermission("customstacksize.list")) {
                    sender.sendMessage(ChatColor.GOLD + "/" + label + " list" + ChatColor.WHITE + ": displays all custom stack sizes.");
                }
                return true;
            }

            // TODO: commands: sizeof <item> and reset <item>

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
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " set <item> <1-64>");
                    return true;
                }

                sender.sendMessage(ChatColor.GOLD + "TO BE IMPLEMENTED");
                // TODO process extra arguments for tabcomplete
                // TODO need to load each item into memory (use setStackSize)
                // TODO set up collective items that handle all the colours eg beds, glass
                return false;
            }

            // LIST
            if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("customstacksize.list")) {
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