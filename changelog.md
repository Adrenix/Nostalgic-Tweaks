# Nostalgic Tweaks (Forge & Fabric) 1.19.2-v2.0.0-Beta-8.2
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
- Removed Herobrine
> These tweaks can be found under Eye Candy > Interface Candy > Window Title Text

# Nostalgic Tweaks (Forge & Fabric) 1.19.2-v2.0.0-Beta-8.0
Major beta release with numerous fixes and new features.
- Removed cloth-config as a required dependency.
- Added a supporter banner to the top of the mod's home settings screen.
> This can be toggled and the toggle state will be remembered.
- Added a new title logo (by Sea Pickle) to the mod's home settings screen.
- Updated the mod's home settings screen gear logo and changed the splash text to the jar's version number.
- Did some codebase optimizations so FPS should run smoother.
- Potentially fixed crash caused by the old torch model tweaks.
- Fixed mod buttons not appearing on the old pause screen (this can be toggled).
- Fixed lag spikes when crossing certain chunk borders.
- Fixed old water lighting not being calculated correctly.
- Fixed disable light flicker tweak not appearing in the config menu.
- Fixed old debug entity id nametags not rendering as full-bright like it was in beta.
- Fixed old debug menu not respecting the reduced debug info game rule.
- Fixed incorrect holding positions of some items with old item holding enabled.
- Fixed players being able to sprint with enabled swimming but disabled sprinting.
- Fixed water fog colors being influenced by block light when old light rendering is enabled.
- Fixed zombie pigmen spawning rare drops when old drops is enabled.
- Fixed old light rendering issues during weather events.
- Fixed old nether lighting not updating after toggling.
- Fixed void fog rendering issues due to old light rendering.
- Fixed sneak + attack placing blocks with old left click tweaks enabled.
- Fixed text paragraphs not rendering correctly on certain GUI scales.
- Fixed search results visual issues when returning from the cancel changes screen.
- Made search results clickable when hovering over search crumbs or tweak titles.
- Moved mod's config file to a new config folder where backups and presets are stored.
- Moved debug client light data into new subcategory called extra debug information.
- Redid universal old fog tweak.
> This tweak now comes with `Classic`, `Inf-dev`, `Alpha - Beta`, and `Modern`.
- Redid the old fog rendering logic so that fog looks more like it did in alpha/beta.
- Redid the old blue void tweak so that the color is correct at night and during weather events.
- Redid the old universal fog tweak so that the color is correct at night and during weather events.
- Redid the old nether fog and old nether sky colors so that they match alpha/beta colors.
- Redid the old swing speed list screen.
- Improved search results so that words that are closely related to tweaks appear in the results.
- Added display biome data and block target info tweaks to extra debug information subcategory.
- Added client-only filter checkbox to the disable/enable all tweak changes section.
- Added see changes button to the all tweak changes section.
- Added automatic config backups when a config file needs reset due to a structure change or invalid data.
- Added dynamic alert tags that appear when two tweak values conflict with each other.
- Added mod loader specific APIs for Nostalgic Tweaks.
> Documentation on how to develop against the API is included in the 1.19.2-multiplayer README file.
- Added AppleSkin support for old HUD tweaks.
- Changed the infinite burn tweak to be disabled by default to prevent performance issues.
- Cleaned up the mod's codebase.
- Added network protocol verification so that servers running Nostalgic Tweaks requires the client to have it as well.
> The mod version does not have to match, only the protocol version does.
> The protocol version does not change often.
- Added a new `Config Management` category to the `General` config group.
> You can open the config file directory, create a new backup config file, and change the max number of backup files allowed.
- Added new `World Lighting` tweak: `Fix Chunk Border Lag`
> This tweak fixes reported lag spike issues when crossing chunk borders.
- Added new `Fog Candy` tweak: `Old World Fog`
> This changes world fog based on the selected game version range.
- Added new `Fog Candy` tweak: `Old Dynamic Fog`
> This changes fog color depending on biome temperature and render distance.
- Added new `Fog Candy` tweak: `Old Nether Fog`
> This separates the old nether fog color out from the `Old Universal Fog Color` tweak.
- Added new `Sky Candy` tweak: `Old Nether Sky`
> This separates the old nether sky color out from the `Old Universal Sky Color` tweak.
- Added new `Sky Candy` tweak: `Old Dynamic Sky`
> This changes sky color depending on biome temperature.
- Added new `Item Candy` tweak: `Ignored Items For Old Holding`
> Add, edit, and/or remove items that will be ignored by the old item holding tweak.
- Added new `Fog Candy > Custom Fog` embedded subcategory
> This embedded subcategory contains **4** new tweaks to customize overworld and nether fog colors.
- Added new `Sky Candy > Custom Sky` embedded subcategory
> This embedded subcategory contains **4** new tweaks to customize overworld and nether sky colors.
- Added new `Block Candy > Old Missing Texture`
> Brings back the old missing textures ranging from Beta 1.4 until 1.12.
> Missing texture logic contributed by forkiesassds on GitHub.
- Added new `Block Candy > Hitbox Outlines` subcategory
> This category contains **5** new tweaks.
- Added new `Hitbox Outlines` tweak: `Old Fence Outline`
- Added new `Hitbox Outlines` tweak: `Old Slab Outline`
- Added new `Hitbox Outlines` tweak: `Old Stair Outline`
- Added new `Hitbox Outlines` tweak: `Old Wall Outlines`
- Added new `Hitbox Outlines` tweak: `Custom Full Block Outlines`
> Add, edit, and/or remove block items that will have full block outlines.
- Added new `Hunger System > Food` tweak: `Custom Food Stacking`
> This new list screen replaces the previous food stacking system.
- Added new `Hunger System > Food` tweak: `Custom Food Health`
> This new list screen replaces the old nutritional value system.
- Added new `Game Mechanics > Block` tweak: `Disable Bed Bounce`
> Disable entities bouncing on beds and disable fall damage reduction when landing on beds.
- Added new `Item Candy` tweak: `Old Damage Armor Tint`
> The armor will now have a red tint when the wearer receives damage.
- Added new `Anvil Screen` tweak: `Old Anvil Screen`
> Bring back the old anvil screen from before Minecraft 1.13.
- Added new `World Lighting` tweak: `Old Classic Light Rendering`
> This tweak is controlled by the server since this could be considered cheating in vanilla.
> The sun/moon doesn't render when this tweak is enabled.
- Added new `Fog Candy` tweak: `Old Dark Fog`
> Fog will get darker when the max light value surrounding the player gets darker.
- Added new `Arm Animations` tweak: `Old Classic Swing`
> Changes the breaking swing animation and placement swing animation to simulate Minecraft classic.
- Added new `Block Particles` tweak: `Disable Lava Particles`
> Prevents the lava pop particles from appearing (_not enabled by default_).
- Added new `Block Particles` tweak: `Disable Model Destruction Particles`
> Disables the voxel shape of a block model from influencing the amount of particles that appear when the block is destroyed.
- Added new `Particle Candy` tweak: `Disable Underwater Particles`
> Disables the particles that spawn while the player is underwater.
- Added new `Bed Sound` tweak: `Old Bed Sounds`
> Replaces the wood sounds with stone sounds when breaking or walking on beds.
- Added new `Ambience Sound` tweak: `Disable Nether Ambience`
> Disables the ambient sounds that play while the player is in a Nether biome.
- Added new `Ambience Sound` tweak: `Disable Water Ambience`
> Disables the ambient sounds that play while the player is underwater.
- Added new `Fish Sound` tweaks: `Disable Fish Swim`, `Disable Fish Hurt`, and `Disable Fish Death`
> Disable various sounds from all fish entities.
- Added new `Lava Sound` tweaks: `Disable Lava Ambience` and `Disable Lava Pop`
> Disable ambient lava or lava pop sounds.
- Added new `Block Sound` tweak: `Disable Furnace Sounds`
> Disable the sounds emitted by furnace blocks when items are being cooked.
- Added new `Squid Sound` tweaks: `Disable Glow Squid Ambience` and `Disable Glow Squid Other`
> Disable various sounds from glow squids.
- Added new `Generic Sound` tweak: `Disable Swim Sounds`
> Disable player swim sounds.
- Added new `Chat Screen` tweak: `Chat Offset`
> Offset the start position for chat messages by a certain amount.
> This is useful for mods that add things in front of chat messages such as the chat heads mod.
- Added new `Chat Screen` tweak: `Disable Signature Boxes`
> Removes the colored chat signature rectangles that appear in front of chat messages (off by default).
- Added new `Pause Screen` tweak: `Remove Extra Buttons`
> Remove any extra buttons that are added by other mods from the nostalgic pause screen.
> This will not remove extra buttons from the modern pause screen.
- Added new `Alternative Level Text` tweak: `Show in Creative`
> Shows the alternative xp level text while in creative mode (off by default).
- Added new `Alternative Progress Text` tweak: `Show in Creative`
> Shows the alternative xp progress text while in creative mode (off by default).
- Added new `Combat System` tweak: `Disable Critical Hit`
> Removes the ability for players to perform critical hit attacks.
- Added new `Combat System` tweak: `Old Damage Values`
> Reverts the damage dealt by swords and tool items back to their old values.
- Revamped Swing Speeds.
> Since the old classic swing tweak introduced the ability for swing speeds to be separated into left and right components,
> the custom swing speed tweaks have been redone to match this new logic. It is now possible to change swinging animation
> speeds based on whether the player is attacking (left-click) or using an item (right-click). The old custom speed list
> has been separated into a custom left-click speed list and custom right-click speed list.
- Removed Herobrine

### The following part of this changelog is no longer maintained due to the amount of changes made by newer betas

# N.T (Forge & Fabric): 1.19-v2.0.0 (Multiplayer) (OUTDATED)
This update is focused on implementing server support, adding a new Gameplay category into Nostalgic Tweaks, overhauling the mod's configuration menu, and fixing reported issues.
All updates that start with a 2.x.x version number will support multiplayer.
- Added server support for all server controlled tweaks.
- Added LAN support for all server controlled tweaks.
- Added multiplayer support into the configuration menu.
> The configuration menu will sync with the server and inform the user on the current state of a tweak.

- Added new tweak categories in the configuration menu.
  * Sound: Block Sounds, Damage Sounds, Experience Sounds, and Mob Sounds
  * Animation: Arm Animations, Item Animations, Mob Animations, and Player Animations

- Added subcategories for large categories (e.g., Fog Candy, Sky Candy, and Void Candy subcategories for the World Candy category in Eye Candy)
- Categories and subcategories will now remember their expansion state when the window resizes or a tab changes.
- Entries are now indented that are underneath categories and subcategories for easier reading.
- Entries now have a colored tree indent line to connect entries with their corresponding category for easier reading.
> This tree can be disabled and have its color changed under General -> Menu Settings -> Category Tree.
> If you click the colored box next to the hex input box, a color picker window will pop up with RGBA sliders.
> The color picker window has a draggable title bar and will update all colors and hex inputs automatically when sliding.

- Entry highlight color, display, and fade effect can now be changed under General -> Menu Settings -> Row Highlighting
- Configuration entries will now condense long text with three dots (...) and a tooltip will appear when hovering over condensed text.
- Added a category/subcategory/tweak jumper overlay which can be accessed by clicking the category button next to the General button.
> Clicking on a text entry within this window will auto-jump you to the selected category/subcategory/tweak.

- Redesigned the mod's searching functionality and display.
> You can now 'fuzzy' search for tweaks and search for words within a tweak's tooltip bubble.
> Additionally, you can type a search query, and then narrow it using the new search tag button.
> The mod will automatically refill the search box with a search tag and your previous query.

- Added Tab key and Shift + Tab key support for config row lists and config overlay screens.
- Added a new "@save" search tag that will filter out tweaks that will be saved when the "Save & Quit" button is pressed.
- Added a checkbox to the override tweak subcategory that filters server only tweaks.

- Redid the mod's console output.
- Redid the mod's config runtime and loader system. Some users may see about a 10-20 FPS increase. 
- Redid the old alpha logo, so it refreshes at the game's FPS.
- Readjusted the settings screen's category button positions.
- Readjusted item model gap correctional values for Forge.
- Readjusted animation for the old sneaking tweak.
- Readjusted old alpha logo positioning.
- Readjusted star brightness.
- Readjusted night sky color.
- Added debugging functionality, it can be activated/deactivated with Ctrl + Shift + D in the mod's home settings screen.
- Added new tweak category: Gameplay
- Added new Combat System subcategory for Gameplay
- Added new tweak: (Combat System) Instant Bow
- Added new tweak: (Combat System) Arrow Speed - Used in conjunction with the Instant Bow tweak
- Added new tweak: (Combat System) Invincible Bow
- Added new tweak: (Combat System) Disable Miss Timer
- Added new tweak: (Combat System) Disable Cooldown
- Added new tweak: (Combat System) Disable Sweep
- Added new Experience System subcategory for Gameplay
- Added new tweak: (Experience System) Alternative Experience Bar
- Added new tweak: (Experience System) Disable Experience Bar
- Added new tweak: (Experience System) Disable Orb Spawning
- Added new tweak: (Experience System) Disable Orb Rendering
- Added new tweak: (Experience System) Disable Anvil
- Added new tweak: (Experience System) Disable Enchantment Table
- Added new Game Mechanics subcategory for Gameplay
- Added new tweak: (Game Mechanics) Old Fire
- Added new tweak: (Game Mechanics) Infinite Burn
- Added new tweak: (Game Mechanics) Instant Air Refill
- Added new tweak: (Game Mechanics) Disable Swimming
- Added new tweak: (Game Mechanics) Disable Sprinting
- Added new Hunger System subcategory for Gameplay
- Added new tweak: (Hunger System) Alternative Hunger Bar
- Added new tweak: (Hunger System) Disable Hunger Bar
- Added new tweak: (Hunger System) Disable Hunger
> Only vanilla items will be impacted. Effects given to the player are removed and the nutrition values of different foods will change.
- Added new tweak: (Hunger System) Instant Eat
- Added new tweak: (Hunger System) Old Food Stacking
> Only vanilla items will be impacted. Foods that restore half a heart can stack up to 8 like potatoes and carrots.
- Added new tweak: (Player Animation) Disable Death Topple
- Added new tweak: (Player Animation) Old Backwards Walking
- Added new tweak: (Player Animation) Old Creative Crouching
- Added new tweak: (Player Animation) Old Directional Damage
- Added new tweak: (Player Animation) Old Random Damage
- Added new tweak: (Particle Candy) Disable Nether Biome Particles
- Added new tweak: (Lighting Candy) Disable Gamma
- Added new tweak: (Lighting Candy) Old Lighting Brightness
- Added new tweak: (Tooltip Parts) Show Dye Information
- Added new tweak: (Tooltip Parts) Show Enchantment Information
- Added new tweak: (Tooltip Parts) Show Modifier Information
- Added new tweak: (Gui Interface) Old Gui Backgrounds
- Added new tweak: (Gui Interface) Custom Gui Backgrounds
- Added new tweak: (Interface Candy) Show Debug Entity IDs
- Added new tweak: (Interface Candy) Old Pause Screen
- Added new tweak: (Interface Candy) Include Mods Button for Pause Screen
- Added new tweak: (Interface Candy) Include Mods Button for Title Screen
> The "Mods" button will only appear in Fabric when Mod Menu is installed.
- Added new tweak: (Title Screen Candy) Remove Realms Button
- Added new tweak: (Inventory Candy) Modify Recipe Button
- Added new tweak: (Inventory Candy) Modify Armor Slot
- Added new tweak: (Inventory Candy) Inverted Player Lighting
- Added new tweak: (Inventory Candy) Inverted Block Lighting
- Added new tweak: (Crafting Screen) Old Crafting Table Screen
- Added new tweak: (Crafting Screen) Modify Recipe Button
- Added new tweak: (Furnace Screen) Old Furnace Screen
- Added new tweak: (Furnace Screen) Modify Recipe Button
- Added new tweak: (Sky Candy) Disable Sunrise/Sunset Colors
- Added new tweak: (Bugs) Old Ladder Gap
- Added new tweak: (Sound) Ignore Modded Mob Steps - Prevents the mod from overriding the footstep sounds for modded entities.
- Added new tweak: (Sound) Disable Chest Sounds
- Added new tweak: (Sound) Old Chest Sounds
- Added new void candy subcategory: Void Fog
- Added new tweak: (Void Fog) Creative Void Fog
- Added new tweak: (Void Fog) Creative Void Particles
- Added new tweak: (Void Fog) Light Removes Void Fog
- Added new tweak: (Void Fog) Void Fog Color
- Added new tweak: (Void Fog) Fog Encroachment Amount
- Added new tweak: (Void Fog) Fog Starting Level
- Added new tweak: (Void Fog) Void Particle Starting Level
- Added new tweak: (Void Fog) Void Particle Radius
- Added new tweak: (Void Fog) Void Particle Density
- Added new tweak: (World Candy) Old Name Tag Rendering
- Added new tweak: (Player Animation) Old Swing Interrupt
