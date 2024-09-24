package mod.adrenix.nostalgic.helper.candy.screen.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

import java.util.Iterator;


@Environment(EnvType.CLIENT)
public class ClassicCreativeModeInventoryScreen extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    private static final int NUM_ROWS = 9;
    private static final int NUM_COLS = 8;

    public ClassicCreativeModeInventoryScreen(LocalPlayer localPlayer) {
        super(new CreativeModeInventoryScreen.ItemPickerMenu(localPlayer), localPlayer.getInventory(), CommonComponents.EMPTY);
        localPlayer.containerMenu = this.menu;
        this.imageHeight = 136;
        this.imageWidth = 195;;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

        guiGraphics.blit(selectedTab.getBackgroundTexture(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.searchBox.render(guiGraphics, mouseX, mouseY, partialTick);
        int i = this.leftPos + 175;
        int j = this.topPos + 18;
        int k = j + 112;
        if (selectedTab.canScroll()) {
            ResourceLocation resourceLocation = this.canScroll() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
            guiGraphics.blitSprite(resourceLocation, i, j + (int)((float)(k - j - 17) * this.scrollOffs), 12, 15);
        }

    }
}
