package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InGameHud.class)
public abstract class GuiMixin extends DrawableHelper
{
    /* Shadows */

    @Shadow private int screenWidth;
    @Shadow protected abstract LivingEntity getPlayerVehicleWithHealth();

    /* Injections */

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    /**
     * Disables the rendering of the selected item name above the hotbar.
     * Controlled by the old selected item name tweak.
     */
    @Inject(method = "renderSelectedItemName", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onRenderSelectedItemName(MatrixStack poseStack, CallbackInfo callback)
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
    private void NT$onDrawSelectedItemName(MatrixStack poseStack, CallbackInfo callback, MutableText mutableComponent)
    {
        if (ModConfig.Candy.oldPlainSelectedItemName())
            mutableComponent.formatted(Formatting.RESET);
    }

    /**
     * Moves the position of the player's armor bar and air bubble bar.
     * Controlled by various gameplay tweaks.
     */
    @Redirect(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"))
    private void NT$onRenderPlayerHealth(InGameHud instance, MatrixStack poseStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        boolean isHungerRendered = !ModConfig.Gameplay.disableHungerBar();
        boolean isExperienceRendered = !ModConfig.Gameplay.disableExperienceBar();
        boolean isVehiclePresent = this.getPlayerVehicleWithHealth() != null;
        boolean isAbsorbing = false;

        if (isVehiclePresent || isHungerRendered)
        {
            instance.drawTexture(poseStack, x, y + (isExperienceRendered || isVehiclePresent ? 0 : 7), uOffset, vOffset, uWidth, vHeight);
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
                ModClientUtil.Gui.renderInverseArmor(poseStack, this.getZOffset(), mirrorX, y + armorY, uOffset, vOffset, uWidth, vHeight);
            else
                instance.drawTexture(poseStack, mirrorX, y + armorY, uOffset, vOffset, uWidth, vHeight);
        }
        // Air bubbles texture
        else if ((uOffset == 16 && vOffset == 18) || (uOffset == 25 && vOffset == 18))
            instance.drawTexture(poseStack, mirrorX + (width % 2 == 0 ? 1 : 0), y + bubbleY, uOffset, vOffset, uWidth, vHeight);
        // Icons texture has all hunger related icons on y-27.
        else if (vOffset != 27)
            instance.drawTexture(poseStack, x + 1, y, uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * Moves the position of the player's heart bar.
     * Controlled by various gameplay tweaks.
     */
    @Redirect(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"))
    private void NT$onRenderHeart(InGameHud instance, MatrixStack poseStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        if (this.getPlayerVehicleWithHealth() != null || !ModConfig.Gameplay.disableExperienceBar())
            instance.drawTexture(poseStack, x, y, uOffset, vOffset, uWidth, vHeight);
        else
            instance.drawTexture(poseStack, x, y + 7, uOffset, vOffset, uWidth, vHeight);
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
    private void NT$onRenderAttackIndicator(MatrixStack poseStack, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableCooldown())
            callback.cancel();
    }
}
