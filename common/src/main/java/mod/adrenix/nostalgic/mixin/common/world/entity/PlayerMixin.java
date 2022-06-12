package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.mixin.duck.IReequipSlot;
import mod.adrenix.nostalgic.util.SoundUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IReequipSlot
{
    /* Dummy Constructor */

    private PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Shadows */

    @Shadow private ItemStack lastItemInMainHand;

    /* Reequip and Slot Tracking Ducking */

    @Unique public int NT$lastSlot = -1;
    @Unique public boolean NT$reequip = false;

    @Override public void setLastSlot(int slot) { this.NT$lastSlot = slot; }
    @Override public void setReequip(boolean state) { this.NT$reequip = state; }

    @Override public int getLastSlot() { return NT$lastSlot; }
    @Override public boolean getReequip() { return NT$reequip; }
    @Override public ItemStack getLastItem() { return this.lastItemInMainHand; }

    /* Mixin Injections */

    /**
     * Client:
     *
     * Brings back the old "oof" sounds while also disabling the other custom hurt sounds.
     * Controlled by the old hurt sound tweak.
     */
    @Inject(method = "getHurtSound", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onGetHurtSound(DamageSource damageSource, CallbackInfoReturnable<SoundEvent> callback)
    {
        if (MixinConfig.Sound.oldDamage())
            callback.setReturnValue(SoundUtil.Event.PLAYER_HURT.get());
    }

    /**
     * Multiplayer:
     *
     * Disables on sounds when attacking.
     * Controlled by the sound attack tweak.
     */
    @Redirect
    (
        method = "attack",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
        )
    )
    private void NT$onAttack(Level level, Player player, double x, double y, double z, SoundEvent event, SoundSource sound, float volume, float pitch)
    {
        if (!MixinConfig.Sound.oldAttack())
            level.playSound(null, x, y, z, event, sound, volume, pitch);
    }

    /**
     * Client:
     *
     * Updates the camera pitching when the player moves up and down.
     */
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSpeed(F)V"))
    private void NT$onAiStep(CallbackInfo callback)
    {
        double deltaY = this.getDeltaMovement().y;
        float rotation = (float) (Math.atan(-deltaY * 0.20000000298023224D) * 15.0D);
        float current = this.getCameraPitch();

        // Fixes weird bug that occurs when standing on a slime block.
        boolean isGrounded = deltaY < -0.07 && deltaY > -0.08;
        if (isGrounded || this.onGround || this.getHealth() <= 0.0F)
            rotation = 0.0F;

        this.setCameraPitch(current + (rotation - current) * 0.8F);
    }

    /**
     * Client:
     *
     * Disables the bobbing animation when the player provides input for movement but cannot move further.
     * Controlled by the bob on collision tweak.
     */
    @ModifyArg(method = "aiStep", index = 1, at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F"))
    private float NT$onAiBobbing(float min, float current)
    {
        if (!MixinConfig.Animation.oldCollideBobbing())
            return current;
        else if (this.walkDist == this.walkDistO)
            return 0.0F;
        return current;
    }

    /**
     * Client:
     *
     * Prevents the swinging animation when dropping an item from the hand or within an inventory screen.
     * Controlled by the swing drop tweak.
     */
    @Redirect
    (
        method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;swing(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private void NT$itemDroppingProxy(Player player, InteractionHand hand)
    {
        if (MixinConfig.Animation.oldSwingDropping())
            return;
        player.swing(InteractionHand.MAIN_HAND);
    }
}