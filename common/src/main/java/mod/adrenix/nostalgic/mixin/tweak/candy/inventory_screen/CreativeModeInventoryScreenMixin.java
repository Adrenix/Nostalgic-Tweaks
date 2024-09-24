package mod.adrenix.nostalgic.mixin.tweak.candy.inventory_screen;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.candy.screen.inventory.ClassicCreativeModeInventoryScreen;
import mod.adrenix.nostalgic.helper.candy.screen.inventory.InventoryScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import java.util.List;
// Menu test, might need to have it be its own thing?
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    private CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }


    @Inject(
            method = "containerTick",
            at = @At("HEAD")
    )
    public void nt_creative_inventory$containerTick(CallbackInfo callback) {
        this.minecraft.setScreen(new ClassicCreativeModeInventoryScreen(this.minecraft.player ));
    }
    public Collection<ItemStack>  GetItems() {
        var items = new ArrayList<ItemStack>();

        items.add(new ItemStack(Items.COBBLESTONE));
        items.add(new ItemStack(Items.STONE));
        items.add(new ItemStack(Items.DIAMOND_ORE));
        items.add(new ItemStack(Items.GOLD_ORE));
        items.add(new ItemStack(Items.IRON_ORE));
        items.add(new ItemStack(Items.COAL_ORE));
        items.add(new ItemStack(Items.LAPIS_ORE));
        items.add(new ItemStack(Items.REDSTONE_ORE));
        items.add(new ItemStack(Items.STONE_BRICKS));
        items.add(new ItemStack(Items.MOSSY_STONE_BRICKS));
        items.add(new ItemStack(Items.CRACKED_STONE_BRICKS));
        items.add(new ItemStack(Items.CHISELED_STONE_BRICKS));
        items.add(new ItemStack(Items.CLAY));
        items.add(new ItemStack(Items.DIAMOND_BLOCK));
        items.add(new ItemStack(Items.GOLD_BLOCK));
        items.add(new ItemStack(Items.IRON_BLOCK));
        items.add(new ItemStack(Items.LAPIS_BLOCK));
        items.add(new ItemStack(Items.BRICKS));
        items.add(new ItemStack(Items.MOSSY_COBBLESTONE));
        items.add(new ItemStack(Items.SMOOTH_STONE_SLAB));
        items.add(new ItemStack(Items.SANDSTONE_SLAB));
        items.add(new ItemStack(Items.OAK_SLAB));
        items.add(new ItemStack(Items.COBBLESTONE_SLAB));
        items.add(new ItemStack(Items.STONE_BRICK_SLAB));
        System.out.println("hi");
        return items;
    }


}
