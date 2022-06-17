## Version 1.0.4 (1.18, 1.18.1, and 1.18.2)
This update fixes reported issues, adjusts current tweaks, and adds a lot of new tweaks.
- Fixed opaque experience orbs not rendering correctly when colormatic is installed.
- Fixed old fog tweaks from rendering correctly when the void fog mod is installed.
- Fixed old reequip logic tweak for Forge.
- Fixed diffused lighting rendering issues with item entities when sodium is installed.
- Fixed shading issues inside leaf blocks with smooth lighting and old lighting are enabled.
- Fixed 2D throwing items not rendering in 2D.
- Fixed Esc and Ctrl (or Alt) + Left (or Right) keyboard shortcuts in the config menu cancelling text input.
- Fixed the tab key from not being respected when the modern button layout is being used with the classic title screen.
- Fixed radio group options in the config GUI by removing the semi-transparent with yellow text that indicates the default option.
- Fixed tweaks from not disabling/enabling correctly when using the disabling/enabling all tweaks overrides.
- Tweaks that require resource pack reloads will now do it automatically when changed and saved.
- Added the ability to access the mod's settings screen from the title screen with the user's defined hotkey.
- Added new eye candy subcategory: Block Candy
- Added a new tweak: "Old Chest" (Block Candy) - Turns normal chests into full-sized block.
- Added a new tweak: "Old Chest Voxel" (Block Candy) - (Disabled by default) Bring back the full-sized chest voxel shape. This will change chest block interaction behavior and will require a cache clear world optimization if disabled at a later time.
- Added a new tweak: "Old Ender Chest" (Block Candy) - Turns ender chests into a full-sized chest block.
- Added a new tweak: "Old Trapped Chest" (Block Candy) - Turns trapped chests into a full-sized chest block.
- Added a new tweak: "Fix Ambient Occlusion" (Block Candy) - Fixes ambient occlusion for broken vanilla blocks such as soul sand and powdered snow.
- Added a new tweak: "Old Door Placing" (Sound Candy) - Disable the placing sound when placing a door in the world.
- Added a new tweak: "Old Bed Placing" (Sound Candy) - Disable the placing sound when placing a bed in the world.
- Added a new tweak: "Old Button Layout" (Title Screen Candy) - Bring back various classic title screen layouts.
- Added a new tweak: "Remove Accessibility Button" (Title Screen Candy) - Remove the accessibility button from the title screen.
- Added a new tweak: "Remove Language Button" (Title Screen Candy) - Remove the language button from the title screen.
- Added a new tweak: "Uncap Title FPS" (Title Screen Candy) - Uncaps the 60 FPS limiter on the title screen and related menu screens.
- Added a new tweak: "Old No Item Tooltips" (Interface Candy) - Removes tooltip boxes when hovering over items within an inventory (disabled by default).
- Added a new tweak: "Old Game Loading Overlay" (Interface Candy) - Bring back alpha, beta, or post-release game loading overlays.
- Added a new tweak: "Remove Loading Progress Bar" (Interface Candy) - The progress bar can be enabled for both classic overlays. Does not impact modern loading overlay.
- Added a new tweak: "Old Chat Input" (Interface Candy) - Adds a '>' to the beginning of the chat box input window.
- Added a new tweak: "Old Chat Box" (Interface Candy) - Brings back the old chat box size, position, and fading animations.
- Added a new tweak: "Old Creative Hotbar" (Interface Candy) - Bring back the starting items in the hotbar when joining a world with an empty inventory in creative mode.
- Added a new tweak: "Old Ghast Charging" (Animation) - Bring back the 'squishy' ghast charging animation.
- Added a new eye candy subcategory "Lighting Candy".
- Added a new tweak: "Old Smooth Lighting" (Lighting Candy) - Smooth lighting now renders as it did in beta.
- Added a new tweak: "Old Leaves Lighting" (Lighting Candy) - Leaves will no longer have AO when smooth lighting is enabled like in beta.
- Added a new tweak: "Old Water Lighting" (Lighting Candy) - Water will now block 3 light levels instead of 1 (disabled by default since this tweak changes water blocks in the world).
- Added a new tweak: "Old Dark Void Height" (World Candy) - Changes the void's position based on the player's y-level so the void will always start at horizon.
- Added a new tweak: "Old Stars" (World Candy) - Bring back the old chunkier stars in the night sky.
- Redid a tweak: "Old Blue Void" (World Candy) - This tweak no longer requires a game restart for changes to take effect.
- Redid a tweak: "Old Attack Sounds" (Sounds) - Attack sounds are now controlled by the client.
- Redid a tweak: "Old Mob Steps" (Sounds) - Unique mob stepping sounds are now controlled by the client.
- Redid tweaks: All Particle Candy tweaks are now controlled by the client.

## Version 1.0.3 (1.18, 1.18.1, and 1.18.2)
This update is focused on fixing reported crashes and compatibility issues, squashing bugs, and adjusting tweaks as suggested on the mod's discord.
- Fixed crash that occurs on the Forge version of N.T when the hurt sound tries to play on multiplayer.
- Fixed a startup crash that occurs on the Fabric version of N.T when using the sodium-extras mod.
- Fixed a startup crash that occurs on the Fabric version of N.T when using the BetterNether mod.
- Moved explosion particle tweaks from "World Candy" to "Particle Candy"
- Fixed the old item holding tweak from mirroring properly on the left hand. (Fix contribution by InboundBark)
- Lowered the mod's mixin loader priority to increase compatibility with other mods.
- Replaced "Old Title Screen" tweak with a new eye candy subcategory "Title Screen Candy"
- Added a new tweak: "Old Dirt Background" (Title Screen Candy) - Toggles between panorama mode and dirt background.
- Added a new tweak: "Old Logo Outline" (Title Screen Candy) - Removes the black outline around static logos on the title screen.
- Added a new tweak: "Remove Mod Loader Text" (Title Screen Candy) - Removes mod loader related text from the title screen.
- Added a new tweak: "Title Version Text" (Title Screen Candy) - Add custom text to the title screen, comes with color support.
- Added a new tweak: "Bottom Left Title Text" (Title Screen Candy) - Move the title text from the top-left to bottom-left.
- Added a new tweak: "Old Nether Lighting" (World Candy) - Changes brightness and light shading directions in the Nether.
- Added a new tweak: "Old Fog Terrain" (World Candy) - Brings back the old terrain fog from alpha/beta.
- Added a new tweak: "Old Fog Horizon" (World Candy) - Brings back the old horizon fog from alpha.
- Redid a tweak: "Old Cloud Height" (World Candy) - This tweak is now a slider that ranges from 108 to 192 with alpha, beta, and modern labels.

## Version 1.0.2 (1.18, 1.18.1, and 1.18.2)
Small patch to help increase mod compatibility.
- Removed the mod's high global mixin priority.
- Added a small priority bump to some mixins that safely overrides some commonly used mods.
> It is possible that some conflicts might occur with other mods, but only if those other mods don't raise their priority above 1001.

## Version 1.0.1 (1.18, 1.18.1, and 1.18.2)
Adds a few features to enhance the old fog tweak.
- Added new Key Bindings subcategory in the General settings menu.
- Added new Toggle Fog key binding (unbound by default).
> The toggle fog hotkey will change the render distance by switching between far (16 chunks), normal (8 chunks), short (4 chunks), and tiny (2 chunks).
- Removed "New" tags from currently released tweaks.

## Version 1.0.0 (1.18.2)
Initial release of Nostalgic Tweaks.
