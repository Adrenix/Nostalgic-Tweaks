package mod.adrenix.nostalgic.common.config.v2.tweak;

import mod.adrenix.nostalgic.common.config.v2.container.group.AnimationGroup;
import mod.adrenix.nostalgic.common.config.v2.gui.SliderType;
import mod.adrenix.nostalgic.common.config.v2.gui.TweakSlider;

public abstract class AnimationTweak
{
    // Constants

    public static float SNEAK_HEIGHT = 1.41F;

    // Arm

    public static final Tweak<Boolean> OLD_ARM_SWAY = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.ARM).build();
    public static final Tweak<Boolean> ARM_SWAY_MIRROR = Tweak.builder(false, TweakSide.CLIENT, AnimationGroup.ARM).load().build();
    public static final Tweak<Integer> ARM_SWAY_INTENSITY = Tweak.builder(100, TweakSide.CLIENT, AnimationGroup.ARM).slider(TweakSlider.builder(100, 0, 300, 10).type(SliderType.INTENSITY).build()).load().build();
    public static final Tweak<Boolean> OLD_SWING = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.ARM).build();
    public static final Tweak<Boolean> OLD_SWING_INTERRUPT = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.ARM).newForUpdate().build();
    public static final Tweak<Boolean> OLD_SWING_DROPPING = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.ARM).build();
    public static final Tweak<Boolean> OLD_CLASSIC_SWING = Tweak.builder(false, TweakSide.CLIENT, AnimationGroup.ARM).newForUpdate().build();

    // Item

    public static final Tweak<Boolean> OLD_ITEM_COOLDOWN = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.ITEM).build();
    public static final Tweak<Boolean> OLD_ITEM_REEQUIP = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.ITEM).build();
    public static final Tweak<Boolean> OLD_TOOL_EXPLOSION = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.ITEM).build();

    // Mob

    public static final Tweak<Boolean> OLD_ZOMBIE_ARMS = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.MOB).build();
    public static final Tweak<Boolean> OLD_SKELETON_ARMS = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.MOB).build();
    public static final Tweak<Boolean> OLD_GHAST_CHARGING = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.MOB).build();

    // Player

    public static final Tweak<Boolean> OLD_BACKWARD_WALKING = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.PLAYER).newForUpdate().build();
    public static final Tweak<Boolean> OLD_COLLIDE_BOBBING = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.PLAYER).build();
    public static final Tweak<Boolean> OLD_VERTICAL_BOBBING = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.PLAYER).build();
    public static final Tweak<Boolean> OLD_CREATIVE_CROUCH = Tweak.builder(true, TweakSide.DYNAMIC, AnimationGroup.PLAYER).newForUpdate().build();
    public static final Tweak<Boolean> OLD_DIRECTIONAL_DAMAGE = Tweak.builder(true, TweakSide.SERVER, AnimationGroup.PLAYER).newForUpdate().build();
    public static final Tweak<Boolean> OLD_RANDOM_DAMAGE = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.PLAYER).newForUpdate().build();
    public static final Tweak<Boolean> OLD_SNEAKING = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.PLAYER).build();
    public static final Tweak<Boolean> DISABLE_DEATH_TOPPLE = Tweak.builder(true, TweakSide.CLIENT, AnimationGroup.PLAYER).newForUpdate().build();
}
