package mod.adrenix.nostalgic.client.config.gui.screen.config;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import mod.adrenix.nostalgic.client.config.gui.overlay.CategoryListOverlay;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.button.OverlapButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.StateButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.StateWidget;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.common.ArrayUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;

import java.util.*;

/**
 * This class builds and provides logic for all config screen widgets.
 * Widget event handling is done by the parent screen.
 */

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
    private Button list;
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
    public Button getList() { return this.list; }
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

    /**
     * Create a new config screen widget provider instance.
     * @param parent The parent config screen instance.
     */
    public ConfigWidgets(ConfigScreen parent) { this.parent = parent; }

    /* Widget Construction */

    /**
     * Generate the widgets for this instance.
     * This should be done during screen initialization.
     */
    public void generate()
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
        this.input.setBordered(true);
        this.input.setVisible(false);
        this.input.setTextColor(0xFFFFFF);
        this.input.setValue(this.input.getValue());
        this.input.setResponder(this::runSearch);

        this.clear = generateClearButton();
        this.tag = generateTagButton();
        this.fuzzy = generateFuzzyState();
        this.bubble = generateBubbleState();

        this.children.add(this.input);
        this.children.add(this.configRowList);

        int gap = 0;
        int width = 0;

        // Config screen configuration tabs
        this.categories = new Button[] { list, general, sound, candy, gameplay, animation, swing, search };

        // Config screen exit controls
        Button[] exits = new Button[] { cancel, save };

        // Config screen search tab controls
        List<Button> search = this.getSearchControls();

        this.children.addAll(List.of(this.categories));
        this.children.addAll(List.of(exits));
        this.children.addAll(search);

        // Adds all generated widgets to the parent screen for rendering
        for (Widget widget : this.children)
            this.parent.addRenderableWidget((GuiEventListener & Widget & NarratableEntry) widget);

        // Get the full width from all the config tab buttons
        for (Button button : this.categories)
            width += button.getWidth() + gap;

        // Hides search button controls since that tab is not visible during generation
        for (Button button : search)
            button.visible = false;

        // The left side starting x-position from the center of the tab bar
        int prevX = (this.parent.width / 2) - ((width - gap) / 2);

        // Align configuration tab buttons so that they are side-by-side
        for (Button button : this.categories)
        {
            button.x = prevX;
            prevX = button.x + button.getWidth() - 1;
        }

        /*
           Some buttons in the config tab bar will experience gap issues due to centering inconsistencies with even/odd
           spacing. The following logic fixes this issue using the first two buttons as a guide based on their starting
           x-positions. Each button after the first two will follow the same pattern.
         */

        boolean isOddChecked = general.x % 2 != 0;

        for (int i = 0; i < this.categories.length; i++)
        {
            Button first = ArrayUtil.get(this.categories, i);
            Button second = ArrayUtil.get(this.categories, i + 1);
            boolean adjust = false;

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

                if (ArrayUtil.get(this.categories, i + 2) != null)
                {
                    for (int j = i + 2; j < this.categories.length; j++)
                        this.categories[j].x -= 1;
                }
            }
        }
    }

    /* Widget Providers */

    /**
     * The search config tab has unique controls that changes the results provided during from search queries.
     * @return A list of control buttons.
     */
    public List<Button> getSearchControls() { return List.of(this.fuzzy, this.bubble, this.tag, this.clear); }

    /**
     * The config row list instance provides rows of tweak controls. These rows can be automatically generated or
     * manually created.
     * @return A config row list instance.
     */
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

    /**
     * The list button jumps to a screen that lists every tweak in the mod and its respective control.
     * @return A list tab button.
     */
    private Button generateListButton()
    {
        Button.OnPress action = (button) ->
        {
            this.parent.setConfigTab(ConfigScreen.ConfigTab.ALL);
            new CategoryListOverlay();
        };

        return new OverlapButton(Component.empty(), action).setAsList();
    }

    /**
     * The general tab contains various config menu controls and general information about config controls and tweaks.
     * @return A general tab button.
     */
    private Button generateGeneralButton()
    {
        return new OverlapButton
        (
            Component.translatable(ConfigScreen.ConfigTab.GENERAL.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.GENERAL)
        );
    }

    /**
     * The sound tab contains tweaks that modify game sound.
     * @return A sound tab button.
     */
    private Button generateSoundButton()
    {
        return new OverlapButton
        (
            Component.translatable(ConfigScreen.ConfigTab.SOUND.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.SOUND)
        );
    }

    /**
     * The eye candy tab contains tweaks that modify visual aspects of the game.
     * @return An eye candy tab button.
     */
    private Button generateCandyButton()
    {
        return new OverlapButton
        (
            Component.translatable(ConfigScreen.ConfigTab.CANDY.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.CANDY)
        );
    }

    /**
     * The gameplay tab contains tweaks that modify gameplay elements of the game.
     * @return A gameplay tab button.
     */
    private Button generateGameplayButton()
    {
        return new OverlapButton
        (
            Component.translatable(ConfigScreen.ConfigTab.GAMEPLAY.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.GAMEPLAY)
        );
    }

    /**
     * The animation tab contains tweaks that modify animations of the game.
     * @return An animation tab button.
     */
    private Button generateAnimationButton()
    {
        return new OverlapButton
        (
            Component.translatable(ConfigScreen.ConfigTab.ANIMATION.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.ANIMATION)
        );
    }

    /**
     * The swing tab contains that modify first-person swing animations.
     * This tab also contains a special screen that controls individual item swings.
     * @return A swing tab button.
     */
    private Button generateSwingButton()
    {
        return new OverlapButton
        (
            Component.translatable(ConfigScreen.ConfigTab.SWING.getLangKey()),
            (button) -> this.parent.setConfigTab(ConfigScreen.ConfigTab.SWING)
        );
    }

    /**
     * The search tab allows the user to quickly search for tweaks.
     * @return A search tab button.
     */
    private Button generateSearchButton()
    {
        Component translation = Component.translatable(ConfigScreen.ConfigTab.SEARCH.getLangKey());
        Component title = Component.literal("    " + translation.getString());

        Button.OnPress action = (button) ->
        {
            this.parent.setConfigTab(ConfigScreen.ConfigTab.SEARCH);
            this.focusInput = true;
        };

        return new OverlapButton(title, action);
    }

    /**
     * The cancel button will exit the config screen without saving changes.
     * @return A cancel button.
     */
    private Button generateCancelButton()
    {
        return new Button
        (
            this.parent.width / 2 - getSmallWidth() - 3,
            this.parent.height - BOTTOM_OFFSET,
            getSmallWidth(),
            BUTTON_HEIGHT,
            Component.translatable(LangUtil.Vanilla.GUI_CANCEL),
            (button) -> this.parent.onCancel()
        );
    }

    /**
     * The save button will exit the config screen while also saving any changes.
     * @return A save button.
     */
    private Button generateSaveButton()
    {
        return new Button
        (
            this.parent.width / 2 + 3,
            this.parent.height - BOTTOM_OFFSET,
            getSmallWidth(),
            BUTTON_HEIGHT,
            Component.translatable(LangUtil.Gui.BUTTON_SAVE_AND_DONE),
            (button) -> this.parent.onClose(false)
        );
    }

    /**
     * Gets a width for small buttons based on the parent screen width.
     * @return A width of 200 or smaller.
     */
    private int getSmallWidth() { return Math.min(200, (this.parent.width - 50 - 12) / 3); }

    /**
     * This input box is used by the search tab.
     * @return An edit box widget.
     */
    private EditBox generateInputBox()
    {
        int startX = (this.parent.width / 2) - (INPUT_WIDTH / 2);
        Component title = Component.translatable(LangUtil.Vanilla.SEARCH).withStyle(ChatFormatting.ITALIC);

        return new EditBox(this.parent.getFont(), startX, 3, INPUT_WIDTH, INPUT_HEIGHT, title);
    }

    /**
     * Provides a text group that gives information about swing speeds.
     * @return A text group widget.
     */
    private TextGroup generateSwingSpeedPrefix()
    {
        return new TextGroup(Component.translatable(LangUtil.Gui.SETTINGS_SPEED_HELP));
    }

    /**
     * This button toggles whether tooltips should be checked during search result generation.
     * @return A state button widget.
     */
    private StateButton generateBubbleState()
    {
        EditBox search = this.getSearchInput();

        return new StateButton(StateWidget.BUBBLE, search.x - 61, search.y - 1, false, (button) ->
        {
            this.runSearch(search.getValue());
            this.focusSearch();
        });
    }

    /**
     * This button toggles whether search results should be fuzzy or be 100% exact.
     * @return A state button widget.
     */
    private StateButton generateFuzzyState()
    {
        EditBox search = this.getSearchInput();

        return new StateButton(StateWidget.FUZZY, search.x - 42, search.y - 1, (button) ->
        {
            this.runSearch(search.getValue());
            this.focusSearch();
        });
    }

    /**
     * This button toggles tag restrictions that are applied to search results, regardless of fuzzy state.
     * @return A state button widget.
     */
    private StateButton generateTagButton()
    {
        EditBox search = this.getSearchInput();

        return new StateButton(StateWidget.TAG, search.x - 23, search.y - 1, (button) ->
        {
            ConfigWidgets.setTagCycle(search);
            this.focusSearch();
        });
    }

    /**
     * This button clears any input from the edit box used by the search tab.
     * @return A state button widget.
     */
    private StateButton generateClearButton()
    {
        EditBox search = this.getSearchInput();

        return new StateButton(StateWidget.CLEAR, search.x + search.getWidth() + 3, search.y - 1, (button) ->
        {
            search.setValue("");
            this.focusSearch();
        });
    }

    /* Private Widget Helpers */

    /**
     * These tags are used in the search tab.
     * Each tag will restrict the search results regardless of fuzzy search.
     */
    public enum SearchTag
    {
        CLIENT, SERVER, CONFLICT, RESET, NEW, SAVE, ALL;

        @Override
        public String toString() { return super.toString().toLowerCase(); }
    }

    /**
     * Changes the tag being used in the search box.
     * If there is an already existing search query then it will be preserved during tab cycling.
     * @param search The edit box widget instance being used by the search tab.
     */
    private static void setTagCycle(EditBox search)
    {
        StringBuilder query = new StringBuilder();
        String[] words = search.getValue().split(" ");
        String atTag = ArrayUtil.get(words, 0);

        for (String word : words)
        {
            if (!word.contains("@"))
                query.append(" ").append(word);
        }

        if (atTag == null || !atTag.startsWith("@"))
            search.setValue(String.format("@%s %s", SearchTag.CLIENT, query).replaceAll("\s+", " "));
        else
        {
            SearchTag[] searchTags = SearchTag.values();
            SearchTag found = null;
            SearchTag next = null;

            for (int i = 0; i < searchTags.length; i++)
            {
                SearchTag tag = searchTags[i];

                if (atTag.replace("@", "").equals(tag.toString()))
                    found = tag;

                if (found != null)
                {
                    if (Screen.hasShiftDown())
                    {
                        // Go back one if shift is down
                        if (ArrayUtil.get(searchTags, i - 1) != null)
                            next = searchTags[i - 1];
                        else // We're at the front of the array, so get the last one
                            next = searchTags[searchTags.length - 1];
                    }
                    else
                    {
                        // Go forward one
                        if (ArrayUtil.get(searchTags, i + 1) != null)
                            next = searchTags[i + 1];
                        else // We're at the end of the array, so get the first one
                            next = searchTags[0];
                    }

                    break;
                }
            }

            if (next == null)
                next = SearchTag.CLIENT;

            search.setValue(("@" + next + " " + query).replaceAll("\s+", " "));
        }
    }

    /**
     * Get a list of strings of lower case related words from the provided tweak.
     * @param tweak A tweak to get related words from.
     * @return A string array of lower case words that are related to the given tweak.
     */
    private String[] getRelatedWords(TweakClientCache<?> tweak)
    {
        return Component.translatable(tweak.getRelatedKey()).getString().toLowerCase().trim().split("\s*,\s*");
    }

    /**
     * Checks if the returned words from a tweak are actually relatable.
     * @param words A string array of words that resulted from a tweak.
     * @return Whether the results are actually related words.
     */
    private boolean areWordsUnrelated(String[] words)
    {
        return words.length == 0 || words[0].contains(TweakClientCache.RELATED_KEY.toLowerCase());
    }

    /**
     * Checks if the given query includes any of the related words defined in the language file.
     * @param tweak The tweak to get related words from.
     * @param query The current search query.
     * @return Whether any related words were exactly contained within the query.
     */
    private boolean isRelatedExact(TweakClientCache<?> tweak, String query)
    {
        String[] words = this.getRelatedWords(tweak);

        if (this.areWordsUnrelated(words))
            return false;

        for (String word : words)
        {
            if (query.contains(word))
                return true;
        }

        return false;
    }

    /**
     * Fuzzily checks if the given query includes any of the related words defined in the language file.
     * @param tweak The tweak to get related words from.
     * @param query The current search query.
     * @return The largest weight found from the related words.
     */
    private int isRelatedFuzzy(TweakClientCache<?> tweak, String query)
    {
        String[] words = this.getRelatedWords(tweak);
        int largestWeight = 0;

        if (this.areWordsUnrelated(words))
            return 0;

        for (String word : words)
        {
            int weight = FuzzySearch.weightedRatio(word, query);

            if (weight > largestWeight)
                largestWeight = weight;
        }

        return largestWeight;
    }

    /* Public Widget Helpers */

    /**
     * Ensures that the edit box in the search tab is visible, focused, and editable.
     */
    public void focusSearch()
    {
        this.getSearchInput().setVisible(true);
        this.getSearchInput().setFocus(true);
        this.getSearchInput().setEditable(true);
    }

    /**
     * Performs searching operations on the provided query.
     * @param search A query string to use when running search instructions.
     */
    public void runSearch(String search)
    {
        this.getConfigRowList().children().clear();
        this.parent.search.clear();

        if (search.isBlank() || search.equals("@"))
            return;

        search = search.toLowerCase();

        HashMap<String, TweakClientCache<?>> entries = TweakClientCache.all();
        StringBuilder searchBuilder = new StringBuilder();
        String[] words = search.split(" ");
        String atTag = ArrayUtil.get(words, 0);
        String requestedTag = atTag != null ? atTag.replace("@", "").toLowerCase() : "";

        SearchTag tag = null;

        for (String word : words)
        {
            if (!word.contains("@"))
                searchBuilder.append(" ").append(word);
        }

        for (SearchTag searchTag : SearchTag.values())
        {
            if (requestedTag.equals(searchTag.toString()))
                tag = searchTag;
        }

        for (TweakClientCache<?> tweak : entries.values())
        {
            tweak.setWeight(0);

            if (TweakGroup.isManual(tweak.getGroup()))
                continue;

            boolean isTagged = false;

            if (tag != null)
            {
                switch (tag)
                {
                    case ALL -> isTagged = true;
                    case NEW -> isTagged = tweak.isNew();
                    case CLIENT -> isTagged = tweak.isClient();
                    case SERVER -> isTagged = tweak.isServer();
                    case CONFLICT -> isTagged = tweak.getStatus() != TweakStatus.LOADED;
                    case RESET -> isTagged = tweak.isResettable();
                    case SAVE -> isTagged = tweak.isSavable();
                }
            }

            // Don't add the tweak to the results if we're narrowing the search
            if (isTagged && searchBuilder.isEmpty())
                this.parent.search.put(tweak.getId(), tweak);

            // Don't add the tweak to the results if the tweak doesn't belong to the tag group
            if ((!isTagged && tag != null) || (!isTagged && search.contains("@")))
                continue;

            String query = searchBuilder.toString().replaceAll("\s+", " ").trim().toLowerCase();

            // Fuzzy search or exact search - can be expanded with a tooltip bubble search
            if (this.getFuzzy().getState())
            {
                int title = FuzzySearch.weightedRatio(tweak.getTranslation().toLowerCase(), query);
                int bubble = FuzzySearch.weightedRatio(tweak.getTooltipTranslation().toLowerCase(), query);
                int container = FuzzySearch.weightedRatio(tweak.getContainerTranslation().toLowerCase(), query);
                int related = this.isRelatedFuzzy(tweak, query);

                if (!this.getBubble().getState())
                    bubble = 0;

                tweak.setWeight(MathUtil.getLargest(title, bubble, container, related));

                if (tweak.getWeight() > 0)
                    this.parent.search.put(tweak.getId(), tweak);
            }
            else
            {
                boolean isInBubble = tweak.getTooltipTranslation().toLowerCase().contains(query);
                boolean isBubbleOn = this.getBubble().getState();
                boolean isRelatedWord = this.isRelatedExact(tweak, query);
                boolean isInContainer = tweak.getContainerTranslation().toLowerCase().contains(query);
                boolean isValidResult = isInContainer || isRelatedWord || (isBubbleOn && isInBubble);

                if (tweak.getTranslation().toLowerCase().contains(query) || isValidResult)
                    this.parent.search.put(tweak.getId(), tweak);
            }
        }
    }
}
