package mod.adrenix.nostalgic.mixin.tweak.candy.inventory_screen;

import mod.adrenix.nostalgic.helper.candy.screen.inventory.ClassicCreativeModeInventoryScreen;
import mod.adrenix.nostalgic.helper.candy.screen.inventory.OldCreativeModeInventoryScreen;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.OldCreativeInventory;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Menu test, might need to have it be its own thing?
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    private CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    @Inject(
            method = "init",
            at = @At("HEAD")
    )
    public void nt_creative_inventory$OverrideInit(CallbackInfo callback) {

        if (CandyTweak.OLD_CREATIVE_INVENTORY.get() == OldCreativeInventory.BETA)
            this.minecraft.setScreen(new OldCreativeModeInventoryScreen( this.minecraft.player ));

        if (CandyTweak.OLD_CREATIVE_INVENTORY.get() == OldCreativeInventory.CLASSIC)
            this.minecraft.setScreen(new ClassicCreativeModeInventoryScreen( this.minecraft.player ));
    }

    @Inject(
            method = "containerTick",
            at = @At("HEAD")
    )
    public void nt_creative_inventory$containerTick(CallbackInfo callback) {
        if (CandyTweak.OLD_CREATIVE_INVENTORY.get() == OldCreativeInventory.BETA)
            this.minecraft.setScreen(new OldCreativeModeInventoryScreen( this.minecraft.player ));

        if (CandyTweak.OLD_CREATIVE_INVENTORY.get() == OldCreativeInventory.CLASSIC)
            this.minecraft.setScreen(new ClassicCreativeModeInventoryScreen( this.minecraft.player ));
    }


}
