package mod.adrenix.nostalgic.common.config.v2.tweak;

import mod.adrenix.nostalgic.common.config.v2.container.TweakCategory;
import mod.adrenix.nostalgic.common.config.v2.container.group.SwingGroup;
import mod.adrenix.nostalgic.common.config.v2.gui.TweakSlider;

import java.util.LinkedHashMap;

public abstract class SwingTweak
{
    // Constants

    public static final int DISABLED = -1;
    public static final int NEW_SPEED = 6;
    public static final int OLD_SPEED = 8;
    public static final int MIN_SPEED = 0;
    public static final int MAX_SPEED = 16;

    // Builders

    private static TweakSlider slider(Number value, Number min)
    {
        return TweakSlider.builder(value, min, MAX_SPEED, 1).build();
    }

    private static TweakSlider disabled() { return slider(DISABLED, DISABLED); }
    private static TweakSlider enabled() { return slider(OLD_SPEED, MIN_SPEED); }

    // Global Speeds

    public static final Tweak<Boolean> OVERRIDE_SPEEDS = Tweak.builder(false, TweakSide.CLIENT, TweakCategory.SWING).top().whenDisabled(true).build();
    public static final Tweak<Boolean> LEFT_CLICK_SPEED_ON_BLOCK_INTERACT = Tweak.builder(true, TweakSide.CLIENT, TweakCategory.SWING).newForUpdate().top().whenDisabled(true).load().build();
    public static final Tweak<Integer> LEFT_GLOBAL_SPEED = Tweak.builder(DISABLED, TweakSide.CLIENT, TweakCategory.SWING).newForUpdate().top().slider(disabled()).load().build();
    public static final Tweak<Integer> RIGHT_GLOBAL_SPEED = Tweak.builder(DISABLED, TweakSide.CLIENT, TweakCategory.SWING).newForUpdate().top().slider(disabled()).load().build();

    // Item Speeds

    public static final Tweak<Integer> LEFT_ITEM_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.ITEM).newForUpdate().top().whenDisabled(NEW_SPEED).slider(enabled()).load().build();
    public static final Tweak<Integer> RIGHT_ITEM_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.ITEM).newForUpdate().top().whenDisabled(NEW_SPEED).slider(enabled()).load().build();
    public static final Tweak<Integer> LEFT_TOOL_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.ITEM).newForUpdate().top().whenDisabled(NEW_SPEED).slider(enabled()).load().build();
    public static final Tweak<Integer> RIGHT_TOOL_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.ITEM).newForUpdate().top().whenDisabled(NEW_SPEED).slider(enabled()).load().build();
    public static final Tweak<Integer> LEFT_BLOCK_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.ITEM).newForUpdate().top().whenDisabled(NEW_SPEED).slider(enabled()).load().build();
    public static final Tweak<Integer> RIGHT_BLOCK_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.ITEM).newForUpdate().top().whenDisabled(NEW_SPEED).slider(enabled()).load().build();
    public static final Tweak<Integer> LEFT_SWORD_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.ITEM).newForUpdate().top().whenDisabled(NEW_SPEED).slider(enabled()).load().build();
    public static final Tweak<Integer> RIGHT_SWORD_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.ITEM).newForUpdate().top().whenDisabled(NEW_SPEED).slider(enabled()).load().build();

    // Potion Speeds

    public static final Tweak<Integer> LEFT_HASTE_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.POTION).newForUpdate().top().whenDisabled(NEW_SPEED).slider(disabled()).load().build();
    public static final Tweak<Integer> RIGHT_HASTE_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.POTION).newForUpdate().top().whenDisabled(NEW_SPEED).slider(disabled()).load().build();
    public static final Tweak<Integer> LEFT_FATIGUE_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.POTION).newForUpdate().top().whenDisabled(NEW_SPEED).slider(disabled()).load().build();
    public static final Tweak<Integer> RIGHT_FATIGUE_SPEED = Tweak.builder(OLD_SPEED, TweakSide.CLIENT, SwingGroup.POTION).newForUpdate().top().whenDisabled(NEW_SPEED).slider(disabled()).load().build();

    // Custom Speeds

    public static final Tweak<LinkedHashMap<String, Integer>> LEFT_CLICK_SWING_SPEEDS = Tweak.builder(new LinkedHashMap<String, Integer>(), TweakSide.CLIENT, TweakCategory.SWING).load().build();
    public static final Tweak<LinkedHashMap<String, Integer>> RIGHT_CLICK_SWING_SPEEDS = Tweak.builder(new LinkedHashMap<String, Integer>(), TweakSide.CLIENT, TweakCategory.SWING).load().build();
}
