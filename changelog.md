## Version 1.0.3 (1.18.1 & 1.18.2)
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

## Version 1.0.2 (1.18.1 & 1.18.2)
Small patch to help increase mod compatibility.
- Removed the mod's high global mixin priority.
- Added a small priority bump to some mixins that safely overrides some commonly used mods.
> It is possible that some conflicts might occur with other mods, but only if those other mods don't raise their priority above 1001.

## Version 1.0.1 (1.18.1 & 1.18.2)
Adds a few features to enhance the old fog tweak.
- Added new Key Bindings subcategory in the General settings menu.
- Added new Toggle Fog key binding (unbound by default).
> The toggle fog hotkey will change the render distance by switching between far (16 chunks), normal (8 chunks), short (4 chunks), and tiny (2 chunks).
- Removed "New" tags from currently released tweaks.

## Version 1.0.0 (1.18.2)
Initial release of Nostalgic Tweaks.
