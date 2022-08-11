package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.network.packet.PacketS2CHurtDirection;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import mod.adrenix.nostalgic.util.server.ModServerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * All mixins within this class are injected into both client and server.
 * Do not class load any vanilla client code here.
 * @see mod.adrenix.nostalgic.mixin.client.world.entity.LivingEntityMixin
 * @see mod.adrenix.nostalgic.mixin.server.LivingEntityMixin
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    /* Dummy Constructor */

    private LivingEntityMixin(EntityType<?> entity, World level)
    {
        super(entity, level);
    }

    /* Shadows */

    @Shadow public float hurtDir;

    /* Injections */

    /**
     * Sends a last hurt source packet to the client so the damage tilt animation can be applied.
     * Controlled by the old damage tilt tweak.
     */
    @Inject(method = "hurt", at = @At("TAIL"))
    private void NT$onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback)
    {
        LivingEntity living = (LivingEntity) (Object) this;

        if (ModConfig.Animation.oldDirectionTilt() && living instanceof ServerPlayerEntity player)
            PacketUtil.sendToPlayer(player, new PacketS2CHurtDirection(this.hurtDir));
    }

    /**
     * Prevents living entities from playing the food consumption sound.
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
     * Prevents the spawning of food consumption particles and eating sounds.
     * Controlled by the old hunger tweak.
     */
    @Inject(method = "triggerItemUseEffects", at = @At("HEAD"), cancellable = true)
    private void NT$onAddEatEffect(ItemStack itemStack, int count, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.instantEat() && itemStack.getUseAction() == UseAction.EAT)
            callback.cancel();
    }

    /**
     * Brings back the old backwards walking animation.
     * Controlled by the old backwards walk tweak.
     */
    @ModifyConstant
    (
        method = "tick",
        slice = @Slice(to = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;attackAnim:F")),
        constant = @Constant(floatValue = 180.0F)
    )
    private float NT$onBackwardsRotation(float vanilla)
    {
        return ModConfig.Animation.oldBackwardsWalking() ? 0.0F : vanilla;
    }

    /**
     * Immediately refills the player's air supply when they go above water.
     * Controlled by the instant air tweak.
     */
    @Inject(method = "increaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void NT$onIncreaseAirSupply(int currentAir, CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.instantAir())
            callback.setReturnValue(this.getMaxAir());
    }

    /**
     * Makes zombie flesh not as overpowered by swapping the hunger effect with poison.
     * Controlled by the old hunger tweak.
     */
    @Inject
    (
        cancellable = true,
        method = "addEatEffect",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/world/food/FoodProperties;getEffects()Ljava/util/List;"
        )
    )
    private void NT$onAddEatEffect(ItemStack itemStack, World level, LivingEntity entity, CallbackInfo callback)
    {
        if (!ModConfig.Gameplay.disableHunger())
            return;

        Item item = itemStack.getItem();
        boolean isRotten = item.equals(Items.ROTTEN_FLESH);
        boolean isGolden = item.equals(Items.GOLDEN_APPLE);
        boolean isEffectOverride = isRotten || isGolden;

        if (isRotten)
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0));

        if (isEffectOverride)
            callback.cancel();
    }

    /**
     * Ensures that any food loot dropped doesn't get overridden by the old food stacking tweaking.
     * Not controlled by any tweak.
     */

    @Inject(method = "dropFromLootTable", at = @At("HEAD"))
    private void NT$onStartDropFromLootTable(DamageSource damageSource, boolean hitByPlayer, CallbackInfo callback)
    {
        ModServerUtil.Item.isDroppingLoot = true;
    }

    @Inject(method = "dropFromLootTable", at = @At("TAIL"))
    private void NT$onEndDropFromLootTable(DamageSource damageSource, boolean hitByPlayer, CallbackInfo callback)
    {
        ModServerUtil.Item.isDroppingLoot = false;
    }
}
