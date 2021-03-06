package customstacksize;

import net.minecraft.server.v1_16_R1.Item;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class tabcomplete implements TabCompleter {
    List<String> arguments = new ArrayList<String>();
    List<String> emptyList = new ArrayList<String>();
    List<String> bulkGroups = Arrays.asList("all_dye", "all_wool", "all_carpet", "all_terracotta", "all_glazed_terracotta", "all_glass", "all_glass_pane", "all_concrete", "all_concrete_powder", "all_bed", "all_banner", "all_shulker_box", "all_planks", "all_slab", "all_stairs", "all_sapling", "all_log", "all_wood", "all_stripped_log", "all_stripped_wood", "all_leaves", "all_fence", "all_fence_gate", "all_sign", "all_boat", "all_door", "all_trapdoor", "all_button", "all_pressure_plate");

    private main pluginInstance;

    tabcomplete(main pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (arguments.isEmpty()) {
            if (sender.hasPermission("customstacksize.reload")) {
                arguments.add("reload");
            }
            if (sender.hasPermission("customstacksize.modify")) {
                arguments.add("set");
                arguments.add("reset");
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
                for (String a : bulkGroups) {
                    if (a.toLowerCase().startsWith(args[1].toLowerCase())) {
                        arg1.add(a.toLowerCase());
                    }
                }
                return arg1;
            } else if (args[0].equalsIgnoreCase("reset") && sender.hasPermission("customstacksize.modify")) {
                Set<String> itemsFromConfig;
                List<String> arg1 = new ArrayList<String>();
                try {
                    itemsFromConfig = pluginInstance.getConfig().getKeys(true);
                } catch (NullPointerException error1) {
                    return emptyList;
                }

                for (String eachItem : itemsFromConfig) {
                    Material currentMaterial = Material.matchMaterial(eachItem);
                    if (currentMaterial == null) {
                        continue;
                    }
                    if (currentMaterial.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                        arg1.add(currentMaterial.name().toLowerCase());
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