# 🛡️ Hide Your Armor

A premium, highly customizable Fabric client-side utility mod that allows you to set granular transparency values on your armor, accessories, and shield slots in real-time.

---

## 🌟 Key Features

*   **🎛️ Granular Opacity Sliders**: Don't just hide your armor — adjust the transparency level dynamically from **0%** (fully hidden) to **100%** (fully visible) with instant feedback.
*   **📑 Tabbed Interface Categories**:
    *   **Armor**: Opacity controls mapped individually for Helmet, Chestplate, Leggings, and Boots.
    *   **Shield**: Visibility toggles for off-hand shields.
*   **🎭 Specialized Accessory Options**:
    *   **Elytra**: Toggle wings on/off without having to unequip them.
    *   **Skulls & Blocks**: Adjust the visibility of decorative vanity head items.
    *   **WGFM (Wildfire Female Gender Mod) Support**: Transparent armor adjustments map automatically onto custom model breast armor layers. Control glint directly on custom body meshes.
*   **📡 Multiplayer Sync**:
    *   A compass icon toggle in the Visibility row lets you opt-in to sharing your settings with other players.
    *   Other players who also have the mod installed will see your armor as you configured it.
*   **✨ Premium UX**:
    *   Menu doesn't pause the game — see changes in real-time.
    *   Smooth slide-in animations and live player preview.
    *   No black HUD artifacts on transparent shields in first-person.
    *   **New Blocking Hotbar Visual Effect**: A custom sweeping cooldown overlay is displayed on the off-hand shield hotbar slot while blocking (when shield visibility is hidden) that instantly disappears upon releasing.

---

## 🎮 How to Use

1. **Open the Config Screen**: Press `H` (default bind) anywhere in-game.
2. **Real-time Preview**: Modify sliders and toggles with an active side-by-side view of your avatar.
3. **Save System**: All settings save automatically on screen closure to `config/hidearmor.json`.

---

## 🌐 Multiplayer Compatibility

Enabling the **Compass/Globe toggle** in the Visibility row broadcasts your opacity settings to other players.

| Scenario | Works? |
| :--- | :---: |
| **Single player** | ✅ Always |
| **LAN (host + friends, all with mod)** | ✅ Yes |
| **Dedicated server with mod installed server-side** | ✅ Yes |
| **Dedicated server without server-side mod** | ❌ No — settings stay local |

> [!NOTE]  
> Players without this mod installed will simply see standard, opaque armor models. No server conflicts, crashes, or data issues occur on vanilla setups.

---

## 📥 Installation

1. Make sure you are using **Minecraft 1.21.11 / 26.1** running the **Fabric Loader**.
2. Download the latest release `.jar` from the **Versions** tab.
3. Drop the file inside your `.minecraft/mods` directory.
4. *(Highly Recommended)* Install **Fabric API** to enable full cross-dependency features.

---

## 📜 License

Distributed under the **MIT License**.
