# HideArmorMod (v1.4.2)

A premium, highly customizable Fabric mod for Minecraft 1.21.11 that gives you total control over the visibility and transparency of your equipment.

## ✨ Key Features

-   **Granular Opacity Sliders**: Don't just hide your armor — set it to any transparency from 0% to 100%.
-   **Tabbed Interface**:
    -   **🪖 Armor**: Individual opacity sliders for Helmet, Chestplate, Leggings, and Boots.
    -   **🛡️ Shield**: Visibility toggle for off-hand shields.
-   **Special Accessory Toggles**:
    -   **Elytra**: Toggle wings on/off without unequipping.
    -   **Skulls & Blocks**: Control visibility of decorative head items.
-   **Wildfire Female Gender Mod (WGFM) Support**:
    -   Applies your opacity settings to WGFM's custom breast armor layers.
    -   Set to 0% to fully hide, or anything above to keep visible.
-   **Multiplayer Sync** *(new in 1.4.0)*:
    -   A compass icon toggle in the Visibility row lets you opt-in to sharing your settings with other players.
    -   Other players who also have the mod installed will see your armor as you configured it.
-   **Premium UX**:
    -   Menu doesn't pause the game — see changes in real-time.
    -   Smooth slide-in animations and live player preview.
    -   No black HUD artifacts on transparent shields in first-person.

## 🎮 How to Use

-   **Open Menu**: Press `H` (default) while in-game.
-   **Adjust Visibility**: Drag sliders or click icons to configure each armor piece.
-   All changes are saved automatically when you close the menu.

## 🌐 Multiplayer

Enabling the **Compass/Globe toggle** in the Visibility row broadcasts your opacity settings to other players.

| Scenario | Works? |
|---|---|
| Single player | ✅ Always |
| LAN (host + friends, all with mod) | ✅ Yes |
| Dedicated server with mod installed server-side | ✅ Yes |
| Dedicated server without server-side mod | ❌ No — settings stay local |

> [!NOTE]
> Players without the mod always see normal, fully-visible armor. No crashes or errors occur on either side.

## 📥 Installation

1.  Requires **Minecraft 1.21.11** and **Fabric Loader**.
2.  Drop `hidearmor-1.4.2.jar` into your `.minecraft/mods` folder.
3.  (Highly Recommended) Install **Fabric API** for full compatibility.

## 🛠️ Configuration

Settings are saved in `config/hidearmor.json`. Use the in-game GUI for the best experience.

## 📜 License

MIT License.
