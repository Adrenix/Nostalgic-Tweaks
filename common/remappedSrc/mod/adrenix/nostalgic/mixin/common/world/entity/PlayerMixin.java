package mod.adrenix.nostalgic.mixin.common.world.entity;

import com.mojang.authlib.GameProfile;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.gameplay.OldFoodData;
import mod.adrenix.nostalgic.mixin.widen.IMixinLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity
{
    /* Dummy Constructor */

    private PlayerMixin(EntityType<? extends LivingEntity> type, World level)
    {
        super(type, level);
    }

    /* Shadows */

    @Shadow @Final private PlayerAbilities abilities;
    @Shadow protected HungerManager foodData;
    @Shadow public abstract float getCurrentItemAttackStrengthDelay();

    /* Injections */

    /**
     * Replaces the food system in the player class.
     * Controlled by the old hunger tweak.
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void NT$onInitPlayer(World level, BlockPos pos, float yaw, GameProfile profile, ProfilePublicKey key, CallbackInfo callback)
    {
        this.foodData = new OldFoodData((PlayerEntity) (Object) this);
    }

    /**
     * Blocks the eating sound emitted by players when consuming food items.
     * Controlled by the old hunger tweak.
     */
    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void NT$onEat(World instance, PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch)
    {
        if (ModConfig.Gameplay.instantEat())
            return;
        instance.playSound(player, x, y, z, sound, category, volume, pitch);
    }

    /**
     * Resets the attack strength scale back to one everytime the player attacks.
     * Controlled by the disabled cooldown tweak.
     */
    @Inject(method = "attack", at = @At("HEAD"))
    private void NT$onAttack(Entity targetEntity, CallbackInfo callback)
    {
        if (!ModConfig.Gameplay.disableCooldown())
            return;

        ((IMixinLivingEntity) this).NT$setAttackStrengthTicker((int) Math.ceil(this.getCurrentItemAttackStrengthDelay()));
    }

    /**
     * Sets the attack strength scale to 1.
     * Controlled by the disabled cooldown tweak.
     */
    @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
    private void NT$onGetAttackStrengthScale(float adjustTicks, CallbackInfoReturnable<Float> callback)
    {
        if (ModConfig.Gameplay.disableCooldown())
            callback.setReturnValue(1.0F);
    }

    /**
     * Disables sweep attacking.
     * Controlled by the disabled sweep attack tweak.
     */
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<LivingEntity> NT$onGetEntitiesToSweep(World instance, Class<LivingEntity> aClass, Box aabb)
    {
        if (ModConfig.Gameplay.disableSweep())
            return new ArrayList<>();
        return instance.getNonSpectatingEntities(aClass, aabb);
    }

    /**
     * Changes the connected player's crouching pose when flying in creative.
     * Controlled by the old creative crouch animation tweak.
     */
    @ModifyArg(method = "updatePlayerPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setPose(Lnet/minecraft/world/entity/Pose;)V"))
    private EntityPose NT$onSetPlayerPose(EntityPose vanilla)
    {
        if (!ModConfig.Animation.oldCreativeCrouch())
            return vanilla;

        EntityPose pose = this.isFallFlying() ? EntityPose.FALL_FLYING :
            this.isSleeping() ? EntityPose.SLEEPING :
            this.isSwimming() ? EntityPose.SWIMMING :
            this.isUsingRiptide() ? EntityPose.SPIN_ATTACK :
            this.isSneaking() ? EntityPose.CROUCHING :
            EntityPose.STANDING
        ;

        return this.isSpectator() || this.hasVehicle() || this.wouldPoseNotCollide(pose) ? pose :
            this.wouldPoseNotCollide(EntityPose.CROUCHING) ? EntityPose.CROUCHING : EntityPose.SWIMMING
        ;
    }

    /**
     * Changes the camera position when the crouch pose is in use and prevents jittery camera movement in creative mode
     * when the old crouching tweak is enabled.
     *
     * Controlled by the old sneaking tweak and old crouching tweak.
     */
    @ModifyConstant(method = "getStandingEyeHeight", constant = @Constant(floatValue = 1.27F))
    private float NT$onGetStandingEyeHeight(float vanilla)
    {
        if (ModConfig.Animation.oldCreativeCrouch() && this.abilities.flying)
            return 1.62F;
        return ModConfig.Animation.oldSneaking() ? ModConfig.Animation.getSneakHeight() : 1.27F;
    }
}
