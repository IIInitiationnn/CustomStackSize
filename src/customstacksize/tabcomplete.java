package customstacksize;

import net.minecraft.server.v1_16_R1.Item;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class tabcomplete implements TabCompleter {
    List<String> arguments = new ArrayList<String>();
    List<String> emptyList = new ArrayList<String>();
    List<String> bulkColourGroups = Arrays.asList("all_dye", "all_wool", "all_carpet", "all_terracotta", "all_glazed_terracotta", "all_glass", "all_glass_pane", "all_concrete", "all_concrete_powder", "all_bed", "all_banner");

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (arguments.isEmpty()) {
            if (sender.hasPermission("customstacksize.reload")) {
                arguments.add("reload");
            }
            if (sender.hasPermission("customstacksize.modify")) {
                arguments.add("set");
            }
            if (sender.hasPermission("customstacksize.view")) {
                arguments.add("display");
                arguments.add("list");
            }
        }

        if (args.length == 1) {
            List<String> arg0 = new ArrayList<String>();
            for (String a: arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    arg0.add(a);
                }
            }
            return arg0;
        } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set") && sender.hasPermission("customstacksize.modify")) {
                List<String> arg1 = new ArrayList<String>();
                Material[] materials = Material.values();
                for (Material a : materials) {
                    if (!a.isItem()) {
                        continue;
                    }
                    if (a.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                        arg1.add(a.name().toLowerCase());
                    }
                }
                for (String a : bulkColourGroups) {
                    if (a.toLowerCase().startsWith(args[1].toLowerCase())) {
                        arg1.add(a.toLowerCase());
                    }
                }
                return arg1;
            } else if (args[0].equalsIgnoreCase("display") && sender.hasPermission("customstacksize.view")) {
                List<String> arg1 = new ArrayList<String>();
                Material[] materials = Material.values();
                for (Material a : materials) {
                    if (!a.isItem()) {
                        continue;
                    }
                    if (a.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                        arg1.add(a.name().toLowerCase());
                    }
                }
                return arg1;
            } else {
                return emptyList;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set") && sender.hasPermission("customstacksize.modify")) {
                List<String> arg2 = new ArrayList<String>();
                int[] sizes = {1, 64};
                for (int a : sizes) {
                    if (String.valueOf(a).toLowerCase().startsWith(args[2].toLowerCase())) {
                        arg2.add(String.valueOf(a));
                    }
                }
                return arg2;
            } else {
                return emptyList;
            }
        } else {
            return emptyList;
        }
    }
}
