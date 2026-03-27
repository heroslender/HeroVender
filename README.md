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

#### Prices menu placeholders

- `:price:` - The unit price of the item
- `:price-formatted:` - The unit price of the item, formated with two decimals

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
# Configure the auto-sell
autosell:
  # Strategy to use for auto-selling, available strategies:
  # https://github.com/heroslender/HeroVender/tree/master/src/main/java/com/heroslender/herovender/autosell/AutoSellStategy.java
  strategy: "PICKUP_ITEM"
  settings:
    # Is it required for the inventory to be full in order to sell?
    require-inventory-full: true
    # If the strategy is set to TIMER, this is the delay to use between checks
    timer-delay: 10
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
      CARROT:
        item: minecraft:carrot
        price: 2.0
      test:
        price: 1.0
        ignore-durability: false
        item:
          ==: org.bukkit.inventory.ItemStack
          DataVersion: 4440
          id: minecraft:golden_carrot
          count: 1
          components:
            minecraft:lore: '[{color:"gray",text:"A very lucky apple."},{color:"yellow",text:"Found
              in the farm."}]'
            minecraft:custom_name: '{color:"gold",text:"Golden Apple of Luck",italic:false}'
          schema_version: 1
  zombie:
    permission: shop.zombie
    inherits:
    - default
    items:
      CARROT:
        item: minecraft:carrot
        price: 5500
      ROTTEN_FLESH:
        item: ROTTEN_FLESH
        price: 1500
```

### Messages

```yaml
sell:
  sold: '&aYou sold &7:invoice-item-count: &afor &f:invoice-total-formatted:&a!'
  no-items: '&cYou don''t have any items that can be sold!'
  delay: '&cYou must wait :delay-formated: to sell again!'
  command:
    actionbar: false
    chat: true
    ignore-empty: false
  autosell:
    actionbar: true
    chat: false
    ignore-empty: true
    'on': '&aYou have activated the Auto-Sell!'
    'off': '&cYou have deactivated the Auto-Sell!'
  shiftsell:
    actionbar: true
    chat: false
    ignore-empty: false
    'on': '&aYou have activated the Shift-Sell!'
    'off': '&cYou have deactivated the Shift-Sell!'
  custom:
    chat: true
    actionbar: true
    ignore-empty: true
```
