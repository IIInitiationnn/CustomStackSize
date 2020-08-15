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
                if (!sender.hasPermission("customstacksize.reload") && !sender.hasPermission("customstacksize.modify") && !sender.hasPermission("customstacksize.view")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return false;
                }
                sender.sendMessage(ChatColor.GOLD + "CustomStackSize commands you have access to:");
                if (sender.hasPermission("customstacksize.reload")) {
                    sender.sendMessage(ChatColor.GOLD + "/" + label + " reload" + ChatColor.WHITE + ": reloads the config and plugin.");
                }
                if (sender.hasPermission("customstacksize.modify")) {
                    sender.sendMessage(ChatColor.GOLD + "/" + label + " set <item> <size>" + ChatColor.WHITE + ": sets the stack size of the item to an integer from 1 to 64 and adds it to the config.");
                }
                if (sender.hasPermission("customstacksize.view")) {
                    sender.sendMessage(ChatColor.GOLD + "/" + label + " display <item>" + ChatColor.WHITE + ": displays custom stack size of the item.");
                    sender.sendMessage(ChatColor.GOLD + "/" + label + " list" + ChatColor.WHITE + ": displays all items with custom stack sizes.");
                }
                return false;
            }

            // RELOAD
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("customstacksize.reload")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return false;
                }
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " reload");
                    return false;
                }
                pluginInstance.reload();
                sender.sendMessage(ChatColor.GREEN + "CustomStackSize has been successfully reloaded.");
                return true;
            }

            // SET ITEM STACKSIZE
            else if (args[0].equalsIgnoreCase("set")) {
                if (!sender.hasPermission("customstacksize.modify")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return false;
                }

                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " set <item> <size>");
                    return false;
                }

                return !pluginInstance.setStackCommand(sender, args[1].toLowerCase(), args[2]);
            }

            // RESET ITEM STACKSIZE
            else if (args[0].equalsIgnoreCase("reset")) {
                if (!sender.hasPermission("customstacksize.modify")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return false;
                }

                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " reset <item>");
                    return false;
                }

                return !pluginInstance.resetStackCommand(sender, args[1].toLowerCase());
            }

            // DISPLAY
            else if (args[0].equalsIgnoreCase("display")) {
                if (!sender.hasPermission("customstacksize.view")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return false;
                }

                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " display <item>");
                    return false;
                }

                return !pluginInstance.display(sender, args[1].toUpperCase());
            }

            // LIST
            else if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("customstacksize.view")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
                    pluginInstance.permissionDenied(sender);
                    return false;
                }

                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " list");
                    return false;
                }

                pluginInstance.list(sender);
                return true;
            }

            // INVALID COMMAND
            else {
                sender.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " is not a valid command.");
                return false;
            }
        }
        return false;
    }
}
