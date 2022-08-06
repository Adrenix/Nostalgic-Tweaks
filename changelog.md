# N.T (Forge & Fabric): 1.19-v2.0.0 (Multiplayer)
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
- Added new tweak: (Inventory Candy) Inverted Player Lighting
- Added new tweak: (Inventory Candy) Inverted Block Lighting
- Added new tweak: (Sky Candy) Disable Sunrise/Sunset Colors
- Added new tweak: (Bugs) Old Ladder Gap
- Added new tweak: (Sound) Ignore Modded Mob Steps - Prevents the mod from overriding the footstep sounds for modded entities.
- Added new tweak: (Sound) Disable Chest Sounds
- Added new tweak: (Sound) Old Chest Sounds
