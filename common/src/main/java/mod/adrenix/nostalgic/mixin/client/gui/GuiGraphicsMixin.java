package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

            int damageForegroundColor = 0xFF000000 | (255 - damage << 16 | damage << 8);
            int damageBackgroundColor = 0xFF000000 | ((255 - damage) / 4 << 16 | 0x3F00);

            int startX = x + 2;
            int startY = y + 13;

            ((GuiGraphics) (Object) this).fill(RenderType.guiOverlay(), startX, startY, startX + 13, startY + 2, 0xFF000000);
            ((GuiGraphics) (Object) this).fill(RenderType.guiOverlay(), startX, startY, startX + 12, startY + 1, damageBackgroundColor);
            ((GuiGraphics) (Object) this).fill(RenderType.guiOverlay(), startX, startY, startX + width, startY + 1, damageForegroundColor);

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
}
