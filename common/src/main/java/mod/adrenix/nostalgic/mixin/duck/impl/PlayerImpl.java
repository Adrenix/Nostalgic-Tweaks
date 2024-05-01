package mod.adrenix.nostalgic.mixin.duck.impl;

import mod.adrenix.nostalgic.mixin.duck.SlotTracker;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerImpl extends LivingEntity implements SlotTracker
{
    private PlayerImpl(EntityType<? extends LivingEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    @Shadow private ItemStack lastItemInMainHand;

    @Unique public int nt$lastSlot = -1;
    @Unique public boolean nt$reequip = false;
    @Unique private float nt$cameraPitch = 0.0F;
    @Unique private float nt$prevCameraPitch = 0.0F;

    @Override
    public void nt$setLastSlot(int slot)
    {
        this.nt$lastSlot = slot;
    }

    @Override
    public void nt$setReequip(boolean state)
    {
        this.nt$reequip = state;
    }

    @Override
    public int nt$getLastSlot()
    {
        return this.nt$lastSlot;
    }

    @Override
    public boolean nt$getReequip()
    {
        return this.nt$reequip;
    }

    @Override
    public ItemStack nt$getLastItem()
    {
        return this.lastItemInMainHand;
    }

    @Override
    public void nt$setCameraPitch(float cameraPitch)
    {
        this.nt$cameraPitch = cameraPitch;
    }

    @Override
    public void nt$setPrevCameraPitch(float prevCameraPitch)
    {
        this.nt$prevCameraPitch = prevCameraPitch;
    }

    @Override
    public float nt$getCameraPitch()
    {
        return this.nt$cameraPitch;
    }

    @Override
    public float nt$getPrevCameraPitch()
    {
        return this.nt$prevCameraPitch;
    }

    /**
     * Updates camera pitching when the player moves up and down.
     */
    @Inject(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;tick()V"
        )
    )
    private void nt_camera_pitching$onPlayerAiStep(CallbackInfo callback)
    {
        this.nt$setPrevCameraPitch(this.nt$getCameraPitch());

        double deltaY = this.getDeltaMovement().y;
        float rotation = (float) (Math.atan(-deltaY * 0.20000000298023224D) * 15.0D);
        boolean isGrounded = deltaY < -0.07 && deltaY > -0.08 && !this.getBlockStateOn().isAir();

        if (this.onGround() || this.getHealth() <= 0.0F || isGrounded)
            rotation = 0.0F;

        float current = this.nt$getCameraPitch();

        this.nt$setCameraPitch(current + ((rotation - current) * 0.8F));
    }
}
