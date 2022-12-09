package mod.adrenix.nostalgic.client.config.gui.widget.text;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.client.config.gui.screen.list.AbstractListScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListMapScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * A title widget is responsible for rendering text that is associated with a saved list entry. Different rendering
 * instructions are needed depending on whether the title is associated with a map entry, or a set entry.
 */

public class TextTitle<V> extends AbstractWidget
{
    /* Fields */

    private final String resourceKey;

    @Nullable private final Map.Entry<String, V> entry;
    @Nullable private final V currentValue;

    /* Constructors */

    public TextTitle(@Nullable ListMapScreen<V> listMapScreen, @Nullable Map.Entry<String, V> entry, String resourceKey)
    {
        super(0, 0, ConfigRowList.getInstance().getRowWidth(), 12, Component.empty());

        this.entry = entry;
        this.resourceKey = resourceKey;

        if (listMapScreen != null && this.entry != null)
            this.currentValue = listMapScreen.getCopiedValue(entry);
        else
            this.currentValue = null;
    }

    /**
     * Create a new entry title widget without it being associated with a map entry.
     * @param resourceKey The item resource key associated with the item title.
     */
    public TextTitle(String resourceKey) { this(null, null, resourceKey); }

    /* Methods */

    /**
     * Handler method for when the mouse clicks on an entry title widget.
     * Always returns false to prevent a clicking sound from playing when this widget is left-clicked.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse click event.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) { return false; }

    /**
     * Handler method for rendering an entry title widget.
     * @param poseStack The current pose stack.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        boolean isInvalid = !ItemClientUtil.isValidEntry(this.resourceKey);
        int startX = ConfigRowList.getStartX();
        Font font = Minecraft.getInstance().font;
        AbstractListScreen screen = (AbstractListScreen) Minecraft.getInstance().screen;

        if (ClassUtil.isNotInstanceOf(screen, AbstractListScreen.class))
            return;

        ItemStack itemStack = isInvalid ? new ItemStack(Items.BARRIER) : ItemClientUtil.getItemStack(this.resourceKey);
        String itemName = ItemClientUtil.getLocalizedItem(this.resourceKey);
        Component entryTitle = Component.literal(itemName);
        Component literalKey = Component.translatable(LangUtil.Gui.LIST_ITEM_KEY, this.resourceKey);
        List<Component> tooltip = TextUtil.Wrap.tooltip(literalKey, 50);

        if (this.entry != null)
        {
            if (!this.entry.getValue().equals(this.currentValue))
                entryTitle = Component.literal(ChatFormatting.ITALIC + entryTitle.copy().getString());

            if (Minecraft.getInstance().screen instanceof ListMapScreen<?> mapScreen)
            {
                if (mapScreen.getDeletedEntries().contains(this.entry))
                    entryTitle = Component.literal(ChatFormatting.RED + entryTitle.copy().getString());
            }

            if (isInvalid)
                entryTitle = Component.literal(ChatFormatting.GOLD + entryTitle.copy().getString());
            else if (screen.isItemAdded(itemStack))
                entryTitle = Component.literal(ChatFormatting.GREEN + entryTitle.copy().getString());
        }

        int startY = this.y + 1;

        if (itemStack.getItem() instanceof BlockItem)
            startY += 1;

        screen.getItemRenderer().renderGuiItem(itemStack, startX, startY);
        Screen.drawString(poseStack, font, entryTitle, startX + 21, this.y + 6, 0xFFFFFF);

        boolean isHovering = MathUtil.isWithinBox(mouseX, mouseY, startX, this.y + 4, font.width(entryTitle) + 21, 14);
        boolean isAbove = mouseY <= ConfigWidgets.ROW_LIST_TOP;
        boolean isBelow = mouseY >= screen.height - ConfigWidgets.ROW_LIST_BOTTOM_OFFSET;

        if (isHovering && !isAbove && !isBelow)
            screen.renderLast.add(() -> screen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY));
    }

    /* Required Overrides */

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}
