package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin
{
    /* Shadows */

    @Shadow @Final private Minecraft minecraft;

    /* Injections */

    /**
     * Informs the client whether the current game-mode is using the experience system.
     * Controlled by the old experience tweak.
     */
    @Inject(method = "hasExperience", at = @At("HEAD"), cancellable = true)
    private void NT$onHasExperience(CallbackInfoReturnable<Boolean> callback)
    {
        LocalPlayer player = this.minecraft.player;
        boolean isRidingWithoutJump = player != null && player.getVehicle() instanceof LivingEntity && !player.isRidingJumpable();

        if (ModConfig.Gameplay.disableExperienceBar() && !isRidingWithoutJump)
            callback.setReturnValue(false);
    }
}
