package mod.adrenix.nostalgic.helper.candy.screen.inventory;

import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.impl.util.Localization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;


@Environment(EnvType.CLIENT)
public class ClassicCreativeModeInventoryScreen extends EffectRenderingInventoryScreen<ClassicCreativeModeInventoryScreen.ClassicItemPickerMenu> {
    private static final int NUM_ROWS = 9;
    private static final int NUM_COLS = 8;
    private static final int CONTAINER_SIZE = 72;
    static final SimpleContainer CONTAINER = new SimpleContainer(CONTAINER_SIZE);
    private CreativeInventoryListener listener;

    public ClassicCreativeModeInventoryScreen(LocalPlayer localPlayer) {
        super(new ClassicCreativeModeInventoryScreen.ClassicItemPickerMenu(localPlayer), localPlayer.getInventory(), CommonComponents.EMPTY);
        localPlayer.containerMenu = this.menu;
        this.menu.minecraft = this.minecraft;
        this.imageHeight = 208;
        this.imageWidth = 176;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.menu.refreshItems();
    }

    @Override
    protected void init() {
        super.init();

        this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
        this.listener = new CreativeInventoryListener(this.minecraft);
        this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
    }

    private boolean hasClickedOutside;
    @Override
    protected void slotClicked(@Nullable Slot slot, int slotId, int mouseButton, ClickType type) {
        boolean bl = type == ClickType.QUICK_MOVE;
        type = slotId == -999 && type == ClickType.PICKUP ? ClickType.THROW : type;
        if (slot != null && !slot.mayPickup(this.minecraft.player)) {
            return;
        }
        if (slot == null) return;
        ItemStack itemStack = ((CreativeModeInventoryScreen.ItemPickerMenu) this.menu).getCarried();
        ItemStack itemStack2 = slot.getItem();
        int j;
        if (slot.container == CONTAINER) {
            if (type == ClickType.SWAP) {
                if (!itemStack2.isEmpty()) {
                    this.minecraft.player.getInventory().setItem(mouseButton, itemStack2.copyWithCount(itemStack2.getMaxStackSize()));
                    this.minecraft.player.inventoryMenu.broadcastChanges();
                }

                return;
            }

            ItemStack itemStack3;
            if (type == ClickType.CLONE) {
                if (((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getCarried().isEmpty() && slot.hasItem()) {
                    itemStack3 = slot.getItem();
                    ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).setCarried(itemStack3.copyWithCount(itemStack3.getMaxStackSize()));
                }

                return;
            }

            if (type == ClickType.THROW) {
                if (!itemStack2.isEmpty()) {
                    itemStack3 = itemStack2.copyWithCount(mouseButton == 0 ? 1 : itemStack2.getMaxStackSize());
                    this.minecraft.player.drop(itemStack3, true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(itemStack3);
                }

                return;
            }

            if (!itemStack.isEmpty() && !itemStack2.isEmpty() && ItemStack.isSameItemSameComponents(itemStack, itemStack2)) {
                if (mouseButton == 0) {
                    if (bl) {
                        itemStack.setCount(itemStack.getMaxStackSize());
                    } else if (itemStack.getCount() < itemStack.getMaxStackSize()) {
                        itemStack.grow(1);
                    }
                } else {
                    itemStack.shrink(1);
                }
            } else if (!itemStack2.isEmpty() && itemStack.isEmpty()) {
                j = bl ? itemStack2.getMaxStackSize() : itemStack2.getCount();
                ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).setCarried(itemStack2.copyWithCount(j));
            } else if (mouseButton == 0) {
                ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
            } else if (!((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getCarried().isEmpty()) {
                ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getCarried().shrink(1);
            }
        } else if (this.menu != null) {
            var ff = CONTAINER_SIZE;
            var ts = CONTAINER_SIZE - 9;
            itemStack = slot == null ? ItemStack.EMPTY : ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getSlot(slot.index).getItem();
            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).clicked(slot == null ? slotId : slot.index, mouseButton, type, this.minecraft.player);
            if (AbstractContainerMenu.getQuickcraftHeader(mouseButton) == 2) {
                for(int k = 0; k < 9; ++k) {
                    this.minecraft.gameMode.handleCreativeModeItemAdd(((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getSlot(CONTAINER_SIZE + k).getItem(), ts + k);
                }
            } else if (slot != null) {
                itemStack2 = ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getSlot(slot.index).getItem();
                this.minecraft.gameMode.handleCreativeModeItemAdd(itemStack2, slot.index - ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.size() + 9 + ts);
                j = ff + mouseButton;
                if (type == ClickType.SWAP) {
                    this.minecraft.gameMode.handleCreativeModeItemAdd(itemStack, j - ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.size() + 9 + ts);
                } else if (type == ClickType.THROW && !itemStack.isEmpty()) {
                    ItemStack itemStack4 = itemStack.copyWithCount(mouseButton == 0 ? 1 : itemStack.getMaxStackSize());
                    this.minecraft.player.drop(itemStack4, true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(itemStack4);
                }

                this.minecraft.player.inventoryMenu.broadcastChanges();
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, "Item Selection", this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TextureLocation.ALL_ITEMS, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
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

    public static class ClassicItemPickerMenu extends CreativeModeInventoryScreen.ItemPickerMenu {

        Player localPlayer;
        Minecraft minecraft;
        public float currentScroll = 0.0f;

        public Collection<ItemStack> GetItems() {
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

            if (this.minecraft != null) {
                ClientPacketListener clientPacketListener = this.minecraft.getConnection();
                SessionSearchTrees sessionSearchTrees = clientPacketListener.searchTrees();
                SearchTree searchTree;
                searchTree = sessionSearchTrees.creativeTagSearch();
                String string = "";
                items.addAll(searchTree.search(string.toLowerCase(Locale.ROOT)));
            }

            System.out.println("hi");
            return items;
        }
        public ClassicItemPickerMenu(Player player) {
            super(player);
            this.localPlayer = player;
            this.slots.clear();
            refreshItems();
            Inventory inventory = player.getInventory();

            int i;
            for(i = 0; i < NUM_ROWS; ++i) {
                for(int j = 0; j < NUM_COLS; ++j) {
                    this.addSlot(new ClassicCreativeModeInventoryScreen.CustomCreativeSlot(ClassicCreativeModeInventoryScreen.CONTAINER, i * 8 + j, 8 + j * 18, 18 + i * 18));
                }
            }

            for(i = 0; i < 9; ++i) {
                this.addSlot(new Slot(inventory, i, 8 + i * 18, 184));
            }

            this.scrollTo(0.0F);
        }
        public void refreshItems() {
            this.items.clear();
            this.items.addAll(GetItems());
        }

        @Override
        public void scrollTo(float pos) {
            System.out.println("hi2");
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
}
