package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.tweak.container.group.SoundGroup;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import mod.adrenix.nostalgic.tweak.factory.TweakStringSet;
import mod.adrenix.nostalgic.tweak.listing.ListingSuggestion;
import mod.adrenix.nostalgic.tweak.listing.StringSet;

// @formatter:off
public interface SoundTweak
{
    // Disabled

    TweakStringSet DISABLED_POSITIONED_SOUNDS = TweakStringSet.client(new StringSet(ListingSuggestion.SOUND), SoundGroup.DISABLED).newForUpdate().build();
    TweakStringSet DISABLED_GLOBAL_SOUNDS = TweakStringSet.client(new StringSet(ListingSuggestion.SOUND), SoundGroup.DISABLED).newForUpdate().build();

    // Ambience

    TweakFlag OLD_CAVE_AMBIENCE = TweakFlag.client(true, SoundGroup.AMBIENT).newForUpdate().build();
    TweakFlag DISABLE_NETHER_AMBIENCE = TweakFlag.client(true, SoundGroup.AMBIENT).newForUpdate().build();
    TweakFlag DISABLE_WATER_AMBIENCE = TweakFlag.client(true, SoundGroup.AMBIENT).newForUpdate().build();

    // Bed Block

    TweakFlag OLD_BED = TweakFlag.client(true, SoundGroup.BLOCK_BED).newForUpdate().build();
    TweakFlag DISABLE_BED_PLACE = TweakFlag.client(true, SoundGroup.BLOCK_BED).newForUpdate().build();

    // Chest Block

    TweakFlag OLD_CHEST = TweakFlag.client(true, SoundGroup.BLOCK_CHEST).newForUpdate().build();
    TweakFlag DISABLE_CHEST = TweakFlag.client(true, SoundGroup.BLOCK_CHEST).newForUpdate().build();

    // Lava Block

    TweakFlag DISABLE_LAVA_AMBIENCE = TweakFlag.client(true, SoundGroup.BLOCK_LAVA).newForUpdate().build();
    TweakFlag DISABLE_LAVA_POP = TweakFlag.client(true, SoundGroup.BLOCK_LAVA).newForUpdate().build();

    // Furnace Block

    TweakFlag DISABLE_FURNACE = TweakFlag.client(true, SoundGroup.BLOCK_FURNACE).newForUpdate().build();
    TweakFlag DISABLE_BLAST_FURNACE = TweakFlag.client(true, SoundGroup.BLOCK_FURNACE).newForUpdate().build();

    // Blocks

    TweakFlag DISABLE_GROWTH = TweakFlag.client(true, SoundGroup.BLOCK).newForUpdate().build();
    TweakFlag DISABLE_DOOR_PLACE = TweakFlag.client(true, SoundGroup.BLOCK).build();

    // Damage

    TweakFlag OLD_ATTACK = TweakFlag.client(true, SoundGroup.DAMAGE).build();
    TweakFlag OLD_HURT = TweakFlag.client(true, SoundGroup.DAMAGE).build();
    TweakFlag OLD_FALL = TweakFlag.client(true, SoundGroup.DAMAGE).build();

    // Experience

    TweakFlag OLD_XP = TweakFlag.client(false, SoundGroup.EXPERIENCE).build();
    TweakFlag DISABLE_XP_PICKUP = TweakFlag.client(false, SoundGroup.EXPERIENCE).newForUpdate().build();
    TweakFlag DISABLE_XP_LEVEL = TweakFlag.client(false, SoundGroup.EXPERIENCE).newForUpdate().build();

    // Mobs

    TweakFlag DISABLE_GENERIC_SWIM = TweakFlag.client(true, SoundGroup.MOB_GENERIC).newForUpdate().build();
    TweakFlag DISABLE_FISH_SWIM = TweakFlag.client(true, SoundGroup.MOB_FISH).newForUpdate().build();
    TweakFlag DISABLE_FISH_HURT = TweakFlag.client(true, SoundGroup.MOB_FISH).newForUpdate().build();
    TweakFlag DISABLE_FISH_DEATH = TweakFlag.client(true, SoundGroup.MOB_FISH).newForUpdate().build();
    TweakFlag DISABLE_SQUID = TweakFlag.client(true, SoundGroup.MOB_SQUID).newForUpdate().build();
    TweakFlag DISABLE_GLOW_SQUID_OTHER = TweakFlag.client(true, SoundGroup.MOB_SQUID).newForUpdate().build();
    TweakFlag DISABLE_GLOW_SQUID_AMBIENCE = TweakFlag.client(false, SoundGroup.MOB_SQUID).newForUpdate().build();
    TweakFlag OLD_STEP = TweakFlag.client(true, SoundGroup.MOB).build();
    TweakFlag IGNORE_MODDED_STEP = TweakFlag.client(false, SoundGroup.MOB).newForUpdate().build();
}
