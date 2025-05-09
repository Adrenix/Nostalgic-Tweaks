# Nostalgic Tweaks 1.20.1 (Fabric/Forge/NeoForge) Changelog

## 2.0.0-beta911 (in-dev)

This update fixes reported issues, adds new tweaks, and includes general mod improvements.

- Fixed crash with Create 0.6+ and Flywheel 1.0+
    - Create 0.5.x and Flywheel 0.6.x still work with this version of the mod.

## 2.0.0-beta910

This update fixes reported issues, adds new tweaks, and includes general mod improvements. The most notable new tweaks
are the stamina based sprinting system. Starting with all releases after beta.904, Nostalgic Tweaks now uses the LGPLv3
license. If you are interested in joining the mod's development team, then please reach out to us at the mod's Discord.

- Added home screen tutorial overlay to manually disable Sodium cloud rendering override
- Added warning banner and overlay to the home screen that indicates if significant conflict mods are installed
- Added category filtering next to the search bar in the config menu screen
- Added category filtering to the `Toggle All Tweaks` section in config management
- Search results now search everywhere by default
- Updated the mod's config home screen
- Made smooth scrolling in Nostalgic Tweaks menus more responsive
- Improved old cave ambience tweak (no more spooky cave sounds under trees at night)
- Removed Sodium cloud overrides
- Changed the `Disable Vanilla Brightness` tweak to be off by default
- Fixed decorated pot items appearing as 2D
- Fixed disable block offsets tweak applying to blocks with collision
- Fixed game soft locking when downloading a resource pack for a server
- Fixed crash when deleting more than one item in a tweak list
- Fixed server console log spam when a connected player has a different mod version
- Fixed held item model gap fix tweak not working on Forge
- Fixed incorrect air bubble offset when player is without armor on Forge
- Fixed incorrect air bubble offset on the HUD for Fabric
- Fixed missing vehicle health offset when player is mounted without armor
- Fixed block placing/breaking sounds not playing on Valkyrien Skies 2 ships
- Fixed disabled sprinting and swimming overriding creative/spectator
- Fixed custom hitbox outline tweaks conflicting with other mods
- Fixed classic swing not working as intended when holding the "use" button (mouse right click)
- Fixed classic attack swing missing its rotation (applies only to held block items)
- Fixed left-handed third person sword blocking animation
- Fixed favorite tweaks not appearing in the favorites list
- Fixed infinite seed tilling bug
- Fixed scrollbar in Nostalgic Tweaks menus scrolling irrationally on some occasions
- Fixed sky color not taking over when the `Dynamic Sky Color` tweak is disabled
- Added `Dynamic Light Brightness` lighting tweak
- Added `Stamina System` subcategory to the `Gameplay` category
- Added `Stamina Bar` subcategory to the `Heads-up Display` subcategory
- Added `Alternative Stamina Text` subcategory to the `Stamina Bar` subcategory
- Added `Stamina Sprinting` tweak (dynamic) (no server-side-only)
- Added `Disable Custom Hitbox Override` tweak
- Added `2D Item Exception` list tweak
- Added `Always Open Select World Screen` screen tweak
- Added `Hide New Recipe Toasts` tweak
- Added `Hide Tutorial Toasts` tweak
- Added `Hide Advancement Toasts` tweak
- Added `Hide Advancement Chats` tweak
- Added `Disable Smooth Lighting` tweak (off by default) (overrides video setting)
- Added `Disable Shovel Pathing` tweak (dynamically controlled)
- Added `Disable Axe Stripping` tweak (dynamically controlled)
- Added `Self Block Drops` tweak list (off by default) (includes vanilla ores)
- Added `Old Mob Death Topple` animation tweak
- Added `Old Mob Head & Body Turning` animation tweak
- Added `Fishing Mechanics` gameplay mechanics category
- Added `Old Fishing Loot` gameplay tweak (overrides fishing loot table)
- Added `Old Fishing Luring` gameplay tweak (brings back old bobber)
- Added `Old Fishing Casting` gameplay tweak (changes fishing rod sounds)
- Added `Old Creeper Strafing` gameplay tweak (on by default)
- Added `Disable Skeleton Strafing` gameplay tweak (off by default) (not available for Forge 1.20.1)
- Added `Disable Bubble Column Ambience` sound tweak

## 2.0.0-beta903

- Added a new first-time config setup overlay to the mod's home screen
- Added panorama cycle buttons to the mod's home screen
- Updated the mod's home screen panorama images
- Fixed a rare occurrence where not all tweaks would sync with a modded server
- Fixed the `Remove Mipmaps` tweak always overriding vanilla settings
- Fixed 2D item rendering when an item is in a fixated display context

## 2.0.0-beta902

- Initial release for 1.20.1