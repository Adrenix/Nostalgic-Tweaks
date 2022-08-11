package mod.adrenix.nostalgic.client.config.gui.screen.config;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.overlay.CategoryList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.button.OverlapButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.StateButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.StateType;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.chat.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
    public static final int INPUT_HEIGHT = 18;
    public static final int INPUT_WIDTH = 196;
    public static final int TOP_ROW = 24;

    /* Widget Instances */

    public final Set<Drawable> children = new HashSet<>();
    public boolean focusInput = false;
    private ButtonWidget[] categories;
    private TextFieldWidget input;
    private ButtonWidget list;
    private ButtonWidget general;
    private ButtonWidget sound;
    private ButtonWidget candy;
    private ButtonWidget gameplay;
    private ButtonWidget animation;
    private ButtonWidget swing;
    private ButtonWidget search;
    private ButtonWidget cancel;
    private ButtonWidget save;
    private ButtonWidget clear;
    private StateButton fuzzy;
    private StateButton bubble;
    private StateButton tag;
    private ConfigRowList configRowList;
    private TextGroup swingSpeedPrefix;
    private final ConfigScreen parent;

    /* Instance Getters */

    public ButtonWidget[] getCategories() { return this.categories; }
    public ButtonWidget getList() { return this.list; }
    public ButtonWidget getGeneral() { return this.general; }
    public ButtonWidget getSound() { return this.sound; }
    public ButtonWidget getCandy() { return this.candy; }
    public ButtonWidget getGameplay() { return this.gameplay; }
    public ButtonWidget getAnimation() { return this.animation; }
    public ButtonWidget getSwing() { return this.swing; }
    public ButtonWidget getSearch() { return this.search; }
    public ButtonWidget getSave() { return this.save; }
    public ButtonWidget getCancel() { return this.cancel; }
    public ButtonWidget getClear() { return this.clear; }
    public TextFieldWidget getSearchInput() { return this.input; }
    public TextGroup getSwingSpeedPrefix() { return this.swingSpeedPrefix; }
    public StateButton getFuzzy() { return this.fuzzy; }
    public StateButton getBubble() { return this.bubble; }
    public ConfigRowList getConfigRowList() { return this.configRowList; }

    /* Provider Constructor */

    public ConfigWidgets(ConfigScreen parent) { this.parent = parent; }

    /* Widget Construction */

    public void addWidgets()
    {
        this.configRowList = generateConfigRowList();
        this.swingSpeedPrefix = generateSwingSpeedPrefix();
        this.list = generateListButton();
        this.general = generateGeneralButton();
        this.sound = generateSoundButton();
        this.candy = generateCandyButton();
        this.gameplay = generateGameplayButton();
        this.animation = generateAnimationButton();
        this.swing = generateSwingButton();
        this.search = generateSearchButton();
        this.cancel = generateCancelButton();
        this.save = generateSaveButton();

        this.input = generateInputBox();
        this.input.setMaxLength(35);
        this.input.setDrawsBackground(true);
        this.input.setVisible(false);
        this.input.setEditableColor(0xFFFFFF);
        this.input.setText(this.input.getText());
        this.input.setChangedListener(this::checkSearch);

        this.clear = generateClearButton();
        this.tag = generateTagButton();
        this.fuzzy = generateFuzzyState();
        this.bubble = generateBubbleState();

        this.children.add(this.input);
        this.children.add(this.configRowList);

        int gap = 0;
        int width = 0;

        this.categories = new ButtonWidget[] { list, general, sound, candy, gameplay, animation, swing, search };
        ButtonWidget[] exits = new ButtonWidget[] { cancel, save };
        List<ButtonWidget> search = this.getSearchControls();

        this.children.addAll(List.of(this.categories));
        this.children.addAll(List.of(exits));
        this.children.addAll(search);

        for (Drawable widget : this.children) this.parent.addDrawableChild((Element & Drawable & Selectable) widget);
        for (ButtonWidget button : this.categories) width += button.getWidth() + gap;
        for (ButtonWidget button : search) button.visible = false;

        int prevX = (this.parent.width / 2) - ((width - gap) / 2);
        for (ButtonWidget button : this.categories)
        {
            button.x = prevX;
            prevX = button.x + button.getWidth() - 1;
        }

        boolean isOddChecked = general.x % 2 != 0;

        for (int i = 0; i < this.categories.length; i++)
        {
            boolean adjust = false;
            ButtonWidget first = NostalgicUtil.Array.get(this.categories, i);
            ButtonWidget second = NostalgicUtil.Array.get(this.categories, i + 1);
            if (first == null || second == null)
                break;

            if (isOddChecked ? (first.x % 2 != 0 && second.x % 2 != 0) : (first.x % 2 == 0 && second.x % 2 == 0))
            {
                adjust = true;
                isOddChecked = !isOddChecked;
            }

            if (adjust)
            {
                second.x -= 1;
                if (NostalgicUtil.Array.get(this.categories, i + 2) != null)
                {
                    for (int j = i + 2; j < this.categories.length; j++)
                        this.categories[j].x -= 1;
                }
            }
        }
    }

    /* Widget Providers */

    public List<ButtonWidget> getSearchControls()
    {
        return List.of(this.fuzzy, this.bubble, this.tag, this.clear);
    }

    private ConfigRowList generateConfigRowList()
    {
        return new ConfigRowList
        (
            this.parent,
            this.parent.width,
            this.parent.height,
            ROW_LIST_TOP,
            this.parent.height - ROW_LIST_BOTTOM_OFFSET,
            ROW_ITEM_HEIGHT
        );
    }

    private ButtonWidget generateListButton()
    {
        return new OverlapButton
        (
            this.parent,
            Text.empty(),
            (button) ->
            {
                this.parent.setConfigTab(ConfigScreen.ConfigTab.ALL);
                CategoryList.OVERLAY.open(this.configRowList);
            }
        ).setAsList();
    }

    private ButtonWidget generateGeneralButton()
    {
        return new OverlapButton
        (
            this.parent,
            Text.translatable(ConfigScreen.ConfigTab.GENERAL.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.GENERAL)
        );
    }

    private ButtonWidget generateSoundButton()
    {
        return new OverlapButton
        (
            this.parent,
            Text.translatable(ConfigScreen.ConfigTab.SOUND.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.SOUND)
        );
    }

    private ButtonWidget generateCandyButton()
    {
        return new OverlapButton
        (
            this.parent,
            Text.translatable(ConfigScreen.ConfigTab.CANDY.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.CANDY)
        );
    }

    private ButtonWidget generateGameplayButton()
    {
        return new OverlapButton
        (
            this.parent,
            Text.translatable(ConfigScreen.ConfigTab.GAMEPLAY.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.GAMEPLAY)
        );
    }

    private ButtonWidget generateAnimationButton()
    {
        return new OverlapButton
        (
            this.parent,
            Text.translatable(ConfigScreen.ConfigTab.ANIMATION.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.ANIMATION)
        );
    }

    private ButtonWidget generateSwingButton()
    {
        return new OverlapButton
        (
            this.parent,
            Text.translatable(ConfigScreen.ConfigTab.SWING.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.SWING)
        );
    }

    private ButtonWidget generateSearchButton()
    {
        Text translation = Text.translatable(ConfigScreen.ConfigTab.SEARCH.getLangKey());
        Text title = Text.literal("    " + translation.getString());

        return new OverlapButton
        (
            this.parent,
            title,
            (button) -> { this.parent.setConfigTab(ConfigScreen.ConfigTab.SEARCH); this.focusInput = true; }
        );
    }

    private ButtonWidget generateCancelButton()
    {
        return new ButtonWidget
        (
            this.parent.width / 2 - getSmallWidth() - 3,
            this.parent.height - BOTTOM_OFFSET,
            getSmallWidth(),
            BUTTON_HEIGHT,
            Text.translatable(NostalgicLang.Vanilla.GUI_CANCEL),
            (button) -> this.parent.onCancel()
        );
    }

    private ButtonWidget generateSaveButton()
    {
        return new ButtonWidget
        (
            this.parent.width / 2 + 3,
            this.parent.height - BOTTOM_OFFSET,
            getSmallWidth(),
            BUTTON_HEIGHT,
            Text.translatable(NostalgicLang.Cloth.SAVE_AND_DONE),
            (button) -> this.parent.onClose(false)
        );
    }

    private int getSmallWidth() { return Math.min(200, (this.parent.width - 50 - 12) / 3); }

    private TextFieldWidget generateInputBox()
    {
        int x = (this.parent.width / 2) - (INPUT_WIDTH / 2);
        return new TextFieldWidget(this.parent.getFont(), x, 3, INPUT_WIDTH, INPUT_HEIGHT, Text.translatable(NostalgicLang.Vanilla.SEARCH).withStyle(Formatting.ITALIC));
    }

    private TextGroup generateSwingSpeedPrefix()
    {
        return new TextGroup(this.configRowList, Text.translatable(NostalgicLang.Gui.SETTINGS_SPEED_HELP));
    }

    private StateButton generateBubbleState()
    {
        TextFieldWidget search = this.getSearchInput();
        return new StateButton(this.parent, StateType.BUBBLE, search.x - 61, search.y - 1, false, (button) -> {
            this.checkSearch(search.getText());
            this.setSearchFocus();
        });
    }

    private StateButton generateFuzzyState()
    {
        TextFieldWidget search = this.getSearchInput();
        return new StateButton(this.parent, StateType.FUZZY, search.x - 42, search.y - 1, (button) -> {
            this.checkSearch(search.getText());
            this.setSearchFocus();
        });
    }

    private StateButton generateTagButton()
    {
        TextFieldWidget search = this.getSearchInput();
        return new StateButton(this.parent, StateType.TAG, search.x - 23, search.y - 1, (button) -> {
            ConfigWidgets.setTagCycle(search);
            this.setSearchFocus();
        });
    }

    private StateButton generateClearButton()
    {
        TextFieldWidget search = this.getSearchInput();
        return new StateButton(this.parent, StateType.CLEAR, search.x + search.getWidth() + 3, search.y - 1, (button) -> {
            search.setText("");
            this.setSearchFocus();
        });
    }

    /* Private Widget Helpers */

    private static void setTagCycle(TextFieldWidget search)
    {
        StringBuilder query = new StringBuilder();
        String[] words = search.getText().split(" ");
        String atTag = NostalgicUtil.Array.get(words, 0);

        for (String word : words)
        {
            if (!word.contains("@"))
                query.append(" ").append(word);
        }

        if (atTag == null || !atTag.startsWith("@"))
            search.setText(("@client " + query).replaceAll("\s+", " "));
        else
        {
            ConfigScreen.SearchTag[] searchTags = ConfigScreen.SearchTag.values();
            ConfigScreen.SearchTag found = null;
            ConfigScreen.SearchTag next = null;

            for (int i = 0; i < searchTags.length; i++)
            {
                ConfigScreen.SearchTag tag = searchTags[i];

                if (atTag.replace("@", "").equals(tag.toString()))
                    found = tag;

                if (found != null)
                {
                    if (Screen.hasShiftDown())
                    {
                        // Go back one if shift is down
                        if (NostalgicUtil.Array.get(searchTags, i - 1) != null)
                            next = searchTags[i - 1];
                        else // We're at the front of the array, so get the last one
                            next = searchTags[searchTags.length - 1];
                    }
                    else
                    {
                        // Go forward one
                        if (NostalgicUtil.Array.get(searchTags, i + 1) != null)
                            next = searchTags[i + 1];
                        else // We're at the end of the array, so get the first one
                            next = searchTags[0];
                    }

                    break;
                }
            }

            if (next == null)
                next = ConfigScreen.SearchTag.CLIENT;

            search.setText(("@" + next + " " + query).replaceAll("\s+", " "));
        }
    }

    /* Public Widget Helpers */

    public void setSearchFocus()
    {
        this.getSearchInput().setVisible(true);
        this.getSearchInput().setTextFieldFocused(true);
        this.getSearchInput().setEditable(true);
    }

    public void checkSearch(String search)
    {
        this.getConfigRowList().children().clear();
        this.parent.search.clear();

        if (search.isBlank() || search.equals("@"))
            return;

        search = search.toLowerCase();

        HashMap<String, TweakClientCache<?>> entries = TweakClientCache.all();
        ConfigScreen.SearchTag tag = null;
        StringBuilder query = new StringBuilder();
        String[] words = search.split(" ");
        String atTag = NostalgicUtil.Array.get(words, 0);
        String requestedTag = atTag != null ? atTag.replace("@", "").toLowerCase() : "";

        for (String word : words)
        {
            if (!word.contains("@"))
                query.append(" ").append(word);
        }

        for (ConfigScreen.SearchTag searchTag : ConfigScreen.SearchTag.values())
        {
            if (requestedTag.equals(searchTag.toString()))
                tag = searchTag;
        }

        for (TweakClientCache<?> tweak : entries.values())
        {
            tweak.setWeight(0);

            if (GroupType.isManual(tweak.getGroup()))
                continue;

            boolean isTagged = false;
            if (tag != null)
            {
                switch (tag)
                {
                    case ALL -> isTagged = true;
                    case NEW -> isTagged = CommonReflect.getAnnotation(tweak, TweakClient.Gui.New.class) != null;
                    case CLIENT -> isTagged = CommonReflect.getAnnotation(tweak, TweakSide.Client.class) != null;
                    case SERVER -> isTagged = CommonReflect.getAnnotation(tweak, TweakSide.Server.class) != null;
                    case CONFLICT -> isTagged = tweak.getStatus() != StatusType.LOADED;
                    case RESET -> isTagged = tweak.isResettable();
                    case SAVE -> isTagged = tweak.isSavable();
                }
            }

            // Don't add the tweak to the results if we're narrowing the search
            if (isTagged && query.isEmpty())
                this.parent.search.put(tweak.getId(), tweak);

            // Don't add the tweak to the results if the tweak doesn't belong to the tag group
            if ((!isTagged && tag != null) || (!isTagged && search.contains("@")))
                continue;

            // Fuzzy search or exact search - can be expanded with a tooltip bubble search
            if (this.getFuzzy().getState())
            {
                int title = FuzzySearch.weightedRatio(tweak.getTranslation().toLowerCase(), query.toString().replaceAll("\s+", " "));
                int bubble = FuzzySearch.weightedRatio(tweak.getTooltipTranslation().toLowerCase(), query.toString().replaceAll("\s+", " "));

                tweak.setWeight(Math.max(title, this.getBubble().getState() ? bubble : 0));
                if (tweak.getWeight() > 0)
                    this.parent.search.put(tweak.getId(), tweak);
            }
            else
            {
                String find = query.toString().replaceAll("\s+", " ").trim();
                boolean isBubbleOn = this.getBubble().getState();
                boolean isInBubble = tweak.getTooltipTranslation().toLowerCase().contains(find);

                if (tweak.getTranslation().toLowerCase().contains(find) || (isBubbleOn && isInBubble))
                    this.parent.search.put(tweak.getId(), tweak);
            }
        }
    }
}
