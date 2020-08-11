# CustomStacksize
A Bukkit plugin to modify maximum stack sizes of items.
### Compatibility
Tested with Spigot 1.16.1.
### Overview
- Functionality to edit item stack sizes within the range 1 to 64.
- Convenient control over bulk resource groups such as by colour or type.
- Ability to view any item's stack size.
- Commands called using `/customstacksize` or its alias `/css`.
- Changes to the configuration file `config.yml` loaded using `/css reload`.
- Handles strange behaviour induced by certain interactions (emptying buckets, drinking stews).
- Handles player statistics for items with strange behaviour.

### Commands
| Command | Description | Permission |
| ------- | ----------- | ---------- |
| `css display <item>` | Displays custom stack size of the item. | `customstacksize.view` |
| `css list` | Displays all items with custom stack sizes. | `customstacksize.view` |
| `css reload` | Reloads the config and plugin. | `customstacksize.reload` |
| `css set <item/group> <size>` | Sets the stack size of the item or group, and adds it to the config. | `customstacksize.modify` |
| `css reset <item>` | Resets the stack size of the item to its Vanilla size and removes it from the config. | `customstacksize.modify` |
### Permissions
| Permission | Description | Default |
| ---------- | ----------- | ------- |
| `customstacksize.view` | Permission to use `css display` and `css list`. | All |
| `customstacksize.reload` | Permission to use `css reload`. | Operator |
| `customstacksize.modify` | Permission to use `css set` and `css reset`. | Operator |