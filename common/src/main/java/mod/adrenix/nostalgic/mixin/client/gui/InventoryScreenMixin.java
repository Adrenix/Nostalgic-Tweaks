package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.client.screen.SlotTracker;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.mixin.widen.AbstractContainerScreenAccessor;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu>
{
    /* Dummy Constructor */

    private InventoryScreenMixin(AbstractContainerMenu menu, Inventory inventory, Component component)
    {
        super((InventoryMenu) menu, inventory, component);
    }

    /* Shadows & Uniques */

    @Unique private Slot NT$offHand;
    @Shadow private float xMouse;
    @Shadow private float yMouse;
    @Shadow @Final private RecipeBookComponent recipeBookComponent;

    /* Unique Helpers */

    @Unique
    private void NT$setShader()
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Unique
    private void NT$renderOffhandSlot(GuiGraphics graphics)
    {
        TweakType.InventoryShield shield = ModConfig.Candy.getInventoryShield();
        this.NT$setShader();

        boolean isModernOverride = shield.equals(TweakType.InventoryShield.MODERN) && ModConfig.Candy.oldInventory();

        if (shield.equals(TweakType.InventoryShield.BOTTOM_LEFT) && this.NT$offHand != null)
        {
            if (this.recipeBookComponent.isVisible())
            {
                graphics.blit(TextureLocation.INVENTORY, this.leftPos + 172, this.height / 2 + 51, 200, 33, 26, 32);
                SlotTracker.OFF_HAND.move(this.NT$offHand, 174, 142);
            }
            else
            {
                graphics.blit(TextureLocation.INVENTORY, this.leftPos - 22, this.height / 2 + 51, 200, 0, 25, 32);
                SlotTracker.OFF_HAND.move(this.NT$offHand, -14, 142);
            }
        }
        else if (shield.equals(TweakType.InventoryShield.MIDDLE_RIGHT) || isModernOverride)
            graphics.blit(TextureLocation.INVENTORY, this.leftPos + 151, this.height / 2 - 22, 178, 56, 18, 18);

        if (!ModConfig.Candy.oldInventory() && !shield.equals(TweakType.InventoryShield.MODERN))
            graphics.blit(TextureLocation.INVENTORY, this.leftPos + 76, this.height / 2 - 22, 178, 74, 18, 18);
    }

    /* Injections */

    @Inject(
        method = "renderLabels",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onRenderLabels(GuiGraphics graphics, int mouseX, int mouseY, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldInventory())
        {
            graphics.drawString(this.font, this.title, 86, 16, 0x404040, false);
            callback.cancel();
        }
    }

    /**
     * Changes the x, y positions of slots. Controlled by various inventory tweak.
     */
    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void NT$onConstruction(Player player, CallbackInfo callback)
    {
        TweakType.InventoryShield shield = ModConfig.Candy.getInventoryShield();

        for (Slot slot : this.menu.slots)
        {
            SlotTracker.CRAFT_TOP_LEFT.moveOrReset(slot, 88, 26);
            SlotTracker.CRAFT_TOP_RIGHT.moveOrReset(slot, 106, 26);
            SlotTracker.CRAFT_BOTTOM_LEFT.moveOrReset(slot, 88, 44);
            SlotTracker.CRAFT_BOTTOM_RIGHT.moveOrReset(slot, 106, 44);
            SlotTracker.CRAFT_RESULT.moveOrReset(slot, 144, 36);

            if (SlotTracker.OFF_HAND.isEqualTo(slot))
            {
                this.NT$offHand = slot;

                if (shield.equals(TweakType.InventoryShield.MODERN))
                    SlotTracker.OFF_HAND.moveOrReset(slot, 152, 62);

                switch (shield)
                {
                    case INVISIBLE -> SlotTracker.OFF_HAND.move(slot, -9999, -9999);
                    case MIDDLE_RIGHT -> SlotTracker.OFF_HAND.move(slot, 152, 62);
                    case BOTTOM_LEFT ->
                    {
                        if (this.recipeBookComponent.isVisible())
                            SlotTracker.OFF_HAND.move(slot, 174, 142);
                        else
                            SlotTracker.OFF_HAND.move(slot, -14, 142);
                    }
                }
            }
        }
    }

    /**
     * Changes the x, y, and texture of the recipe button. Controlled by the inventory recipe button tweak.
     */
    @Inject(
        method = "init",
        at = @At("TAIL")
    )
    private void NT$onInit(CallbackInfo callback)
    {
        GuiUtil.createRecipeButton((AbstractContainerScreenAccessor) this, ModConfig.Candy.getInventoryBook());
    }

    /**
     * Overrides the rendered background by using the mod's inventory gui. Controlled by the old inventory tweak.
     */

    @Inject(
        method = "renderBg",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onStartBackgroundRender(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo callback)
    {
        if (!ModConfig.Candy.oldInventory() || this.minecraft == null || this.minecraft.player == null)
            return;

        this.NT$setShader();
        graphics.blit(TextureLocation.INVENTORY, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.NT$renderOffhandSlot(graphics);

        int x = this.leftPos + 51;
        int y = this.topPos + 75;
        float mx = (float) (this.leftPos + 51) - this.xMouse;
        float my = (float) (this.topPos + 75 - 50) - this.yMouse;

        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, x, y, 30, mx, my, this.minecraft.player);

        callback.cancel();
    }

    @Inject(
        method = "renderBg",
        at = @At("TAIL")
    )
    private void NT$onFinishBackgroundRender(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo callback)
    {
        this.NT$renderOffhandSlot(graphics);
    }

    /**
     * Fixes the issue of having slots outside the rendered GUI area. Not controlled by any tweaks.
     */
    @Inject(
        method = "hasClickedOutside",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onHasClickedOutSide(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton, CallbackInfoReturnable<Boolean> callback)
    {
        int x = guiLeft + (this.recipeBookComponent.isVisible() ? this.imageWidth : -25);
        int y = guiTop + this.imageHeight - 32;
        boolean isNearShield = MathUtil.isWithinBox(mouseX, mouseY, x, y, 25, 32);
        boolean isBottomLeft = ModConfig.Candy.getInventoryShield().equals(TweakType.InventoryShield.BOTTOM_LEFT);

        if (isNearShield && isBottomLeft)
            callback.setReturnValue(false);
    }
}
