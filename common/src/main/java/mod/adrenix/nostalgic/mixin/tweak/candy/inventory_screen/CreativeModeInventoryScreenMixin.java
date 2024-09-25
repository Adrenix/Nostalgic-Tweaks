package mod.adrenix.nostalgic.mixin.tweak.candy.inventory_screen;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.candy.screen.inventory.ClassicCreativeModeInventoryScreen;
import mod.adrenix.nostalgic.helper.candy.screen.inventory.InventoryScreenHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperBulbBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
// Menu test, might need to have it be its own thing?
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    private CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    @Inject(
            method = "<INIT>",
            at = @At("HEAD")
    )
    public void nt_creative_inventory$OverrideInv(CallbackInfo callback) {

        if (CandyTweak.OLD_CREATIVE_INVENTORY.get())
            this.minecraft.setScreen(new ClassicCreativeModeInventoryScreen(this.minecraft.player ));
    }


    @Inject(
            method = "init",
            at = @At("HEAD")
    )
    public void nt_creative_inventory$OverrideInit(CallbackInfo callback) {

        if (CandyTweak.OLD_CREATIVE_INVENTORY.get())
            this.minecraft.setScreen(new ClassicCreativeModeInventoryScreen(this.minecraft.player ));
    }

    @Inject(
            method = "containerTick",
            at = @At("HEAD")
    )
    public void nt_creative_inventory$containerTick(CallbackInfo callback) {
        if (CandyTweak.OLD_CREATIVE_INVENTORY.get())
            this.minecraft.setScreen(new ClassicCreativeModeInventoryScreen(this.minecraft.player ));
    }


}
