package mod.adrenix.nostalgic.mixin.tweak.candy.inventory_screen;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.helper.candy.screen.inventory.InventoryScreenHelper;
import mod.adrenix.nostalgic.mixin.access.AbstractContainerScreenAccess;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.InventoryShield;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu>
{
    /* Fake Constructor */

    private InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    /* Unique & Shadows */

    @Unique @Nullable private Slot nt$offHand;
    @Shadow @Final private RecipeBookComponent recipeBookComponent;

    /* Injections */

    /**
     * Changes the (x,y) coordinates of the inventory screen's slots.
     */
    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void nt_inventory_screen$onConstruct(Player player, CallbackInfo callback)
    {
        this.nt$offHand = InventoryScreenHelper.setPositionsForSlots(this.menu.slots, this.recipeBookComponent);
    }

    /**
     * Changes the (x,y) coordinate and texture of the inventory screen's recipe button.
     */
    @Inject(
        method = "init",
        at = @At("TAIL")
    )
    private void nt_inventory_screen$onInit(CallbackInfo callback)
    {
        InventoryScreenHelper.setRecipeButton((AbstractContainerScreenAccess) this, CandyTweak.INVENTORY_BOOK.get());
    }

    /**
     * Moves the inventory screen's label to the old position.
     */
    @WrapOperation(
        method = "renderLabels",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"
        )
    )
    private int nt_inventory_screen$wrapRenderedLabel(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean dropShadow, Operation<Integer> operation)
    {
        if (CandyTweak.OLD_INVENTORY.get())
            return graphics.drawString(this.font, this.title, 86, 16, 0x404040, false);

        return operation.call(graphics, font, text, x, y, color, dropShadow);
    }

    /**
     * Changes the inventory's background texture to the mod's since the changes are significant enough to warrant a new
     * texture.
     */
    @WrapOperation(
        method = "renderBg",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
        )
    )
    private void nt_inventory_screen$wrapBackgroundRenderer(GuiGraphics graphics, ResourceLocation atlas, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, Operation<Void> operation)
    {
        if (CandyTweak.OLD_INVENTORY.get())
            graphics.blit(TextureLocation.INVENTORY, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        else
            operation.call(graphics, atlas, x, y, uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * Renders the off-hand slot changes as needed. It is possible to use a modified off-hand slot without using the
     * modern inventory.
     */
    @Inject(
        method = "renderBg",
        at = @At("TAIL")
    )
    private void nt_inventory_screen$onFinishBackgroundRendering(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo callback)
    {
        InventoryScreenHelper.renderOffHandSlot(graphics, this.recipeBookComponent, this.nt$offHand, this.leftPos, this.height);
    }

    /**
     * The off-hand slot can be outside the normal inventory screen area. This injection is needed so that an off-screen
     * off-hand slot can be properly interacted with.
     */
    @ModifyReturnValue(
        method = "hasClickedOutside",
        at = @At("RETURN")
    )
    private boolean nt_inventory_screen$hasClickedOutside(boolean hasClickedOutside, double mouseX, double mouseY, int guiLeft, int guiTop)
    {
        int x = guiLeft + (this.recipeBookComponent.isVisible() ? this.imageWidth : -25);
        int y = guiTop + this.imageHeight - 32;
        boolean isNearShield = MathUtil.isWithinBox(mouseX, mouseY, x, y, 25, 32);
        boolean isBottomLeft = CandyTweak.INVENTORY_SHIELD.get() == InventoryShield.BOTTOM_LEFT;

        if (isNearShield && isBottomLeft)
            return false;

        return hasClickedOutside;
    }
}
