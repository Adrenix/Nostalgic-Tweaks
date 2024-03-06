package mod.adrenix.nostalgic.mixin.tweak.candy.anvil_screen;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu>
{
    /* Fake Constructor */

    private AnvilScreenMixin(AnvilMenu menu, Inventory inventory, Component title, ResourceLocation menuLocation)
    {
        super(menu, inventory, title, menuLocation);
    }

    /* Injections */

    /**
     * Disables rendering of the "inventory" text on the screen, but keeps the top anvil screen title.
     */
    @WrapWithCondition(
        method = "renderLabels",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/ItemCombinerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V"
        )
    )
    private boolean nt_anvil_screen$drawLabel(ItemCombinerScreen<?> screen, GuiGraphics graphics, int mouseX, int mouseY)
    {
        if (!CandyTweak.OLD_ANVIL_SCREEN.get())
            return true;

        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);

        return false;
    }

    /**
     * Disables the cost text background.
     */
    @WrapWithCondition(
        method = "renderLabels",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
        )
    )
    private boolean nt_anvil_screen$drawFill(GuiGraphics graphics, int minX, int minY, int maxX, int maxY, int color)
    {
        return !CandyTweak.OLD_ANVIL_SCREEN.get();
    }

    /**
     * Changes the cost text color and position.
     */
    @WrapWithCondition(
        method = "renderLabels",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"
        )
    )
    private boolean nt_anvil_screen$drawText(GuiGraphics graphics, Font font, Component text, int x, int y, int color)
    {
        if (!CandyTweak.OLD_ANVIL_SCREEN.get())
            return true;

        int foreground = 0x203F08;
        int background = 0x80FF20;

        if (color == 0xFF6060)
        {
            foreground = 0x3F1818;
            background = 0xFF6060;
        }

        RenderUtil.beginBatching();
        DrawText.begin(graphics, text).pos(x + 3, y - 2).color(foreground).flat().draw();
        DrawText.begin(graphics, text).pos(x + 3, y - 1).color(foreground).flat().draw();
        DrawText.begin(graphics, text).pos(x + 2, y - 1).color(foreground).flat().draw();
        DrawText.begin(graphics, text).pos(x + 2, y - 2).color(background).flat().draw();
        RenderUtil.endBatching();

        return false;
    }
}
