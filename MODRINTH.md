# Hide Your Armor

A client-side utility for granular armor, shield, and elytra visibility control with opacity sliders. Finally take that screenshot without your bulky armor ruining the shot, or create the perfect roleplay outfit without hiding your skin underneath.

## Why Hide Your Armor?

Ever wanted to show off your custom skin but had to sacrifice protection? Want the perfect screenshot without taking off your gear? Hide Your Armor gives you full control over what other players see — without ever unequipping your armor. Adjust each piece independently, from fully visible to completely invisible, with smooth opacity sliders that let you find the perfect balance.

## Features

### Per-Piece Opacity Control
- **Helmet** — Adjust opacity from 0% to 100%
- **Chestplate** — Adjust opacity from 0% to 100%
- **Leggings** — Adjust opacity from 0% to 100%
- **Boots** — Adjust opacity from 0% to 100%
- **Shield** — Adjust opacity from 0% to 100%

Each armor piece has its own independent slider, so you can make your helmet invisible while keeping your chestplate fully visible, or any combination you like.

### Enchantment Glint Toggles
Toggle the enchantment glint on each armor piece individually. Want your diamond chestplate's shimmer but not on your helmet? No problem. Each piece has its own glint toggle next to its opacity slider.

### Elytra Visibility
A dedicated toggle for elytra visibility that works independently from the chestplate opacity slider. Since elytra and chestplates share the same equipment slot in vanilla Minecraft, Hide Your Armor treats them separately so hiding your chestplate doesn't accidentally hide your elytra too.

### Skulls & Blocks Toggle
Toggle visibility of skull and block items worn on the head slot, such as dragon heads, player skulls, and carved pumpkins.

### Preset System
- Save your favorite configurations as named presets
- Up to **9 quick-load presets** accessible directly from the config GUI
- Two built-in defaults: **Full Visibility** and **Invisible** (these cannot be deleted)
- Create new presets with the **+** button and give them custom names
- Right-click any custom preset to delete it
- Presets are saved to your config file and persist across sessions

### Hotbar Blocking Animation
When your shield opacity is below 25%, a smooth animated overlay appears on the shield's hotbar slot while blocking. The overlay scales proportionally — the more hidden your shield is, the more prominent the blocking animation becomes. This gives you visual feedback that you're blocking even when the shield itself is mostly invisible.

### Player Preview
A live 3D preview of your character is displayed in the config GUI, so you can see your changes in real-time before closing the screen.

## Multiplayer Sync

When enabled (on by default), your visibility settings are automatically broadcast to other players on the server who also have the mod installed. This means:
- Other players see your armor exactly how you want it
- You see their settings too — everyone's preferences are respected
- Settings are periodically re-synced to handle late joiners
- Disable anytime with the sync toggle in the config GUI

## Configuration GUI

Press **H** (configurable in Minecraft's controls menu) to open the settings screen.

The GUI features two tabs:
- **Armor Tab** — Opacity sliders and glint toggles for helmet, chestplate, leggings, and boots
- **Shield Tab** — Opacity slider and glint toggle for your shield

Below the sliders you'll find visibility toggles for skulls/blocks, elytra, and multiplayer sync. The preset strip above the main panel lets you quickly save and load configurations.

## Compatibility

### Female Gender Mod
Full compatibility with Wildfire's Female Gender Mod:
- When chestplate opacity is set to 0%, breast size automatically adjusts to full (no more armor compression shrinking)
- Breast armor transparency follows the chestplate opacity setting
- Breast armor trim rendering is properly handled
- Enchantment glint on breast armor can be toggled independently

### Multi-Loader Support
A single universal JAR works on both **Fabric** and **NeoForge** mod loaders, powered by Architectury and Forgix. Just drop the same file into your mods folder regardless of which loader you use.

### General Compatibility
- Armor trim mods
- Rendering and shader mods
- Other client-side cosmetic mods
- Server-side only mods (no server installation required)

## Requirements

- Minecraft 26.2
- Fabric Loader 0.19.3+ or NeoForge 26.2+
- Java 25+

## Notes

- This is a **client-side only** mod — no server installation required
- Your visibility settings are purely visual — armor still provides full protection regardless of opacity
- Settings are stored locally in `config/hidearmor.json`
- Compatible with multiplayer — other modded clients will see your settings when sync is enabled
