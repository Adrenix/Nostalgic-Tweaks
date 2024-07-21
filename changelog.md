# Nostalgic Tweaks 1.21 (NeoForge/Fabric) Changelog

## 2.0.0-beta901

- Added `Minecart Rider Turning` tweak (`Gameplay > Game Mechanics > Minecart`)
    - Bring back the old minecart rider turning mechanic where the rider's body rotates with a minecart.
- Added `Safe Minecart Rider Turning` tweak (`Gameplay > Game Mechanics > Minecart`)
    - Prevents the `Minecart Rider Turning` tweak from working on servers without the mod installed.
- Optimized tweak listings for large lists
- Fixed old name tags not appearing
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

- Initial release for 1.21