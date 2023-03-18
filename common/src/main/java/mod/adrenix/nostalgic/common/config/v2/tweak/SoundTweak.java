package mod.adrenix.nostalgic.common.config.v2.tweak;

import mod.adrenix.nostalgic.common.config.v2.container.group.SoundGroup;

public abstract class SoundTweak
{
    // Ambience

    public static final Tweak<Boolean> DISABLE_NETHER_AMBIENCE = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.AMBIENT).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_WATER_AMBIENCE = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.AMBIENT).newForUpdate().build();

    // Bed Block

    public static final Tweak<Boolean> OLD_BED = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.BLOCK_BED).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_BED_PLACE = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.BLOCK_BED).newForUpdate().build();

    // Chest Block

    public static final Tweak<Boolean> OLD_CHEST = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.BLOCK_CHEST).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_CHEST = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.BLOCK_CHEST).newForUpdate().build();

    // Lava Block

    public static final Tweak<Boolean> DISABLE_LAVA_AMBIENCE = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.BLOCK_LAVA).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_LAVA_POP = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.BLOCK_LAVA).newForUpdate().build();

    // Blocks

    public static final Tweak<Boolean> DISABLE_GROWTH = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.BLOCK).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_FURNACE = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.BLOCK).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_DOOR_PLACE = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.BLOCK).build();

    // Damage

    public static final Tweak<Boolean> OLD_ATTACK = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.DAMAGE).build();
    public static final Tweak<Boolean> OLD_HURT = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.DAMAGE).build();
    public static final Tweak<Boolean> OLD_FALL = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.DAMAGE).build();

    // Experience

    public static final Tweak<Boolean> OLD_XP = Tweak.builder(false, TweakSide.CLIENT, SoundGroup.EXPERIENCE).build();
    public static final Tweak<Boolean> DISABLE_XP_PICKUP = Tweak.builder(false, TweakSide.CLIENT, SoundGroup.EXPERIENCE).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_XP_LEVEL = Tweak.builder(false, TweakSide.CLIENT, SoundGroup.EXPERIENCE).newForUpdate().build();

    // Mobs

    public static final Tweak<Boolean> DISABLE_GENERIC_SWIM = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.MOB_GENERIC).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_FISH_SWIM = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.MOB_FISH).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_FISH_HURT = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.MOB_FISH).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_FISH_DEATH = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.MOB_FISH).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_SQUID = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.MOB_SQUID).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_GLOW_SQUID_OTHER = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.MOB_SQUID).newForUpdate().build();
    public static final Tweak<Boolean> DISABLE_GLOW_SQUID_AMBIENCE = Tweak.builder(false, TweakSide.CLIENT, SoundGroup.MOB_SQUID).newForUpdate().build();
    public static final Tweak<Boolean> OLD_STEP = Tweak.builder(true, TweakSide.CLIENT, SoundGroup.MOB).build();
    public static final Tweak<Boolean> IGNORE_MODDED_STEP = Tweak.builder(false, TweakSide.CLIENT, SoundGroup.MOB).newForUpdate().build();
}
