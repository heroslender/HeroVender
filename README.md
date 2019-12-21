<img src="https://avatars1.githubusercontent.com/u/16785313?s=96&v=4" alt="Heroslender" title="Heroslender" align="right" height="96" width="96"/>

# HeroVender

[![GitHub stars](https://img.shields.io/github/stars/heroslender/HeroVender.svg)](https://github.com/heroslender/HeroVender/stargazers)
[![bStats Servers](https://img.shields.io/bstats/servers/3757.svg?color=1bcc1b)](https://bstats.org/plugin/bukkit/HeroVender)
[![GitHub All Releases](https://img.shields.io/github/downloads/heroslender/HeroVender/total.svg?logoColor=fff)](https://github.com/heroslender/HeroVender/releases/latest)
[![GitHub issues](https://img.shields.io/github/issues-raw/heroslender/HeroVender.svg?label=issues)](https://github.com/heroslender/HeroVender/issues)
[![GitHub last commit](https://img.shields.io/github/last-commit/heroslender/HeroVender.svg)](https://github.com/heroslender/HeroVender/commit)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/98c5f1bd76134d168989b8425cd17524)](https://app.codacy.com/app/heroslender/HeroVender?utm_source=github.com&utm_medium=referral&utm_content=heroslender/HeroVender&utm_campaign=Badge_Grade_Dashboard)
[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)

A plugin that allows your players to sell their inventory with a simple command or automatically using shift or when their inventory gets full:

- [HeroVender](#HeroVender)
  - [Commands](#Commands)
  - [Permissions](#Permissions)
  - [Placeholders](#Placeholders)
      - [Sell placeholders](#Sell-placeholders)
      - [Delay placeholders](#Delay-placeholders)
  - [Configuration](#Configuration)
    - [Default](#Default)
    - [Messages](#Messages)
  - [API](#API)
    - [Maven](#Maven)
    - [Events](#Events)

## Commands

- `/sell` - Sell the inventory
- `/sellmenu` - Open the selling menu, that allows the player to activate shift-sell or auto-sell
- `/shiftsell` - Toggle the Shift-Sell status
- `/autosell` - Toggle the Auto-Sell status
- `/herovender reload` - Reload the plugin(configuration and messages)
- `/herovender item` - See the ID of the item the player is holding

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

### Default

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

### Messages

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

## API

To get the plugin instance you can use the `HeroVender.getInstance()` method. 
With the plugin instance you can access all controllers and use them.

### Maven

```xml
<repository>
    <id>heroslender-repo</id>
    <url>https://nexus.heroslender.com/repository/maven-public/</url>
</repository>

<dependency>
    <groupId>com.heroslender</groupId>
    <artifactId>HeroVender</artifactId>
    <version>0.4.4</version>
    <scope>provided</scope>
</dependency>
```

### Events

- `PlayerSellEvent` - Called when a player sells their inventory
