package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin extends GuiComponent
{
    /* Shadows */

    @Shadow protected Font font;
    @Shadow public int width;
    @Shadow public int height;

    /* Injections */

    /**
     * Disables tooltips from appearing when hovering items within an inventory.
     * Controlled by the old no item tooltip boxes tweak.
     */
    @Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
    private void NT$onRenderItemTooltip(PoseStack poseStack, ItemStack itemStack, int mouseX, int mouseY, CallbackInfo callback)
    {
        if (MixinConfig.Candy.oldNoItemTooltips())
            callback.cancel();
    }

    /**
     * The best non-intrusive solution to bringing back the old black transparent tooltip box is modifying the color for
     * each fill gradient call. There are 9 fill gradient calls each with two color arguments to modify.
     */

    /* 0th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 0, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientZeroSeven(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 0, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientZeroEight(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 1st Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 1, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientOneSeven(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 1, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientOneEight(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 2nd Fill Gradient - This is the actual tooltip box */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 2, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientTwoSeven(int vanilla)
    {
        return MixinConfig.Candy.oldTooltips() ? 0xc0000000 : vanilla;
    }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 2, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientTwoEight(int vanilla)
    {
        return MixinConfig.Candy.oldTooltips() ? 0xc0000000 : vanilla;
    }

    /* 3rd Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 3, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientThreeSeven(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 3, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientThreeEight(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 4th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 4, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientFourSeven(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 4, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientFourEight(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 5th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 5, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientFiveSeven(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 5, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientFiveEight(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 6th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 6, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientSixSeven(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 6, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientSixEight(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 7th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 7, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientSevenSeven(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 7, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientSevenEight(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    /* 8th Fill Gradient */

    @ModifyArg(method = "renderTooltipInternal", index = 7, at = @At(ordinal = 8, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientEightSeven(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }

    @ModifyArg(method = "renderTooltipInternal", index = 8, at = @At(ordinal = 8, value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    private int NT$onFillGradientEightEight(int vanilla) { return MixinConfig.Candy.oldTooltips() ? 0 : vanilla; }
}