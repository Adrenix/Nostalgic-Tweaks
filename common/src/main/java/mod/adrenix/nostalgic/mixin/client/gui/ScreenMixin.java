package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.mixin.duck.IWidgetManager;
import mod.adrenix.nostalgic.util.common.ModUtil;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin extends GuiComponent implements IWidgetManager
{
    /* Shadows */

    @Shadow protected abstract void removeWidget(GuiEventListener listener);
    @Shadow protected abstract GuiEventListener addRenderableWidget(GuiEventListener widget);

    /* Widget Manager Overrides */

    @Override
    public <T extends GuiEventListener & Widget> void NT$addRenderableWidget(T widget)
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
     * Disables tooltips from appearing when hovering over items within an inventory.
     * Controlled by the old no item tooltip tweak.
     */
    @Inject
    (
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
     * Changes the fill gradient background color.
     * Controlled by various GUI background tweaks.
     */
    @Redirect
    (
        method = "renderBackground(Lcom/mojang/blaze3d/vertex/PoseStack;I)V",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"
        )
    )
    private void NT$onRenderBackground(Screen instance, PoseStack poseStack, int x, int y, int w, int h, int colorFrom, int colorTo)
    {
        if (ModConfig.Candy.customGuiBackground())
        {
            int top = ModUtil.Text.toHexInt(ModConfig.Candy.customTopGradient());
            int bottom = ModUtil.Text.toHexInt(ModConfig.Candy.customBottomGradient());

            this.fillGradient(poseStack, x, y, w, h, top, bottom);
        }
        else if (!ModConfig.Candy.oldGuiBackground().equals(TweakType.GuiBackground.SOLID_BLACK))
        {
            switch (ModConfig.Candy.oldGuiBackground())
            {
                case SOLID_BLUE -> this.fillGradient(poseStack, x, y, w, h, 0xA0303060, 0xA0303060);
                case GRADIENT_BLUE -> this.fillGradient(poseStack, x, y, w, h, 0x60050500, 0xA0303060);
            }
        }
        else
            this.fillGradient(poseStack, x, y, w, h, colorFrom, colorTo);
    }

    /**
     * The best non-intrusive solution to bringing back the old black transparent tooltip box is modifying the color for
     * each fill gradient call. There are 9 fill gradient calls each with two color arguments to modify.
     */

    /* 0th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 0, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientZeroSeven(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 0, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientZeroEight(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 1st Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 1, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientOneSeven(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 1, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientOneEight(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 2nd Fill Gradient - This is the actual tooltip box */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 2, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientTwoSeven(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0xc0000000 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 2, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientTwoEight(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0xc0000000 : vanilla; }

    /* 3rd Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 3, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientThreeSeven(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 3, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientThreeEight(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 4th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 4, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientFourSeven(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 4, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientFourEight(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 5th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 5, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientFiveSeven(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 5, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientFiveEight(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 6th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 6, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientSixSeven(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 6, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientSixEight(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 7th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 7, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientSevenSeven(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 7, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientSevenEight(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 8th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 8, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientEightSeven(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 8, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientEightEight(int vanilla) { return ModConfig.Candy.oldTooltips() ? 0 : vanilla; }
}