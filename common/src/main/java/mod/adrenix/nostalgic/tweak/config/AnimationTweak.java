package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.tweak.container.group.AnimationGroup;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import mod.adrenix.nostalgic.tweak.factory.TweakNumber;
import mod.adrenix.nostalgic.tweak.gui.SliderType;

// @formatter:off
public interface AnimationTweak
{
    // Constants

    // TODO: Move to animation utility
    float SNEAK_HEIGHT = 1.41F;

    // Arm

    TweakFlag OLD_ARM_SWAY = TweakFlag.client(true, AnimationGroup.ARM).build();
    TweakFlag ARM_SWAY_MIRROR = TweakFlag.client(false, AnimationGroup.ARM).load().build();
    TweakNumber<Integer> ARM_SWAY_INTENSITY = TweakNumber.client(100, AnimationGroup.ARM).slider(0, 300, 10, SliderType.INTENSITY).build();

    TweakFlag OLD_SWING = TweakFlag.client(true, AnimationGroup.ARM).build();
    TweakFlag OLD_SWING_INTERRUPT = TweakFlag.client(true, AnimationGroup.ARM).newForUpdate().build();
    TweakFlag OLD_SWING_DROPPING = TweakFlag.client(true, AnimationGroup.ARM).build();
    TweakFlag OLD_CLASSIC_SWING = TweakFlag.client(false, AnimationGroup.ARM).newForUpdate().build();

    // Item

    TweakFlag OLD_ITEM_COOLDOWN = TweakFlag.client(true, AnimationGroup.ITEM).build();
    TweakFlag OLD_ITEM_REEQUIP = TweakFlag.client(true, AnimationGroup.ITEM).build();
    TweakFlag OLD_TOOL_EXPLOSION = TweakFlag.client(true, AnimationGroup.ITEM).build();

    // Mob

    TweakFlag OLD_ZOMBIE_ARMS = TweakFlag.client(true, AnimationGroup.MOB).build();
    TweakFlag OLD_SKELETON_ARMS = TweakFlag.client(true, AnimationGroup.MOB).build();
    TweakFlag OLD_GHAST_CHARGING = TweakFlag.client(true, AnimationGroup.MOB).build();

    // Player

    TweakFlag OLD_BACKWARD_WALKING = TweakFlag.client(true, AnimationGroup.PLAYER).newForUpdate().build();
    TweakFlag OLD_COLLIDE_BOBBING = TweakFlag.client(true, AnimationGroup.PLAYER).build();
    TweakFlag OLD_VERTICAL_BOBBING = TweakFlag.client(true, AnimationGroup.PLAYER).build();
    TweakFlag OLD_CREATIVE_CROUCH = TweakFlag.dynamic(true, AnimationGroup.PLAYER).newForUpdate().build();
    TweakFlag OLD_DIRECTIONAL_DAMAGE = TweakFlag.server(true, AnimationGroup.PLAYER).newForUpdate().build();
    TweakFlag OLD_RANDOM_DAMAGE = TweakFlag.client(true, AnimationGroup.PLAYER).newForUpdate().build();
    TweakFlag OLD_SNEAKING = TweakFlag.client(true, AnimationGroup.PLAYER).build();
    TweakFlag DISABLE_DEATH_TOPPLE = TweakFlag.client(true, AnimationGroup.PLAYER).newForUpdate().build();
}
