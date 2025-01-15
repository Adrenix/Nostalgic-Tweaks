package mod.adrenix.nostalgic.mixin.tweak.animation.swing;

import mod.adrenix.nostalgic.mixin.duck.SwingBlocker;
import mod.adrenix.nostalgic.helper.animation.PlayerArmHelper;
import mod.adrenix.nostalgic.helper.swing.SwingType;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /* Shadows */

    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Final public Options options;

    /* Injections */

    /**
     * Resets the swing attack animation tracker.
     */
    @Inject(
        method = "startAttack",
        at = @At("HEAD")
    )
    private void nt_animation_swing$onStartAttack(CallbackInfoReturnable<Boolean> callback)
    {
        PlayerArmHelper.SWING_TYPE.set(SwingType.ATTACK);

        if (AnimationTweak.OLD_SWING_INTERRUPT.get() && this.player != null)
        {
            this.player.attackAnim = 0.0F;
            this.player.swingTime = 0;
        }
    }

    /**
     * Sets the swing type tracker when the player uses an item.
     */
    @Inject(
        method = "startUseItem",
        at = @At("HEAD")
    )
    private void nt_animation_swing$onStartUseItem(CallbackInfo callback)
    {
        if (PlayerArmHelper.SWING_TYPE.get() == SwingType.ATTACK && this.options.keyAttack.isDown())
            return;

        PlayerArmHelper.SWING_TYPE.set(SwingType.USE);
    }

    /**
     * Prevents the hand swing animation when dropping an item.
     */
    @Inject(
        method = "handleKeybinds",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private void nt_animation_swing$onDropItem(CallbackInfo callback)
    {
        SwingBlocker swingBlocker = (SwingBlocker) this.player;

        if (AnimationTweak.OLD_SWING_DROPPING.get() && swingBlocker != null)
            swingBlocker.nt$setSwingBlocked(true);
    }
}
