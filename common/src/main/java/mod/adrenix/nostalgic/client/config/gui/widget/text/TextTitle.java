package mod.adrenix.nostalgic.client.config.gui.widget.text;

import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListMapScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListSetScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.RemoveType;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.common.config.list.ListId;
import mod.adrenix.nostalgic.util.common.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A title widget is responsible for rendering text that is associated with a saved list entry. Different rendering
 * instructions are needed depending on whether the title is associated with a map entry, or a set entry.
 */

public class TextTitle<V> extends AbstractWidget
{
    /* Fields */

    private static final int START_X = 0;
    private static final int START_Y = 0;
    private static final int HEIGHT = 12;
    private static final int WIDTH = ConfigRowList.getInstance().getRowWidth();

    private final String resourceKey;

    private final RemoveType removeType;
    private final Map.Entry<String, V> entry;
    private final Supplier<Boolean> isRemoved;
    private final V currentValue;

    /* Constructors */

    public TextTitle(ListMapScreen<V> listMapScreen, @NotNull Map.Entry<String, V> entry, String resourceKey)
    {
        super(START_X, START_Y, WIDTH, HEIGHT, Component.empty());

        this.entry = entry;
        this.resourceKey = resourceKey;
        this.currentValue = listMapScreen.getCachedValue(entry);
        this.removeType = RemoveType.SAVED;
        this.isRemoved = null;
    }

    /**
     * Create a new entry title widget without it being associated with a map entry.
     * @param resourceKey The item resource key associated with the item title.
     */
    public TextTitle(@NotNull RemoveType removeType, String resourceKey, @NotNull Supplier<Boolean> isRemoved)
    {
        super(START_X, START_Y, WIDTH, HEIGHT, Component.empty());

        this.entry = null;
        this.currentValue = null;
        this.resourceKey = resourceKey;
        this.removeType = removeType;
        this.isRemoved = isRemoved;
    }

    /* Methods */

    /**
     * Gets the default value associated with a map resource key.
     * @param mapScreen The map screen associated with this text title widget.
     * @return A formatted string with a custom prefix, color, and value.
     */
    private String getDefaultTitle(ListMapScreen<?> mapScreen)
    {
        ListId listId = mapScreen.getListId();
        Object value = mapScreen.getListMap().getDefaultMap().get(this.resourceKey);
        String prefix = Component.translatable(LangUtil.Gui.SLIDER_VALUE).getString();
        String color = ChatFormatting.YELLOW.toString();
        String gray = ChatFormatting.GRAY.toString();

        if (listId == ListId.CUSTOM_FOOD_STACKING)
            prefix = Component.translatable(LangUtil.Gui.SLIDER_STACK).getString();

        if (listId == ListId.CUSTOM_FOOD_HEALTH)
        {
            prefix = Component.translatable(LangUtil.Gui.SLIDER_HEARTS).getString();
            color = ChatFormatting.GREEN.toString();
            value = (int) value / 2.0F;
        }

        if (value instanceof Boolean bool)
            color = bool ? ChatFormatting.GREEN.toString() : ChatFormatting.RED.toString();

        return String.format(" %s(%s: %s%s%s)", gray, prefix, color, TextUtil.toTitleCase(value.toString()), gray);
    }

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
     * @param graphics The current GuiGraphics object.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        boolean isInvalid = !ItemCommonUtil.isValidKey(this.resourceKey);
        int startX = ConfigRowList.getStartX() - 1;
        Font font = Minecraft.getInstance().font;
        ListScreen listScreen = (ListScreen) Minecraft.getInstance().screen;

        if (ClassUtil.isNotInstanceOf(listScreen, ListScreen.class))
            return;

        ItemStack itemStack = isInvalid ? new ItemStack(Items.BARRIER) : ItemCommonUtil.getItemStack(this.resourceKey);
        String itemName = ItemCommonUtil.getLocalizedItem(this.resourceKey);
        Component entryTitle = Component.literal(itemName);
        Component literalKey = Component.translatable(LangUtil.Gui.LIST_ITEM_KEY, this.resourceKey);
        List<Component> tooltip = TextUtil.Wrap.tooltip(literalKey, 50);

        if (this.removeType == RemoveType.DEFAULT && listScreen instanceof ListMapScreen<?> mapScreen)
            entryTitle = Component.literal(entryTitle.getString() + this.getDefaultTitle(mapScreen));

        boolean isEntryChanged = this.entry != null && !this.entry.getValue().equals(this.currentValue);
        boolean isKeyChanged = this.isRemoved != null && this.isRemoved.get();
        boolean isItalics = this.removeType == RemoveType.SAVED && (isEntryChanged || isKeyChanged);

        if (isItalics)
            entryTitle = Component.literal(ChatFormatting.ITALIC + entryTitle.copy().getString());

        boolean isEntryDeleted = listScreen instanceof ListMapScreen<?> mapScreen && mapScreen.getDeletedEntries().contains(this.entry);
        boolean isKeyDeleted = listScreen instanceof ListSetScreen setScreen && setScreen.getDeletedKeys().contains(this.resourceKey);
        boolean isDefaultDisabled = listScreen.isDefaultItemDisabled(this.resourceKey);
        boolean isTitleRed = this.removeType == RemoveType.SAVED ? (isEntryDeleted || isKeyDeleted) : isDefaultDisabled;
        boolean isOverridden = this.removeType == RemoveType.DEFAULT && listScreen.isItemSaved(itemStack);

        if (isTitleRed || isOverridden)
            entryTitle = Component.literal(ChatFormatting.RED + entryTitle.copy().getString());

        if (isInvalid)
            entryTitle = Component.literal(ChatFormatting.GOLD + entryTitle.copy().getString());
        else if (listScreen.isItemAdded(itemStack) && this.removeType != RemoveType.DEFAULT)
            entryTitle = Component.literal(ChatFormatting.GREEN + entryTitle.copy().getString());

        int startY = this.getY() + 1;

        if (itemStack.getItem() instanceof BlockItem)
            startY += 1;

        if (this.removeType == RemoveType.DEFAULT && listScreen.isItemSaved(itemStack))
        {
            String saved = Component.translatable(LangUtil.Gui.LIST_SAVED_ITEMS).getString();
            entryTitle = Component.literal(entryTitle.getString() + ChatFormatting.RED + " (" + saved + ")");
        }

        graphics.renderItem(itemStack, startX, startY);
        graphics.drawString(font, entryTitle, startX + 21, this.getY() + 6, 0xFFFFFF);

        boolean isHovering = MathUtil.isWithinBox(mouseX, mouseY, startX, this.getY() + 4, font.width(entryTitle) + 21, 14);
        boolean isInBounds = ConfigWidgets.isInsideRowList(mouseY);
        boolean isNotDefault = this.removeType != RemoveType.DEFAULT;

        if (isHovering && isInBounds && isNotDefault)
            listScreen.renderLast.add(() -> graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY));
    }

    /* Required Overrides */

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
