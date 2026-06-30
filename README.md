# <img src="src/main/resources/assets/hidearmor/icon.png" width="38" align="center" /> Hide Your Armor

<p align="center">
  <img src="src/main/resources/assets/hidearmor/textures/gui/banner.png" alt="Hide Your Armor Banner" width="700" />
</p>

<p align="center">
  <a href="https://github.com/G10W5/Hide-Your-Armor/releases">
    <img src="https://img.shields.io/github/v/release/G10W5/Hide-Your-Armor?style=for-the-badge&color=8A2BE2" alt="Releases" />
  </a>
  <a href="https://github.com/G10W5/Hide-Your-Armor/actions">
    <img src="https://img.shields.io/github/actions/workflow/status/G10W5/Hide-Your-Armor/gradle.yml?branch=main&style=for-the-badge" alt="Build Status" />
  </a>
  <img src="https://img.shields.io/badge/Minecraft-26.1%20%2F%201.21.11-darkgreen?style=for-the-badge" alt="Minecraft Version" />
  <img src="https://img.shields.io/badge/Loader-Fabric-blue?style=for-the-badge" alt="Fabric Loader" />
</p>

A premium, highly customizable client-side utility mod that grants complete control over the visibility, opacity, and rendering effects of your armor, accessories, and shield slots in real-time.

---

## 🌟 Features

### 🎛️ Granular Opacity Sliders
Don’t settle for binary toggles. Adjust your transparency level dynamically from **0%** (fully hidden) to **100%** (fully visible) with instant feedback in our non-pausing preview UI.

### 📑 Tabbed Interface Categories
*   **🛡️ Armor Configuration**: Opacity controls mapped individually for Helmet, Chestplate, Leggings, and Boots.
*   **⚔️ Off-Hand Shield Configuration**: Seamlessly toggle visibility for held shields.

### 🎭 Specialized Accessory Options
*   **🪶 Elytra Toggle**: Hide or display wings without having to unequip them.
*   **💀 Skulls & Blocks**: Adjust the visibility of decorative vanity head items.
*   **🧬 WGFM (Wildfire Female Gender Mod) Support**: Transparent armor adjustments map automatically onto custom model breast armor layers. Control glint directly on custom body meshes.

### 📡 Multiplayer Synchronization
Opt-in to broadcasting your configurations by toggling the in-game **Compass** switch. Other players running the mod will instantly view you with your customized armor opacities.

---

## 🎮 In-Game Controls & Interface

1. **Open the Config Screen**: Press `H` (default bind) anywhere in-game.
2. **Real-time Preview**: Modify sliders and toggles with a active side-by-side view of your avatar.
3. **Save System**: All settings save automatically on screen closure to `config/hidearmor.json`.

---

## 🌐 Multiplayer Behavior Matrix

| Client Setup | Server Mod Status | Synced Opacities? |
| :--- | :--- | :---: |
| **Singleplayer / LAN** | Local | ✅ **Yes** |
| **Modded Server** | Mod Installed Server-side | ✅ **Yes** |
| **Vanilla Server** | Mod Not Installed | ❌ **No (Settings stay local)** |

> [!IMPORTANT]
> Players without this mod installed will simply see standard, opaque armor models. No server conflicts, crashes, or data issues occur on vanilla setups.

---

## 📥 Installation

1. Make sure you are using **Minecraft 1.21.11 / 26.1** running the **Fabric Loader**.
2. Download the latest release `.jar` from the [Releases Tab](https://github.com/G10W5/Hide-Your-Armor/releases).
3. Drop the file inside your `.minecraft/mods` directory.
4. *(Optional but Recommended)* Add **Fabric API** to enable full cross-dependency features.

---

## 📜 License & Credits

Distributed under the **MIT License**. Check out [LICENSE](LICENSE) for more details.
