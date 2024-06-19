package mod.adrenix.nostalgic.mixin.tweak.candy.gui_background;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.enums.GuiBackground;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin
{
    /* Shadows */

    @Shadow public int width;
    @Shadow public int height;

    /* Injections */

    /**
     * Changes the fill gradient background color for standard GUI screens.
     */
    @WrapOperation(
        method = "renderBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void nt_gui_background$wrapBackgroundRenderer(Screen screen, GuiGraphics graphics, Operation<Void> operation)
    {
        if (CandyTweak.CUSTOM_GUI_BACKGROUND.get())
        {
            int top = HexUtil.parseInt(CandyTweak.CUSTOM_GUI_TOP_GRADIENT.get());
            int bottom = HexUtil.parseInt(CandyTweak.CUSTOM_GUI_BOTTOM_GRADIENT.get());

            graphics.fillGradient(0, 0, this.width, this.height, top, bottom);
        }
        else if (CandyTweak.OLD_GUI_BACKGROUND.get() != GuiBackground.SOLID_BLACK)
        {
            switch (CandyTweak.OLD_GUI_BACKGROUND.get())
            {
                case SOLID_BLUE -> graphics.fillGradient(0, 0, this.width, this.height, 0xA0303060, 0xA0303060);
                case GRADIENT_BLUE -> graphics.fillGradient(0, 0, this.width, this.height, 0x60050500, 0xA0303060);
            }
        }
        else
            operation.call(screen, graphics);
    }

    /**
     * Darkens the menu background if using the dirt texture.
     */
    @Inject(
        method = "renderMenuBackgroundTexture",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIFFIIII)V"
        )
    )
    private static void nt_gui_background$preRenderMenuBackgroundTexture(GuiGraphics graphics, ResourceLocation texture, int x, int y, float uOffset, float vOffset, int width, int height, CallbackInfo callback)
    {
        if (Screen.MENU_BACKGROUND == texture && CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get())
            graphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
    }

    /**
     * Changes the background texture used by the screen.
     */
    @ModifyArg(
        method = "renderMenuBackgroundTexture",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIFFIIII)V"
        )
    )
    private static ResourceLocation nt_gui_background$shouldRenderMenuBackground(ResourceLocation texture)
    {
        if (Screen.MENU_BACKGROUND == texture && CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get())
            return TextureLocation.DIRT_BACKGROUND;

        return texture;
    }

    /**
     * Brightens the menu background after drawing the background.
     */
    @Inject(
        method = "renderMenuBackgroundTexture",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIFFIIII)V"
        )
    )
    private static void nt_gui_background$postRenderMenuBackgroundTexture(GuiGraphics graphics, ResourceLocation texture, int x, int y, float uOffset, float vOffset, int width, int height, CallbackInfo callback)
    {
        if (ModTweak.ENABLED.get())
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
