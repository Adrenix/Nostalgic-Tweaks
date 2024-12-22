package mod.adrenix.nostalgic.forge.mixin.tweak.candy.old_hud;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.helper.candy.hud.HudHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public abstract class ForgeGuiMixin
{
    /* Shadows */

    @Shadow public int leftHeight;
    @Shadow public int rightHeight;

    /* Injections */

    /**
     * Prevents rendering of the modern armor sprites if the hunger bar is hidden.
     */
    @WrapWithCondition(
        method = "renderArmor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
        )
    )
    private boolean nt_forge_old_hud$shouldRenderModernArmor(GuiGraphics graphics, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        return !CandyTweak.HIDE_HUNGER_BAR.get();
    }

    /**
     * Renders the old armor sprites on the right side of the heads-up display.
     */
    @Inject(
        remap = false,
        method = "renderArmor",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V"
        )
    )
    private void nt_forge_old_hud$renderOldArmor(GuiGraphics graphics, int width, int height, CallbackInfo callback)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get())
            HudHelper.renderArmor(graphics, this.rightHeight);
    }

    /**
     * Modifies left and right height offsets according to armor context.
     */
    @Inject(
        remap = false,
        method = "renderArmor",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V"
        )
    )
    private void nt_forge_old_hud$modifyArmorHeightOffsets(CallbackInfo callback)
    {
        if (!CandyTweak.HIDE_HUNGER_BAR.get())
            return;

        this.leftHeight -= 10;
        this.rightHeight += 10;
    }

    /**
     * Modifies the health offset of a mounted vehicle if the hunger bar is disabled and the player does not have any
     * armor.
     */
    @Inject(
        remap = false,
        method = "renderHealthMount",
        at = @At("HEAD")
    )
    private void nt_forge_old_hud$modifyHealthMountOffsets(CallbackInfo callback)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get() && NullableResult.getOrElse(Minecraft.getInstance().player, 0, LocalPlayer::getArmorValue) == 0)
            this.rightHeight -= 10;
    }

    /**
     * Prevents rendering of the modern air sprites if the hunger bar is hidden.
     */
    @WrapWithCondition(
        method = "renderAir",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
        )
    )
    private boolean nt_forge_old_hud$shouldRenderModernAir(GuiGraphics graphics, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        return !CandyTweak.HIDE_HUNGER_BAR.get();
    }

    /**
     * Renders the old air sprites on the left side of the heads-up display above the hearts.
     */
    @Inject(
        method = "renderAir",
        at = @At(
            shift = At.Shift.BEFORE,
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;ceil(D)I"
        )
    )
    private void nt_forge_old_hud$renderOldAir(int width, int height, GuiGraphics graphics, CallbackInfo callback)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get())
            HudHelper.renderAir(graphics, this.leftHeight);
    }

    /**
     * Modifies left and right height offsets according to air context.
     */
    @Inject(
        remap = false,
        method = "renderAir",
        at = @At(
            ordinal = 2,
            shift = At.Shift.AFTER,
            value = "FIELD",
            target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;rightHeight:I"
        )
    )
    private void nt_forge_old_hud$modifyAirHeightOffsets(CallbackInfo callback)
    {
        if (!CandyTweak.HIDE_HUNGER_BAR.get())
            return;

        this.leftHeight += 10;
        this.rightHeight -= 10;
    }
}
