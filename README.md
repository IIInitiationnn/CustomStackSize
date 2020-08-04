# CustomStacksize
A Bukkit plugin to modify maximum stack sizes of items.
### Compatibility
Tested with Spigot 1.16.1.
### Overview
- Provides functionality to edit items' stack sizes from 1 to 64.
- Provides ability to view item stack size.
- Commands can be called using `customstacksize` or its alias `css`.
- Changes to the configuration file `config.yml` can be loaded using `css reload`.
- Handles strange behaviour induced by certain interactions (emptying buckets, drinking stews).

### Commands
| Command | Description | Permission |
| ------- | ----------- | ---------- |
| `css display <item>` | Displays custom stack size of the item. | `customstacksize.view` |
| `css list` | Displays all items with custom stack sizes. | `customstacksize.view` |
| `css reload` | Reloads the config and plugin. | `customstacksize.reload` |
| `css set <item> <size>` | Sets the stack size of the item and adds it to the config. | `customstacksize.modify` |
| `css reset <item>` | Resets the stack size of the item to its Vanilla size and removes it from the config. | `customstacksize.modify` |
### Permissions
| Permission | Description | Default |
| ---------- | ----------- | ------- |
| `customstacksize.view` | Permission to use `css display` and `css list`. | All |
| `customstacksize.reload` | Permission to use `css reload`. | Operator |
| `customstacksize.modify` | Permission to use `css set` and `css reset`. | Operator |