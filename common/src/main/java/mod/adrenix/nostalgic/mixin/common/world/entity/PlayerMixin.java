package mod.adrenix.nostalgic.mixin.common.world.entity;

import com.mojang.authlib.GameProfile;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.gameplay.OldFoodData;
import mod.adrenix.nostalgic.mixin.widen.IMixinLivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity
{
    /* Dummy Constructor */

    private PlayerMixin(EntityType<? extends LivingEntity> type, Level level)
    {
        super(type, level);
    }

    /* Shadows */

    @Shadow @Final private Abilities abilities;
    @Shadow protected FoodData foodData;
    @Shadow public abstract float getCurrentItemAttackStrengthDelay();

    /* Injections */

    /**
     * Replaces the food system in the player class.
     * Controlled by the old hunger tweak.
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void NT$onInitPlayer(Level level, BlockPos pos, float yaw, GameProfile profile, ProfilePublicKey key, CallbackInfo callback)
    {
        this.foodData = new OldFoodData((Player) (Object) this);
    }

    /**
     * Blocks the eating sound emitted by players when consuming food items.
     * Controlled by the old hunger tweak.
     */
    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void NT$onEat(Level instance, Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch)
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
    private List<LivingEntity> NT$onGetEntitiesToSweep(Level instance, Class<LivingEntity> aClass, AABB aabb)
    {
        if (ModConfig.Gameplay.disableSweep())
            return new ArrayList<>();
        return instance.getEntitiesOfClass(aClass, aabb);
    }

    /**
     * Changes the connected player's crouching pose when flying in creative.
     * Controlled by the old creative crouch animation tweak.
     */
    @ModifyArg(method = "updatePlayerPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setPose(Lnet/minecraft/world/entity/Pose;)V"))
    private Pose NT$onSetPlayerPose(Pose vanilla)
    {
        if (!ModConfig.Animation.oldCreativeCrouch())
            return vanilla;

        Pose pose = this.isFallFlying() ? Pose.FALL_FLYING :
            this.isSleeping() ? Pose.SLEEPING :
            this.isSwimming() ? Pose.SWIMMING :
            this.isAutoSpinAttack() ? Pose.SPIN_ATTACK :
            this.isShiftKeyDown() ? Pose.CROUCHING :
            Pose.STANDING
        ;

        return this.isSpectator() || this.isPassenger() || this.canEnterPose(pose) ? pose :
            this.canEnterPose(Pose.CROUCHING) ? Pose.CROUCHING : Pose.SWIMMING
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
