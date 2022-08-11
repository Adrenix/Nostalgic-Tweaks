package mod.adrenix.nostalgic.mixin.client.world;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientLevelMixin
{
    /* Shadows */

    @Shadow @Final private MinecraftClient minecraft;
    @Shadow public abstract void playLocalSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean distanceDelay);

    /**
     * Brings back the old nether shading.
     * Controlled by the old lighting tweak.
     */
    @Redirect
    (
        method = "getShade",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;constantAmbientLight()Z"
        )
    )
    private boolean NT$onGetNetherShade(DimensionEffects instance)
    {
        if (ModConfig.Candy.oldNetherLighting() && MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().world.getRegistryKey() == World.NETHER)
            return false;
        return instance.isDarkened();
    }

    /**
     * Disables sounds when attacking.
     * Controlled by the sound attack tweak.
     *
     * Disables chest sounds on opening.
     * Controlled by various old chest tweaks.
     *
     * Disables unique mob stepping sounds in multiplayer.
     * Controlled by the old mob steps tweak.
     */
    @Inject
    (
        method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFJ)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onPlayPositionedSound(PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, long randomSeed, CallbackInfo callback)
    {
        /* Attack Sounds */

        boolean isAttack = sound == SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK ||
            sound == SoundEvents.ENTITY_PLAYER_ATTACK_CRIT ||
            sound == SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE ||
            sound == SoundEvents.ENTITY_PLAYER_ATTACK_STRONG ||
            sound == SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP ||
            sound == SoundEvents.ENTITY_PLAYER_ATTACK_WEAK
        ;

        if (ModConfig.Sound.oldAttack() && isAttack)
        {
            callback.cancel();
            return;
        }

        /* Chest Sounds */

        BlockPos pos = new BlockPos(x, y, z);
        ClientWorld level = this.minecraft.world;
        boolean isWoodenChest = sound == SoundEvents.BLOCK_CHEST_OPEN || sound == SoundEvents.BLOCK_CHEST_CLOSE;
        boolean isEnderChest = sound == SoundEvents.BLOCK_ENDER_CHEST_OPEN || sound == SoundEvents.BLOCK_ENDER_CHEST_CLOSE;
        boolean isChestSound = false;

        if (level == null)
            return;

        BlockState state = level.getBlockState(pos);

        if (ModConfig.Sound.disableChest() && state.isOf(Blocks.CHEST) && isWoodenChest)
            isChestSound = true;
        else if (ModConfig.Sound.disableChest() && state.isOf(Blocks.ENDER_CHEST) && isEnderChest)
            isChestSound = true;
        else if (ModConfig.Sound.disableChest() && state.isOf(Blocks.TRAPPED_CHEST) && isWoodenChest)
            isChestSound = true;

        if (isChestSound)
        {
            callback.cancel();
            return;
        }

        boolean isOldChest = false;

        if (ModConfig.Sound.oldChest() && state.isOf(Blocks.CHEST) && isWoodenChest)
            isOldChest = true;
        else if (ModConfig.Sound.oldChest() && state.isOf(Blocks.ENDER_CHEST) && isEnderChest)
            isOldChest = true;
        else if (ModConfig.Sound.oldChest() && state.isOf(Blocks.TRAPPED_CHEST) && isWoodenChest)
            isOldChest = true;

        if (isOldChest && MinecraftClient.getInstance().world != null)
        {
            SoundEvent chestSound = SoundEvents.BLOCK_WOODEN_DOOR_OPEN;
            if (sound == SoundEvents.BLOCK_CHEST_CLOSE || sound == SoundEvents.BLOCK_ENDER_CHEST_CLOSE)
                chestSound = SoundEvents.BLOCK_WOODEN_DOOR_CLOSE;

            RandomSource randomSource = MinecraftClient.getInstance().world.random;
            this.playLocalSound(x, y, z, chestSound, SoundCategory.BLOCKS, 1.0F, randomSource.nextFloat() * 0.1f + 0.9f, false);

            callback.cancel();
            return;
        }

        /*
            Bed & Door Placement Sounds:

            Disables the placement sound if the block at the given position is a bed or door.
            Controlled by the old door sound tweak and the old bed sound tweak.
         */

        boolean isBlockedSound = false;

        if (ModConfig.Sound.disableDoor() && state.getBlock() instanceof DoorBlock)
            isBlockedSound = true;
        else if (ModConfig.Sound.disableBed() && state.getBlock() instanceof BedBlock)
            isBlockedSound = true;

        if (isBlockedSound)
        {
            callback.cancel();
            return;
        }

        /*
            Entity Walking Sounds:

            The following logic is intended for multiplayer support.

            While this does work in ideal environments, there may be points during play where sounds can become
            inconsistent if there is significant latency or if there is a path without the 'entity.' and '.step'
            identifiers.
         */

        boolean isEntityStep = sound.getId().getPath().contains("entity.") && sound.getId().getPath().contains(".step");

        if (ModConfig.Sound.oldStep() && !MinecraftClient.getInstance().isIntegratedServerRunning() && isEntityStep)
        {
            Entity entity = null;

            for (Entity next : level.getEntities())
            {
                if (next instanceof ItemEntity)
                    continue;

                boolean isX = NostalgicUtil.Numbers.tolerance((int) next.getX(), (int) x);
                boolean isY = NostalgicUtil.Numbers.tolerance((int) next.getY(), (int) y);
                boolean isZ = NostalgicUtil.Numbers.tolerance((int) next.getZ(), (int) z);

                if (isX && isY && isZ)
                {
                    entity = next;
                    break;
                }
            }

            if (entity == null)
                return;

            boolean isMinecraftEntity = entity.getType().getTranslationKey().contains("minecraft");
            boolean isEntityIgnored = entity instanceof SpiderEntity || entity instanceof SilverfishEntity;
            boolean isModdedIgnored = ModConfig.Sound.ignoreModdedStep() && !isMinecraftEntity;

            if (isEntityIgnored)
                callback.cancel();
            else if (!isModdedIgnored)
            {
                BlockState standing = level.getBlockState(pos.down());

                if (standing.getMaterial().isLiquid())
                    return;
                else if (standing.isOf(Blocks.AIR))
                {
                    callback.cancel();
                    return;
                }

                BlockState inside = level.getBlockState(pos);
                BlockSoundGroup soundType = inside.isIn(BlockTags.INSIDE_STEP_SOUND_BLOCKS) ? inside.getSoundGroup() : standing.getSoundGroup();
                this.playLocalSound(x, y, z, soundType.getStepSound(), entity.getSoundCategory(), soundType.getVolume() * 0.15F, soundType.getPitch(), false);

                callback.cancel();
            }
        }
    }
}
