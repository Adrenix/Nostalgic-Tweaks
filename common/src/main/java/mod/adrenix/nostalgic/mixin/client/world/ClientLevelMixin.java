package mod.adrenix.nostalgic.mixin.client.world;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.BlockClientUtil;
import mod.adrenix.nostalgic.util.client.FogUtil;
import mod.adrenix.nostalgic.util.client.SoundClientUtil;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
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
     * Disables or plays various sounds based on tweak states.
     */
    @Inject(
        method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onPlayPositionedSound(Player player, double x, double y, double z, Holder<SoundEvent> soundHolder, SoundSource category, float volume, float pitch, long seed, CallbackInfo callback)
    {
        if (NostalgicTweaks.isForge())
            return;

        if (SoundClientUtil.isSoundAtPositionHandled((ClientLevel) (Object) this, x, y, z, soundHolder, this::playLocalSound))
            callback.cancel();
    }
}
