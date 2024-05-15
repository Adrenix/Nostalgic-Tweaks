package mod.adrenix.nostalgic.tweak.container.group;

import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

// @formatter:off
public interface SoundGroup
{
    // Music

    Container MUSIC = Container.group(Category.SOUND, "music").color(0x8CF1FF).icon(Icons.MUSIC_NOTES).build();

    // Ambient Sound

    Container AMBIENT = Container.group(Category.SOUND, "ambient").color(0xEFD251).icon(Items.MUSIC_DISC_13).build();

    // Block Sound

    Container BLOCK = Container.group(Category.SOUND, "block").color(0xCC8663).icon(Blocks.JUKEBOX).build();
    Container BLOCK_BED = Container.group(BLOCK, "block_bed").color(0xDB433B).icon(Blocks.RED_BED).build();
    Container BLOCK_LAVA = Container.group(BLOCK, "block_lava").color(0xE38629).icon(Items.LAVA_BUCKET).build();
    Container BLOCK_CHEST = Container.group(BLOCK, "block_chest").color(0xFFB444).icon(Blocks.CHEST).build();
    Container BLOCK_FURNACE = Container.group(BLOCK, "block_furnace").color(0x8B8B8B).icon(Blocks.FURNACE).build();

    // Damage Sound

    Container DAMAGE = Container.group(Category.SOUND, "damage").color(0xB6896C).icon(Items.PLAYER_HEAD).build();

    // Mob Sound

    Container MOB = Container.group(Category.SOUND, "mob").color(0x208A1E).icon(Items.CREEPER_HEAD).build();
    Container MOB_FISH = Container.group(MOB, "mob_fish").color(0x6B9F93).icon(Items.COD).build();
    Container MOB_SQUID = Container.group(MOB, "mob_squid").color(0xA7CCE5).icon(Items.INK_SAC).build();
    Container MOB_GENERIC = Container.group(MOB, "mob_generic").color(0x7BC653).icon(Items.ZOMBIE_HEAD).build();

    // Experience Sound

    Container EXPERIENCE = Container.group(Category.SOUND, "experience").color(0xC0F283).icon(Items.EXPERIENCE_BOTTLE).build();

    // Disabled Sound

    Container DISABLED = Container.group(Category.SOUND, "disabled").color(0xDB433B).icon(Blocks.BARRIER).build();
}
