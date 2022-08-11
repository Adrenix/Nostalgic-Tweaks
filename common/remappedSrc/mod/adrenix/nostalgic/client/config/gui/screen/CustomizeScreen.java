package mod.adrenix.nostalgic.client.config.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.client.config.CustomSwings;
import mod.adrenix.nostalgic.client.config.gui.ItemSuggestionHelper;
import mod.adrenix.nostalgic.client.config.gui.widget.list.CustomizedRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.ToggleCheckbox;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
    private static final ClientConfig config = ClientConfigCache.getRoot();
    private final CheckboxWidget toolsCheckbox;
    private final CheckboxWidget blocksCheckbox;
    private final CheckboxWidget itemsCheckbox;
    private final CheckboxWidget resetCheckbox;
    private ButtonWidget resetButton;
    private ButtonWidget addItemButton;
    private ButtonWidget autofillButton;
    private TextFieldWidget searchBox;
    private ItemSuggestionHelper itemSuggestions;
    private CustomizedRowList customizedRowList;
    private final Screen parent;
    private final Map<String, Integer> undo;
    private final List<Drawable> renderables = Lists.newArrayList();


    // Constructor
    public CustomizeScreen(Screen parent)
    {
        super(parent, Text.translatable(NostalgicLang.Gui.CUSTOMIZE), false);

        this.parent = parent;
        this.undo = Maps.newHashMap(config.custom);

        int x = 2, y = TOP_ROW_Y, w = CHECKBOX_W, h = CHECKBOX_H;
        this.toolsCheckbox = new ToggleCheckbox(this, x, y + 27, w, h, Text.translatable(NostalgicLang.Gui.CUSTOMIZE_TOOL), true);
        this.blocksCheckbox = new ToggleCheckbox(this, x, y + 52, w, h, Text.translatable(NostalgicLang.Gui.CUSTOMIZE_BLOCK), true);
        this.itemsCheckbox = new ToggleCheckbox(this, x, y + 77, w, h, Text.translatable(NostalgicLang.Gui.CUSTOMIZE_ITEM), true);
        this.resetCheckbox = new ToggleCheckbox(this, x, y - 1, w, h, Text.translatable(NostalgicLang.Gui.CUSTOMIZE_RESET), false);
    }

    /* Getters */

    public ButtonWidget getAddItemButton() { return addItemButton; }
    public MinecraftClient getMinecraft() { return client; }
    public ItemRenderer getItemRenderer() { return this.itemRenderer; }

    /* Helpers */

    public boolean suggestionsAreClosed()
    {
        if (this.itemSuggestions == null)
            return true;
        return !this.itemSuggestions.isSuggesting();
    }

    public void setSuggestionFocus(boolean state) { this.searchBox.setTextFieldFocused(state); }

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
        Text message = Text.translatable(NostalgicLang.Gui.CUSTOMIZE_ADD).withStyle(Formatting.WHITE);
        Text display = Text.translatable(item.getName(item.getDefaultStack()).getString()).withStyle(Formatting.GREEN);
        this.client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, message, display));
    }

    private void clearSearchBox() { this.itemSuggestions.resetInputBox(); }

    private void refresh()
    {
        this.clearSearchBox();
        this.client.setScreen(this);
        this.searchBox.setTextFieldFocused(false);
        this.resetButton.active = this.resetCheckbox.isChecked();
    }

    private void resetCustomizedList()
    {
        config.custom.clear();
        CustomizedRowList.added = null;

        this.resetCheckbox.onPress();
        this.clearSearchBox();
        this.client.setScreen(this);
    }

    private int getSavePosition() { return this.width / 2 + 3; }

    private void renderWidget(Drawable widget, MatrixStack stack, int mouseX, int mouseY, float ticks)
    {
        if (widget instanceof ButtonWidget && ((ButtonWidget) widget).x == this.getSavePosition())
            ((ButtonWidget) widget).active = this.isSavable();
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
        boolean addTools = this.toolsCheckbox.isChecked();
        boolean addBlocks = this.blocksCheckbox.isChecked();
        boolean addItems = this.itemsCheckbox.isChecked();
        List<Map.Entry<String, Integer>> sorted = CustomSwings.getSortedItems(addTools, addBlocks, addItems);
        for (Map.Entry<String, Integer> entry : sorted)
            this.customizedRowList.addItem(entry);
    }

    /* Overrides */

    @Override
    public void tick()
    {
        this.searchBox.tick();

        if (this.resetButton.active != this.resetCheckbox.isChecked())
            this.resetButton.active = this.resetCheckbox.isChecked();
    }

    @Override
    public void resize(MinecraftClient minecraft, int x, int y)
    {
        String searching = this.searchBox.getText();
        this.init(minecraft, x, y);
        this.searchBox.setText(searching);
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
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T widget)
    {
        this.renderables.add(widget);
        return this.addSelectableChild(widget);
    }

    @Override
    protected void remove(Element widget)
    {
        if (widget instanceof Drawable)
            this.renderables.remove(widget);
        super.remove(widget);
    }

    @Override
    protected void clearChildren()
    {
        this.renderables.clear();
        super.clearChildren();
    }

    @Override
    protected void init()
    {
        this.addDrawableChild(this.toolsCheckbox);
        this.addDrawableChild(this.blocksCheckbox);
        this.addDrawableChild(this.itemsCheckbox);
        this.addDrawableChild(this.resetCheckbox);

        WidgetProvider widget = new WidgetProvider();

        this.customizedRowList = widget.createCustomizedRowList();
        this.searchBox = widget.createSearchBox();
        this.searchBox.setChangedListener(this::onEdited);

        this.sortCustomizedRowList();

        this.addItemButton = widget.createAddButton();
        this.autofillButton = widget.createAutofillButton();
        this.resetButton = widget.createResetButton();
        this.resetButton.active = false;
        this.autofillButton.active = false;
        this.addItemButton.active = false;

        this.addSelectableChild(this.customizedRowList);
        this.addDrawableChild(this.searchBox);
        this.addDrawableChild(this.addItemButton);
        this.addDrawableChild(this.autofillButton);
        this.addDrawableChild(this.resetButton);
        this.addDrawableChild(widget.createCancelButton());
        this.addDrawableChild(widget.createSaveButton());

        this.setInitialFocus(this.searchBox);

        this.itemSuggestions = new ItemSuggestionHelper(this, this.searchBox, this.textRenderer, 7, -16777216);
        this.itemSuggestions.setAllowSuggestions(true);
        this.itemSuggestions.updateItemSuggestions();
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float ticks)
    {
        this.autofillButton.active = this.client.world != null;
        if (this.client.world != null)
            this.fillGradient(poseStack, 0, 0, this.width, this.height, 839913488, 16777216);
        else
            this.renderBackgroundTexture(0);
        this.customizedRowList.render(poseStack, mouseX, mouseY, ticks);
        this.renderScreenTitle(poseStack, 7);
        this.searchBox.render(poseStack, mouseX, mouseY, ticks);
        this.renderables.forEach(widget -> this.renderWidget(widget, poseStack, mouseX, mouseY, ticks));
        this.itemSuggestions.render(poseStack, mouseX, mouseY);
        this.itemRenderer.renderGuiItemIcon(new ItemStack(Items.TNT), this.resetButton.x + 2, this.resetButton.y + 2);
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
                CustomizeScreen.this.client.setScreen(CustomizeScreen.this.parent);
            }
            else
                CustomizeScreen.this.client.setScreen(CustomizeScreen.this);
        }
    }

    /* On-click Handlers */

    private void onEdited(String ignored) { this.itemSuggestions.updateItemSuggestions(); }

    private void onAddCustomizedSwing() { this.addCustomizedSwing(this.itemSuggestions.getItem()); }

    private void onAutofill()
    {
        if (this.client.player != null)
            this.addCustomizedSwing(this.client.player.getMainHandStack().getItem());
    }

    private void onCancel()
    {
        if (!this.isSavable())
        {
            this.onClose(true);
            return;
        }

        this.client.setScreen(
            new ConfirmScreen(
                new CancelConsumer(),
                    Text.translatable(NostalgicLang.Cloth.QUIT_CONFIG),
                    Text.translatable(NostalgicLang.Cloth.QUIT_CONFIG_SURE),
                    Text.translatable(NostalgicLang.Cloth.QUIT_DISCARD),
                    Text.translatable(NostalgicLang.Vanilla.GUI_CANCEL)
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

        super.close();
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

        public TextFieldWidget createSearchBox()
        {
            return new TextFieldWidget(
                CustomizeScreen.this.textRenderer,
                CustomizeScreen.this.width / 2 - 112,
                TOP_ROW_Y,
                SEARCH_BOX_W,
                SEARCH_BOX_H,
                CustomizeScreen.this.searchBox,
                Text.empty()
            );
        }

        public ButtonWidget createAddButton()
        {
            Text tooltip = Text.translatable(NostalgicLang.Gui.CUSTOMIZE_ADD_TOOLTIP).withStyle(Formatting.GREEN);
            return new ButtonWidget(
                CustomizeScreen.this.width / 2 + 116,
                TOP_ROW_Y - 1,
                CHECKBOX_W,
                CHECKBOX_H,
                Text.literal("+").withStyle(Formatting.GREEN),
                (button) -> CustomizeScreen.this.onAddCustomizedSwing(),
                (button, stack, mouseX, mouseY) -> CustomizeScreen.this.renderTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        public ButtonWidget createAutofillButton()
        {
            Text tooltip = Text.translatable(NostalgicLang.Gui.CUSTOMIZE_AUTOFILL_TOOLTIP).withStyle(Formatting.YELLOW);
            return new ButtonWidget(
                CustomizeScreen.this.width / 2 - 134,
                TOP_ROW_Y - 1,
                CHECKBOX_W,
                CHECKBOX_H,
                Text.literal("\u26a1").withStyle(Formatting.YELLOW).withStyle(Formatting.BOLD),
                (button) -> CustomizeScreen.this.onAutofill(),
                (button, stack, mouseX, mouseY) -> CustomizeScreen.this.renderTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        public ButtonWidget createResetButton()
        {
            List<Text> tooltip = Lists.newArrayList(
                    Text.translatable(NostalgicLang.Gui.CUSTOMIZE_RESET_TOOLTIP_0).withStyle(Formatting.RED),
                    Text.translatable(NostalgicLang.Gui.CUSTOMIZE_RESET_TOOLTIP_1).withStyle(Formatting.WHITE)
            );

            return new ButtonWidget(
                CustomizeScreen.this.autofillButton.x - 21,
                TOP_ROW_Y - 1,
                CHECKBOX_W,
                CHECKBOX_H,
                Text.empty(),
                (button) -> CustomizeScreen.this.resetCustomizedList(),
                (button, stack, mouseX, mouseY) -> CustomizeScreen.this.renderComponentTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        public ButtonWidget createSaveButton()
        {
            return new ButtonWidget(
                CustomizeScreen.this.getSavePosition(),
                CustomizeScreen.this.height - DONE_BUTTON_TOP_OFFSET,
                this.getSmallWidth(),
                BUTTON_HEIGHT,
                Text.translatable(NostalgicLang.Cloth.SAVE_AND_DONE),
                (button) -> CustomizeScreen.this.onClose(false)
            );
        }

        public ButtonWidget createCancelButton()
        {
            return new ButtonWidget(
                CustomizeScreen.this.width / 2 - getSmallWidth() - 3,
                CustomizeScreen.this.height - DONE_BUTTON_TOP_OFFSET,
                this.getSmallWidth(),
                BUTTON_HEIGHT,
                Text.translatable(NostalgicLang.Vanilla.GUI_CANCEL),
                (button) -> CustomizeScreen.this.onCancel()
            );
        }

        private int getSmallWidth() { return Math.min(200, (CustomizeScreen.this.width - 50 - 12) / 3); }
    }
}
