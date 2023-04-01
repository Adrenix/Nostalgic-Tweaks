package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.mixin.duck.WidgetManager;
import mod.adrenix.nostalgic.util.common.ColorUtil;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin extends GuiComponent implements WidgetManager
{
    /* Shadows */

    @Shadow
    protected abstract void removeWidget(GuiEventListener listener);

    @Shadow
    protected abstract GuiEventListener addRenderableWidget(GuiEventListener widget);

    /* Widget Manager Implementation */

    @Override
    public <T extends GuiEventListener & Renderable> void NT$addRenderableWidget(T widget)
    {
        this.addRenderableWidget(widget);
    }

    @Override
    public void NT$removeWidget(GuiEventListener listener)
    {
        this.removeWidget(listener);
    }

    /* Injections */

    /**
     * Disables tooltips from appearing when hovering over items within an inventory. Controlled by the old no item
     * tooltip tweak.
     */
    @Inject(
        cancellable = true,
        at = @At("HEAD"),
        method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V"
    )
    private void NT$onRenderItemTooltip(PoseStack poseStack, ItemStack itemStack, int mouseX, int mouseY, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldNoItemTooltips())
            callback.cancel();
    }

    /**
     * Changes the fill gradient background color. Controlled by various GUI background tweaks.
     */
    @Redirect(
        method = "renderBackground(Lcom/mojang/blaze3d/vertex/PoseStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"
        )
    )
    private void NT$onRenderBackground(PoseStack poseStack, int x, int y, int w, int h, int colorFrom, int colorTo)
    {
        if (ModConfig.Candy.customGuiBackground())
        {
            int top = ColorUtil.toHexInt(ModConfig.Candy.customTopGradient());
            int bottom = ColorUtil.toHexInt(ModConfig.Candy.customBottomGradient());

            Screen.fillGradient(poseStack, x, y, w, h, top, bottom);
        }
        else if (!ModConfig.Candy.oldGuiBackground().equals(TweakType.GuiBackground.SOLID_BLACK))
        {
            switch (ModConfig.Candy.oldGuiBackground())
            {
                case SOLID_BLUE -> Screen.fillGradient(poseStack, x, y, w, h, 0xA0303060, 0xA0303060);
                case GRADIENT_BLUE -> Screen.fillGradient(poseStack, x, y, w, h, 0x60050500, 0xA0303060);
            }
        }
        else
            Screen.fillGradient(poseStack, x, y, w, h, colorFrom, colorTo);
    }
}