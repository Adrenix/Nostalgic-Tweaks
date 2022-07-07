package mod.adrenix.nostalgic.client.config.gui.screen.config;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.widget.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.TextGroup;
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
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
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
    public static final int INPUT_HEIGHT = 18;
    public static final int INPUT_WIDTH = 196;
    public static final int TOP_ROW = 24;

    /* Widget Instances */

    public final Set<Widget> children = new HashSet<>();
    public boolean focusInput = false;
    private Button[] categories;
    private EditBox input;
    private Button general;
    private Button sound;
    private Button candy;
    private Button gameplay;
    private Button animation;
    private Button swing;
    private Button search;
    private Button cancel;
    private Button save;
    private Button clear;
    private StateButton fuzzy;
    private StateButton bubble;
    private StateButton tag;
    private ConfigRowList configRowList;
    private TextGroup swingSpeedPrefix;
    private final ConfigScreen parent;

    /* Instance Getters */

    public Button[] getCategories() { return this.categories; }
    public Button getGeneral() { return this.general; }
    public Button getSound() { return this.sound; }
    public Button getCandy() { return this.candy; }
    public Button getGameplay() { return this.gameplay; }
    public Button getAnimation() { return this.animation; }
    public Button getSwing() { return this.swing; }
    public Button getSearch() { return this.search; }
    public Button getSave() { return this.save; }
    public Button getCancel() { return this.cancel; }
    public Button getClear() { return this.clear; }
    public EditBox getSearchInput() { return this.input; }
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
        this.input.setBordered(true);
        this.input.setVisible(false);
        this.input.setTextColor(0xFFFFFF);
        this.input.setValue(this.input.getValue());
        this.input.setResponder(this::checkSearch);

        this.clear = generateClearButton();
        this.tag = generateTagButton();
        this.fuzzy = generateFuzzyState();
        this.bubble = generateBubbleState();

        this.children.add(this.input);
        this.children.add(this.configRowList);

        int gap = 0;
        int width = 0;

        this.categories = new Button[] { general, sound, candy, gameplay, animation, swing, search };
        Button[] exits = new Button[] { cancel, save };
        List<Button> search = this.getSearchControls();

        this.children.addAll(List.of(this.categories));
        this.children.addAll(List.of(exits));
        this.children.addAll(search);

        for (Widget widget : this.children) this.parent.addRenderableWidget((GuiEventListener & Widget & NarratableEntry) widget);
        for (Button button : this.categories) width += button.getWidth() + gap;
        for (Button button : search) button.visible = false;

        int prevX = (this.parent.width / 2) - ((width - gap) / 2);
        for (Button button : this.categories)
        {
            button.x = prevX;
            prevX = button.x + button.getWidth() - 1;
        }

        boolean isOddChecked = general.x % 2 != 0;

        for (int i = 0; i < this.categories.length; i++)
        {
            boolean adjust = false;
            Button first = NostalgicUtil.Array.get(this.categories, i);
            Button second = NostalgicUtil.Array.get(this.categories, i + 1);
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

    public List<Button> getSearchControls()
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

    private Button generateGeneralButton()
    {
        return new OverlapButton
        (
            this.parent,
            Component.translatable(ConfigScreen.ConfigTab.GENERAL.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.GENERAL)
        );
    }

    private Button generateSoundButton()
    {
        return new OverlapButton
        (
            this.parent,
            Component.translatable(ConfigScreen.ConfigTab.SOUND.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.SOUND)
        );
    }

    private Button generateCandyButton()
    {
        return new OverlapButton
        (
            this.parent,
            Component.translatable(ConfigScreen.ConfigTab.CANDY.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.CANDY)
        );
    }

    private Button generateGameplayButton()
    {
        return new OverlapButton
        (
            this.parent,
            Component.translatable(ConfigScreen.ConfigTab.GAMEPLAY.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.GAMEPLAY)
        );
    }

    private Button generateAnimationButton()
    {
        return new OverlapButton
        (
            this.parent,
            Component.translatable(ConfigScreen.ConfigTab.ANIMATION.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.ANIMATION)
        );
    }

    private Button generateSwingButton()
    {
        return new OverlapButton
        (
            this.parent,
            Component.translatable(ConfigScreen.ConfigTab.SWING.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.SWING)
        );
    }

    private Button generateSearchButton()
    {
        Component translation = Component.translatable(ConfigScreen.ConfigTab.SEARCH.getLangKey());
        Component title = Component.literal("    " + translation.getString());

        return new OverlapButton
        (
            this.parent,
            title,
            (button) -> { this.parent.setConfigTab(ConfigScreen.ConfigTab.SEARCH); this.focusInput = true; }
        );
    }

    private Button generateCancelButton()
    {
        return new Button
        (
            this.parent.width / 2 - getSmallWidth() - 3,
            this.parent.height - BOTTOM_OFFSET,
            getSmallWidth(),
            BUTTON_HEIGHT,
            Component.translatable(NostalgicLang.Vanilla.GUI_CANCEL),
            (button) -> this.parent.onCancel()
        );
    }

    private Button generateSaveButton()
    {
        return new Button
        (
            this.parent.width / 2 + 3,
            this.parent.height - BOTTOM_OFFSET,
            getSmallWidth(),
            BUTTON_HEIGHT,
            Component.translatable(NostalgicLang.Cloth.SAVE_AND_DONE),
            (button) -> this.parent.onClose(false)
        );
    }

    private int getSmallWidth() { return Math.min(200, (this.parent.width - 50 - 12) / 3); }

    private EditBox generateInputBox()
    {
        int x = (this.parent.width / 2) - (INPUT_WIDTH / 2);
        return new EditBox(this.parent.getFont(), x, 3, INPUT_WIDTH, INPUT_HEIGHT, Component.translatable(NostalgicLang.Vanilla.SEARCH).withStyle(ChatFormatting.ITALIC));
    }

    private TextGroup generateSwingSpeedPrefix()
    {
        return new TextGroup(this.configRowList, Component.translatable(NostalgicLang.Gui.SETTINGS_SPEED_HELP));
    }

    private StateButton generateBubbleState()
    {
        EditBox search = this.getSearchInput();
        return new StateButton(this.parent, StateType.BUBBLE, search.x - 61, search.y - 1, false, (button) -> {
            this.checkSearch(search.getValue());
            this.setSearchFocus();
        });
    }

    private StateButton generateFuzzyState()
    {
        EditBox search = this.getSearchInput();
        return new StateButton(this.parent, StateType.FUZZY, search.x - 42, search.y - 1, (button) -> {
            this.checkSearch(search.getValue());
            this.setSearchFocus();
        });
    }

    private StateButton generateTagButton()
    {
        EditBox search = this.getSearchInput();
        return new StateButton(this.parent, StateType.TAG, search.x - 23, search.y - 1, (button) -> {
            ConfigWidgets.setTagCycle(search);
            this.setSearchFocus();
        });
    }

    private StateButton generateClearButton()
    {
        EditBox search = this.getSearchInput();
        return new StateButton(this.parent, StateType.CLEAR, search.x + search.getWidth() + 3, search.y - 1, (button) -> {
            search.setValue("");
            this.setSearchFocus();
        });
    }

    /* Private Widget Helpers */

    private static void setTagCycle(EditBox search)
    {
        StringBuilder query = new StringBuilder();
        String[] words = search.getValue().split(" ");
        String atTag = NostalgicUtil.Array.get(words, 0);

        for (String word : words)
        {
            if (!word.contains("@"))
                query.append(" ").append(word);
        }

        if (atTag == null || !atTag.startsWith("@"))
            search.setValue(("@client " + query).replaceAll("\s+", " "));
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

            search.setValue(("@" + next + " " + query).replaceAll("\s+", " "));
        }
    }

    /* Public Widget Helpers */

    public void setSearchFocus()
    {
        this.getSearchInput().setVisible(true);
        this.getSearchInput().setFocus(true);
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
                    case NEW ->
                    {
                        if (CommonReflect.getAnnotation(tweak, TweakClient.Gui.New.class) != null)
                            isTagged = true;
                    }
                    case CONFLICT ->
                    {
                        if (tweak.getStatus() != StatusType.LOADED)
                            isTagged = true;
                    }
                    case RESET ->
                    {
                        if (tweak.isResettable())
                            isTagged = true;
                    }
                    case CLIENT ->
                    {
                        if (CommonReflect.getAnnotation(tweak, TweakSide.Client.class) != null)
                            isTagged = true;
                    }
                    case SERVER ->
                    {
                        if (CommonReflect.getAnnotation(tweak, TweakSide.Server.class) != null)
                            isTagged = true;
                    }
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
