package mod.adrenix.nostalgic.mixin.client.world;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.BlockClientUtil;
import mod.adrenix.nostalgic.util.client.FogUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    /* Shadows */

    @Shadow
    public abstract void playLocalSound(double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay);

    /**
     * Brings back the old nether shading. Controlled by the old lighting tweak.
     */
    @Redirect(
        method = "getShade",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;constantAmbientLight()Z"
        )
    )
    private boolean NT$onGetNetherShade(DimensionSpecialEffects instance)
    {
        boolean isOldNether = ModConfig.Candy.oldNetherLighting() &&
            Minecraft.getInstance().level != null &&
            Minecraft.getInstance().level.dimension() == Level.NETHER;

        if (isOldNether)
            return false;

        return instance.constantAmbientLight();
    }

    /**
     * Adjusts the darkness of the sky color at night to match the old star colors. Controlled by the old stars tweak.
     */
    @ModifyArg(
        method = "getSkyColor",
        index = 1,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;clamp(FFF)F"
        )
    )
    private float NT$onClampSkyColor(float vanilla)
    {
        return switch (ModConfig.Candy.getStars())
        {
            case ALPHA, BETA -> 0.005F;
            default -> vanilla;
        };
    }

    /**
     * Tracks and changes the current sky color, so it can be properly updated by cave/void fog. Updates controlled by
     * the void fog tweak.
     */

    @ModifyArg(
        method = "getSkyColor",
        index = 0,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"
        )
    )
    private double NT$onSetSkyColorRed(double red)
    {
        FogUtil.Void.setSkyRed((float) red);
        return FogUtil.Void.isRendering() ? FogUtil.Void.getSkyRed() : red;
    }

    @ModifyArg(
        method = "getSkyColor",
        index = 1,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"
        )
    )
    private double NT$onSetSkyColorGreen(double green)
    {
        FogUtil.Void.setSkyGreen((float) green);
        return FogUtil.Void.isRendering() ? FogUtil.Void.getSkyGreen() : green;
    }

    @ModifyArg(
        method = "getSkyColor",
        index = 2,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"
        )
    )
    private double NT$onSetSkyColorBlue(double blue)
    {
        FogUtil.Void.setSkyBlue((float) blue);
        return FogUtil.Void.isRendering() ? FogUtil.Void.getSkyBlue() : blue;
    }

    /**
     * Attempts to block the spawning of falling particles. Controlled by the disable falling particles tweak.
     */
    @Inject(
        method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZDDDDDD)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onAddParticle(ParticleOptions options, boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfo callback)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        boolean isDisabled = ModConfig.Candy.disableFallingParticles() && options.getType().equals(ParticleTypes.BLOCK);
        boolean isSprinting = player != null && player.canSpawnSprintParticle();

        if (!isDisabled || isSprinting || player == null)
            return;

        boolean isParticleAtPlayer = MathUtil.tolerance(player.getX(), x, 0.01F) &&
            MathUtil.tolerance(player.getY(), y, 0.01F) &&
            MathUtil.tolerance(player.getZ(), z, 0.01F);

        if (isParticleAtPlayer)
            callback.cancel();
    }

    /**
     * Adds void fog particles to the client level if conditions are met. Controlled by various void fog particle
     * tweaks.
     */
    @Inject(
        method = "doAnimateTick",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"
        )
    )
    private void NT$onAddBiomeParticles(int x, int y, int z, int randomBound, RandomSource randomSource, Block block, BlockPos.MutableBlockPos blockPos, CallbackInfo callback)
    {
        Entity entity = Minecraft.getInstance().getCameraEntity();
        ClientLevel level = Minecraft.getInstance().level;

        boolean isFogDisabled = ModConfig.Candy.disableVoidFog() || !FogUtil.Void.isBelowHorizon();
        boolean isCreativeDisabled = !ModConfig.Candy.creativeVoidParticles() &&
            entity instanceof Player player &&
            player.isCreative();

        boolean isDisabled = isFogDisabled || isCreativeDisabled;

        if (isDisabled || entity == null || level == null)
            return;

        BlockPos playerPos = entity.blockPosition();
        int radius = ModConfig.Candy.getVoidParticleRadius();
        int particleStart = ModConfig.Candy.getVoidParticleStart();

        float density = (float) ModConfig.Candy.getVoidParticleDensity() / 100.0F;
        float yLevel = (float) FogUtil.Void.getYLevel(entity);

        if (Math.random() <= density && yLevel <= particleStart && level.dimension().equals(Level.OVERWORLD))
        {
            BlockPos randX = BlockClientUtil.getRandomPos(randomSource, radius);
            BlockPos randY = BlockClientUtil.getRandomPos(randomSource, radius);
            BlockPos randomPos = randX.subtract(randY).offset(playerPos);
            BlockState state = level.getBlockState(randomPos);

            boolean isValidEmptySpace = state.isAir() &&
                level.getFluidState(randomPos).isEmpty() &&
                randomPos.getY() - level.getMinBuildHeight() <= particleStart;

            if (isValidEmptySpace)
            {
                if (randomSource.nextInt(8) <= particleStart)
                {
                    double px = randomPos.getX() + randomSource.nextFloat();
                    double py = randomPos.getY() + randomSource.nextFloat();
                    double pz = randomPos.getZ() + randomSource.nextFloat();

                    boolean nearBedrock = BlockClientUtil.isNearBedrock(randomPos, level);
                    double ySpeed = nearBedrock ? randomSource.nextFloat() : 0.0D;

                    ParticleOptions particle = nearBedrock ? ParticleTypes.ASH : ParticleTypes.MYCELIUM;

                    level.addParticle(particle, px, py, pz, 0.0D, ySpeed, 0.0D);
                }
            }
        }
    }

    /**
     * Disables growth sounds when using bone meal. Controlled by the disable growth sound tweak.
     */
    @Inject(
        method = "playSound",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onPlaySimpleSound(double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean delayed, long seed, CallbackInfo callback)
    {
        boolean isGrowthOff = ModConfig.Sound.disableGrowth() && sound == SoundEvents.BONE_MEAL_USE;
        boolean isSwimOff = ModConfig.Sound.disableGenericSwim() && sound == SoundEvents.GENERIC_SWIM ||
            sound == SoundEvents.PLAYER_SWIM;

        if (isGrowthOff || isSwimOff)
            callback.cancel();
    }

    /**
     * Disables sounds when attacking. Controlled by the sound attack tweak.
     * <p>
     * Disables chest sounds on opening. Controlled by various old chest tweaks.
     * <p>
     * Disables unique mob stepping sounds in multiplayer. Controlled by the old mob steps tweak.
     */
    @Inject(
        method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onPlayPositionedSound(Player player, double x, double y, double z, Holder<SoundEvent> soundHolder, SoundSource category, float volume, float pitch, long seed, CallbackInfo callback)
    {
        SoundEvent sound = soundHolder.value();

        /* Attack Sounds */

        boolean isAttack = sound == SoundEvents.PLAYER_ATTACK_KNOCKBACK ||
            sound == SoundEvents.PLAYER_ATTACK_CRIT ||
            sound == SoundEvents.PLAYER_ATTACK_NODAMAGE ||
            sound == SoundEvents.PLAYER_ATTACK_STRONG ||
            sound == SoundEvents.PLAYER_ATTACK_SWEEP ||
            sound == SoundEvents.PLAYER_ATTACK_WEAK;

        if (ModConfig.Sound.oldAttack() && isAttack)
        {
            callback.cancel();
            return;
        }

        /* Squid Sounds */

        boolean isSquid = sound == SoundEvents.SQUID_AMBIENT ||
            sound == SoundEvents.SQUID_DEATH ||
            sound == SoundEvents.SQUID_HURT ||
            sound == SoundEvents.SQUID_SQUIRT;

        if (ModConfig.Sound.disableSquid() && isSquid)
        {
            callback.cancel();
            return;
        }

        boolean isGlowSquid = sound == SoundEvents.GLOW_SQUID_DEATH ||
            sound == SoundEvents.GLOW_SQUID_HURT ||
            sound == SoundEvents.GLOW_SQUID_SQUIRT;

        if (ModConfig.Sound.disableGlowSquidOther() && isGlowSquid)
        {
            callback.cancel();
            return;
        }

        if (ModConfig.Sound.disableGlowSquidAmbience() && sound == SoundEvents.GLOW_SQUID_AMBIENT)
        {
            callback.cancel();
            return;
        }

        /* Fish Sounds */

        if (ModConfig.Sound.disableFishSwim() && sound == SoundEvents.FISH_SWIM)
        {
            callback.cancel();
            return;
        }

        boolean isFishHurt = sound == SoundEvents.COD_HURT ||
            sound == SoundEvents.PUFFER_FISH_HURT ||
            sound == SoundEvents.SALMON_HURT ||
            sound == SoundEvents.TADPOLE_HURT ||
            sound == SoundEvents.TROPICAL_FISH_HURT;

        if (ModConfig.Sound.disableFishHurt() && isFishHurt)
        {
            callback.cancel();
            return;
        }

        boolean isFishDeath = sound == SoundEvents.COD_DEATH ||
            sound == SoundEvents.PUFFER_FISH_DEATH ||
            sound == SoundEvents.SALMON_DEATH ||
            sound == SoundEvents.TADPOLE_DEATH ||
            sound == SoundEvents.TROPICAL_FISH_DEATH;

        if (ModConfig.Sound.disableFishDeath() && isFishDeath)
        {
            callback.cancel();
            return;
        }

        /* Chest Sounds */

        ClientLevel level = (ClientLevel) (Object) this;
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockState state = level.getBlockState(pos);

        boolean isWoodChestSound = sound == SoundEvents.CHEST_OPEN || sound == SoundEvents.CHEST_CLOSE;
        boolean isEnderChestSound = sound == SoundEvents.ENDER_CHEST_OPEN || sound == SoundEvents.ENDER_CHEST_CLOSE;
        boolean isChestDisabled = false;

        if (ModConfig.Sound.disableChest() && state.is(Blocks.CHEST) && isWoodChestSound)
            isChestDisabled = true;
        else if (ModConfig.Sound.disableEnderChest() && state.is(Blocks.ENDER_CHEST) && isEnderChestSound)
            isChestDisabled = true;
        else if (ModConfig.Sound.disableTrappedChest() && state.is(Blocks.TRAPPED_CHEST) && isWoodChestSound)
            isChestDisabled = true;

        if (isChestDisabled)
        {
            callback.cancel();
            return;
        }

        boolean isOldChest = false;

        if (ModConfig.Sound.oldChest() && state.is(Blocks.CHEST) && isWoodChestSound)
            isOldChest = true;
        else if (ModConfig.Sound.oldChest() && state.is(Blocks.ENDER_CHEST) && isEnderChestSound)
            isOldChest = true;
        else if (ModConfig.Sound.oldChest() && state.is(Blocks.TRAPPED_CHEST) && isWoodChestSound)
            isOldChest = true;

        if (isOldChest)
        {
            SoundEvent chestSound = SoundEvents.WOODEN_DOOR_OPEN;
            RandomSource randomSource = level.random;

            if (sound == SoundEvents.CHEST_CLOSE || sound == SoundEvents.ENDER_CHEST_CLOSE)
                chestSound = SoundEvents.WOODEN_DOOR_CLOSE;

            this.playLocalSound(x, y, z, chestSound, SoundSource.BLOCKS, 1.0F, randomSource.nextFloat() * 0.1f +
                0.9f, false);

            callback.cancel();
            return;
        }

        /*
            Bed & Door Placement Sounds:

            Disables the placement sound if the block at the given position is a bed or door.
            Controlled by the old door sound tweak and the old bed sound tweak.
         */

        boolean isBlockedSound = false;
        boolean isPlacingSound = sound == SoundEvents.WOOD_PLACE || sound == SoundEvents.METAL_PLACE;

        if (ModConfig.Sound.disableDoor() && isPlacingSound && state.getBlock() instanceof DoorBlock)
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

        boolean isEntityStep = sound.getLocation().getPath().contains("entity.") &&
            sound.getLocation().getPath().contains(".step");

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
                return;

            boolean isMinecraftEntity = entity.getType().getDescriptionId().contains("minecraft");
            boolean isEntityIgnored = entity instanceof Spider || entity instanceof Silverfish;
            boolean isModdedIgnored = ModConfig.Sound.ignoreModdedStep() && !isMinecraftEntity;

            if (isEntityIgnored)
                callback.cancel();
            else if (!isModdedIgnored)
            {
                BlockState standing = level.getBlockState(pos.below());

                //TODO: Does this even work at all?
                if (!standing.getFluidState().isEmpty())
                    return;
                else if (standing.is(Blocks.AIR))
                {
                    callback.cancel();
                    return;
                }

                BlockState inside = level.getBlockState(pos);
                SoundType soundType = inside.is(BlockTags.INSIDE_STEP_SOUND_BLOCKS) ? inside.getSoundType() : standing.getSoundType();

                this.playLocalSound(x, y, z, soundType.getStepSound(), entity.getSoundSource(), soundType.getVolume() *
                    0.15F, soundType.getPitch(), false);

                callback.cancel();
            }
        }
    }
}
