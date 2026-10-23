# <img src="common/src/main/resources/assets/hidearmor/icon.png" width="38" align="center" /> Hide Your Armor

<p align="center">
  <img src="common/src/main/resources/assets/hidearmor/textures/gui/banner.png" alt="Hide Your Armor Banner" width="700" />
</p>

<p align="center">
  <a href="https://github.com/G10W5/Hide-Your-Armor/releases">
    <img src="https://img.shields.io/github/v/release/G10W5/Hide-Your-Armor?style=for-the-badge&color=8A2BE2" alt="Releases" />
  </a>
  <a href="https://github.com/G10W5/Hide-Your-Armor/actions">
    <img src="https://img.shields.io/github/actions/workflow/status/G10W5/Hide-Your-Armor/gradle.yml?branch=main&style=for-the-badge" alt="Build Status" />
  </a>
  <img src="https://img.shields.io/badge/Minecraft-26.1.2%20%7C%2026.2%20%7C%2026.3-darkgreen?style=for-the-badge" alt="Minecraft Version" />
  <img src="https://img.shields.io/badge/Loader-Fabric%20%2B%20NeoForge-blue?style=for-the-badge" alt="Loader" />
</p>

Client-side mod for hiding or fading your armor. Works locally by default, and optionally syncs to other players if the server has it too.

---

## Features

**Opacity, not just on/off**
Every piece has a slider from 0% to 100%. Drag it and see the change instantly on the preview doll. No need to re-equip anything.

* **Armor** – Helmet, Chestplate, Leggings, Boots each have their own slider
* **Shield** – Off-hand shield opacity
* **Misc** – Elytra, Skulls & blocks worn on head, Capes, and Armor trims
* **Enchantment glint** – Toggle the glint separately for each armor piece and the shield

<p align="center">
  <img src="docs/preview.gif" alt="Preview" width="700" />
</p>

**Three tabs**
Armor / Offhand / Misc. Misc uses the map icon – that's where the elytra/cape/trim stuff lives.

**Live preview**
The config screen doesn't pause the game. You can spin the preview model and it updates in real time. Settings save to `config/hidearmor.json` when you close the screen.

**Presets**
Save your current sliders as a preset and apply it later. Right-click a preset to delete it.

**Global preset**
The globe button next to the multiplayer sync (compass) icon applies your current preset to every player you see. It's off by default; mobs and armor stands are never affected.

**Wildfire Female Gender Mod compat**
If you use WGFM, breast armor layers follow your chestplate opacity and the glint toggles work on those meshes too.

---

## Controls

* **H** – open Hide Your Armor (rebindable in Controls)
* While the screen is open: drag sliders, toggle glint, switch tabs. Hit `Esc` to close and save.

---

## Multiplayer

The mod is client-side only. You can join vanilla servers fine – your settings just stay local.

If you want others to see you the way you configured it, turn on **Multiplayer Sync** (compass icon in the top-right of the config screen). When enabled, your config is sent to the server and the server relays it to other players who also have the mod.

| You | Server | Others see your opacity? |
| :--- | :--- | :---: |
| Singleplayer / LAN | – | Yes |
| Modded server | Mod installed | Yes |
| Vanilla / no mod | Not installed | No – stays local |

Players without the mod just see normal armor. No crashes or desyncs.

---

## Installation

Each jar is universal and works on both Fabric and NeoForge (built with Architectury + Forgix).

| Minecraft | File |
| :--- | :--- |
| 26.1, 26.1.1, 26.1.2, 26.2 | `HideYourArmor-1.7.1+mc26.1.2-universal.jar` |
| 26.3 | `HideYourArmor-1.7.1+mc26.3-universal.jar` |

1. **Fabric:** Fabric Loader 0.19.3+ and Fabric API
2. **NeoForge:** NeoForge 26.1.2+ (26.3+ for the 26.3 jar)

Download the matching file from [Releases](https://github.com/G10W5/Hide-Your-Armor/releases) and drop it in `mods/`.

Fabric API is required on Fabric. No extra dependencies on NeoForge.

---

## Config & Commands

No commands. Config lives at `config/hidearmor.json` and is edited in-game. You can back it up or delete it to reset.

---

## License

MIT – see [LICENSE](LICENSE).
