package mod.adrenix.nostalgic.client.config.gui.screen.config;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.screen.CustomizeScreen;
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
import mod.adrenix.nostalgic.common.config.tweak.IDisableTweak;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public record ConfigRenderer(ConfigScreen parent)
{
    private static void sort
    (
        TweakClientCache<?> entry,
        TweakClient.Gui.Placement placement,
        HashMap<String, TweakClientCache<?>> translated,
        HashMap<Integer, TweakClientCache<?>> top,
        HashMap<Integer, TweakClientCache<?>> bottom
    )
    {
        if (placement == null)
            translated.put(entry.getTranslation(), entry);
        else
        {
            if (entry.getPosition() == TweakClient.Gui.Position.TOP)
                top.put(entry.getOrder(), entry);
            else if (entry.getPosition() == TweakClient.Gui.Position.BOTTOM)
                bottom.put(entry.getOrder(), entry);
        }
    }

    private static Supplier<ArrayList<ConfigRowList.Row>> getChildren
    (
        ConfigRowList list,
        @Nullable TweakClient.Category category,
        @Nullable TweakClient.Subcategory subcategory,
        @Nullable TweakClient.Embedded embedded
    )
    {
        return () ->
        {
            ArrayList<ConfigRowList.Row> rows = new ArrayList<>();

            HashMap<String, TweakClientCache<?>> translated = new HashMap<>();
            HashMap<Integer, TweakClientCache<?>> bottom = new HashMap<>();
            HashMap<Integer, TweakClientCache<?>> top = new HashMap<>();

            Set<TweakClient.Subcategory> subcategories = new HashSet<>();
            Set<TweakClient.Embedded> embeds = new HashSet<>();

            TweakClientCache.all().forEach((key, entry) -> {
                TweakClient.Gui.Cat cat = CommonReflect.getAnnotation(entry, TweakClient.Gui.Cat.class);
                TweakClient.Gui.Sub sub = CommonReflect.getAnnotation(entry, TweakClient.Gui.Sub.class);
                TweakClient.Gui.Emb emb = CommonReflect.getAnnotation(entry, TweakClient.Gui.Emb.class);
                TweakClient.Gui.Placement placement = CommonReflect.getAnnotation(entry, TweakClient.Gui.Placement.class);

                if (category != null)
                {
                    boolean isCategory = cat != null && cat.group() == category && entry.getGroup() == category.getGroup();
                    boolean isSubcategory = sub != null && !subcategories.contains(sub.group()) && sub.group().getCategory() == category && entry.getGroup() == category.getGroup();

                    if (isCategory)
                        sort(entry, placement, translated, top, bottom);
                    else if (isSubcategory)
                        subcategories.add(sub.group());
                }
                else if (subcategory != null)
                {
                    boolean isSubcategory = sub != null && sub.group() == subcategory && entry.getGroup() == subcategory.getCategory().getGroup();
                    boolean isEmbed = emb != null && !embeds.contains(emb.group()) && emb.group().getSubcategory() == subcategory && entry.getGroup() == subcategory.getCategory().getGroup();

                    if (isSubcategory)
                        sort(entry, placement, translated, top, bottom);
                    else if (isEmbed)
                        embeds.add(emb.group());
                }
                else if (embedded != null)
                {
                    if (emb != null && emb.group() == embedded && entry.getGroup() == embedded.getSubcategory().getCategory().getGroup())
                        sort(entry, placement, translated, top, bottom);
                }
            });

            EnumSet<TweakClient.Subcategory> allSubs = EnumSet.allOf(TweakClient.Subcategory.class);
            EnumSet<TweakClient.Embedded> allEmbeds = EnumSet.allOf(TweakClient.Embedded.class);

            allSubs.forEach((sub) -> {
                if (subcategories.contains(sub))
                    rows.add(getSubcategory(sub, list).add());
            });

            allEmbeds.forEach((embed) -> {
                if (embeds.contains(embed))
                    rows.add(getEmbedded(embed, list).add());
            });

            SortedMap<Integer, TweakClientCache<?>> sortTop = new TreeMap<>(top);
            SortedMap<String, TweakClientCache<?>> sortMiddle = new TreeMap<>(translated);
            SortedMap<Integer, TweakClientCache<?>> sortBottom = new TreeMap<>(bottom);

            sortTop.forEach((key, tweak) -> rows.add(list.getRow(tweak.getGroup(), tweak.getKey(), tweak.getCurrent())));
            sortMiddle.forEach((key, tweak) -> rows.add(list.getRow(tweak.getGroup(), tweak.getKey(), tweak.getCurrent())));
            sortBottom.forEach((key, tweak) -> rows.add(list.getRow(tweak.getGroup(), tweak.getKey(), tweak.getCurrent())));

            return rows;
        };
    }

    private static ConfigRowList.CategoryRow getSubcategory(TweakClient.Subcategory subcategory, ConfigRowList list)
    {
        return new ConfigRowList.CategoryRow
        (
            list,
            Component.translatable(subcategory.getLangKey()),
            getChildren(list, null, subcategory, null),
            subcategory,
            ConfigRowList.CatType.SUBCATEGORY
        );
    }

    private static ConfigRowList.CategoryRow getCategory(TweakClient.Category category, ConfigRowList list)
    {
        return new ConfigRowList.CategoryRow
        (
            list,
            Component.translatable(category.getLangKey()),
            getChildren(list, category, null, null),
            category
        );
    }

    private static ConfigRowList.CategoryRow getEmbedded(TweakClient.Embedded embedded, ConfigRowList list)
    {
        return new ConfigRowList.CategoryRow
        (
            list,
            Component.translatable(embedded.getLangKey()),
            getChildren(list, null, null, embedded),
            embedded,
            ConfigRowList.CatType.EMBEDDED
        );
    }

    private static List<ConfigRowList.Row> getCategories(ConfigRowList list, GroupType group)
    {
        List<ConfigRowList.Row> rows = new ArrayList<>();

        EnumSet<TweakClient.Category> categories = EnumSet.allOf(TweakClient.Category.class);
        categories.forEach((category) -> {
            if (category.getGroup() == group)
                rows.add(getCategory(category, list).add());
        });

        return rows;
    }

    private void addRows(GroupType group)
    {
        getCategories(this.parent.getWidgets().getConfigRowList(), group).forEach((row) -> this.parent.getWidgets().getConfigRowList().addRow(row));

        Comparator<String> translationComparator = Comparator.comparing((String key) -> TweakClientCache.get(group, key).getTranslation());
        Comparator<String> orderComparator = Comparator.comparing((String key) -> TweakClientCache.get(group, key).getOrder());

        HashMap<String, Object> top = new HashMap<>();
        HashMap<String, Object> middle = new HashMap<>();
        HashMap<String, Object> bottom = new HashMap<>();
        HashMap<String, Object> all = new HashMap<>(ClientReflect.getGroup(group));

        all.forEach((key, value) -> {
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

        sortTop.forEach((key, value) -> this.parent.getWidgets().getConfigRowList().addRow(group, key, value));
        sortMiddle.forEach((key, value) -> this.parent.getWidgets().getConfigRowList().addRow(group, key, value));
        sortBottom.forEach((key, value) -> this.parent.getWidgets().getConfigRowList().addRow(group, key, value));
    }

    private void addSearchRows(TweakClientCache<?> tweak)
    {
        TextWidget text = new TextWidget(ConfigRowList.TEXT_START, tweak.getSearchGroup());
        ConfigRowList list = this.parent.getWidgets().getConfigRowList();
        ConfigRowList.Row row = list.getRow(tweak.getGroup(), tweak.getKey(), tweak.getCurrent());

        row.children.add(text);

        this.parent.getWidgets().getConfigRowList().addRow(row);
    }

    private void addFound()
    {
        if (this.parent.search.isEmpty())
            return;

        String[] words = this.parent.getWidgets().getSearchInput().getValue().split(" ");
        String first = NostalgicUtil.Array.get(words, 0);

        boolean isTagOnly = first != null && first.startsWith("@") && words.length == 1;

        Map<String, TweakClientCache<?>> found = this.parent.search;
        Map<String, TweakClientCache<?>> sorted = isTagOnly ?
            new TreeMap<>(Comparator.comparing(key -> found.get(key).getTranslation())) :
            new TreeMap<>((firstKey, secondKey) -> TweakClientCache.compareWeights(found.get(secondKey).getWeight(), found.get(firstKey).getWeight()))
        ;

        sorted.putAll(found);
        sorted.forEach((key, tweak) -> this.addSearchRows(tweak));
    }

    private void addGeneral()
    {
        ConfigRowList list = this.parent.getWidgets().getConfigRowList();

        /* Mod Enabled */

        TweakClientCache<Boolean> isModEnabled = TweakClientCache.get(GroupType.ROOT, ClientConfig.ROOT_KEY);
        list.addRow(new ConfigRowList.BooleanRow(GroupType.ROOT, isModEnabled.getKey(), isModEnabled.getCurrent()).add());

        /* Key Bindings */

        Supplier<ArrayList<ConfigRowList.Row>> bindings = () -> {
            ArrayList<ConfigRowList.Row> rows = new ArrayList<>();

            KeyMapping openConfig = KeyUtil.find(NostalgicLang.Key.OPEN_CONFIG);
            KeyMapping toggleFog = KeyUtil.find(NostalgicLang.Key.TOGGLE_FOG);

            if (openConfig != null)
                rows.add(new ConfigRowList.BindingRow(openConfig).add());
            if (toggleFog != null)
                rows.add(new ConfigRowList.BindingRow(toggleFog).add());

            return rows;
        };

        list.addRow(new ConfigRowList.CategoryRow(list, Component.translatable(NostalgicLang.Gui.GENERAL_BINDINGS), bindings, GroupId.BINDINGS_CONFIG).add());

        /* Menu Settings */

        Supplier<ArrayList<ConfigRowList.Row>> settings = () -> {
            ArrayList<ConfigRowList.Row> subcategories = new ArrayList<>();

            // Default Screen Options
            Supplier<ArrayList<ConfigRowList.Row>> defaultScreen = () -> {
                TextGroup menuHelp = new TextGroup(list, Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_SCREEN_INFO));
                TweakClientCache<MenuOption> screenCache = TweakClientCache.get(GuiTweak.DEFAULT_SCREEN);
                RadioGroup<MenuOption> screens = new RadioGroup<>
                (
                    list,
                    MenuOption.class,
                    DefaultConfig.Gui.DEFAULT_SCREEN,
                    screenCache::getCurrent,
                    (option) -> MenuOption.getTranslation((MenuOption) option),
                    (selected) -> screenCache.setCurrent((MenuOption) selected)
                );

                ArrayList<ConfigRowList.Row> rows = new ArrayList<>(menuHelp.getRows());
                rows.addAll(screens.getRows());

                return rows;
            };

            subcategories.add(new ConfigRowList.CategoryRow(list, Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_SCREEN_TITLE), defaultScreen, GroupId.DEFAULT_SCREEN_CONFIG, ConfigRowList.CatType.SUBCATEGORY).add());

            // Tree Indent Options
            Supplier<ArrayList<ConfigRowList.Row>> treeConfig = () -> {
                TextGroup treeHelp = new TextGroup(list, Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_TREE_INFO));
                ArrayList<ConfigRowList.Row> rows = new ArrayList<>(treeHelp.getRows());

                TweakClientCache<Boolean> tree = TweakClientCache.get(GuiTweak.DISPLAY_CATEGORY_TREE);
                rows.add(new ConfigRowList.BooleanRow(GroupType.GUI, tree.getKey(), tree.getCurrent()).add());

                TweakClientCache<String> color = TweakClientCache.get(GuiTweak.CATEGORY_TREE_COLOR);
                rows.add(new ConfigRowList.ColorRow(GroupType.GUI, color.getKey(), color.getCurrent()).add());

                return rows;
            };

            subcategories.add(new ConfigRowList.CategoryRow(list, Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_TREE_TITLE), treeConfig, GroupId.TREE_CONFIG, ConfigRowList.CatType.SUBCATEGORY).add());

            // Row Highlighting Options
            Supplier<ArrayList<ConfigRowList.Row>> rowConfig = () -> {
                TextGroup rowHelp = new TextGroup(list, Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_ROW_INFO));
                ArrayList<ConfigRowList.Row> rows = new ArrayList<>(rowHelp.getRows());

                TweakClientCache<Boolean> highlight = TweakClientCache.get(GuiTweak.DISPLAY_ROW_HIGHLIGHT);
                rows.add(new ConfigRowList.BooleanRow(GroupType.GUI, highlight.getKey(), highlight.getCurrent()).add());

                TweakClientCache<Boolean> fade = TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_FADE);
                rows.add(new ConfigRowList.BooleanRow(GroupType.GUI, fade.getKey(), fade.getCurrent()).add());

                TweakClientCache<String> color = TweakClientCache.get(GuiTweak.ROW_HIGHLIGHT_COLOR);
                rows.add(new ConfigRowList.ColorRow(GroupType.GUI, color.getKey(), color.getCurrent()).add());

                return rows;
            };

            subcategories.add(new ConfigRowList.CategoryRow(list, Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_ROW_TITLE), rowConfig, GroupId.ROW_CONFIG, ConfigRowList.CatType.SUBCATEGORY).add());

            /* Tag Options */
            Supplier<ArrayList<ConfigRowList.Row>> tagging = () -> {
                TextGroup tagHelp = new TextGroup(list, Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_TAGS_INFO));
                ArrayList<ConfigRowList.Row> rows = new ArrayList<>(tagHelp.getRows());

                // New Tags

                TweakClientCache<Boolean> newCache = TweakClientCache.get(GuiTweak.DISPLAY_NEW_TAGS);
                ToggleCheckbox toggleNewTags = new ToggleCheckbox
                (
                    this.parent,
                    Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_NEW_TAGS_LABEL),
                    newCache::getCurrent,
                    newCache::setCurrent
                );

                ConfigRowList.ManualRow displayNewTag = new ConfigRowList.ManualRow(List.of(toggleNewTags));
                rows.add(displayNewTag.add());

                // Sided tags

                TweakClientCache<Boolean> sidedCache = TweakClientCache.get(GuiTweak.DISPLAY_SIDED_TAGS);
                ToggleCheckbox toggleSidedTags = new ToggleCheckbox
                (
                    this.parent,
                    Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_SIDED_TAGS_LABEL),
                    sidedCache::getCurrent,
                    sidedCache::setCurrent
                );

                ConfigRowList.ManualRow displaySidedTag = new ConfigRowList.ManualRow(List.of(toggleSidedTags));
                rows.add(displaySidedTag.add());

                // Tag Tooltips

                TweakClientCache<Boolean> tooltipCache = TweakClientCache.get(GuiTweak.DISPLAY_TAG_TOOLTIPS);
                ToggleCheckbox toggleTagTooltips = new ToggleCheckbox
                (
                    this.parent,
                    Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_TAG_TOOLTIPS_LABEL),
                    tooltipCache::getCurrent,
                    tooltipCache::setCurrent
                );

                ConfigRowList.ManualRow displayTagTooltips = new ConfigRowList.ManualRow(List.of(toggleTagTooltips));
                rows.add(displayTagTooltips.add());
                rows.add(new ConfigRowList.ManualRow(new ArrayList<>()).add());

                // Feature Status

                TextGroup statusHelp = new TextGroup(list, Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_TWEAK_STATUS_HELP));
                rows.addAll(statusHelp.getRows());

                TweakClientCache<Boolean> featureCache = TweakClientCache.get(GuiTweak.DISPLAY_FEATURE_STATUS);
                ToggleCheckbox toggleFeatureStatus = new ToggleCheckbox
                (
                    this.parent,
                    Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_TWEAK_STATUS_LABEL),
                    featureCache::getCurrent,
                    featureCache::setCurrent
                );

                ConfigRowList.ManualRow displayFeatureStatus = new ConfigRowList.ManualRow(List.of(toggleFeatureStatus));
                rows.add(displayFeatureStatus.add());

                return rows;
            };

            subcategories.add(new ConfigRowList.CategoryRow(list, Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_TAGS_TITLE), tagging, GroupId.TITLE_TAGS_CONFIG, ConfigRowList.CatType.SUBCATEGORY).add());

            return subcategories;
        };

        list.addRow(new ConfigRowList.CategoryRow(list, Component.translatable(NostalgicLang.Gui.GENERAL_CONFIG_TITLE), settings, GroupId.GENERAL_CONFIG).add());

        /* Override Config */

        Supplier<ArrayList<ConfigRowList.Row>> globalOptions = () -> {
            TextGroup help = new TextGroup(list, Component.translatable(NostalgicLang.Gui.GENERAL_OVERRIDE_HELP));
            AtomicBoolean serverOnly = new AtomicBoolean(false);

            Button.OnPress onDisable = (button) -> Arrays.stream(GroupType.values()).forEach((group) -> {
                if (!GroupType.isManual(group))
                {
                    ClientReflect.getGroup(group).forEach((key, value) -> {
                        TweakClientCache<Boolean> entry = TweakClientCache.get(group, key);

                        boolean isDisableIgnored = CommonReflect.getAnnotation(entry, TweakClient.Gui.IgnoreDisable.class) != null;
                        boolean isClientIgnored = serverOnly.get() && entry.isClient() && !entry.isDynamic();
                        boolean isLocked = entry.isLocked();
                        boolean isChangeable = !isDisableIgnored && !isLocked && !isClientIgnored;

                        if (!isClientIgnored && !isLocked)
                            entry.reset();

                        if (value instanceof Boolean && isChangeable)
                        {
                            TweakClient.Gui.DisabledBoolean disabledBoolean = CommonReflect.getAnnotation(entry, TweakClient.Gui.DisabledBoolean.class);

                            if (disabledBoolean == null && entry.getDefault())
                            {
                                entry.reset();
                                entry.setCurrent(!entry.getCurrent());
                            }
                            else if (disabledBoolean != null)
                                entry.setCurrent(disabledBoolean.disabled());
                        }

                        if (value instanceof Integer && isChangeable)
                        {
                            TweakClient.Gui.DisabledInteger disabledInteger = CommonReflect.getAnnotation(entry, TweakClient.Gui.DisabledInteger.class);

                            if (disabledInteger != null)
                            {
                                TweakClientCache<Integer> entryInteger = TweakClientCache.get(group, key);
                                entryInteger.setCurrent(disabledInteger.disabled());
                            }
                        }

                        if (value instanceof IDisableTweak<?> && isChangeable)
                        {
                            TweakClientCache<Enum<?>> version = TweakClientCache.get(group, key);
                            version.setCurrent(((IDisableTweak<?>) value).getDisabled());
                        }
                    });
                }
            });

            Button.OnPress onEnable = (button) -> Arrays.stream(GroupType.values()).forEach((group) -> {
                if (!GroupType.isManual(group))
                {
                    ClientReflect.getGroup(group).forEach((key, value) -> {
                        TweakClientCache<?> entry = TweakClientCache.get(group, key);

                        boolean isClientIgnored = serverOnly.get() && entry.isClient() && !entry.isDynamic();
                        boolean isLocked = entry.isLocked();
                        boolean isChangeable = !isLocked && !isClientIgnored;

                        if (isChangeable)
                            entry.reset();
                    });
                }
            });

            ControlButton disableAll = new ControlButton(Component.translatable(NostalgicLang.Gui.GENERAL_OVERRIDE_DISABLE), onDisable);
            ControlButton enableAll = new ControlButton(Component.translatable(NostalgicLang.Gui.GENERAL_OVERRIDE_ENABLE), onEnable);

            ToggleCheckbox toggleServerOnly = new ToggleCheckbox
            (
                this.parent,
                Component.translatable(NostalgicLang.Gui.GENERAL_OVERRIDE_SERVER),
                serverOnly::get,
                serverOnly::set
            );

            toggleServerOnly.setTooltip(Component.translatable(NostalgicLang.Gui.GENERAL_OVERRIDE_SERVER_TIP));

            ConfigRowList.SingleLeftRow server = new ConfigRowList.SingleLeftRow(toggleServerOnly, ConfigRowList.CAT_TEXT_START);
            ConfigRowList.SingleLeftRow disable = new ConfigRowList.SingleLeftRow(disableAll, ConfigRowList.CAT_TEXT_START);
            ConfigRowList.SingleLeftRow enable = new ConfigRowList.SingleLeftRow(enableAll, ConfigRowList.CAT_TEXT_START);

            ArrayList<ConfigRowList.Row> rows = new ArrayList<>(help.getRows());

            rows.add(server.add());
            rows.add(disable.add());
            rows.add(enable.add());

            return rows;
        };

        list.addRow(new ConfigRowList.CategoryRow(list, Component.translatable(NostalgicLang.Gui.GENERAL_OVERRIDE_TITLE), globalOptions, GroupId.OVERRIDE_CONFIG).add());

        /* Notifications */

        Supplier<ArrayList<ConfigRowList.Row>> notifications = () ->
        {
            TextGroup notify = new TextGroup(list, Component.translatable(NostalgicLang.Gui.GENERAL_NOTIFY_CONFLICT, TweakClientCache.getConflicts()));
            return new ArrayList<>(notify.getRows());
        };

        list.addRow(new ConfigRowList.CategoryRow(list, Component.translatable(NostalgicLang.Gui.GENERAL_NOTIFY_TITLE), notifications, GroupId.NOTIFY_CONFIG).add());

        /* Search Tags */

        Supplier<ArrayList<ConfigRowList.Row>> searchTags = () -> {
            Component help = Component.translatable(NostalgicLang.Gui.GENERAL_SEARCH_HELP);
            Component newTag = Component.translatable(NostalgicLang.Gui.GENERAL_SEARCH_NEW);
            Component conflictTag = Component.translatable(NostalgicLang.Gui.GENERAL_SEARCH_CONFLICT);
            Component resetTag = Component.translatable(NostalgicLang.Gui.GENERAL_SEARCH_RESET);
            Component clientTag = Component.translatable(NostalgicLang.Gui.GENERAL_SEARCH_CLIENT);
            Component serverTag = Component.translatable(NostalgicLang.Gui.GENERAL_SEARCH_SERVER);
            Component saveTag = Component.translatable(NostalgicLang.Gui.GENERAL_SEARCH_SAVE);
            Component allTag = Component.translatable(NostalgicLang.Gui.GENERAL_SEARCH_ALL);

            return new TextGroup(list, NostalgicUtil.Text.combine(new Component[] {
                help, newTag, conflictTag, resetTag, clientTag, serverTag, saveTag, allTag
            })).getRows();
        };

        list.addRow(new ConfigRowList.CategoryRow(list, Component.translatable(NostalgicLang.Gui.GENERAL_SEARCH_TITLE), searchTags, GroupId.SEARCH_TAGS_CONFIG).add());

        /* Keyboard Shortcuts */

        Supplier<ArrayList<ConfigRowList.Row>> shortcuts = () -> {
            Component help = Component.translatable(NostalgicLang.Gui.GENERAL_SHORTCUT_HELP);
            Component find = Component.translatable(NostalgicLang.Gui.GENERAL_SHORTCUT_FIND);
            Component save = Component.translatable(NostalgicLang.Gui.GENERAL_SHORTCUT_SAVE);
            Component exit = Component.translatable(NostalgicLang.Gui.GENERAL_SHORTCUT_EXIT);
            Component jump = Component.translatable(NostalgicLang.Gui.GENERAL_SHORTCUT_JUMP);
            Component all = Component.translatable(NostalgicLang.Gui.GENERAL_SHORTCUT_ALL);
            Component group = Component.translatable(NostalgicLang.Gui.GENERAL_SHORTCUT_GROUP);

            return new TextGroup(list, NostalgicUtil.Text.combine(new Component[]{ help, find, save, exit, jump, all, group })).getRows();
        };

        list.addRow(new ConfigRowList.CategoryRow(list, Component.translatable(NostalgicLang.Gui.GENERAL_SHORTCUT_TITLE), shortcuts, GroupId.SHORTCUTS_CONFIG).add());
    }

    public void generateAllList()
    {
        addRows(GroupType.SOUND);
        addRows(GroupType.CANDY);
        addRows(GroupType.GAMEPLAY);
        addRows(GroupType.ANIMATION);
        addRows(GroupType.SWING);
    }

    public void generateGroupList(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (this.parent.getConfigTab() == ConfigScreen.ConfigTab.SWING)
        {
            this.parent.getWidgets().getSwingSpeedPrefix().render(poseStack, mouseX, mouseY, partialTick);

            ConfigRowList.SingleCenteredRow custom = new ConfigRowList.SingleCenteredRow
            (
                this.parent,
                new ControlButton
                (
                    Component.translatable(NostalgicLang.Gui.CUSTOMIZE),
                    (button) -> this.parent.getMinecraft().setScreen(new CustomizeScreen(this.parent))
                )
            );

            this.parent.getWidgets().getConfigRowList().addRow(custom.add());
        }
        else if (this.parent.getConfigTab() == ConfigScreen.ConfigTab.SEARCH && this.parent.search.isEmpty())
        {
            String[] words = this.parent.getWidgets().getSearchInput().getValue().split(" ");
            String first = NostalgicUtil.Array.get(words, 0);

            boolean isInvalidTag = this.parent.getWidgets().getSearchInput().getValue().startsWith("@");

            if (first != null)
            {
                for (ConfigScreen.SearchTag tag : ConfigScreen.SearchTag.values())
                {
                    if (tag.toString().equals(first.replaceAll("@", "")))
                        isInvalidTag = false;
                }
            }

            if (isInvalidTag)
                this.parent.renderLast.add(() -> Screen.drawCenteredString(
                    poseStack,
                    this.parent.getFont(),
                    Component.translatable(NostalgicLang.Gui.SEARCH_INVALID, this.parent.getWidgets().getSearchInput().getValue()),
                    this.parent.width / 2,
                    this.parent.height / 2,
                    0xFFFFFF
                ));
            else
            {
                this.parent.renderLast.add(() -> Screen.drawCenteredString(
                    poseStack,
                    this.parent.getFont(),
                    Component.translatable(NostalgicLang.Gui.SEARCH_EMPTY),
                    this.parent.width / 2,
                    this.parent.height / 2,
                    0xFFFFFF
                ));
            }
        }

        switch (this.parent.getConfigTab())
        {
            case GENERAL -> addGeneral();
            case SOUND -> addRows(GroupType.SOUND);
            case CANDY -> addRows(GroupType.CANDY);
            case GAMEPLAY -> addRows(GroupType.GAMEPLAY);
            case ANIMATION -> addRows(GroupType.ANIMATION);
            case SWING -> addRows(GroupType.SWING);
            case SEARCH -> addFound();
            case ALL -> generateAllList();
        }
    }
}
