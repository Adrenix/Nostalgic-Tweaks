package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.util.common.ColorUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin
{
    /**
     * Simulates the old durability bar colors. Controlled by the old damage colors tweak.
     */
    @Inject(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "RETURN")
    )
    private void NT$onRenderItemDecorations(Font font, ItemStack itemStack, int x, int y, String text, CallbackInfo callback)
    {
        if (itemStack.isEmpty())
            return;

        if (ModConfig.Candy.oldDurabilityColors() && itemStack.isBarVisible())
        {
            RenderSystem.disableDepthTest();

            double health = (double) itemStack.getDamageValue() / (double) itemStack.getMaxDamage();
            double healthRemaining = ((double) itemStack.getDamageValue() * 255.0D) / (double) itemStack.getMaxDamage();

            int width = Math.round(13.0F - (float) health * 13.0F);
            int damage = (int) Math.round(255.0D - healthRemaining);

            Color damageForegroundColor = new Color(255 - damage << 16 | damage << 8);
            Color damageBackgroundColor = new Color((255 - damage) / 4 << 16 | 0x3F00);

            int startX = x + 2;
            int startY = y + 13;

            ((GuiGraphics)(Object)this).fill(startX, startY, startX + 13, startY + 2, 0xFF000000);
            ((GuiGraphics)(Object)this).fill(startX, startY, startX + 12, startY + 1, damageBackgroundColor.getRGB());
            ((GuiGraphics)(Object)this).fill(startX, startY, startX + width, startY + 1, damageForegroundColor.getRGB());

            RenderSystem.enableDepthTest();
        }
    }

    /**
     * Disables tooltips from appearing when hovering over items within an inventory. Controlled by the old no item
     * tooltip tweak.
     */
    @Inject(
            cancellable = true,
            at = @At("HEAD"),
            method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V"
    )
    private void NT$onRenderItemTooltip(Font font, ItemStack itemStack, int mouseX, int mouseY, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldNoItemTooltips())
            callback.cancel();
    }

    /**
     * Changes the fill gradient background color. Controlled by various GUI background tweaks.
     */
    @Redirect(
            method = "fillGradient(IIIIII)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIIII)V"
            )
    )
    private void NT$onRenderBackground(GuiGraphics graphics, int x, int y, int w, int h, int unknown, int colorFrom, int colorTo)
    {
        if (ModConfig.Candy.customGuiBackground())
        {
            int top = ColorUtil.toHexInt(ModConfig.Candy.customTopGradient());
            int bottom = ColorUtil.toHexInt(ModConfig.Candy.customBottomGradient());

            graphics.fillGradient(x, y, w, h, top, bottom);
        }
        else if (!ModConfig.Candy.oldGuiBackground().equals(TweakType.GuiBackground.SOLID_BLACK))
        {
            switch (ModConfig.Candy.oldGuiBackground())
            {
                case SOLID_BLUE -> graphics.fillGradient(x, y, w, h, unknown, 0xA0303060, 0xA0303060);
                case GRADIENT_BLUE -> graphics.fillGradient(x, y, w, h, unknown, 0x60050500, 0xA0303060);
            }
        }
        else
            graphics.fillGradient(x, y, w, h, unknown, colorFrom, colorTo);
    }
}
