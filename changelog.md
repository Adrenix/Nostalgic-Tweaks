# Nostalgic Tweaks 1.20.6 (NeoForge/Fabric) Changelog

## 2.0.0-beta.903

This update focuses on performance, optimizations, and bug fixes. The network protocol has changed in this version.
Servers using this update will require players to update as well. No new tweaks were added in this update.

- Created a tweak result caching system that greatly improves mod performance
- Added a new first-time config setup overlay to the mod's home screen
- Added panorama cycle buttons to the mod's home screen
- Updated the mod's home screen panorama images
- Slightly increased the default animal spawn cap for the old animal spawning tweak
- Fixed old durability color tweak overriding custom item durability bar colors
- Fixed player being able to sprint and/or swim by holding the sprint key going in/out of water
- Fixed disabled boat rowing sounds playing when connected to non-modded server
- Fixed a rare occurrence where some tweaks would not sync with a modded server
- Fixed the `Remove Mipmaps` tweak always overriding vanilla settings
- Fixed 2D item rendering when an item is in a fixated display context
- Fixed the `Quick Add` button not working correctly for old chest tweak lists
- Fixed being unable to move the color overlay window

## 2.0.0-beta.902

This version was built for 1.20.1, and its changes are listed in the beta-903 changelog above.

## 2.0.0-beta901

- Added `Minecart Rider Turning` tweak (`Gameplay > Game Mechanics > Minecart`)
    - Bring back the old minecart rider turning mechanic where the rider's body rotates with a minecart.
- Added `Safe Minecart Rider Turning` tweak (`Gameplay > Game Mechanics > Minecart`)
    - Prevents the `Minecart Rider Turning` tweak from working on servers without the mod installed.
- Optimized tweak listings for large lists
- Fixed incorrect inverted inventory player lighting
- Fixed modern stars being dimmed by the mod
- Fixed server crash when using boats
- Fixed client crash caused by an empty light engine data layer
- Fixed fog improperly cancelling the fog event on NeoForge
- Fixed armor trims not using the old damage armor tint
- Fixed custom full-block voxel collision not working on all blocks
- Fixed animal spawn rate being overridden by monster spawn rate
- Lowered the default animal spawn cap rate
- Renamed and reorganized full-block outline/collision tweaks
    - This change will require copying over your previous lists to the new config, a backup of your previous config is
      made at game startup.

## 2.0.0-beta900

- Initial release for 1.20.6