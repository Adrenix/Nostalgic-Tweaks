package mod.adrenix.nostalgic.mixin.tweak.candy.crafting_screen;

import mod.adrenix.nostalgic.mixin.access.AbstractContainerScreenAccess;
import mod.adrenix.nostalgic.helper.candy.screen.inventory.InventoryScreenHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
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
    /* Fake Constructor */

    private CraftingScreenMixin(CraftingMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    /* Injections */

    /**
     * Adds the mod's custom recipe book button to the crafting table screen.
     */
    @Inject(
        method = "init",
        at = @At("TAIL")
    )
    private void nt_crafting_screen$onScreenInit(CallbackInfo callback)
    {
        InventoryScreenHelper.setRecipeButton((AbstractContainerScreenAccess) this, CandyTweak.CRAFTING_BOOK.get());
    }
}
