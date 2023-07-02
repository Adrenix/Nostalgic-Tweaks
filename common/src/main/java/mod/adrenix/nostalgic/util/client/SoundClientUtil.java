package mod.adrenix.nostalgic.util.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.common.SoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SoundClientUtil
{
    /**
     * Disables various sounds or plays various sounds depending on various tweak states. If this method returns a state
     * of {@code true}, then the sound callback/event should be canceled.
     *
     * @param level       The current client level.
     * @param x           The x-position of the sound.
     * @param y           The y-position of the sound.
     * @param z           The z-position of the sound.
     * @param soundHolder A holder with a sound event.
     * @param playSound   A consumer function that will play the given sound.
     * @return Whether the sound at the position was handled.
     */
    public static boolean isSoundAtPositionHandled(ClientLevel level, double x, double y, double z, Holder<SoundEvent> soundHolder, SoundUtil.PlaySound playSound)
    {
        if (SoundUtil.isSoundAtPositionHandled(level, x, y, z, soundHolder, playSound))
            return true;

        if (soundHolder == null)
            return false;

        /*
            Entity Walking Sounds:

            The following logic is intended for multiplayer support.

            While this does work in ideal environments, there may be points during play where sounds can become
            inconsistent if there is significant latency or if there is a path without the 'entity.' and '.step'
            identifiers.
         */

        SoundEvent sound = soundHolder.value();
        BlockPos blockPos = BlockPos.containing(x, y, z);

        boolean isEntityStep = sound.getLocation().getPath().contains("entity.") && sound.getLocation()
            .getPath()
            .contains(".step");

        if (ModConfig.Sound.oldStep() && !Minecraft.getInstance().hasSingleplayerServer() && isEntityStep)
        {
            Entity entity = null;

            for (Entity next : level.entitiesForRendering())
            {
                if (next instanceof ItemEntity)
                    continue;

                boolean isX = MathUtil.tolerance((int) next.getX(), (int) x);
                boolean isY = MathUtil.tolerance((int) next.getY(), (int) y);
                boolean isZ = MathUtil.tolerance((int) next.getZ(), (int) z);

                if (isX && isY && isZ)
                {
                    entity = next;
                    break;
                }
            }

            if (entity == null)
                return false;

            boolean isMinecraftEntity = entity.getType().getDescriptionId().contains("minecraft");
            boolean isEntityIgnored = entity instanceof Spider || entity instanceof Silverfish;
            boolean isModdedIgnored = ModConfig.Sound.ignoreModdedStep() && !isMinecraftEntity;

            if (isEntityIgnored)
                return true;
            else if (!isModdedIgnored)
            {
                BlockState standing = level.getBlockState(blockPos.below());

                if (!standing.getFluidState().isEmpty())
                    return false;
                else if (standing.is(Blocks.AIR))
                    return true;

                BlockState inside = level.getBlockState(blockPos);
                SoundType soundType = inside.is(BlockTags.INSIDE_STEP_SOUND_BLOCKS) ? inside.getSoundType() : standing.getSoundType();

                playSound.accept(x, y, z, soundType.getStepSound(), entity.getSoundSource(), soundType.getVolume() * 0.15F, soundType.getPitch(), false);

                return true;
            }
        }

        return false;
    }
}
