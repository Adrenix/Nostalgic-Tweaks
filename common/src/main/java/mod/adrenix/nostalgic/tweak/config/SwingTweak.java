package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.group.SwingGroup;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import mod.adrenix.nostalgic.tweak.factory.TweakItemMap;
import mod.adrenix.nostalgic.tweak.factory.TweakNumber;
import mod.adrenix.nostalgic.tweak.gui.SliderType;
import mod.adrenix.nostalgic.tweak.gui.TweakSlider;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;
import mod.adrenix.nostalgic.util.common.asset.Icons;

public interface SwingTweak
{
    // Constants

    int DISABLED = -1;
    int NEW_SPEED = 6;
    int OLD_SPEED = 8;
    int MIN_SPEED = 0;
    int MAX_SPEED = 16;
    int PHOTOSENSITIVE = 0;

    // Slider Builders

    /**
     * These sliders are intended for swing tweaks that can have a "disabled" state when the slider value is set to a
     * value of {@code -1}. The custom speed maps will need manually updated if this method body changes.
     */
    private static TweakNumber.Builder<Integer> disable(TweakSlider.Factory<TweakNumber.Builder<Integer>> builder)
    {
        return builder.range(DISABLED, MAX_SPEED).type(SliderType.SWING);
    }

    /**
     * These sliders are intended for swing tweaks that are always active. The values from these sliders can only be
     * deactivated by the override speed tweak. The custom speed maps will need manually updated if this method body
     * changes.
     */
    private static TweakNumber.Builder<Integer> enable(TweakSlider.Factory<TweakNumber.Builder<Integer>> builder)
    {
        return builder.range(MIN_SPEED, MAX_SPEED).type(SliderType.SWING);
    }

    // Global Speeds

    /**
     * When this tweak is enabled, it will override every tweak within this category. This is useful for when the mod
     * goes into a disabled state.
     */
    TweakFlag OVERRIDE_SPEEDS = TweakFlag.client(false, Category.SWING).whenDisabled(true).top().build();

    /**
     * Assigns a global swing speed for attacking. This will override everything.
     */
    TweakNumber<Integer> ATTACK_GLOBAL_SPEED = TweakNumber.client(DISABLED, SwingGroup.GLOBAL)
        .newForUpdate()
        .apply(SwingTweak::disable)
        .build();

    /**
     * Assigns a global swing speed for using an item or block. This will override everything.
     */
    TweakNumber<Integer> USE_GLOBAL_SPEED = TweakNumber.client(DISABLED, SwingGroup.GLOBAL)
        .newForUpdate()
        .apply(SwingTweak::disable)
        .build();

    // Item Speeds

    /**
     * Assigns the global attacking swing speed for any item that is a block, tool, or sword.
     */
    TweakNumber<Integer> ATTACK_ITEM_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .apply(SwingTweak::enable)
        .build();

    /**
     * Assigns the global using item swing speed for any item that is a block, tool, or sword.
     */
    TweakNumber<Integer> USE_ITEM_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .apply(SwingTweak::enable)
        .build();

    /**
     * Assigns the global attack swing speed for tool items.
     */
    TweakNumber<Integer> ATTACK_TOOL_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .apply(SwingTweak::enable)
        .build();

    /**
     * Assigns the global using swing speed for tool items.
     */
    TweakNumber<Integer> USE_TOOL_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .apply(SwingTweak::enable)
        .build();

    /**
     * Assigns the global attack swing speed for block items.
     */
    TweakNumber<Integer> ATTACK_BLOCK_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .apply(SwingTweak::enable)
        .build();

    /**
     * Assigns the global using swing speed for block items.
     */
    TweakNumber<Integer> USE_BLOCK_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .apply(SwingTweak::enable)
        .build();

    /**
     * Assigns the global attack swing speed for sword items.
     */
    TweakNumber<Integer> ATTACK_SWORD_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .apply(SwingTweak::enable)
        .build();

    /**
     * Assigns the global using swing speed for sword items.
     */
    TweakNumber<Integer> USE_SWORD_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .apply(SwingTweak::enable)
        .build();

    // Potion Speeds

    /**
     * Assigns a global attack swing speed when the player has the haste potion effect.
     */
    TweakNumber<Integer> ATTACK_HASTE_SPEED = TweakNumber.client(DISABLED, SwingGroup.POTION)
        .newForUpdate()
        .apply(SwingTweak::disable)
        .build();

    /**
     * Assigns a global use swing speed when the player as the haste potion effect.
     */
    TweakNumber<Integer> USE_HASTE_SPEED = TweakNumber.client(DISABLED, SwingGroup.POTION)
        .newForUpdate()
        .apply(SwingTweak::disable)
        .build();

    /**
     * Assigns a global attack swing speed when the player has the fatigue potion effect.
     */
    TweakNumber<Integer> ATTACK_FATIGUE_SPEED = TweakNumber.client(DISABLED, SwingGroup.POTION)
        .newForUpdate()
        .apply(SwingTweak::disable)
        .build();

    /**
     * Assigns a global use swing speed when the player has the fatigue potion effect.
     */
    TweakNumber<Integer> USE_FATIGUE_SPEED = TweakNumber.client(DISABLED, SwingGroup.POTION)
        .newForUpdate()
        .apply(SwingTweak::disable)
        .build();

    // Custom Speeds

    /**
     * Assigns individual items a custom swing speed when the attack swing is used.
     */
    TweakItemMap<Integer> ATTACK_SWING_SPEEDS = TweakItemMap.client(new ItemMap<>(OLD_SPEED), SwingGroup.CUSTOM)
        .slider(MIN_SPEED, MAX_SPEED, SliderType.SWING)
        .icon(Icons.BREAK_WOOD)
        .build();

    /**
     * Assigns individual items a custom swing speed when the use item or block swing is used.
     */
    TweakItemMap<Integer> USE_SWING_SPEEDS = TweakItemMap.client(new ItemMap<>(OLD_SPEED), SwingGroup.CUSTOM)
        .slider(MIN_SPEED, MAX_SPEED, SliderType.SWING)
        .icon(Icons.BREAK_WOOD)
        .build();
}
