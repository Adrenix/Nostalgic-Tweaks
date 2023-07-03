# Nostalgic Tweaks (Forge & Fabric) 1.19.4-v2.0.0-Beta-8.4
- Lots of fixes and stability improvements
- Added support for the Exordium mod
- Added support for the Rubidium mod
- Added old/fast clouds quality option when Sodium/Rubidium is installed
- Fixed debug screen tweaks being overridden by Sodium/Rubidium
- Fixed HUD armor texture rendering issues when ImmediatelyFast is installed
- Fixed mod config interface not using the vanilla keyboard shortcuts
- Fixed custom window title tweak not replacing %v with the current Minecraft version
- Added tweak that can remove widget focus when the Esc key is pressed
> This tweak can be found in Eye Candy > Interface Candy > Screen Candy
- Changed skylight calculation algorithm in the old light rendering engine to work with mods like the Aether and the Twilight Forest
- Fixed not being able to leave the resource pack screen if it was entered by using the title screen
- Fixed sound tweaks not working when connected to a Forge server
- Fixed crash when entering a world with the mod in a disabled state
- Fixed darkness effect not working with old fog/lighting
- Fixed conduit power effect not working with old water fog
- Fixed old minecart boosters not maintaining momentum when a cart is pushed by multiple carts
- Fixed random damage tilt influencing directional damage tilt
- Fixed not being able to click on config input boxes
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

# Nostalgic Tweaks (Forge & Fabric) 1.19.4-v2.0.0-Beta-8.3
- Added old minecart physics tweak
- Added monster spawn cap modifier tweak
- Added separated disable chest sound tweaks for the ender chest and trapped chest
- Added experimental server-side-only (SSO) mode for dedicated server installations
- Removed old damage hurt direction tweak since it now exists in vanilla Minecraft 1.19.4
- Fixed crash caused by the custom hud mod
- Fixed alternate HUD text appearing in spectator mode
- Fixed not being able to climb trapdoors with old ladder gap bug enabled on Forge
- Fixed old torch brightness not being applied to all torches
- Fixed Enhanced Block Entities mod conflict
- Fixed world aesthetic rendering issues when playing on servers
- Fixed old missing texture tweak not rendering correctly
