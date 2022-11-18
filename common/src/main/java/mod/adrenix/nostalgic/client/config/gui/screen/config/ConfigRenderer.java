package mod.adrenix.nostalgic.client.config.gui.screen.config;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.screen.SwingScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.MenuOption;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ControlButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.GroupId;
import mod.adrenix.nostalgic.client.config.gui.widget.group.RadioGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.ClientReflect;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.client.config.gui.widget.*;
import mod.adrenix.nostalgic.common.config.tweak.DisabledTweak;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.ModUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * This record defines complex rendering instructions for the parent configuration screen.
 * @param parent A configuration screen instance.
 */

public record ConfigRenderer(ConfigScreen parent)
{
    /**
     * Sorts tweaks based on placement metadata and alphabetical translation.
     * @param tweak A tweak client cache value.
     * @param placement Any placement metadata.
     * @param translated Translation data.
     * @param top Predefined tweaks that render on top.
     * @param bottom Predefined tweaks that render on the bottom.
     */
    private static void sort
    (
        TweakClientCache<?> tweak,
        TweakClient.Gui.Placement placement,
        HashMap<String, TweakClientCache<?>> translated,
        HashMap<Integer, TweakClientCache<?>> top,
        HashMap<Integer, TweakClientCache<?>> bottom
    )
    {
        if (placement == null)
            translated.put(tweak.getTranslation(), tweak);
        else
        {
            if (tweak.getPosition() == TweakClient.Gui.Position.TOP)
                top.put(tweak.getOrder(), tweak);
            else if (tweak.getPosition() == TweakClient.Gui.Position.BOTTOM)
                bottom.put(tweak.getOrder(), tweak);
        }
    }

    /**
     * Generates a list of rows based on given container metadata.
     * @param list A config row list instance.
     * @param category A category enumeration value, or null.
     * @param subcategory A subcategory enumeration value, or null.
     * @param embedded An embedded enumeration value, or null.
     * @return An array list of properly sorted config rows.
     */
    private static ArrayList<ConfigRowList.Row> generateContainerRows
    (
        ConfigRowList list,
        @Nullable TweakClient.Category category,
        @Nullable TweakClient.Subcategory subcategory,
        @Nullable TweakClient.Embedded embedded
    )
    {
        ArrayList<ConfigRowList.Row> rows = new ArrayList<>();

        HashMap<String, TweakClientCache<?>> translated = new HashMap<>();
        HashMap<Integer, TweakClientCache<?>> bottom = new HashMap<>();
        HashMap<Integer, TweakClientCache<?>> top = new HashMap<>();

        Set<TweakClient.Subcategory> subcategories = new HashSet<>();
        Set<TweakClient.Embedded> embeds = new HashSet<>();

        TweakClientCache.all().forEach((id, tweak) ->
        {
            /*
               Tweaks may have category metadata (which does not have any subcategory or embedded data)
               Tweaks may have subcategory metadata (which will have category data)
               Tweaks may have embedded metadata (which will have subcategory and category data)
             */

            TweakClient.Gui.Cat cat = tweak.getCategory();
            TweakClient.Gui.Sub sub = tweak.getSubcategory();
            TweakClient.Gui.Emb emb = tweak.getEmbedded();
            TweakClient.Gui.Placement placement = tweak.getPlacement();

            /*
               If this method is invoked with a category enumeration value, then the array list of rows must contain
               data for not only the provided category, but also subcategory and embedded rows. This is achieved through
               recursion if a category contains subcategory and embed containers.

               If this method is invoked with a subcategory enumeration value (which will be done by recursion), then
               the array list of rows must contain data for subcategory and embed rows. Embed data is generated through
               recursion.

               If this method is invoked with an embed enumeration value (which will be done by recursion), then the
               array list of rows only needs to contain embed data since this will be the deepest container type.
             */

            if (category != null)
            {
                /*
                   If this tweak resides in a category then it can be added and sorted if the categories match.
                   The tweak's group must also match the category's group.
                 */
                boolean isTweakInCategory =
                    cat != null &&
                    cat.group() == category &&
                    tweak.getGroup() == category.getGroup()
                ;

                /*
                   Subcategory rows are generated here if this category has it. If this tweak is within a subcategory
                   that is in this category, then its data must also be added via recursion.

                   The subcategory is only added to the set if it hasn't already been added by a previous tweak.
                 */
                boolean isTweakInSubcategory =
                    sub != null &&
                    !subcategories.contains(sub.group()) &&
                    sub.group().getCategory() == category &&
                    tweak.getGroup() == category.getGroup()
                ;

                /*
                   It may be possible that a tweak is in an embedded container that is within a subcategory with no
                   tweaks. In this case, the embed's subcategory will never be added since no tweak pushed it into
                   the subcategory set.

                   This checks if a tweak has embedded data and checks if the tweak's subcategory is missing from the
                   subcategory set. If so, then the tweak's embedded subcategory parent will be added.
                 */
                boolean isTweakInEmptySubcategory =
                    emb != null &&
                    !subcategories.contains(emb.group().getSubcategory()) &&
                    emb.group().getSubcategory().getCategory() == category
                ;

                if (isTweakInCategory)
                    sort(tweak, placement, translated, top, bottom);
                else if (isTweakInSubcategory)
                    subcategories.add(sub.group());
                else if (isTweakInEmptySubcategory)
                    subcategories.add(emb.group().getSubcategory());
            }
            else if (subcategory != null)
            {
                /*
                   If this tweak resides in a subcategory then it can be added and sorted if the subcategories match.
                   The tweak's group must also match the subcategory's category's group.
                 */
                boolean isTweakInSubcategory =
                    sub != null &&
                    sub.group() == subcategory &&
                    tweak.getGroup() == subcategory.getCategory().getGroup()
                ;

                /*
                   If this tweak resides in an embed then it can be added and sorted if the subcategories match.
                   The tweak's group must also match the embed's subcategory's category's group.
                 */
                boolean isTweakInEmbed =
                    emb != null &&
                    !embeds.contains(emb.group()) &&
                    emb.group().getSubcategory() == subcategory &&
                    tweak.getGroup() == subcategory.getCategory().getGroup()
                ;

                if (isTweakInSubcategory)
                    sort(tweak, placement, translated, top, bottom);
                else if (isTweakInEmbed)
                    embeds.add(emb.group());
            }
            else if (embedded != null)
            {
                // If this tweak resides in an embed then it can be added and sorted if the main group types match
                if (emb != null && emb.group() == embedded && tweak.getGroup() == embedded.getSubcategory().getCategory().getGroup())
                    sort(tweak, placement, translated, top, bottom);
            }
        });

        EnumSet<TweakClient.Subcategory> allSubs = EnumSet.allOf(TweakClient.Subcategory.class);
        EnumSet<TweakClient.Embedded> allEmbeds = EnumSet.allOf(TweakClient.Embedded.class);

        /*
           Loops through all defined subcategory enumeration values and adds subcategory data via recursion if this
           method was invoked with a category enumeration value.
         */
        allSubs.forEach((sub) ->
        {
            if (subcategories.contains(sub))
                rows.add(getSubcategory(sub, list).generate());
        });

        /*
           Loops through all defined embedded enumeration values and adds embedded data via recursion if this method was
           invoked with a subcategory enumeration value.
         */
        allEmbeds.forEach((embed) ->
        {
            if (embeds.contains(embed))
                rows.add(getEmbedded(embed, list).generate());
        });

        SortedMap<Integer, TweakClientCache<?>> sortTop = new TreeMap<>(top);
        SortedMap<String, TweakClientCache<?>> sortMiddle = new TreeMap<>(translated);
        SortedMap<Integer, TweakClientCache<?>> sortBottom = new TreeMap<>(bottom);

        sortTop.forEach((key, tweak) -> rows.add(list.getRow(tweak.getGroup(), tweak.getKey(), tweak.getValue())));
        sortMiddle.forEach((key, tweak) -> rows.add(list.getRow(tweak.getGroup(), tweak.getKey(), tweak.getValue())));
        sortBottom.forEach((key, tweak) -> rows.add(list.getRow(tweak.getGroup(), tweak.getKey(), tweak.getValue())));

        return rows;
    }

    /**
     * Gets a list supplier of config row instances.
     * @param list A config row list instance.
     * @param category A category enumeration value, or null.
     * @param subcategory A subcategory enumeration value, or null.
     * @param embedded An embed enumeration value, or null.
     * @return A supplier that provides a list of properly sorted rows based on the given container type.
     */
    private static Supplier<ArrayList<ConfigRowList.Row>> getContainerRowSupplier
    (
        ConfigRowList list,
        @Nullable TweakClient.Category category,
        @Nullable TweakClient.Subcategory subcategory,
        @Nullable TweakClient.Embedded embedded
    )
    {
        return () -> generateContainerRows(list, category, subcategory, embedded);
    }

    /**
     * Get a container row instance based on an embed enumeration value.
     * @param embedded An embed enumeration value.
     * @param list A config row list instance.
     * @return A list of properly sorted rows within an embed.
     */
    private static ConfigRowList.ContainerRow getEmbedded(TweakClient.Embedded embedded, ConfigRowList list)
    {
        return new ConfigRowList.ContainerRow
        (
            Component.translatable(embedded.getLangKey()),
            getContainerRowSupplier(list, null, null, embedded),
            embedded,
            ConfigRowList.ContainerType.EMBEDDED
        );
    }

    /**
     * Get a container row instance based on a subcategory enumeration value.
     * @param subcategory A subcategory enumeration value.
     * @param list A config row list instance.
     * @return A list of properly sorted rows within a subcategory.
     */
    private static ConfigRowList.ContainerRow getSubcategory(TweakClient.Subcategory subcategory, ConfigRowList list)
    {
        return new ConfigRowList.ContainerRow
        (
            Component.translatable(subcategory.getLangKey()),
            getContainerRowSupplier(list, null, subcategory, null),
            subcategory,
            ConfigRowList.ContainerType.SUBCATEGORY
        );
    }

    /**
     * Get a container row instance based on a category enumeration value.
     * @param category A category enumeration value.
     * @param list A config row list instance.
     * @return A list of properly sorted rows within a category.
     */
    private static ConfigRowList.ContainerRow getCategory(TweakClient.Category category, ConfigRowList list)
    {
        return new ConfigRowList.ContainerRow
        (
            Component.translatable(category.getLangKey()),
            getContainerRowSupplier(list, category, null, null),
            category
        );
    }

    /**
     * Generate all category, subcategory, embed, and tweak rows for the provided group.
     * @param list A config row list instance.
     * @param group A group type enumeration value.
     * @return A list of properly sorted config row list instances for the given group type.
     */
    private static List<ConfigRowList.Row> getCategories(ConfigRowList list, GroupType group)
    {
        List<ConfigRowList.Row> rows = new ArrayList<>();
        EnumSet<TweakClient.Category> categories = EnumSet.allOf(TweakClient.Category.class);

        categories.forEach((category) ->
        {
            if (category.getGroup() == group)
                rows.add(getCategory(category, list).generate());
        });

        return rows;
    }

    /**
     * Generates all config rows for the given group type.
     * @param group A group type to generate data from.
     */
    private void addRows(GroupType group)
    {
        ConfigRowList list = this.parent.getWidgets().getConfigRowList();

        /*
           The following method automatically generates row data from the client's tweak cache.
           All sorting is handled by the above methods.
         */

        getCategories(list, group).forEach(list::addRow);

        /*
           Any tweaks that do not fall into a category, subcategory, or embed needs row data generated and sorted here.

           If there is any change in sort logic here, then this change may require a reflection of logic in container
           sorting logic if applicable.
         */

        Comparator<String> translationComparator = Comparator.comparing((String key) -> TweakClientCache.get(group, key).getTranslation());
        Comparator<String> orderComparator = Comparator.comparing((String key) -> TweakClientCache.get(group, key).getOrder());

        HashMap<String, Object> top = new HashMap<>();
        HashMap<String, Object> middle = new HashMap<>();
        HashMap<String, Object> bottom = new HashMap<>();
        HashMap<String, Object> all = new HashMap<>(ClientReflect.getGroup(group));

        all.forEach((key, value) ->
        {
            TweakClient.Gui.Placement placement = CommonReflect.getAnnotation(group, key, TweakClient.Gui.Placement.class);
            TweakClient.Gui.Cat cat = CommonReflect.getAnnotation(group, key, TweakClient.Gui.Cat.class);
            TweakClient.Gui.Sub sub = CommonReflect.getAnnotation(group, key, TweakClient.Gui.Sub.class);
            TweakClient.Gui.Emb emb = CommonReflect.getAnnotation(group, key, TweakClient.Gui.Emb.class);

            if (cat == null && sub == null && emb == null)
            {
                if (placement == null)
                    middle.put(key, value);
                else if (placement.pos() == TweakClient.Gui.Position.TOP)
                    top.put(key, value);
                else if (placement.pos() == TweakClient.Gui.Position.BOTTOM)
                    bottom.put(key, value);
            }
        });

        SortedMap<String, Object> sortTop = new TreeMap<>(orderComparator);
        SortedMap<String, Object> sortMiddle = new TreeMap<>(translationComparator);
        SortedMap<String, Object> sortBottom = new TreeMap<>(orderComparator);

        sortTop.putAll(top);
        sortMiddle.putAll(middle);
        sortBottom.putAll(bottom);

        sortTop.forEach((key, value) -> list.addRow(group, key, value));
        sortMiddle.forEach((key, value) -> list.addRow(group, key, value));
        sortBottom.forEach((key, value) -> list.addRow(group, key, value));
    }

    /**
     * Add a new row instance to the search results based on the provided tweak.
     * @param tweak A tweak client cache instance.
     */
    private void addSearchRow(TweakClientCache<?> tweak)
    {
        SearchCrumbs crumbs = new SearchCrumbs(tweak);
        ConfigRowList list = this.parent.getWidgets().getConfigRowList();
        ConfigRowList.Row row = list.getRow(tweak.getGroup(), tweak.getKey(), tweak.getValue());

        row.children.add(crumbs);
        list.addRow(row);
    }

    /**
     * Logic for adding search rows based on the current user input inside the search box.
     * No instructions are executed if the search map is empty.
     */
    private void addFound()
    {
        if (this.parent.search.isEmpty())
            return;

        String[] words = this.parent.getWidgets().getSearchInput().getValue().split(" ");
        String first = ModUtil.Array.get(words, 0);

        boolean isTagOnly = first != null && first.startsWith("@") && words.length == 1;

        Map<String, TweakClientCache<?>> found = this.parent.search;
        Map<String, TweakClientCache<?>> sorted = isTagOnly ?
            new TreeMap<>(Comparator.comparing(key -> found.get(key).getTranslation())) :
            new TreeMap<>((firstKey, secondKey) -> TweakClientCache.compareWeights(found.get(secondKey).getWeight(), found.get(firstKey).getWeight()))
        ;

        sorted.putAll(found);
        sorted.forEach((id, tweak) -> this.addSearchRow(tweak));
    }

    /**
     * Adds rows and provides logic for changing all tweak values instantaneously.
     * @return An array list of config row instances.
     */
    private ArrayList<ConfigRowList.Row> getGeneralOverrideList()
    {
        TextGroup help = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_HELP));
        AtomicBoolean serverOnly = new AtomicBoolean(false);

        Button.OnPress onDisable = (button) -> Arrays.stream(GroupType.values()).forEach((group) ->
        {
            if (!GroupType.isManual(group))
            {
                ClientReflect.getGroup(group).forEach((key, value) ->
                {
                    TweakClientCache<Boolean> tweak = TweakClientCache.get(group, key);

                    boolean isDisableIgnored = tweak.isMetadataPresent(TweakClient.Gui.IgnoreDisable.class);
                    boolean isClientIgnored = serverOnly.get() && tweak.isClient() && !tweak.isDynamic();
                    boolean isLocked = tweak.isLocked();
                    boolean isChangeable = !isDisableIgnored && !isLocked && !isClientIgnored;

                    if (!isClientIgnored && !isLocked)
                        tweak.reset();

                    if (value instanceof Boolean && isChangeable)
                    {
                        TweakClient.Gui.DisabledBoolean disabledBoolean = tweak.getMetadata(TweakClient.Gui.DisabledBoolean.class);

                        if (disabledBoolean == null && tweak.getDefault())
                        {
                            tweak.reset();
                            tweak.setValue(!tweak.getValue());
                        }
                        else if (disabledBoolean != null)
                            tweak.setValue(disabledBoolean.value());
                    }

                    if (value instanceof Integer && isChangeable)
                    {
                        TweakClient.Gui.DisabledInteger disabledInteger = tweak.getMetadata(TweakClient.Gui.DisabledInteger.class);

                        if (disabledInteger != null)
                        {
                            TweakClientCache<Integer> intTweak = TweakClientCache.get(group, key);
                            intTweak.setValue(disabledInteger.value());
                        }
                    }

                    if (value instanceof DisabledTweak<?> && isChangeable)
                    {
                        TweakClientCache<Enum<?>> enumTweak = TweakClientCache.get(group, key);
                        enumTweak.setValue(((DisabledTweak<?>) value).getDisabledValue());
                    }
                });
            }
        });

        Button.OnPress onEnable = (button) -> Arrays.stream(GroupType.values()).forEach((group) ->
        {
            if (!GroupType.isManual(group))
            {
                ClientReflect.getGroup(group).forEach((key, value) ->
                {
                    TweakClientCache<?> tweak = TweakClientCache.get(group, key);

                    boolean isClientIgnored = serverOnly.get() && tweak.isClient() && !tweak.isDynamic();
                    boolean isLocked = tweak.isLocked();
                    boolean isChangeable = !isLocked && !isClientIgnored;

                    if (isChangeable)
                        tweak.reset();
                });
            }
        });

        ControlButton disableAll = new ControlButton(Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_DISABLE), onDisable);
        ControlButton enableAll = new ControlButton(Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_ENABLE), onEnable);

        ToggleCheckbox toggleServerOnly = new ToggleCheckbox
        (
            Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_SERVER),
            serverOnly::get,
            serverOnly::set
        );

        toggleServerOnly.setTooltip(Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_SERVER_TIP));

        ConfigRowList.SingleLeftRow server = new ConfigRowList.SingleLeftRow(toggleServerOnly, ConfigRowList.CAT_TEXT_START);
        ConfigRowList.SingleLeftRow disable = new ConfigRowList.SingleLeftRow(disableAll, ConfigRowList.CAT_TEXT_START);
        ConfigRowList.SingleLeftRow enable = new ConfigRowList.SingleLeftRow(enableAll, ConfigRowList.CAT_TEXT_START);

        ArrayList<ConfigRowList.Row> rows = new ArrayList<>(help.getRows());

        rows.add(server.generate());
        rows.add(disable.generate());
        rows.add(enable.generate());

        return rows;
    }

    /**
     * Generates rows for key binding options in the general options group.
     * @return An array list of config row instances.
     */
    private ArrayList<ConfigRowList.Row> getGeneralBindingsList()
    {
        ArrayList<ConfigRowList.Row> rows = new ArrayList<>();

        KeyMapping openConfig = KeyUtil.find(LangUtil.Key.OPEN_CONFIG);
        KeyMapping toggleFog = KeyUtil.find(LangUtil.Key.TOGGLE_FOG);

        if (openConfig != null)
            rows.add(new ConfigRowList.BindingRow(openConfig).generate());

        if (toggleFog != null)
            rows.add(new ConfigRowList.BindingRow(toggleFog).generate());

        return rows;
    }

    /**
     * Generates rows and containers for configuration screen menu settings.
     * @return An array list of config row instances.
     */
    private ArrayList<ConfigRowList.Row> getGeneralSettingsList()
    {
        ArrayList<ConfigRowList.Row> subcategories = new ArrayList<>();

        /* Default Screen Options */

        Supplier<ArrayList<ConfigRowList.Row>> getScreenOptions = () ->
        {
            TweakClientCache<MenuOption> defaultScreen = TweakClientCache.get(GuiTweak.DEFAULT_SCREEN);
            RadioGroup<MenuOption> radioGroup = new RadioGroup<>
            (
                MenuOption.class,
                DefaultConfig.Gui.DEFAULT_SCREEN,
                defaultScreen::getValue,
                defaultScreen::setValue,
                MenuOption::getTranslation
            );

            TextGroup radioHelp = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_CONFIG_SCREEN_INFO));
            ArrayList<ConfigRowList.Row> rows = new ArrayList<>(radioHelp.getRows());

            rows.addAll(radioGroup.getRows());

            return rows;
        };

        ConfigRowList.ContainerRow screenConfig = new ConfigRowList.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_CONFIG_SCREEN_TITLE),
            getScreenOptions,
            GroupId.DEFAULT_SCREEN_CONFIG,
            ConfigRowList.ContainerType.SUBCATEGORY
        );

        subcategories.add(screenConfig.generate());

        /* Tree Indent Options */

        Supplier<ArrayList<ConfigRowList.Row>> getTreeOptions = () ->
        {
            TextGroup treeHelp = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TREE_INFO));
            ArrayList<ConfigRowList.Row> rows = new ArrayList<>(treeHelp.getRows());

            TweakClientCache<Boolean> tree = TweakClientCache.get(GuiTweak.DISPLAY_CATEGORY_TREE);
            rows.add(new ConfigRowList.BooleanRow(GroupType.GUI, tree.getKey(), tree.getValue()).generate());

            TweakClientCache<String> color = TweakClientCache.get(GuiTweak.CATEGORY_TREE_COLOR);
            rows.add(new ConfigRowList.ColorRow(GroupType.GUI, color.getKey(), color.getValue()).generate());

            return rows;
        };

        ConfigRowList.ContainerRow treeConfig = new ConfigRowList.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TREE_TITLE),
            getTreeOptions,
            GroupId.TREE_CONFIG,
            ConfigRowList.ContainerType.SUBCATEGORY
        );

        subcategories.add(treeConfig.generate());

        /* Row Highlighting Options */

        Supplier<ArrayList<ConfigRowList.Row>> getHighlightOptions = () ->
        {
            TextGroup rowHelp = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_CONFIG_ROW_INFO));
            ArrayList<ConfigRowList.Row> rows = new ArrayList<>(rowHelp.getRows());

            TweakClientCache<Boolean> highlight = TweakClientCache.get(GuiTweak.DISPLAY_ROW_HIGHLIGHT);
            rows.add(new ConfigRowList.BooleanRow(GroupType.GUI, highlight.getKey(), highlight.getValue()).generate());

            TweakClientCache<Boolean> fade = TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_FADE);
            rows.add(new ConfigRowList.BooleanRow(GroupType.GUI, fade.getKey(), fade.getValue()).generate());

            TweakClientCache<String> color = TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_COLOR);
            rows.add(new ConfigRowList.ColorRow(GroupType.GUI, color.getKey(), color.getValue()).generate());

            return rows;
        };

        ConfigRowList.ContainerRow highlightConfig = new ConfigRowList.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_CONFIG_ROW_TITLE),
            getHighlightOptions,
            GroupId.ROW_CONFIG,
            ConfigRowList.ContainerType.SUBCATEGORY
        );

        subcategories.add(highlightConfig.generate());

        /* Tag Options */

        Supplier<ArrayList<ConfigRowList.Row>> getTaggingOptions = () ->
        {
            TextGroup tagHelp = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TAGS_INFO));
            ArrayList<ConfigRowList.Row> rows = new ArrayList<>(tagHelp.getRows());

            // New Tags

            TweakClientCache<Boolean> displayNewTags = TweakClientCache.get(GuiTweak.DISPLAY_NEW_TAGS);
            ToggleCheckbox newTagsCheckbox = new ToggleCheckbox
            (
                Component.translatable(LangUtil.Gui.GENERAL_CONFIG_NEW_TAGS_LABEL),
                displayNewTags::getValue,
                displayNewTags::setValue
            );

            ConfigRowList.ManualRow newTagsRow = new ConfigRowList.ManualRow(List.of(newTagsCheckbox));
            rows.add(newTagsRow.generate());

            // Sided tags

            TweakClientCache<Boolean> displaySidedTags = TweakClientCache.get(GuiTweak.DISPLAY_SIDED_TAGS);
            ToggleCheckbox sidedTagsCheckbox = new ToggleCheckbox
            (
                Component.translatable(LangUtil.Gui.GENERAL_CONFIG_SIDED_TAGS_LABEL),
                displaySidedTags::getValue,
                displaySidedTags::setValue
            );

            ConfigRowList.ManualRow sidedTagsRow = new ConfigRowList.ManualRow(List.of(sidedTagsCheckbox));
            rows.add(sidedTagsRow.generate());

            // Tag Tooltips

            TweakClientCache<Boolean> displayTagTooltips = TweakClientCache.get(GuiTweak.DISPLAY_TAG_TOOLTIPS);
            ToggleCheckbox tagTooltipsCheckbox = new ToggleCheckbox
            (
                Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TAG_TOOLTIPS_LABEL),
                displayTagTooltips::getValue,
                displayTagTooltips::setValue
            );

            ConfigRowList.ManualRow tagTooltipsRow = new ConfigRowList.ManualRow(List.of(tagTooltipsCheckbox));

            rows.add(tagTooltipsRow.generate());
            rows.add(new ConfigRowList.ManualRow(new ArrayList<>()).generate());

            // Feature Status

            TextGroup statusHelp = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TWEAK_STATUS_HELP));
            rows.addAll(statusHelp.getRows());

            TweakClientCache<Boolean> displayFeatureStatus = TweakClientCache.get(GuiTweak.DISPLAY_FEATURE_STATUS);
            ToggleCheckbox featureStatusCheckbox = new ToggleCheckbox
            (
                Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TWEAK_STATUS_LABEL),
                displayFeatureStatus::getValue,
                displayFeatureStatus::setValue
            );

            ConfigRowList.ManualRow featureStatusRow = new ConfigRowList.ManualRow(List.of(featureStatusCheckbox));
            rows.add(featureStatusRow.generate());

            return rows;
        };

        ConfigRowList.ContainerRow taggingConfig = new ConfigRowList.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TAGS_TITLE),
            getTaggingOptions,
            GroupId.TITLE_TAGS_CONFIG,
            ConfigRowList.ContainerType.SUBCATEGORY
        );

        subcategories.add(taggingConfig.generate());

        return subcategories;
    }

    /**
     * Generates information about any important notifications.
     * @return An array list of config row instances.
     */
    private ArrayList<ConfigRowList.Row> getGeneralNotifyList()
    {
        TextGroup notify = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_NOTIFY_CONFLICT, TweakClientCache.getConflicts()));

        return new ArrayList<>(notify.getRows());
    }

    /**
     * Generates information about search tags.
     * @return An array list of config row instances.
     */
    private ArrayList<ConfigRowList.Row> getGeneralSearchTags()
    {
        Component help = Component.translatable(LangUtil.Gui.GENERAL_SEARCH_HELP);
        Component newTag = Component.translatable(LangUtil.Gui.GENERAL_SEARCH_NEW);
        Component conflictTag = Component.translatable(LangUtil.Gui.GENERAL_SEARCH_CONFLICT);
        Component resetTag = Component.translatable(LangUtil.Gui.GENERAL_SEARCH_RESET);
        Component clientTag = Component.translatable(LangUtil.Gui.GENERAL_SEARCH_CLIENT);
        Component serverTag = Component.translatable(LangUtil.Gui.GENERAL_SEARCH_SERVER);
        Component saveTag = Component.translatable(LangUtil.Gui.GENERAL_SEARCH_SAVE);
        Component allTag = Component.translatable(LangUtil.Gui.GENERAL_SEARCH_ALL);
        Component[] tags = new Component[] { help, newTag, conflictTag, resetTag, clientTag, serverTag, saveTag, allTag };

        return new TextGroup(ModUtil.Text.combine(tags)).getRows();
    }

    /**
     * Generates information about keyboard shortcuts.
     * @return An array list of config row instances.
     */
    private ArrayList<ConfigRowList.Row> getGeneralShortcuts()
    {
        Component help = Component.translatable(LangUtil.Gui.GENERAL_SHORTCUT_HELP);
        Component find = Component.translatable(LangUtil.Gui.GENERAL_SHORTCUT_FIND);
        Component save = Component.translatable(LangUtil.Gui.GENERAL_SHORTCUT_SAVE);
        Component exit = Component.translatable(LangUtil.Gui.GENERAL_SHORTCUT_EXIT);
        Component jump = Component.translatable(LangUtil.Gui.GENERAL_SHORTCUT_JUMP);
        Component all = Component.translatable(LangUtil.Gui.GENERAL_SHORTCUT_ALL);
        Component group = Component.translatable(LangUtil.Gui.GENERAL_SHORTCUT_GROUP);

        return new TextGroup(ModUtil.Text.combine(new Component[] { help, find, save, exit, jump, all, group })).getRows();
    }

    /**
     * Instructions for manually creating rows and containers for the general configuration group.
     * No logic is used here to automatically generate rows or containers.
     */
    private void addGeneral()
    {
        ConfigRowList list = this.parent.getWidgets().getConfigRowList();

        /* Mod Enabled */

        TweakClientCache<Boolean> isModEnabled = TweakClientCache.get(GroupType.ROOT, ClientConfig.ROOT_KEY);

        list.addRow(new ConfigRowList.BooleanRow(GroupType.ROOT, isModEnabled.getKey(), isModEnabled.getValue()).generate());

        /* All Tweak Overrides */

        ConfigRowList.ContainerRow changeAllTweaks = new ConfigRowList.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_TITLE),
            this::getGeneralOverrideList,
            GroupId.OVERRIDE_CONFIG
        );

        list.addRow(changeAllTweaks.generate());

        /* Key Bindings */

        ConfigRowList.ContainerRow changeKeyBinds = new ConfigRowList.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_BINDINGS),
            this::getGeneralBindingsList,
            GroupId.BINDINGS_CONFIG
        );

        list.addRow(changeKeyBinds.generate());

        /* Menu Settings */

        ConfigRowList.ContainerRow changeSettings = new ConfigRowList.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TITLE),
            this::getGeneralSettingsList,
            GroupId.GENERAL_CONFIG
        );

        list.addRow(changeSettings.generate());

        /* Notifications */

        ConfigRowList.ContainerRow notifications = new ConfigRowList.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_NOTIFY_TITLE),
            this::getGeneralNotifyList,
            GroupId.NOTIFY_CONFIG
        );

        list.addRow(notifications.generate());

        /* Search Tags */

        ConfigRowList.ContainerRow searchTags = new ConfigRowList.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_SEARCH_TITLE),
            this::getGeneralSearchTags,
            GroupId.SEARCH_TAGS_CONFIG
        );

        list.addRow(searchTags.generate());

        /* Keyboard Shortcuts */

        ConfigRowList.ContainerRow shortcuts = new ConfigRowList.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_SHORTCUT_TITLE),
            this::getGeneralShortcuts,
            GroupId.SHORTCUTS_CONFIG
        );

        list.addRow(shortcuts.generate());
    }

    /**
     * Used by the "all" group tab which displays a list of every tweak in the mod.
     * Any addition group types will need to be added here.
     */
    public void generateRowsFromAllGroups()
    {
        addRows(GroupType.SOUND);
        addRows(GroupType.CANDY);
        addRows(GroupType.GAMEPLAY);
        addRows(GroupType.ANIMATION);
        addRows(GroupType.SWING);
    }

    /**
     * Generates config row lists based on the current config tab and renders special effects based on config tab.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick A change in frame time.
     */
    public void generateAndRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (this.parent.getConfigTab() == ConfigScreen.ConfigTab.SWING)
        {
            this.parent.getWidgets().getSwingSpeedPrefix().render(poseStack, mouseX, mouseY, partialTick);

            ConfigRowList.SingleCenteredRow custom = new ConfigRowList.SingleCenteredRow
            (
                new ControlButton
                (
                    Component.translatable(LangUtil.Gui.SWING),
                    (button) -> this.parent.getMinecraft().setScreen(new SwingScreen(this.parent))
                )
            );

            this.parent.getWidgets().getConfigRowList().addRow(custom.generate());
        }
        else if (this.parent.getConfigTab() == ConfigScreen.ConfigTab.SEARCH && this.parent.search.isEmpty())
        {
            String[] words = this.parent.getWidgets().getSearchInput().getValue().split(" ");
            String first = ModUtil.Array.get(words, 0);

            boolean isInvalidTag = this.parent.getWidgets().getSearchInput().getValue().startsWith("@");

            if (first != null)
            {
                for (ConfigWidgets.SearchTag tag : ConfigWidgets.SearchTag.values())
                {
                    if (tag.toString().equals(first.replaceAll("@", "")))
                        isInvalidTag = false;
                }
            }

            if (isInvalidTag)
                this.parent.renderLast.add(() -> Screen.drawCenteredString
                (
                    poseStack,
                    this.parent.getFont(),
                    Component.translatable(LangUtil.Gui.SEARCH_INVALID, this.parent.getWidgets().getSearchInput().getValue()),
                    this.parent.width / 2,
                    this.parent.height / 2,
                    0xFFFFFF
                ));
            else
            {
                this.parent.renderLast.add(() -> Screen.drawCenteredString
                (
                    poseStack,
                    this.parent.getFont(),
                    Component.translatable(LangUtil.Gui.SEARCH_EMPTY),
                    this.parent.width / 2,
                    this.parent.height / 2,
                    0xFFFFFF
                ));
            }
        }

        switch (this.parent.getConfigTab())
        {
            case ALL -> generateRowsFromAllGroups();
            case GENERAL -> addGeneral();
            case SOUND -> addRows(GroupType.SOUND);
            case CANDY -> addRows(GroupType.CANDY);
            case GAMEPLAY -> addRows(GroupType.GAMEPLAY);
            case ANIMATION -> addRows(GroupType.ANIMATION);
            case SWING -> addRows(GroupType.SWING);
            case SEARCH -> addFound();
        }
    }
}
