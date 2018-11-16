<div align="center">
<img src="https://github.com/heroslender/HeroAPI/raw/master/logo.jpg" alt="heroslender"/>
</div>

# HeroVender
A plugin that allows your players to sell their inventory with a simple command or automatically using shift or when their inventory gets full:

## Commands
- `/sell` - Command used to sell the inventory
- `/sell menu` - Command used to open the selling menu, that allows the player to activate shift-sell or auto-sell
- `/herovender reload` - Command used to reload the plugin(configuration and messages)

## Permissions
- `herovender.shiftsell` - Permission to activate the shift-sell
- `herovender.autosell` - Permission to activate the auto-sell

## Placeholders
- `:player:` - The player name
#### Sell placeholders
- `:invoice-total:` - The total ammount of money the player is receiving for the items
- `:invoice-total-formatted:` - The total ammount of money the player is receiving for the items, with prety formatting
- `:invoice-item-count:` - The total amount of items the player sold
#### Delay placeholders
- `:delay:` - Time the player has to wait to be able to sell again, in miliseconds
- `:delay-formated:` - Time the player has to wait to be able to sell again, in seconds, formated with two decimals

## Configuration
#### Default
```yaml
commands:
  sell:
    name: sell
    aliases:
    - sellall
  herovender:
    name: herovender
    usage: '&bHeroVender &7- &a/herovender <reload>'
bonus:
  1:
    permission: herovender.vip
    name: VIP bonus
    bonus: 0.08
  2:
    permission: herovender.vip.plus
    name: Bonus VIP+
    bonus: 0.15
delay:
  1:
    permission: herovender.default
    delay: 10000
  2:
    permission: herovender.vip
    delay: 1000
  3:
    permission: herovender.staff
    delay: 0
shops:
  default:
    items:
    - INK_SACK:4 1 price:1000
    - DIAMOND 1 price:1000
    - SLIME_BALL 1 name:&aYour_custom_named_item price:1000
  zombie:
    permission: vender.lapis
    inherits:
    - default
    items:
    - ROTTEN_FLESH 1 price:1500
    - CARROT_ITEM 1 price:5500
```
#### Messages
```yaml
sell:
  sold: '&aYou sold &7:invoice-item-count: &afor &f:invoice-total-formatted:&a!'
  no-items: '&cYou don''t have any items that can be sold!'
  delay: '&cYou must wait :delay-formated: to sell again!'
  menu:
    title: Sell menu!
    sell: DOUBLE_PLANT 1 name:&aSell lore:&7Click_here_to_sell_your_inventory!
    shiftsell:
      no-permission: LEVER 1 name:&aShift-Sell lore:&7Sell_your_inventory_by_sneaking||&7(Insufficient_permissions)
      'on': LEVER 1 name:&aShift-Sell lore:&7Sell_your_inventory_by_sneaking||&7Current_state->_&aActive|&7(Click_to_deactivate)
      'off': LEVER 1 name:&cShift-Sell lore:&7Sell_your_inventory_by_sneaking||&7Current_state->_&cInactive|&7(Click_to_activate)
    autosell:
      no-permission: LEVER 1 name:&aAuto-Sell lore:&7Automatically_sell_your_inventory_when_full.||&7(Insufficient_permissions)
      'on': LEVER 1 name:&aAuto-Sell lore:&7Automatically_sell_your_inventory_when_full.||&7Current_state->_&aActive|&7(Click_to_deactivate)
      'off': LEVER 1 name:&cAuto-Sell lore:&7Automatically_sell_your_inventory_when_full.||&7Current_state->_&cInactive|&7(Click_to_activate)
```