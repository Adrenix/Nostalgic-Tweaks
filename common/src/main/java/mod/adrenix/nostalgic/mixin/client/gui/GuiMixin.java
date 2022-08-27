package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public abstract class GuiMixin extends GuiComponent
{
    /* Shadows */

    @Shadow private int screenWidth;
    @Shadow protected abstract LivingEntity getPlayerVehicleWithHealth();

    /* Injections */

    @Shadow protected abstract Player getCameraPlayer();

    /**
     * Disables the rendering of the selected item name above the hotbar.
     * Controlled by the old selected item name tweak.
     */
    @Inject(method = "renderSelectedItemName", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onRenderSelectedItemName(PoseStack poseStack, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldNoSelectedItemName())
            callback.cancel();
    }

    /**
     * Removes the chat formatting of the selected item above the hotbar.
     * Controlled by the old plain selected item name tweak.
     */
    @Inject
    (
        method = "renderSelectedItemName",
        locals = LocalCapture.CAPTURE_FAILSOFT,
        at = @At
        (
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"
        )
    )
    private void NT$onDrawSelectedItemName(PoseStack poseStack, CallbackInfo callback, MutableComponent mutableComponent)
    {
        if (ModConfig.Candy.oldPlainSelectedItemName())
            mutableComponent.withStyle(ChatFormatting.RESET);
    }

    /**
     * Prevents the axis crosshair from overriding the default crosshair.
     * Controlled by the old debug screen tweak.
     */
    @Redirect(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;renderDebug:Z", opcode = Opcodes.GETFIELD))
    private boolean NT$onRenderDebugCrosshair(Options instance)
    {
        if (ModConfig.Candy.getDebugScreen().equals(TweakVersion.Generic.MODERN))
            return instance.renderDebug;
        return false;
    }

    /**
     * Moves the position of the player's armor bar and air bubble bar.
     * Controlled by various gameplay tweaks.
     */
    @Redirect(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"))
    private void NT$onRenderPlayerHealth(Gui instance, PoseStack poseStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        boolean isHungerRendered = !ModConfig.Gameplay.disableHungerBar();
        boolean isExperienceRendered = !ModConfig.Gameplay.disableExperienceBar();
        boolean isVehiclePresent = this.getPlayerVehicleWithHealth() != null;
        boolean isAbsorbing = false;

        if (isVehiclePresent || isHungerRendered)
        {
            instance.blit(poseStack, x, y + (isExperienceRendered || isVehiclePresent ? 0 : 7), uOffset, vOffset, uWidth, vHeight);
            return;
        }

        if (this.getCameraPlayer() != null)
            isAbsorbing = this.getCameraPlayer().getAbsorptionAmount() > 0.0F;

        int width = this.screenWidth;
        int mirrorX = width - x - 10;
        int armorY = isExperienceRendered ? 10 : 17;
        int bubbleY = isExperienceRendered ? 1 : 8;

        if (isAbsorbing)
        {
            armorY += 10;
            bubbleY -= 10;
        }

        // All armor slot textures
        if ((uOffset == 16 && vOffset == 9) || (uOffset == 25 && vOffset == 9) || (uOffset == 34 && vOffset == 9))
        {
            // Half armor texture flip
            if (uOffset == 25)
                GuiUtil.renderInverseArmor(poseStack, this.getBlitOffset(), mirrorX, y + armorY, uOffset, vOffset, uWidth, vHeight);
            else
                instance.blit(poseStack, mirrorX, y + armorY, uOffset, vOffset, uWidth, vHeight);
        }
        // Air bubbles texture
        else if ((uOffset == 16 && vOffset == 18) || (uOffset == 25 && vOffset == 18))
            instance.blit(poseStack, mirrorX + (width % 2 == 0 ? 1 : 0), y + bubbleY, uOffset, vOffset, uWidth, vHeight);
        // Icons texture has all hunger related icons on y-27.
        else if (vOffset != 27)
            instance.blit(poseStack, x + 1, y, uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * Moves the position of the player's heart bar.
     * Controlled by various gameplay tweaks.
     */
    @Redirect(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"))
    private void NT$onRenderHeart(Gui instance, PoseStack poseStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        if (this.getPlayerVehicleWithHealth() != null || !ModConfig.Gameplay.disableExperienceBar())
            instance.blit(poseStack, x, y, uOffset, vOffset, uWidth, vHeight);
        else
            instance.blit(poseStack, x, y + 7, uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * Disables the rendering of the "ready to attack" indicator status.
     * Controlled by the disabled cooldown tweak.
     */
    @Inject
    (
        cancellable = true,
        method = "renderCrosshair",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"
        )
    )
    private void NT$onRenderAttackIndicator(PoseStack poseStack, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableCooldown())
            callback.cancel();
    }
}
