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
     * Assigns a left-click speed when a player interacts with a block.
     */
    TweakFlag LEFT_CLICK_SPEED_ON_BLOCK_INTERACT = TweakFlag.client(true, SwingGroup.GLOBAL)
        .newForUpdate()
        .whenDisabled(true)
        .load()
        .build();

    /**
     * Assigns a global swing speed for the left-hand. This will override everything.
     */
    TweakNumber<Integer> LEFT_GLOBAL_SPEED = TweakNumber.client(DISABLED, SwingGroup.GLOBAL)
        .newForUpdate()
        .apply(SwingTweak::disable)
        .load()
        .build();

    /**
     * Assigns a global swing speed for the right-hand. This will override everything.
     */
    TweakNumber<Integer> RIGHT_GLOBAL_SPEED = TweakNumber.client(DISABLED, SwingGroup.GLOBAL)
        .newForUpdate()
        .apply(SwingTweak::disable)
        .load()
        .build();

    // Item Speeds

    /**
     * Assigns a global left-hand swing speed for any item that is a block, tool, or sword.
     */
    TweakNumber<Integer> LEFT_ITEM_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::enable)
        .load()
        .build();

    /**
     * Assigns a global right-hand swing speed for any item that is a block, tool, or sword.
     */
    TweakNumber<Integer> RIGHT_ITEM_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::enable)
        .load()
        .build();

    /**
     * Assigns a global left-hand swing speed for tool items.
     */
    TweakNumber<Integer> LEFT_TOOL_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::enable)
        .load()
        .build();

    /**
     * Assigns a global right-hand swing speed for tool items.
     */
    TweakNumber<Integer> RIGHT_TOOL_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::enable)
        .load()
        .build();

    /**
     * Assigns a global left-hand swing speed for block items.
     */
    TweakNumber<Integer> LEFT_BLOCK_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::enable)
        .load()
        .build();

    /**
     * Assigns a global right-hand swing speed for block items.
     */
    TweakNumber<Integer> RIGHT_BLOCK_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::enable)
        .load()
        .build();

    /**
     * Assigns a global left-hand swing speed for sword items.
     */
    TweakNumber<Integer> LEFT_SWORD_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::enable)
        .load()
        .build();

    /**
     * Assigns a global right-hand swing speed for sword items.
     */
    TweakNumber<Integer> RIGHT_SWORD_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.ITEM)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::enable)
        .load()
        .build();

    // Potion Speeds

    /**
     * Assigns a global left-hand swing speed when the player has the haste potion effect.
     */
    TweakNumber<Integer> LEFT_HASTE_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.POTION)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::disable)
        .load()
        .build();

    /**
     * Assigns a global right-hand swing speed when the player as the haste potion effect.
     */
    TweakNumber<Integer> RIGHT_HASTE_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.POTION)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::disable)
        .load()
        .build();

    /**
     * Assigns a global left-hand swing speed when the player has the fatigue potion effect.
     */
    TweakNumber<Integer> LEFT_FATIGUE_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.POTION)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::disable)
        .load()
        .build();

    /**
     * Assigns a global right-hand swing speed when the player has the fatigue potion effect.
     */
    TweakNumber<Integer> RIGHT_FATIGUE_SPEED = TweakNumber.client(OLD_SPEED, SwingGroup.POTION)
        .newForUpdate()
        .whenDisabled(NEW_SPEED)
        .apply(SwingTweak::disable)
        .load()
        .build();

    // Custom Speeds

    /**
     * Assigns individual items a custom swing speed when the left mouse button is clicked.
     */
    TweakItemMap<Integer> LEFT_CLICK_SWING_SPEEDS = TweakItemMap.client(new ItemMap<>(OLD_SPEED), SwingGroup.CUSTOM)
        .slider(MIN_SPEED, MAX_SPEED, SliderType.SWING)
        .icon(Icons.BREAK_WOOD)
        .load()
        .build();

    /**
     * Assigns individual items a custom swing speed when the right mouse button is clicked.
     */
    TweakItemMap<Integer> RIGHT_CLICK_SWING_SPEEDS = TweakItemMap.client(new ItemMap<>(OLD_SPEED), SwingGroup.CUSTOM)
        .slider(MIN_SPEED, MAX_SPEED, SliderType.SWING)
        .icon(Icons.BREAK_WOOD)
        .load()
        .build();
}
