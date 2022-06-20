package mod.adrenix.nostalgic.client.config.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.client.config.CommonRegistry;
import mod.adrenix.nostalgic.client.config.CustomSwings;
import mod.adrenix.nostalgic.client.config.gui.ItemSuggestionHelper;
import mod.adrenix.nostalgic.client.config.gui.widget.CustomizedRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.ToggleCheckbox;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class CustomizeScreen extends SettingsScreen
{
    // Widget Constants
    protected static final int TOP_HEIGHT = 24;
    protected static final int BOTTOM_OFFSET = 32;
    protected static final int ITEM_HEIGHT = 25;
    protected static final int SEARCH_BOX_W = 226;
    protected static final int SEARCH_BOX_H = 18;
    protected static final int CHECKBOX_W = 20;
    protected static final int CHECKBOX_H = 20;
    protected static final int TOP_ROW_Y = 23;

    // Customize Fields
    private static final ClientConfig config = CommonRegistry.getRoot();
    private final Checkbox toolsCheckbox;
    private final Checkbox blocksCheckbox;
    private final Checkbox itemsCheckbox;
    private final Checkbox resetCheckbox;
    private Button resetButton;
    private Button addItemButton;
    private Button autofillButton;
    private EditBox searchBox;
    private ItemSuggestionHelper itemSuggestions;
    private CustomizedRowList customizedRowList;
    private final Screen parent;
    private final Map<String, Integer> undo;
    private final List<Widget> renderables = Lists.newArrayList();


    // Constructor
    public CustomizeScreen(Screen parent)
    {
        super(parent, Component.translatable(NostalgicLang.Gui.CUSTOMIZE), false);

        this.parent = parent;
        this.undo = Maps.newHashMap(config.custom);

        int x = 2, y = TOP_ROW_Y, w = CHECKBOX_W, h = CHECKBOX_H;
        this.toolsCheckbox = new ToggleCheckbox(this, x, y + 27, w, h, Component.translatable(NostalgicLang.Gui.CUSTOMIZE_TOOL), true);
        this.blocksCheckbox = new ToggleCheckbox(this, x, y + 52, w, h, Component.translatable(NostalgicLang.Gui.CUSTOMIZE_BLOCK), true);
        this.itemsCheckbox = new ToggleCheckbox(this, x, y + 77, w, h, Component.translatable(NostalgicLang.Gui.CUSTOMIZE_ITEM), true);
        this.resetCheckbox = new ToggleCheckbox(this, x, y - 1, w, h, Component.translatable(NostalgicLang.Gui.CUSTOMIZE_RESET), false);
    }

    /* Getters */

    public Button getAddItemButton() { return addItemButton; }
    public Minecraft getMinecraft() { return minecraft; }
    public ItemRenderer getItemRenderer() { return this.itemRenderer; }

    /* Helpers */

    public boolean suggestionsAreClosed()
    {
        if (this.itemSuggestions == null)
            return true;
        return !this.itemSuggestions.isSuggesting();
    }

    public void setSuggestionFocus(boolean state) { this.searchBox.setFocus(state); }

    private void addCustomizedSwing(Item item)
    {
        CustomSwings.addItem(item);
        AutoConfig.getConfigHolder(ClientConfig.class).save();
        CustomizedRowList.added = CustomSwings.getEntryFromItem(item);

        this.refresh();
        this.openToast(item);
    }

    private void openToast(Item item)
    {
        Component message = Component.translatable(NostalgicLang.Gui.CUSTOMIZE_ADD).withStyle(ChatFormatting.WHITE);
        Component display = Component.translatable(item.getName(item.getDefaultInstance()).getString()).withStyle(ChatFormatting.GREEN);
        this.minecraft.getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.TUTORIAL_HINT, message, display));
    }

    private void clearSearchBox() { this.itemSuggestions.resetInputBox(); }

    private void refresh()
    {
        this.clearSearchBox();
        this.minecraft.setScreen(this);
        this.searchBox.setFocus(false);
        this.resetButton.active = this.resetCheckbox.selected();
    }

    private void resetCustomizedList()
    {
        config.custom.clear();
        CustomizedRowList.added = null;

        this.resetCheckbox.onPress();
        this.clearSearchBox();
        this.minecraft.setScreen(this);
    }

    private int getSavePosition() { return this.width / 2 + 3; }

    private void renderWidget(Widget widget, PoseStack stack, int mouseX, int mouseY, float ticks)
    {
        if (widget instanceof Button && ((Button) widget).x == this.getSavePosition())
            ((Button) widget).active = this.isSavable();
        widget.render(stack, mouseX, mouseY, ticks);
    }

    private boolean isSavable()
    {
        if (this.undo.size() != config.custom.size())
            return true;
        else if (CustomizedRowList.deleted.size() > 0)
            return true;

        for (Map.Entry<String, Integer> entry : config.custom.entrySet())
            if (this.undo.get(entry.getKey()).intValue() != entry.getValue().intValue())
                return true;

        return false;
    }

    private void sortCustomizedRowList()
    {
        boolean addTools = this.toolsCheckbox.selected();
        boolean addBlocks = this.blocksCheckbox.selected();
        boolean addItems = this.itemsCheckbox.selected();
        List<Map.Entry<String, Integer>> sorted = CustomSwings.getSortedItems(addTools, addBlocks, addItems);
        for (Map.Entry<String, Integer> entry : sorted)
            this.customizedRowList.addItem(entry);
    }

    /* Overrides */

    @Override
    public void tick()
    {
        this.searchBox.tick();

        if (this.resetButton.active != this.resetCheckbox.selected())
            this.resetButton.active = this.resetCheckbox.selected();
    }

    @Override
    public void resize(Minecraft minecraft, int x, int y)
    {
        String searching = this.searchBox.getValue();
        this.init(minecraft, x, y);
        this.searchBox.setValue(searching);
        this.itemSuggestions.updateItemSuggestions();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers)
    {
        if (this.itemSuggestions.keyPressed(key))
            return true;
        else if (key == 257 && this.addItemButton.active)
        {
            this.addCustomizedSwing(this.itemSuggestions.getItem());
            return true;
        }
        else if (key == 256 && this.shouldCloseOnEsc())
        {
            this.onCancel();
            return true;
        }
        else if (super.keyPressed(key, scanCode, modifiers))
            return true;
        else
            return key == 257 || key == 335;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double f)
    {
        return this.itemSuggestions.mouseScrolled(f) || super.mouseScrolled(x, y, f);
    }

    @Override
    public boolean mouseClicked(double x, double y, int i)
    {
        return this.itemSuggestions.mouseClicked(x, y) || super.mouseClicked(x, y, i);
    }

    @Override
    protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget)
    {
        this.renderables.add(widget);
        return this.addWidget(widget);
    }

    @Override
    protected void removeWidget(GuiEventListener widget)
    {
        if (widget instanceof Widget)
            this.renderables.remove(widget);
        super.removeWidget(widget);
    }

    @Override
    protected void clearWidgets()
    {
        this.renderables.clear();
        super.clearWidgets();
    }

    @Override
    protected void init()
    {
        this.addRenderableWidget(this.toolsCheckbox);
        this.addRenderableWidget(this.blocksCheckbox);
        this.addRenderableWidget(this.itemsCheckbox);
        this.addRenderableWidget(this.resetCheckbox);

        WidgetProvider widget = new WidgetProvider();

        this.customizedRowList = widget.createCustomizedRowList();
        this.searchBox = widget.createSearchBox();
        this.searchBox.setResponder(this::onEdited);

        this.sortCustomizedRowList();

        this.addItemButton = widget.createAddButton();
        this.autofillButton = widget.createAutofillButton();
        this.resetButton = widget.createResetButton();
        this.resetButton.active = false;
        this.autofillButton.active = false;
        this.addItemButton.active = false;

        this.addWidget(this.customizedRowList);
        this.addRenderableWidget(this.searchBox);
        this.addRenderableWidget(this.addItemButton);
        this.addRenderableWidget(this.autofillButton);
        this.addRenderableWidget(this.resetButton);
        this.addRenderableWidget(widget.createCancelButton());
        this.addRenderableWidget(widget.createSaveButton());

        this.setInitialFocus(this.searchBox);

        this.itemSuggestions = new ItemSuggestionHelper(this, this.searchBox, this.font, 7, -16777216);
        this.itemSuggestions.setAllowSuggestions(true);
        this.itemSuggestions.updateItemSuggestions();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks)
    {
        this.autofillButton.active = this.minecraft.level != null;
        if (this.minecraft.level != null)
            this.fillGradient(poseStack, 0, 0, this.width, this.height, 839913488, 16777216);
        else
            this.renderDirtBackground(0);
        this.customizedRowList.render(poseStack, mouseX, mouseY, ticks);
        this.renderScreenTitle(poseStack, 7);
        this.searchBox.render(poseStack, mouseX, mouseY, ticks);
        this.renderables.forEach(widget -> this.renderWidget(widget, poseStack, mouseX, mouseY, ticks));
        this.itemSuggestions.render(poseStack, mouseX, mouseY);
        this.itemRenderer.renderGuiItem(new ItemStack(Items.TNT), this.resetButton.x + 2, this.resetButton.y + 2);
    }

    /* Cancelling Consumer */

    private class CancelConsumer implements BooleanConsumer
    {
        @Override
        public void accept(boolean understood)
        {
            if (understood)
            {
                CustomizeScreen.this.onClose(true);
                CustomizeScreen.this.minecraft.setScreen(CustomizeScreen.this.parent);
            }
            else
                CustomizeScreen.this.minecraft.setScreen(CustomizeScreen.this);
        }
    }

    /* On-click Handlers */

    private void onEdited(String ignored) { this.itemSuggestions.updateItemSuggestions(); }

    private void onAddCustomizedSwing() { this.addCustomizedSwing(this.itemSuggestions.getItem()); }

    private void onAutofill()
    {
        if (this.minecraft.player != null)
            this.addCustomizedSwing(this.minecraft.player.getMainHandItem().getItem());
    }

    private void onCancel()
    {
        if (!this.isSavable())
        {
            this.onClose(true);
            return;
        }

        this.minecraft.setScreen(
            new ConfirmScreen(
                new CancelConsumer(),
                    Component.translatable(NostalgicLang.Cloth.QUIT_CONFIG),
                    Component.translatable(NostalgicLang.Cloth.QUIT_CONFIG_SURE),
                    Component.translatable(NostalgicLang.Cloth.QUIT_DISCARD),
                    Component.translatable(NostalgicLang.Vanilla.GUI_CANCEL)
            )
        );
    }

    private void onClose(boolean isCancelled)
    {
        if (!isCancelled)
        {
            for (Map.Entry<String, Integer> entry : CustomizedRowList.deleted)
                config.custom.remove(entry.getKey());
        }
        else
        {
            config.custom.clear();
            config.custom.putAll(this.undo);
        }

        CustomizedRowList.deleted.clear();
        CustomizedRowList.added = null;

        super.onClose();
    }

    /* Widget Provider */

    private class WidgetProvider
    {
        public CustomizedRowList createCustomizedRowList()
        {
            return new CustomizedRowList(
                CustomizeScreen.this,
                CustomizeScreen.this.width,
                CustomizeScreen.this.height,
                TOP_HEIGHT + 22,
                CustomizeScreen.this.height - BOTTOM_OFFSET,
                ITEM_HEIGHT
            );
        }

        public EditBox createSearchBox()
        {
            return new EditBox(
                CustomizeScreen.this.font,
                CustomizeScreen.this.width / 2 - 112,
                TOP_ROW_Y,
                SEARCH_BOX_W,
                SEARCH_BOX_H,
                CustomizeScreen.this.searchBox,
                Component.empty()
            );
        }

        public Button createAddButton()
        {
            Component tooltip = Component.translatable(NostalgicLang.Gui.CUSTOMIZE_ADD_TOOLTIP).withStyle(ChatFormatting.GREEN);
            return new Button(
                CustomizeScreen.this.width / 2 + 116,
                TOP_ROW_Y - 1,
                CHECKBOX_W,
                CHECKBOX_H,
                Component.literal("+").withStyle(ChatFormatting.GREEN),
                (button) -> CustomizeScreen.this.onAddCustomizedSwing(),
                (button, stack, mouseX, mouseY) -> CustomizeScreen.this.renderTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        public Button createAutofillButton()
        {
            Component tooltip = Component.translatable(NostalgicLang.Gui.CUSTOMIZE_AUTOFILL_TOOLTIP).withStyle(ChatFormatting.YELLOW);
            return new Button(
                CustomizeScreen.this.width / 2 - 134,
                TOP_ROW_Y - 1,
                CHECKBOX_W,
                CHECKBOX_H,
                Component.literal("\u26a1").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD),
                (button) -> CustomizeScreen.this.onAutofill(),
                (button, stack, mouseX, mouseY) -> CustomizeScreen.this.renderTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        public Button createResetButton()
        {
            List<Component> tooltip = Lists.newArrayList(
                    Component.translatable(NostalgicLang.Gui.CUSTOMIZE_RESET_TOOLTIP_0).withStyle(ChatFormatting.RED),
                    Component.translatable(NostalgicLang.Gui.CUSTOMIZE_RESET_TOOLTIP_1).withStyle(ChatFormatting.WHITE)
            );

            return new Button(
                CustomizeScreen.this.autofillButton.x - 21,
                TOP_ROW_Y - 1,
                CHECKBOX_W,
                CHECKBOX_H,
                Component.empty(),
                (button) -> CustomizeScreen.this.resetCustomizedList(),
                (button, stack, mouseX, mouseY) -> CustomizeScreen.this.renderComponentTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        public Button createSaveButton()
        {
            return new Button(
                CustomizeScreen.this.getSavePosition(),
                CustomizeScreen.this.height - DONE_BUTTON_TOP_OFFSET,
                this.getSmallWidth(),
                BUTTON_HEIGHT,
                Component.translatable(NostalgicLang.Cloth.SAVE_AND_DONE),
                (button) -> CustomizeScreen.this.onClose(false)
            );
        }

        public Button createCancelButton()
        {
            return new Button(
                CustomizeScreen.this.width / 2 - getSmallWidth() - 3,
                CustomizeScreen.this.height - DONE_BUTTON_TOP_OFFSET,
                this.getSmallWidth(),
                BUTTON_HEIGHT,
                Component.translatable(NostalgicLang.Vanilla.GUI_CANCEL),
                (button) -> CustomizeScreen.this.onCancel()
            );
        }

        private int getSmallWidth() { return Math.min(200, (CustomizeScreen.this.width - 50 - 12) / 3); }
    }
}
