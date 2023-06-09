package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.widen.AbstractContainerScreenAccessor;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingScreen.class)
public abstract class CraftingScreenMixin extends AbstractContainerScreen<CraftingMenu>
{
    /* Dummy Constructor */

    private CraftingScreenMixin(CraftingMenu menu, Inventory inventory, Component component)
    {
        super(menu, inventory, component);
    }

    /* Overrides */

    /**
     * Changes the position of the screen title text to match the position of the old crafting table screen.
     * Controlled by the old crafting table screen tweak.
     */
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        if (ModConfig.Candy.oldCraftingScreen())
        {
            graphics.drawString(this.font, this.title, 28, 6, 0x404040, false);
            graphics.drawString(this.font, Component.translatable(LangUtil.Vanilla.INVENTORY), 8, 72, 0x404040, false);
        }
        else
            super.renderLabels(graphics, mouseX, mouseY);
    }

    /* Injections */

    /**
     * Changes the x, y, and texture of the recipe button.
     * Controlled by the crafting table recipe button tweak.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void NT$onInit(CallbackInfo callback)
    {
        GuiUtil.createRecipeButton((AbstractContainerScreenAccessor) this, ModConfig.Candy.getCraftingBook());
    }
}
