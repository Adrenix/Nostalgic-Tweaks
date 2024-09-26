package mod.adrenix.nostalgic.helper.candy.screen.inventory;

import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ClassicCreativeModeInventoryScreen extends EffectRenderingInventoryScreen<ClassicCreativeModeInventoryScreen.ClassicItemPickerMenu> {

    private static final int NUM_ROWS = 5;
    private static final int NUM_COLS = 9;
    private static final int CONTAINER_SIZE = 45;
    static final SimpleContainer CONTAINER = new SimpleContainer(CONTAINER_SIZE);
    LocalPlayer player;

    public ClassicCreativeModeInventoryScreen(LocalPlayer localPlayer) {
        super(new ClassicCreativeModeInventoryScreen.ClassicItemPickerMenu(localPlayer), localPlayer.getInventory(), CommonComponents.EMPTY);
        localPlayer.containerMenu = this.menu;
        this.menu.minecraft = this.minecraft;
        this.menu.refreshItems();
        player = localPlayer;
    }

    @Override
    protected void init() {
        super.init();

        this.imageWidth = this.width / 2 + 120;
        this.imageHeight = 180;
        this.leftPos = this.width / 2 - 120;
        this.topPos = 30;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        return;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

        int left = this.leftPos;
        int top = this.topPos;

        guiGraphics.fillGradient(left, top, this.imageWidth, this.imageHeight, -1878719232, -1070583712);
        guiGraphics.drawCenteredString(this.font, "Select block",this.width / 2, 40,16777215);
        //guiGraphics.blit(TextureLocation.ALL_ITEMS, left, top, 0, 0, this.imageWidth, this.imageHeight);
/*
        int scrollLeft = left + 154;
        int l = scrollLeft + 17;
        int n = l + 160 + 2;
        int scrollTop = top + 17 + (int)((float)(n - l - 17) * this.scrollOffs);

        guiGraphics.blit(TextureLocation.ALL_ITEMS, scrollLeft, scrollTop, 0, 208, 16, 16);*/
    }

    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBg(guiGraphics, partialTick, mouseX, mouseY);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (slot != null && type == ClickType.PICKUP)  {
            this.minecraft.player.setItemInHand(InteractionHand.MAIN_HAND,slot.getItem());
            this.minecraft.player.inventoryMenu.broadcastChanges();
            this.onClose();
        }
    }

    public static class ClassicItemPickerMenu extends CreativeModeInventoryScreen.ItemPickerMenu {
        Minecraft minecraft;
        Player localPlayer;
        public float currentScroll = 0.0f;
        public ClassicItemPickerMenu(Player player) {
            super(player);
            this.localPlayer = player;
            this.slots.clear();
            refreshItems();
            Inventory inventory = player.getInventory();

            int i;
            for (i = 0; i < NUM_ROWS; ++i) {
                for (int j = 0; j < NUM_COLS; ++j) {
                    this.addSlot(new ClassicCreativeModeInventoryScreen.CustomCreativeSlot(ClassicCreativeModeInventoryScreen.CONTAINER, i * 9 + j, 14 + j * 24, 27 + i * 24));
                }
            }
            this.scrollTo(0.0F);
        }

        public void refreshItems() {
            this.items.clear();
            this.items.addAll(OldCreativeModeItemHelper.GetClassicItems());
        }
        @Override
        public void scrollTo(float pos) {
            int i = this.getRowIndexForScroll(pos);
            this.currentScroll = pos;
            for(int j = 0; j < NUM_ROWS; ++j) {
                for(int k = 0; k < NUM_COLS; ++k) {
                    int l = k + (j + i) * NUM_COLS;
                    if (l >= 0 && l < this.items.size()) {
                        ClassicCreativeModeInventoryScreen.CONTAINER.setItem(k + j * NUM_COLS, (ItemStack)this.items.get(l));
                    } else {
                        ClassicCreativeModeInventoryScreen.CONTAINER.setItem(k + j * NUM_COLS, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
    @Environment(EnvType.CLIENT)
    private static class CustomCreativeSlot extends Slot {
        public CustomCreativeSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        public boolean mayPickup(Player player) {
            ItemStack itemStack = this.getItem();
            if (super.mayPickup(player) && !itemStack.isEmpty()) {
                return itemStack.isItemEnabled(player.level().enabledFeatures()) && !itemStack.has(DataComponents.CREATIVE_SLOT_LOCK);
            } else {
                return itemStack.isEmpty();
            }
        }

    }
}
