package mod.adrenix.nostalgic.helper.candy.screen.inventory;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.OldCreativeInventory;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;


@Environment(EnvType.CLIENT)
public class OldCreativeModeInventoryScreen extends EffectRenderingInventoryScreen<OldCreativeModeInventoryScreen.OldItemPickerMenu> {
    private static final int NUM_ROWS = 9;
    private static final int NUM_COLS = 8;
    private static final int CONTAINER_SIZE = 72;
    static final SimpleContainer CONTAINER = new SimpleContainer(CONTAINER_SIZE);
    private CreativeInventoryListener listener;

    public OldCreativeModeInventoryScreen(LocalPlayer localPlayer) {
        super(new OldCreativeModeInventoryScreen.OldItemPickerMenu(localPlayer), localPlayer.getInventory(), CommonComponents.EMPTY);
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
        int j;
        ItemStack itemStack;
        ItemStack itemStack2;
        if (!this.menu.getCarried().isEmpty() && this.hasClickedOutside) {
            if (mouseButton == 0) {
                this.minecraft.player.drop(((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getCarried(), true);
                this.minecraft.gameMode.handleCreativeModeItemDrop(((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getCarried());
                this.menu.setCarried(ItemStack.EMPTY);
            }

            if (mouseButton == 1) {
                itemStack = ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getCarried().split(1);
                this.minecraft.player.drop(itemStack, true);
                this.minecraft.gameMode.handleCreativeModeItemDrop(itemStack);
            }
        } else if (slot != null && slot.container == CONTAINER) {
            itemStack = ((CreativeModeInventoryScreen.ItemPickerMenu) this.menu).getCarried();
            itemStack2 = slot.getItem();
            if (type == ClickType.SWAP) {
                if (!itemStack2.isEmpty()) {
                    this.minecraft.player.getInventory().setItem(mouseButton, itemStack2.copyWithCount(itemStack2.getMaxStackSize()));
                    this.minecraft.player.inventoryMenu.broadcastChanges();
                }

                return;
            }

            ItemStack itemStack3;
            if (type == ClickType.CLONE) {
                if (this.menu.getCarried().isEmpty() && slot.hasItem()) {
                    itemStack3 = slot.getItem();
                    this.menu.setCarried(itemStack3.copyWithCount(itemStack3.getMaxStackSize()));
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
                this.menu.setCarried(itemStack2.copyWithCount(j));
            } else if (mouseButton == 0) {
                this.menu.setCarried(ItemStack.EMPTY);
            } else if (!this.menu.getCarried().isEmpty()) {
                this.menu.getCarried().shrink(1);
            }
        } else if (this.menu != null) {
            var ff = CONTAINER_SIZE;
            var ts = CONTAINER_SIZE - 9;
            itemStack = slot == null ? ItemStack.EMPTY : this.menu.getSlot(slot.index).getItem();
            this.menu.clicked(slot == null ? slotId : slot.index, mouseButton, type, this.minecraft.player);
            if (AbstractContainerMenu.getQuickcraftHeader(mouseButton) == 2) {
                for(int k = 0; k < 9; ++k) {
                    this.minecraft.gameMode.handleCreativeModeItemAdd(this.menu.getSlot(CONTAINER_SIZE + k).getItem(), ts + k);
                }
            } else if (slot != null) {
                itemStack2 = this.menu.getSlot(slot.index).getItem();
                this.minecraft.gameMode.handleCreativeModeItemAdd(itemStack2, slot.index - this.menu.slots.size() + 9 + ts);
                j = ff + mouseButton;
                if (type == ClickType.SWAP) {
                    this.minecraft.gameMode.handleCreativeModeItemAdd(itemStack, j - this.menu.slots.size() + 9 + ts);
                } else if (type == ClickType.THROW && !itemStack.isEmpty()) {
                    ItemStack itemStack4 = itemStack.copyWithCount(mouseButton == 0 ? 1 : itemStack.getMaxStackSize());
                    this.minecraft.player.drop(itemStack4, true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(itemStack4);
                }

                this.minecraft.player.inventoryMenu.broadcastChanges();
            }
        }
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        return mouseX < (double)guiLeft || mouseY < (double)guiTop || mouseX >= (double)(guiLeft + this.imageWidth) || mouseY >= (double)(guiTop + this.imageHeight);
    }

    private boolean scrolling;
    private float scrollOffs;

    private boolean canScroll() {
        return this.menu.canScroll();
    }

    protected boolean insideScrollbar(double mouseX, double mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 155;
        int l = j + 17;
        int m = k + 14;
        int n = l + 160 + 2;
        return mouseX >= (double)k && mouseY >= (double)l && mouseX < (double)m && mouseY < (double)n;
    }
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.canScroll()) {
            return false;
        } else {
            this.scrollOffs = this.menu.subtractInputFromScroll(this.scrollOffs, scrollY);
            this.menu.scrollTo(this.scrollOffs);
            return true;
        }
    }
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling) {
            int i = this.topPos + 16;
            int j = i + 160 + 2;
            this.scrollOffs = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.menu.scrollTo(this.scrollOffs);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if ( this.insideScrollbar(mouseX, mouseY)) {
                this.scrolling = this.canScroll();
                return true;
            }
        }
        this.hasClickedOutside = hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.scrolling = false;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, "Item selection", this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

        int left = this.leftPos;
        int top = this.topPos;

        guiGraphics.blit(TextureLocation.ALL_ITEMS, left, top, 0, 0, this.imageWidth, this.imageHeight);

        int scrollLeft = left + 154;
        int l = scrollLeft + 17;
        int n = l + 160 + 2;
        int scrollTop = top + 17 + (int)((float)(n - l - 17) * this.scrollOffs);

        guiGraphics.blit(TextureLocation.ALL_ITEMS, scrollLeft, scrollTop, 0, 208, 16, 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
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

    @Override
    protected void containerTick() {
        super.containerTick();

        if (CandyTweak.OLD_CREATIVE_INVENTORY.get() != OldCreativeInventory.BETA)
            this.minecraft.setScreen(new CreativeModeInventoryScreen( this.minecraft.player, minecraft.level.enabledFeatures(), false));
    }

    public static class OldItemPickerMenu extends CreativeModeInventoryScreen.ItemPickerMenu {

        Player localPlayer;
        Minecraft minecraft;
        public float currentScroll = 0.0f;

        protected int calculateItemRowCount() {
            return Mth.positiveCeilDiv(this.items.size(), NUM_COLS) - NUM_ROWS;
        }

        protected float subtractInputFromScroll(float scrollOffs, double input) {
            return Mth.clamp(scrollOffs - (float)(input / (double)this.calculateItemRowCount()), 0.0F, 1.0F);
        }

        public OldItemPickerMenu(Player player) {
            super(player);
            this.localPlayer = player;
            this.slots.clear();
            refreshItems();
            Inventory inventory = player.getInventory();

            int i;
            for(i = 0; i < NUM_ROWS; ++i) {
                for(int j = 0; j < NUM_COLS; ++j) {
                    this.addSlot(new OldCreativeModeInventoryScreen.CustomCreativeSlot(OldCreativeModeInventoryScreen.CONTAINER, i * 8 + j, 8 + j * 18, 18 + i * 18));
                }
            }

            for(i = 0; i < 9; ++i) {
                this.addSlot(new Slot(inventory, i, 8 + i * 18, 184));
            }

            this.scrollTo(0.0F);
        }
        public void refreshItems() {
            this.items.clear();
            this.items.addAll(OldCreativeModeItemHelper.GetItems());
        }

        public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
            return slot.container != OldCreativeModeInventoryScreen.CONTAINER;
        }

        public boolean canDragTo(Slot slot) {
            return slot.container != OldCreativeModeInventoryScreen.CONTAINER;
        }

        @Override
        public void scrollTo(float pos) {
            int i = this.getRowIndexForScroll(pos);
            this.currentScroll = pos;
            for(int j = 0; j < NUM_ROWS; ++j) {
                for(int k = 0; k < NUM_COLS; ++k) {
                    int l = k + (j + i) * NUM_COLS;
                    if (l >= 0 && l < this.items.size()) {
                        OldCreativeModeInventoryScreen.CONTAINER.setItem(k + j * NUM_COLS, (ItemStack)this.items.get(l));
                    } else {
                        OldCreativeModeInventoryScreen.CONTAINER.setItem(k + j * NUM_COLS, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}
