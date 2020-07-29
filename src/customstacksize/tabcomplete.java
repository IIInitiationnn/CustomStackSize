package customstacksize;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class tabcomplete implements TabCompleter {
    List<String> arguments = new ArrayList<String>();

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (arguments.isEmpty()) {
            if (sender.hasPermission("customstacksize.reload")) {
                arguments.add("reload");
            }
            if (sender.hasPermission("customstacksize.set")) {
                arguments.add("set");
            }
            if (sender.hasPermission("customstacksize.list")) {
                arguments.add("list");
            }
        }

        List<String> result = new ArrayList<String>();
        if (args.length == 1) {
            for (String a: arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        return null;
    }
}
