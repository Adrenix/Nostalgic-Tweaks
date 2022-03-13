package mod.adrenix.nostalgic.client.config.gui.screen.config;

import mod.adrenix.nostalgic.client.config.annotation.NostalgicEntry;
import mod.adrenix.nostalgic.client.config.gui.widget.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.TextGroup;
import mod.adrenix.nostalgic.client.config.reflect.*;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigWidgets
{
    /* Widget Constants */

    public static final int BUTTON_HEIGHT = 20;
    public static final int BOTTOM_OFFSET = 26;
    public static final int WIDTH_PADDING = 8;
    public static final int ROW_LIST_TOP = 46;
    public static final int ROW_LIST_BOTTOM_OFFSET = 32;
    public static final int ROW_ITEM_HEIGHT = 25;
    public static final int INPUT_HEIGHT = 16;
    public static final int INPUT_WIDTH = 196;
    public static final int TOP_ROW = 23;

    /* Widget Instances */

    public final Set<Widget> children = new HashSet<>();
    public boolean focusInput = false;
    private Button[] categories;
    private EditBox input;
    private Button general;
    private Button sound;
    private Button candy;
    private Button animation;
    private Button swing;
    private Button search;
    private Button cancel;
    private Button save;
    private ConfigRowList configRowList;
    private TextGroup swingSpeedPrefix;
    private final ConfigScreen parent;

    /* Instance Getters */

    public Button[] getCategories() { return this.categories; }
    public Button getGeneral() { return this.general; }
    public Button getSound() { return this.sound; }
    public Button getCandy() { return this.candy; }
    public Button getAnimation() { return this.animation; }
    public Button getSwing() { return this.swing; }
    public Button getSearch() { return this.search; }
    public Button getSave() { return this.save; }
    public Button getCancel() { return this.cancel; }
    public EditBox getInput() { return this.input; }
    public TextGroup getSwingSpeedPrefix() { return this.swingSpeedPrefix; }
    public ConfigRowList getConfigRowList() { return this.configRowList; }

    /* Provider Constructor */

    public ConfigWidgets(ConfigScreen parent) { this.parent = parent; }

    /* Widget Construction */

    public void addWidgets()
    {
        this.configRowList = generateConfigRowList();
        this.swingSpeedPrefix = generateSwingSpeedPrefix();
        this.general = generateGeneralButton();
        this.sound = generateSoundButton();
        this.candy = generateCandyButton();
        this.animation = generateAnimationButton();
        this.swing = generateSwingButton();
        this.search = generateSearchButton();
        this.cancel = generateCancelButton();
        this.save = generateSaveButton();

        this.input = generateInputBox();
        this.input.setMaxLength(35);
        this.input.setBordered(true);
        this.input.setVisible(false);
        this.input.setTextColor(0xFFFFFF);
        this.input.setValue(this.input.getValue());
        this.input.setResponder(this::checkSearch);

        this.children.add(this.input);
        this.children.add(this.configRowList);

        int gap = 2;
        int width = 0;

        this.categories = new Button[] { general, sound, candy, animation, swing, search };
        Button[] exits = new Button[]{ cancel, save };

        this.children.addAll(List.of(this.categories));
        this.children.addAll(List.of(exits));

        for (Widget widget : this.children) this.parent.addRenderableWidget((GuiEventListener & Widget & NarratableEntry) widget);
        for (Button button : this.categories) width += button.getWidth() + gap;

        int prevX = (this.parent.width / 2) - ((width - gap) / 2);
        for (Button button : this.categories)
        {
            button.x = prevX;
            prevX = button.x + button.getWidth() + gap;
        }
    }

    /* Widget Providers */

    private ConfigRowList generateConfigRowList()
    {
        return new ConfigRowList(
            this.parent,
            this.parent.width,
            this.parent.height,
            ROW_LIST_TOP,
            this.parent.height - ROW_LIST_BOTTOM_OFFSET,
            ROW_ITEM_HEIGHT
        );
    }

    private Button generateGeneralButton()
    {
        TranslatableComponent title = new TranslatableComponent(ConfigScreen.ConfigTab.GENERAL.getLangKey());
        return new Button(
            0,
            TOP_ROW,
            this.parent.getFont().width(title) + WIDTH_PADDING,
            BUTTON_HEIGHT,
            title,
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.GENERAL)
        );
    }

    private Button generateSoundButton()
    {
        TranslatableComponent title = new TranslatableComponent(ConfigScreen.ConfigTab.SOUND.getLangKey());
        return new Button(
            0,
            TOP_ROW,
            this.parent.getFont().width(title) + WIDTH_PADDING,
            BUTTON_HEIGHT,
            title,
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.SOUND)
        );
    }

    private Button generateCandyButton()
    {
        TranslatableComponent title = new TranslatableComponent(ConfigScreen.ConfigTab.CANDY.getLangKey());
        return new Button(
            0,
            TOP_ROW,
            this.parent.getFont().width(title) + WIDTH_PADDING,
            BUTTON_HEIGHT,
            title,
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.CANDY)
        );
    }

    private Button generateAnimationButton()
    {
        TranslatableComponent title = new TranslatableComponent(ConfigScreen.ConfigTab.ANIMATION.getLangKey());
        return new Button(
            0,
            TOP_ROW,
            this.parent.getFont().width(title) + WIDTH_PADDING,
            BUTTON_HEIGHT,
            title,
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.ANIMATION)
        );
    }

    private Button generateSwingButton()
    {
        TranslatableComponent title = new TranslatableComponent(ConfigScreen.ConfigTab.SWING.getLangKey());
        return new Button(
            0,
            TOP_ROW,
            this.parent.getFont().width(title) + WIDTH_PADDING,
            BUTTON_HEIGHT,
            title,
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.SWING)
        );
    }

    private Button generateSearchButton()
    {
        TranslatableComponent translation = new TranslatableComponent(ConfigScreen.ConfigTab.SEARCH.getLangKey());
        Component title = new TextComponent("    " + translation.getString());

        return new Button(
            0,
            TOP_ROW,
            this.parent.getFont().width(title) + WIDTH_PADDING,
            BUTTON_HEIGHT,
            title,
            (button) -> { this.parent.setConfigTab(ConfigScreen.ConfigTab.SEARCH); this.focusInput = true; }
        );
    }

    private Button generateCancelButton()
    {
        return new Button(
            this.parent.width / 2 - getSmallWidth() - 3,
            this.parent.height - BOTTOM_OFFSET,
            getSmallWidth(),
            BUTTON_HEIGHT,
            new TranslatableComponent(NostalgicLang.Vanilla.GUI_CANCEL),
            (button) -> this.parent.onCancel()
        );
    }

    private Button generateSaveButton()
    {
        return new Button(
            this.parent.width / 2 + 3,
            this.parent.height - BOTTOM_OFFSET,
            getSmallWidth(),
            BUTTON_HEIGHT,
            new TranslatableComponent(NostalgicLang.Cloth.SAVE_AND_DONE),
            (button) -> this.parent.onClose(false)
        );
    }

    private int getSmallWidth() { return Math.min(200, (this.parent.width - 50 - 12) / 3); }

    private EditBox generateInputBox()
    {
        int x = (this.parent.width / 2) - (INPUT_WIDTH / 2);
        return new EditBox(this.parent.getFont(), x, 4, INPUT_WIDTH, INPUT_HEIGHT, new TextComponent(NostalgicLang.Vanilla.SEARCH).withStyle(ChatFormatting.ITALIC));
    }

    private TextGroup generateSwingSpeedPrefix()
    {
        return new TextGroup(this.configRowList, new TranslatableComponent(NostalgicLang.Gui.SETTINGS_SPEED_HELP));
    }

    /* Widget Helpers */

    public void checkSearch(String search)
    {
        this.parent.search.clear();

        if (search.isBlank())
        {
            this.parent.getWidgets().getConfigRowList().children().clear();
            return;
        }

        search = search.toLowerCase();
        ConfigScreen.SearchTag tag = null;
        String input = this.parent.getWidgets().getInput().getValue().replaceAll("@", "").toLowerCase();
        HashMap<String, EntryCache<?>> entries = EntryCache.all();

        for (ConfigScreen.SearchTag searchTag : ConfigScreen.SearchTag.values())
        {
            if (input.equals(searchTag.toString()))
                tag = searchTag;
        }

        for (EntryCache<?> entry : entries.values())
        {
            if (GroupType.isManual(entry.getGroup()))
                continue;

            if (tag != null)
            {
                switch (tag)
                {
                    case NEW -> {
                        if (ConfigReflect.getAnnotation(entry.getGroup(), entry.getEntryKey(), NostalgicEntry.Gui.New.class) != null)
                            this.parent.search.add(entry);
                    }
                    case CONFLICT -> {
                        if (entry.getStatus() != StatusType.OKAY)
                            this.parent.search.add(entry);
                    }
                    case RESET -> {
                        if (entry.isResettable())
                            this.parent.search.add(entry);
                    }
                    case CLIENT -> {
                        if (ConfigReflect.getAnnotation(entry.getGroup(), entry.getEntryKey(), NostalgicEntry.Gui.Client.class) != null)
                            this.parent.search.add(entry);
                    }
                    case SERVER -> {
                        if (ConfigReflect.getAnnotation(entry.getGroup(), entry.getEntryKey(), NostalgicEntry.Gui.Server.class) != null)
                            this.parent.search.add(entry);
                    }
                }
            }
            else
            {
                if (new TranslatableComponent(entry.getLangKey()).getString().toLowerCase().contains(search))
                    this.parent.search.add(entry);
            }
        }
    }
}
