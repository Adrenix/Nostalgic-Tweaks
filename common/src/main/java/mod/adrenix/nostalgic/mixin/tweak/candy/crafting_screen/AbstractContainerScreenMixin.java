package mod.adrenix.nostalgic.mixin.tweak.candy.crafting_screen;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen
{
    /* Fake Constructor */

    private AbstractContainerScreenMixin(Component title)
    {
        super(title);
    }

    /* Injections */

    /**
     * Changes the position of the screen title text to match the position of the old crafting table screen.
     */
    @WrapWithCondition(
        method = "renderLabels",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"
        )
    )
    private boolean nt_crafting_screen$renderLabels(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean dropShadow)
    {
        if (ClassUtil.isNotInstanceOf(this, CraftingScreen.class) || !CandyTweak.OLD_CRAFTING_SCREEN.get())
            return true;

        RenderUtil.beginBatching();
        DrawText.begin(graphics, this.title).pos(28, 6).color(0x404040).flat().draw();
        DrawText.begin(graphics, Lang.Vanilla.INVENTORY).pos(8, 72).color(0x404040).flat().draw();
        RenderUtil.endBatching();

        return false;
    }
}
