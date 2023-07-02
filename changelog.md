# Nostalgic Tweaks (Forge & Fabric) 1.18.2-v2.0.0-Beta-8.4
- Lots of fixes and stability improvements
- Added support for the Rubidium mod
- Added support for the Exordium mod
- Added old/fast clouds quality option when Sodium/Rubidium is installed
- Changed skylight calculation algorithm in the old light rendering engine to work with mods like the Aether and Twilight Forest
- Fixed crash when entering a world with the mod in a disabled state
- Fixed conduit power effect not working with old water fog
- Fixed old minecart boosters not maintaining momentum when a cart is pushed by multiple carts
- Fixed custom window title tweak not replacing %v with the current Minecraft version
- Changed the default values of some tweaks when a new config file is created
<details>
<summary>Changed Default Tweaks</summary>

- Recipe buttons were changed from disabled to small
- Inventory off-hand slot was changed from disabled to bottom-left
- Tooltip modifies and tooltip dye information were changed from disabled to enabled
- Sheep eating grass was changed from disabled to enabled
- Sheep punching to get wool was changed from enabled to disabled
- All old mob drop tweaks were changed from enabled to disabled
- Instant bow and invincible bow tweaks were changed from enabled to disabled
- Experience bar and experience orb spawning tweaks were changed from disabled to enabled
- Hunger bar was changed from disabled to enabled
- Hunger system was changed from disabled to enabled
- Instant eating was changed from enabled to disabled
- Sprinting and swimming were changed from disabled to enabled
- Bed bouncing was changed from disabled to enabled
- Tilled grass seed spawning was changed from enabled to disabled
</details>

# Nostalgic Tweaks (Forge & Fabric) 1.18.2-v2.0.0-Beta-8.3
- Added old minecart physics tweak
- Added monster spawn cap modifier tweak
- Added separated disable chest sound tweaks for the ender chest and trapped chest
- Added experimental server-side-only (SSO) mode for dedicated server installations
- Fixed crash caused by the custom hud mod
- Fixed alternate HUD text appearing in spectator mode
- Fixed not being able to climb trapdoors with old ladder gap bug enabled on Forge
- Fixed old torch brightness not being applied to all torches
- Fixed Enhanced Block Entities mod conflict
- Fixed world aesthetic rendering issues when playing on servers

# Nostalgic Tweaks (Forge & Fabric) 1.18.2-v2.0.0-Beta-8.2
The first release of a 2.0.0 beta to CurseForge/Modrinth. This beta is deemed mostly stable; however, there may still be
bugs that have not been found and/or fixed.
- Updated Fabric API version requirement to `0.75.1`
- Fixed Create visual issues by adding Flywheel support `(1.18.2 & 1.19.2)`
- Fixed arm rendering issues caused by the Off Hand Combat mod
- Fixed resource pack reloading crash that some users were having
- Fixed rare crash that would occur when opening the config menu
- Fixed HUD conflicts when both the food bar and XP bar are visible
- Fixed weird scrollbar behavior on the search page
- Fixed visual issues in the config menu when tweak alert tags changed state
- Fixed overlap buttons not displaying correctly with resource packs that have button border highlights
- Fixed creative void fog not respecting spectator mode
- Fixed old anvil screen not displaying red text when an action is too expensive
- Changed the config menu's search tag prefix from `@` to `#`
- Removed the notification subcategory from the general config category
- Updated old game loading screens to support Optifine and Optifabric
- Updated the falling blocks animation so that it doesn't start too early when the game finishes loading
- Updated light texture injections to add Distant Horizons mod support
- Added new tweaks that change the game's OS window title
> These tweaks can be found under Eye Candy > Interface Candy > Window Title Text
- Removed Herobrine
