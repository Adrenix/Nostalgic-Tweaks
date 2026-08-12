package mod.adrenix.nostalgic.helper.gameplay.stamina;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Player;

// @formatter:off
public final class StaminaCodec
{
    public static final String NBT_KEY = NostalgicTweaks.MOD_ID + "_stamina";

    /**
     * @return The {@link Codec} that is used by {@link StaminaData}.
     */
    public static Codec<StaminaData> create()
    {
        return RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("stamina").forGetter(StaminaData::getStamina),
            Codec.INT.fieldOf("maximum").forGetter(StaminaData::getMaximum),
            Codec.INT.fieldOf("remaining").forGetter(StaminaData::getRemaining),
            Codec.INT.fieldOf("cooldown").forGetter(StaminaData::getCooldown),
            Codec.BOOL.fieldOf("exhausted").forGetter(StaminaData::isExhausted)
        ).apply(instance, StaminaData::new));
    }

    /**
     * Save the player's stamina data to the given compound tag.
     *
     * @param player   The {@link Player} to get stamina data from.
     * @param compound The {@link CompoundTag} to write data to.
     */
    public static void save(Player player, CompoundTag compound)
    {
        if (player instanceof StaminaHolder holder)
        {
            StaminaData.CODEC.encodeStart(NbtOps.INSTANCE, holder.nt$getStaminaData())
                .resultOrPartial(error -> NostalgicTweaks.LOGGER.error("Failed to serialize player stamina: " + error))
                .ifPresent(tag -> compound.put(NBT_KEY, tag));
        }
    }

    /**
     * Read a player's stamina data if it is present from the given compound tag.
     *
     * @param player   The {@link Player} to link stamina data to.
     * @param compound The {@link CompoundTag} to read data from.
     */
    public static void read(Player player, CompoundTag compound)
    {
        if (player instanceof StaminaHolder holder && compound.contains(NBT_KEY))
        {
            StaminaData.CODEC.parse(NbtOps.INSTANCE, compound.get(NBT_KEY))
                .resultOrPartial(error -> NostalgicTweaks.LOGGER.error("Failed to parse player stamina: " + error))
                .ifPresent(holder::nt$setStaminaData);
        }
    }

    // @formatter:on
    private StaminaCodec()
    {
    }
}
