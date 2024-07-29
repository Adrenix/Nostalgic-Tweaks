package mod.adrenix.nostalgic.mixin.tweak.candy.furnace_screen;

import mod.adrenix.nostalgic.mixin.access.AbstractContainerScreenAccess;
import mod.adrenix.nostalgic.helper.candy.screen.inventory.InventoryScreenHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceScreen.class)
public abstract class AbstractFurnaceScreenMixin extends AbstractContainerScreen<AbstractFurnaceMenu>
{
    /* Fake Constructor */

    private AbstractFurnaceScreenMixin(AbstractFurnaceMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    /* Injections */

    /**
     * Adds the mod's custom recipe book button to the furnace screen.
     */
    @Inject(
        method = "init",
        at = @At("TAIL")
    )
    private void nt_furnace_screen$onScreenInit(CallbackInfo callback)
    {
        InventoryScreenHelper.setRecipeButton((AbstractContainerScreenAccess) this, CandyTweak.FURNACE_BOOK.get());
    }
}
