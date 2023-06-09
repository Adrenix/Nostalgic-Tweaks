package mod.adrenix.nostalgic.client.config.gui.screen.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.annotation.TweakGui;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakCategory;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakEmbed;
import mod.adrenix.nostalgic.client.config.annotation.container.TweakSubcategory;
import mod.adrenix.nostalgic.client.config.gui.overlay.ServerSideModeOverlay;
import mod.adrenix.nostalgic.client.config.gui.screen.MenuOption;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListMapScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.SearchCrumbs;
import mod.adrenix.nostalgic.client.config.gui.widget.ToggleCheckbox;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ContainerId;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ControlButton;
import mod.adrenix.nostalgic.client.config.gui.widget.group.RadioGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowBuild;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowTweak;
import mod.adrenix.nostalgic.client.config.reflect.ClientReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.auto.AutoConfig;
import mod.adrenix.nostalgic.common.config.list.ConfigList;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.common.config.tweak.DisabledTweak;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.common.ArrayUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.PathUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

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
        TweakGui.Placement placement,
        HashMap<String, TweakClientCache<?>> translated,
        HashMap<Integer, TweakClientCache<?>> top,
        HashMap<Integer, TweakClientCache<?>> bottom
    )
    {
        if (placement == null)
            translated.put(tweak.getTranslation(), tweak);
        else
        {
            if (tweak.getPosition() == TweakGui.Position.TOP)
                top.put(tweak.getOrder(), tweak);
            else if (tweak.getPosition() == TweakGui.Position.BOTTOM)
                bottom.put(tweak.getOrder(), tweak);
        }
    }

    /**
     * Generates a list of rows based on given container metadata.
     * @param list A config row list instance.
     * @param tweakCategory A category enumeration value, or null.
     * @param tweakSubcategory A subcategory enumeration value, or null.
     * @param tweakEmbed An embedded enumeration value, or null.
     * @return An array list of properly sorted config rows.
     */
    private static ArrayList<ConfigRowList.Row> generateContainerRows
    (
        ConfigRowList list,
        @Nullable TweakCategory tweakCategory,
        @Nullable TweakSubcategory tweakSubcategory,
        @Nullable TweakEmbed tweakEmbed
    )
    {
        ArrayList<ConfigRowList.Row> rows = new ArrayList<>();

        HashMap<String, TweakClientCache<?>> translated = new HashMap<>();
        HashMap<Integer, TweakClientCache<?>> bottom = new HashMap<>();
        HashMap<Integer, TweakClientCache<?>> top = new HashMap<>();

        Set<TweakSubcategory> subcategories = new HashSet<>();
        Set<TweakEmbed> embeds = new HashSet<>();

        TweakClientCache.all().forEach((id, tweak) ->
        {
            /*
               Tweaks may have category metadata (which does not have any subcategory or embedded data)
               Tweaks may have subcategory metadata (which will have category data)
               Tweaks may have embedded metadata (which will have subcategory and category data)
             */

            TweakGui.Placement placement = tweak.getPlacement();
            TweakGui.Category category = tweak.getCategory();
            TweakGui.Subcategory subcategory = tweak.getSubcategory();
            TweakGui.Embed embed = tweak.getEmbed();

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

            if (tweakCategory != null)
            {
                /*
                   If this tweak resides in a category then it can be added and sorted if the categories match.
                   The tweak's group must also match the category's group.
                 */
                boolean isTweakInCategory =
                    category != null &&
                    category.container() == tweakCategory &&
                    tweak.getGroup() == tweakCategory.getGroup()
                ;

                /*
                   Subcategory rows are generated here if this category has it. If this tweak is within a subcategory
                   that is in this category, then its data must also be added via recursion.

                   The subcategory is only added to the set if it hasn't already been added by a previous tweak.
                 */
                boolean isTweakInSubcategory =
                    subcategory != null &&
                    !subcategories.contains(subcategory.container()) &&
                    subcategory.container().getCategory() == tweakCategory &&
                    tweak.getGroup() == tweakCategory.getGroup()
                ;

                /*
                   It may be possible that a tweak is in an embedded container that is within a subcategory with no
                   tweaks. In this case, the embed's subcategory will never be added since no tweak pushed it into
                   the subcategory set.

                   This checks if a tweak has embedded data and checks if the tweak's subcategory is missing from the
                   subcategory set. If so, then the tweak's embedded subcategory parent will be added.
                 */
                boolean isTweakInEmptySubcategory =
                    embed != null &&
                    !subcategories.contains(embed.container().getSubcategory()) &&
                    embed.container().getSubcategory().getCategory() == tweakCategory
                ;

                if (isTweakInCategory)
                    sort(tweak, placement, translated, top, bottom);
                else if (isTweakInSubcategory)
                    subcategories.add(subcategory.container());
                else if (isTweakInEmptySubcategory)
                    subcategories.add(embed.container().getSubcategory());
            }
            else if (tweakSubcategory != null)
            {
                /*
                   If this tweak resides in a subcategory then it can be added and sorted if the subcategories match.
                   The tweak's group must also match the subcategory's category's group.
                 */
                boolean isTweakInSubcategory =
                    subcategory != null &&
                    subcategory.container() == tweakSubcategory &&
                    tweak.getGroup() == tweakSubcategory.getCategory().getGroup()
                ;

                /*
                   If this tweak resides in an embed then it can be added and sorted if the subcategories match.
                   The tweak's group must also match the embed's subcategory's category's group.
                 */
                boolean isTweakInEmbed =
                    embed != null &&
                    !embeds.contains(embed.container()) &&
                    embed.container().getSubcategory() == tweakSubcategory &&
                    tweak.getGroup() == tweakSubcategory.getCategory().getGroup()
                ;

                if (isTweakInSubcategory)
                    sort(tweak, placement, translated, top, bottom);
                else if (isTweakInEmbed)
                    embeds.add(embed.container());
            }
            else if (tweakEmbed != null)
            {
                // If this tweak resides in an embed then it can be added and sorted if the main group types match
                if (embed != null && embed.container() == tweakEmbed && tweak.getGroup() == tweakEmbed.getSubcategory().getCategory().getGroup())
                    sort(tweak, placement, translated, top, bottom);
            }
        });

        EnumSet<TweakSubcategory> allSubs = EnumSet.allOf(TweakSubcategory.class);
        EnumSet<TweakEmbed> allEmbeds = EnumSet.allOf(TweakEmbed.class);

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

        sortTop.forEach((key, tweak) -> addRowFromTweak(rows, list, tweak));
        sortMiddle.forEach((key, tweak) -> addRowFromTweak(rows, list, tweak));
        sortBottom.forEach((key, tweak) -> addRowFromTweak(rows, list, tweak));

        return rows;
    }

    /**
     * Add a new config row list row to the given list of rows if the tweak allows automatic config setup.
     * @param rows An array list of config row list rows.
     * @param list A config row list instance.
     * @param tweak A tweak client cache instance.
     */
    private static void addRowFromTweak(ArrayList<ConfigRowList.Row> rows, ConfigRowList list, TweakClientCache<?> tweak)
    {
        ConfigRowList.Row row = list.rowFromTweak(tweak);

        if (row != null)
            rows.add(row);
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
        @Nullable TweakCategory category,
        @Nullable TweakSubcategory subcategory,
        @Nullable TweakEmbed embedded
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
    private static ConfigRowGroup.ContainerRow getEmbedded(TweakEmbed embedded, ConfigRowList list)
    {
        return new ConfigRowGroup.ContainerRow
        (
            Component.translatable(embedded.getLangKey()),
            getContainerRowSupplier(list, null, null, embedded),
            embedded,
            ConfigRowGroup.ContainerType.EMBEDDED
        );
    }

    /**
     * Get a container row instance based on a subcategory enumeration value.
     * @param subcategory A subcategory enumeration value.
     * @param list A config row list instance.
     * @return A list of properly sorted rows within a subcategory.
     */
    private static ConfigRowGroup.ContainerRow getSubcategory(TweakSubcategory subcategory, ConfigRowList list)
    {
        return new ConfigRowGroup.ContainerRow
        (
            Component.translatable(subcategory.getLangKey()),
            getContainerRowSupplier(list, null, subcategory, null),
            subcategory,
            ConfigRowGroup.ContainerType.SUBCATEGORY
        );
    }

    /**
     * Get a container row instance based on a category enumeration value.
     * @param category A category enumeration value.
     * @param list A config row list instance.
     * @return A list of properly sorted rows within a category.
     */
    private static ConfigRowGroup.ContainerRow getCategory(TweakCategory category, ConfigRowList list)
    {
        return new ConfigRowGroup.ContainerRow
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
    private static List<ConfigRowList.Row> getCategories(ConfigRowList list, TweakGroup group)
    {
        List<ConfigRowList.Row> rows = new ArrayList<>();
        EnumSet<TweakCategory> categories = EnumSet.allOf(TweakCategory.class);

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
    private void addRows(TweakGroup group)
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
            TweakGui.Placement placement = CommonReflect.getAnnotation(group, key, TweakGui.Placement.class);
            TweakGui.Category category = CommonReflect.getAnnotation(group, key, TweakGui.Category.class);
            TweakGui.Subcategory subcategory = CommonReflect.getAnnotation(group, key, TweakGui.Subcategory.class);
            TweakGui.Embed embed = CommonReflect.getAnnotation(group, key, TweakGui.Embed.class);

            if (category == null && subcategory == null && embed == null)
            {
                if (placement == null)
                    middle.put(key, value);
                else if (placement.pos() == TweakGui.Position.TOP)
                    top.put(key, value);
                else if (placement.pos() == TweakGui.Position.BOTTOM)
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
        ConfigRowList list = this.parent.getWidgets().getConfigRowList();
        ConfigRowList.Row row = list.rowFromTweak(tweak);

        if (row == null)
            return;

        row.children.add(new SearchCrumbs(tweak));
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
        String first = ArrayUtil.get(words, 0);

        boolean isTagOnly = first != null && first.startsWith("#") && words.length == 1;

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
        AtomicBoolean clientOnly = new AtomicBoolean(false);

        Button.OnPress onDisable = (button) -> Arrays.stream(TweakGroup.values()).forEach((group) ->
        {
            if (!TweakGroup.isManual(group))
            {
                ClientReflect.getGroup(group).forEach((key, value) ->
                {
                    TweakClientCache<Boolean> tweak = TweakClientCache.get(group, key);

                    boolean isDisableIgnored = tweak.isMetadataPresent(TweakGui.IgnoreDisable.class);
                    boolean isClientIgnored = serverOnly.get() && tweak.isClient() && !tweak.isDynamic();
                    boolean isServerIgnored = clientOnly.get() && (tweak.isServer() || tweak.isDynamic());
                    boolean isLocked = tweak.isLocked();
                    boolean isChangeable = !isDisableIgnored && !isLocked && !isClientIgnored && !isServerIgnored;

                    if (!isClientIgnored && !isServerIgnored && !isLocked)
                        tweak.reset();

                    if (value instanceof Boolean && isChangeable)
                    {
                        TweakGui.DisabledBoolean disabledBoolean = tweak.getMetadata(TweakGui.DisabledBoolean.class);

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
                        TweakGui.DisabledInteger disabledInteger = tweak.getMetadata(TweakGui.DisabledInteger.class);

                        if (disabledInteger != null)
                        {
                            TweakClientCache<Integer> intTweak = TweakClientCache.get(group, key);
                            intTweak.setValue(disabledInteger.value());
                        }
                    }

                    if (value instanceof String && isChangeable)
                    {
                        TweakGui.DisabledString disabledString = tweak.getMetadata(TweakGui.DisabledString.class);

                        if (disabledString != null)
                        {
                            TweakClientCache<String> stringTweak = TweakClientCache.get(group, key);
                            stringTweak.setValue(disabledString.value());
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

        Button.OnPress onEnable = (button) -> Arrays.stream(TweakGroup.values()).forEach((group) ->
        {
            if (!TweakGroup.isManual(group))
            {
                ClientReflect.getGroup(group).forEach((key, value) ->
                {
                    TweakClientCache<?> tweak = TweakClientCache.get(group, key);

                    boolean isClientIgnored = serverOnly.get() && tweak.isClient() && !tweak.isDynamic();
                    boolean isServerIgnored = clientOnly.get() && (tweak.isServer() || tweak.isDynamic());
                    boolean isLocked = tweak.isLocked();
                    boolean isChangeable = !isLocked && !isClientIgnored && !isServerIgnored;

                    if (isChangeable)
                        tweak.reset();
                });
            }
        });

        Button.OnPress onReview = (button) ->
        {
            this.parent.setConfigTab(ConfigScreen.ConfigTab.SEARCH);
            this.parent.getWidgets().getSearchInput().setValue(String.format("#%s ", ConfigWidgets.SearchTag.SAVE));
        };

        ControlButton disableAll = new ControlButton(Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_DISABLE), onDisable);
        ControlButton enableAll = new ControlButton(Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_ENABLE), onEnable);
        ControlButton reviewAll = new ControlButton(Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_REVIEW), onReview);

        ToggleCheckbox toggleClientOnly = new ToggleCheckbox
        (
            Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_CLIENT),
            clientOnly::get,
            clientOnly::set
        );

        ToggleCheckbox toggleServerOnly = new ToggleCheckbox
        (
            Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_SERVER),
            serverOnly::get,
            serverOnly::set
        );

        toggleClientOnly.setTooltip(Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_CLIENT_TIP));
        toggleServerOnly.setTooltip(Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_SERVER_TIP));

        ConfigRowBuild.BlankRow blank = new ConfigRowBuild.BlankRow();
        ConfigRowBuild.SingleLeftRow client = new ConfigRowBuild.SingleLeftRow(toggleClientOnly, ConfigRowList.CAT_TEXT_START);
        ConfigRowBuild.SingleLeftRow server = new ConfigRowBuild.SingleLeftRow(toggleServerOnly, ConfigRowList.CAT_TEXT_START);
        ConfigRowBuild.SingleLeftRow disable = new ConfigRowBuild.SingleLeftRow(disableAll, ConfigRowList.CAT_TEXT_START);
        ConfigRowBuild.SingleLeftRow enable = new ConfigRowBuild.SingleLeftRow(enableAll, ConfigRowList.CAT_TEXT_START);
        ConfigRowBuild.SingleLeftRow review = new ConfigRowBuild.SingleLeftRow(reviewAll, ConfigRowList.CAT_TEXT_START);

        ArrayList<ConfigRowList.Row> rows = new ArrayList<>(help.generate());

        rows.add(client.generate());
        rows.add(server.generate());
        rows.add(blank.generate());
        rows.add(disable.generate());
        rows.add(enable.generate());
        rows.add(review.generate());

        return rows;
    }

    /**
     * Generates rows for the config manager.
     * @return An array list of config row instances.
     */
    private ArrayList<ConfigRowList.Row> getConfigManagementList()
    {
        Button.OnPress onBackup = (button) ->
        {
            AutoConfig.getConfigHolder(ClientConfig.class).backup();
            Util.getPlatform().openFile(PathUtil.getBackupPath().toFile());
        };

        Button.OnPress onOpen = (button) -> Util.getPlatform().openFile(PathUtil.getConfigPath().toFile());

        ControlButton backup = new ControlButton(Component.translatable(LangUtil.Gui.GENERAL_MANAGEMENT_BACKUP), onBackup);
        ControlButton open = new ControlButton(Component.translatable(LangUtil.Gui.GENERAL_MANAGEMENT_OPEN), onOpen);

        TextGroup groupHelp = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_MANAGEMENT_HELP));
        TextGroup maxHelp = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_MANAGEMENT_MAX_HELP));

        ConfigRowBuild.BlankRow blank = new ConfigRowBuild.BlankRow();
        ConfigRowBuild.SingleCenteredRow backupRow = new ConfigRowBuild.SingleCenteredRow(backup);
        ConfigRowBuild.SingleCenteredRow openRow = new ConfigRowBuild.SingleCenteredRow(open);

        ArrayList<ConfigRowList.Row> rows = new ArrayList<>(groupHelp.generate());
        ConfigRowList list = this.parent.getWidgets().getConfigRowList();

        rows.add(openRow.generate());
        rows.add(backupRow.generate());
        rows.add(blank.generate());
        rows.addAll(maxHelp.generate());
        rows.add(list.rowFromTweak(TweakClientCache.get(GuiTweak.MAXIMUM_BACKUPS)));

        return rows;
    }

    /**
     * Generates rows for key binding options in the general options group.
     * @return An array list of config row instances.
     */
    private ArrayList<ConfigRowList.Row> getGeneralBindingsList()
    {
        ArrayList<ConfigRowList.Row> rows = new ArrayList<>();

        KeyUtil.find(LangUtil.Key.OPEN_CONFIG).ifPresent(mapping -> rows.add(new ConfigRowBuild.BindingRow(mapping).generate()));
        KeyUtil.find(LangUtil.Key.TOGGLE_FOG).ifPresent(mapping -> rows.add(new ConfigRowBuild.BindingRow(mapping).generate()));

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
            ArrayList<ConfigRowList.Row> rows = new ArrayList<>(radioHelp.generate());

            rows.addAll(radioGroup.getRows());

            return rows;
        };

        ConfigRowGroup.ContainerRow screenConfig = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_CONFIG_SCREEN_TITLE),
            getScreenOptions,
            ContainerId.DEFAULT_SCREEN_CONFIG,
            ConfigRowGroup.ContainerType.SUBCATEGORY
        );

        subcategories.add(screenConfig.generate());

        /* Tree Indent Options */

        Supplier<ArrayList<ConfigRowList.Row>> getTreeOptions = () ->
        {
            TextGroup treeHelp = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TREE_INFO));
            ArrayList<ConfigRowList.Row> rows = new ArrayList<>(treeHelp.generate());

            TweakClientCache<Boolean> tree = TweakClientCache.get(GuiTweak.DISPLAY_CATEGORY_TREE);
            rows.add(new ConfigRowTweak.BooleanRow(TweakGroup.GUI, tree.getKey(), tree.getValue()).generate());

            TweakClientCache<String> color = TweakClientCache.get(GuiTweak.CATEGORY_TREE_COLOR);
            rows.add(new ConfigRowTweak.ColorRow(TweakGroup.GUI, color.getKey(), color.getValue()).generate());

            return rows;
        };

        ConfigRowGroup.ContainerRow treeConfig = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TREE_TITLE),
            getTreeOptions,
            ContainerId.TREE_CONFIG,
            ConfigRowGroup.ContainerType.SUBCATEGORY
        );

        subcategories.add(treeConfig.generate());

        /* Row Highlighting Options */

        Supplier<ArrayList<ConfigRowList.Row>> getHighlightOptions = () ->
        {
            TextGroup rowHelp = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_CONFIG_ROW_INFO));
            ArrayList<ConfigRowList.Row> rows = new ArrayList<>(rowHelp.generate());

            TweakClientCache<Boolean> highlight = TweakClientCache.get(GuiTweak.DISPLAY_ROW_HIGHLIGHT);
            rows.add(new ConfigRowTweak.BooleanRow(TweakGroup.GUI, highlight.getKey(), highlight.getValue()).generate());

            TweakClientCache<Boolean> fade = TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_FADE);
            rows.add(new ConfigRowTweak.BooleanRow(TweakGroup.GUI, fade.getKey(), fade.getValue()).generate());

            TweakClientCache<String> color = TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_COLOR);
            rows.add(new ConfigRowTweak.ColorRow(TweakGroup.GUI, color.getKey(), color.getValue()).generate());

            return rows;
        };

        ConfigRowGroup.ContainerRow highlightConfig = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_CONFIG_ROW_TITLE),
            getHighlightOptions,
            ContainerId.ROW_CONFIG,
            ConfigRowGroup.ContainerType.SUBCATEGORY
        );

        subcategories.add(highlightConfig.generate());

        /* Tag Options */

        Supplier<ArrayList<ConfigRowList.Row>> getTaggingOptions = () ->
        {
            TextGroup tagHelp = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TAGS_INFO));
            ArrayList<ConfigRowList.Row> rows = new ArrayList<>(tagHelp.generate());

            // New Tags

            TweakClientCache<Boolean> displayNewTags = TweakClientCache.get(GuiTweak.DISPLAY_NEW_TAGS);
            ToggleCheckbox newTagsCheckbox = new ToggleCheckbox
            (
                Component.translatable(LangUtil.Gui.GENERAL_CONFIG_NEW_TAGS_LABEL),
                displayNewTags::getValue,
                displayNewTags::setValue
            );

            ConfigRowBuild.ManualRow newTagsRow = new ConfigRowBuild.ManualRow(List.of(newTagsCheckbox));
            rows.add(newTagsRow.generate());

            // Sided tags

            TweakClientCache<Boolean> displaySidedTags = TweakClientCache.get(GuiTweak.DISPLAY_SIDED_TAGS);
            ToggleCheckbox sidedTagsCheckbox = new ToggleCheckbox
            (
                Component.translatable(LangUtil.Gui.GENERAL_CONFIG_SIDED_TAGS_LABEL),
                displaySidedTags::getValue,
                displaySidedTags::setValue
            );

            ConfigRowBuild.ManualRow sidedTagsRow = new ConfigRowBuild.ManualRow(List.of(sidedTagsCheckbox));
            rows.add(sidedTagsRow.generate());

            // Tag Tooltips

            TweakClientCache<Boolean> displayTagTooltips = TweakClientCache.get(GuiTweak.DISPLAY_TAG_TOOLTIPS);
            ToggleCheckbox tagTooltipsCheckbox = new ToggleCheckbox
            (
                Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TAG_TOOLTIPS_LABEL),
                displayTagTooltips::getValue,
                displayTagTooltips::setValue
            );

            ConfigRowBuild.ManualRow tagTooltipsRow = new ConfigRowBuild.ManualRow(List.of(tagTooltipsCheckbox));

            rows.add(tagTooltipsRow.generate());
            rows.add(new ConfigRowBuild.BlankRow().generate());

            // Feature Status

            TextGroup statusHelp = new TextGroup(Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TWEAK_STATUS_HELP));
            rows.addAll(statusHelp.generate());

            TweakClientCache<Boolean> displayFeatureStatus = TweakClientCache.get(GuiTweak.DISPLAY_FEATURE_STATUS);
            ToggleCheckbox featureStatusCheckbox = new ToggleCheckbox
            (
                Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TWEAK_STATUS_LABEL),
                displayFeatureStatus::getValue,
                displayFeatureStatus::setValue
            );

            ConfigRowBuild.ManualRow featureStatusRow = new ConfigRowBuild.ManualRow(List.of(featureStatusCheckbox));
            rows.add(featureStatusRow.generate());

            return rows;
        };

        ConfigRowGroup.ContainerRow taggingConfig = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TAGS_TITLE),
            getTaggingOptions,
            ContainerId.TITLE_TAGS_CONFIG,
            ConfigRowGroup.ContainerType.SUBCATEGORY
        );

        subcategories.add(taggingConfig.generate());

        return subcategories;
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

        return new TextGroup(TextUtil.combine(tags)).generate();
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

        return new TextGroup(TextUtil.combine(new Component[] { help, find, save, exit, jump, all, group })).generate();
    }

    /**
     * Instructions for manually creating rows and containers for the general configuration group.
     * No logic is used here to automatically generate rows or containers.
     */
    private void addGeneral()
    {
        ConfigRowList list = this.parent.getWidgets().getConfigRowList();

        /* Mod Enabled */

        TweakClientCache<Boolean> isModEnabled = TweakClientCache.get(TweakGroup.ROOT, ClientConfig.ROOT_KEY);

        list.addRow(new ConfigRowTweak.BooleanRow(TweakGroup.ROOT, isModEnabled.getKey(), isModEnabled.getValue()).generate());

        /* SSO Information */

        if (NostalgicTweaks.getConnection().isPresent() && NetUtil.isPlayerOp() && !NetUtil.isSingleplayer())
            list.addRow(new ConfigRowBuild.SingleCenteredRow(new ControlButton(Component.translatable(LangUtil.Gui.SSO_BUTTON), (button) -> new ServerSideModeOverlay())).generate());

        /* Config Manager */

        ConfigRowGroup.ContainerRow configManagement = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_MANAGEMENT_TITLE),
            this::getConfigManagementList,
            ContainerId.CONFIG_MANAGEMENT
        );

        list.addRow(configManagement.generate());

        /* All Tweak Overrides */

        ConfigRowGroup.ContainerRow changeAllTweaks = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_OVERRIDE_TITLE),
            this::getGeneralOverrideList,
            ContainerId.OVERRIDE_CONFIG
        );

        list.addRow(changeAllTweaks.generate());

        /* Key Bindings */

        ConfigRowGroup.ContainerRow changeKeyBinds = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_BINDINGS),
            this::getGeneralBindingsList,
            ContainerId.BINDINGS_CONFIG
        );

        list.addRow(changeKeyBinds.generate());

        /* Menu Settings */

        ConfigRowGroup.ContainerRow changeSettings = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_CONFIG_TITLE),
            this::getGeneralSettingsList,
            ContainerId.GENERAL_CONFIG
        );

        list.addRow(changeSettings.generate());

        /* Search Tags */

        ConfigRowGroup.ContainerRow searchTags = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_SEARCH_TITLE),
            this::getGeneralSearchTags,
            ContainerId.SEARCH_TAGS_CONFIG
        );

        list.addRow(searchTags.generate());

        /* Keyboard Shortcuts */

        ConfigRowGroup.ContainerRow shortcuts = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.GENERAL_SHORTCUT_TITLE),
            this::getGeneralShortcuts,
            ContainerId.SHORTCUTS_CONFIG
        );

        list.addRow(shortcuts.generate());
    }

    /**
     * Used by the "all" group tab which displays a list of every tweak in the mod.
     * Any addition group types will need to be added here.
     */
    public void generateRowsFromAllGroups()
    {
        addRows(TweakGroup.SOUND);
        addRows(TweakGroup.CANDY);
        addRows(TweakGroup.GAMEPLAY);
        addRows(TweakGroup.ANIMATION);
        addRows(TweakGroup.SWING);
    }

    /**
     * Generates config row lists based on the current config tab and renders special effects based on config tab.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick A change in frame time.
     */
    public void generateAndRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.parent.getConfigTab() == ConfigScreen.ConfigTab.SWING)
        {
            this.parent.getWidgets().getSwingSpeedPrefix().render(graphics, mouseX, mouseY, partialTick);

            ListMapScreen<Integer> leftSpeedsScreen = new ListMapScreen<>
            (
                Component.translatable(LangUtil.Gui.LEFT_SPEEDS),
                ConfigList.LEFT_CLICK_SPEEDS
            );

            ConfigRowBuild.SingleCenteredRow leftSpeedsButton = new ConfigRowBuild.SingleCenteredRow
            (
                new ControlButton
                (
                    Component.translatable(LangUtil.Gui.LEFT_SPEEDS),
                    (button) -> this.parent.getMinecraft().setScreen(leftSpeedsScreen)
                )
            );

            this.parent.getWidgets().getConfigRowList().addRow(leftSpeedsButton.generate());

            ListMapScreen<Integer> rightSpeedsScreen = new ListMapScreen<>
            (
                Component.translatable(LangUtil.Gui.RIGHT_SPEEDS),
                ConfigList.RIGHT_CLICK_SPEEDS
            );

            ConfigRowBuild.SingleCenteredRow rightSpeedsButton = new ConfigRowBuild.SingleCenteredRow
            (
                new ControlButton
                (
                    Component.translatable(LangUtil.Gui.RIGHT_SPEEDS),
                    (button) -> this.parent.getMinecraft().setScreen(rightSpeedsScreen)
                )
            );

            this.parent.getWidgets().getConfigRowList().addRow(rightSpeedsButton.generate());
        }
        else if (this.parent.getConfigTab() == ConfigScreen.ConfigTab.SEARCH && this.parent.search.isEmpty())
        {
            String[] words = this.parent.getWidgets().getSearchInput().getValue().split(" ");
            String first = ArrayUtil.get(words, 0);

            boolean isInvalidTag = this.parent.getWidgets().getSearchInput().getValue().startsWith("#");

            if (first != null)
            {
                for (ConfigWidgets.SearchTag tag : ConfigWidgets.SearchTag.values())
                {
                    if (tag.toString().equals(first.replaceAll("#", "")))
                        isInvalidTag = false;
                }
            }

            if (isInvalidTag)
                this.parent.renderLast.add(() -> graphics.drawCenteredString
                (
                    this.parent.getFont(),
                    Component.translatable(LangUtil.Gui.SEARCH_INVALID, this.parent.getWidgets().getSearchInput().getValue()),
                    this.parent.width / 2,
                    this.parent.height / 2,
                    0xFFFFFF
                ));
            else
            {
                this.parent.renderLast.add(() -> graphics.drawCenteredString
                (
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
            case SOUND -> addRows(TweakGroup.SOUND);
            case CANDY -> addRows(TweakGroup.CANDY);
            case GAMEPLAY -> addRows(TweakGroup.GAMEPLAY);
            case ANIMATION -> addRows(TweakGroup.ANIMATION);
            case SWING -> addRows(TweakGroup.SWING);
            case SEARCH -> addFound();
        }
    }
}
