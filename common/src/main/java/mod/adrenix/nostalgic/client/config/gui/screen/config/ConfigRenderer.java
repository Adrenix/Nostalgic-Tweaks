package mod.adrenix.nostalgic.client.config.gui.screen.config;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.DefaultConfig;
import mod.adrenix.nostalgic.client.config.annotation.NostalgicEntry;
import mod.adrenix.nostalgic.client.config.feature.GuiFeature;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.*;
import mod.adrenix.nostalgic.client.config.reflect.*;
import mod.adrenix.nostalgic.util.KeyUtil;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.*;
import java.util.function.Supplier;

public record ConfigRenderer(ConfigScreen parent)
{
    private void addRows(GroupType group)
    {
        NostalgicEntry.Category.getCategories(this.parent.getWidgets().getConfigRowList(), group).forEach((row) -> this.parent.getWidgets().getConfigRowList().addRow(row));

        Comparator<String> comparator = Comparator.comparing((String key) -> new TranslatableComponent(EntryCache.get(group, key)
            .getLangKey())
            .getString()
        );

        SortedMap<String, Object> sorted = new TreeMap<>(comparator);
        sorted.putAll(ConfigReflect.getGroup(group));
        sorted.forEach((key, value) -> {
            if (ConfigReflect.getAnnotation(group, key, NostalgicEntry.Gui.Sub.class) == null)
                this.parent.getWidgets().getConfigRowList().addRow(group, key, value);
        });
    }

    private void addFound()
    {
        if (this.parent.search.isEmpty()) return;
        Comparator<EntryCache<?>> comparator = Comparator.comparing((EntryCache<?> entry) -> new TranslatableComponent(entry.getLangKey()).getString());
        SortedSet<EntryCache<?>> sorted = new TreeSet<>(comparator);
        sorted.addAll(this.parent.search);
        sorted.forEach((entry) -> this.parent.getWidgets().getConfigRowList().addRow(entry.getGroup(), entry.getEntryKey(), entry.getCurrent()));
    }

    private void addGeneral()
    {
        ConfigRowList list = this.parent.getWidgets().getConfigRowList();

        /* Mod Enabled */

        EntryCache<Boolean> isModEnabled = EntryCache.get(GroupType.ROOT, ClientConfig.ROOT_KEY);
        list.addRow(new ConfigRowList.BooleanRow(GroupType.ROOT, isModEnabled.getEntryKey(), isModEnabled.getCurrent()).add());

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

        list.addRow(new ConfigRowList.CategoryRow(list, new TranslatableComponent(NostalgicLang.Gui.GENERAL_BINDINGS), bindings).add());

        /* Menu Settings */

        Supplier<ArrayList<ConfigRowList.Row>> settings = () -> {

            // Default Screen Options

            TextGroup menuHelp = new TextGroup(list, new TranslatableComponent(NostalgicLang.Gui.GENERAL_CONFIG_HELP_MENU));
            EntryCache<SettingsScreen.OptionScreen> screenCache = EntryCache.get(GroupType.GUI, GuiFeature.DEFAULT_SCREEN.getKey());
            RadioGroup<SettingsScreen.OptionScreen> screens = new RadioGroup<>(
                list,
                SettingsScreen.OptionScreen.class,
                DefaultConfig.Gui.DEFAULT_SCREEN,
                screenCache::getCurrent,
                (option) -> SettingsScreen.OptionScreen.getTranslation((SettingsScreen.OptionScreen) option),
                (selected) -> screenCache.setCurrent((SettingsScreen.OptionScreen) selected)
            );

            ArrayList<ConfigRowList.Row> rows = new ArrayList<>(menuHelp.getRows());
            rows.addAll(screens.getRows());
            rows.add(new ConfigRowList.ManualRow(new ArrayList<>()).add());

            // Tag Options

            TextGroup tagHelp = new TextGroup(list, new TranslatableComponent(NostalgicLang.Gui.GENERAL_CONFIG_HELP_TAGS));
            rows.addAll(tagHelp.getRows());

            // New Tags

            EntryCache<Boolean> newCache = EntryCache.get(GroupType.GUI, GuiFeature.DISPLAY_NEW_TAGS.getKey());
            ToggleCheckbox toggleNewTags = new ToggleCheckbox(
                this.parent,
                new TranslatableComponent(NostalgicLang.Gui.GENERAL_CONFIG_NEW_TAGS_LABEL),
                newCache::getCurrent,
                newCache::setCurrent
            );

            ConfigRowList.ManualRow displayNewTag = new ConfigRowList.ManualRow(List.of(toggleNewTags));
            rows.add(displayNewTag.add());

            // Sided tags

            EntryCache<Boolean> sidedCache = EntryCache.get(GroupType.GUI, GuiFeature.DISPLAY_SIDED_TAGS.getKey());
            ToggleCheckbox toggleSidedTags = new ToggleCheckbox(
                this.parent,
                new TranslatableComponent(NostalgicLang.Gui.GENERAL_CONFIG_SIDED_TAGS_LABEL),
                sidedCache::getCurrent,
                sidedCache::setCurrent
            );

            ConfigRowList.ManualRow displaySidedTag = new ConfigRowList.ManualRow(List.of(toggleSidedTags));
            rows.add(displaySidedTag.add());

            // Tag Tooltips

            EntryCache<Boolean> tooltipCache = EntryCache.get(GroupType.GUI, GuiFeature.DISPLAY_TAG_TOOLTIPS.getKey());
            ToggleCheckbox toggleTagTooltips = new ToggleCheckbox(
                this.parent,
                new TranslatableComponent(NostalgicLang.Gui.GENERAL_CONFIG_TAG_TOOLTIPS_LABEL),
                tooltipCache::getCurrent,
                tooltipCache::setCurrent
            );

            ConfigRowList.ManualRow displayTagTooltips = new ConfigRowList.ManualRow(List.of(toggleTagTooltips));
            rows.add(displayTagTooltips.add());
            rows.add(new ConfigRowList.ManualRow(new ArrayList<>()).add());

            // Feature Status

            TextGroup statusHelp = new TextGroup(list, new TranslatableComponent(NostalgicLang.Gui.GENERAL_CONFIG_TWEAK_STATUS_HELP));
            rows.addAll(statusHelp.getRows());

            EntryCache<Boolean> featureCache = EntryCache.get(GroupType.GUI, GuiFeature.DISPLAY_FEATURE_STATUS.getKey());
            ToggleCheckbox toggleFeatureStatus = new ToggleCheckbox(
                this.parent,
                new TranslatableComponent(NostalgicLang.Gui.GENERAL_CONFIG_TWEAK_STATUS_LABEL),
                featureCache::getCurrent,
                featureCache::setCurrent
            );

            ConfigRowList.ManualRow displayFeatureStatus = new ConfigRowList.ManualRow(List.of(toggleFeatureStatus));
            rows.add(displayFeatureStatus.add());

            return rows;
        };

        list.addRow(new ConfigRowList.CategoryRow(list, new TranslatableComponent(NostalgicLang.Gui.GENERAL_CONFIG_TITLE), settings).add());

        /* Override Config */

        Supplier<ArrayList<ConfigRowList.Row>> globalOptions = () -> {
            TextGroup help = new TextGroup(list, new TranslatableComponent(NostalgicLang.Gui.GENERAL_OVERRIDE_HELP));

            ConfigRowList.SingleCenteredRow disable = new ConfigRowList.SingleCenteredRow(
                this.parent,
                new TranslatableComponent(NostalgicLang.Gui.GENERAL_OVERRIDE_DISABLE),
                (button) -> Arrays.stream(GroupType.values()).forEach((group) -> {
                    if (!GroupType.isManual(group))
                    {
                        ConfigReflect.getGroup(group).forEach((key, value) -> {
                            EntryCache<Boolean> entry = EntryCache.get(group, key);

                            boolean isDisableIgnored = ConfigReflect.getAnnotation(
                                entry.getGroup(),
                                entry.getEntryKey(),
                                NostalgicEntry.Gui.IgnoreDisable.class
                            ) != null;

                            if (value instanceof Boolean && !isDisableIgnored)
                            {
                                entry.reset();
                                entry.setCurrent(!entry.getCurrent());
                            }

                            if (value instanceof Integer && !isDisableIgnored)
                            {
                                NostalgicEntry.Gui.DisabledInteger disabledInteger = ConfigReflect.getAnnotation(
                                    entry.getGroup(),
                                    entry.getEntryKey(),
                                    NostalgicEntry.Gui.DisabledInteger.class
                                );

                                if (disabledInteger != null)
                                {
                                    EntryCache<Integer> entryInteger = EntryCache.get(group, key);
                                    entryInteger.setCurrent(disabledInteger.disabled());
                                }
                            }

                            if (value instanceof DefaultConfig.VERSION && !isDisableIgnored)
                            {
                                EntryCache<DefaultConfig.VERSION> version = EntryCache.get(group, key);
                                version.setCurrent(DefaultConfig.VERSION.MODERN);
                            }
                        });
                    }
                })
            );

            ConfigRowList.SingleCenteredRow enable = new ConfigRowList.SingleCenteredRow(
                this.parent,
                new TranslatableComponent(NostalgicLang.Gui.GENERAL_OVERRIDE_ENABLE),
                (button) -> Arrays.stream(GroupType.values()).forEach((group) -> {
                    if (!GroupType.isManual(group))
                        ConfigReflect.getGroup(group).forEach((key, value) -> EntryCache.get(group, key).reset());
                })
            );

            ArrayList<ConfigRowList.Row> rows = new ArrayList<>(help.getRows());
            rows.add(disable.add());
            rows.add(enable.add());

            return rows;
        };

        list.addRow(new ConfigRowList.CategoryRow(list, new TranslatableComponent(NostalgicLang.Gui.GENERAL_OVERRIDE_TITLE), globalOptions).add());

        /* Notifications */

        Supplier<ArrayList<ConfigRowList.Row>> notifications = () ->
        {
            TextGroup notify = new TextGroup(list, new TranslatableComponent(NostalgicLang.Gui.GENERAL_NOTIFY_CONFLICT, EntryCache.getConflicts()));
            return new ArrayList<>(notify.getRows());
        };

        list.addRow(new ConfigRowList.CategoryRow(list, new TranslatableComponent(NostalgicLang.Gui.GENERAL_NOTIFY_TITLE), notifications).add());

        /* Search Tags */

        Supplier<ArrayList<ConfigRowList.Row>> searchTags = () -> {
            Component help = new TranslatableComponent(NostalgicLang.Gui.GENERAL_SEARCH_HELP);
            Component newTag = new TranslatableComponent(NostalgicLang.Gui.GENERAL_SEARCH_NEW);
            Component conflictTag = new TranslatableComponent(NostalgicLang.Gui.GENERAL_SEARCH_CONFLICT);
            Component resetTag = new TranslatableComponent(NostalgicLang.Gui.GENERAL_SEARCH_RESET);
            Component clientTag = new TranslatableComponent(NostalgicLang.Gui.GENERAL_SEARCH_CLIENT);
            Component serverTag = new TranslatableComponent(NostalgicLang.Gui.GENERAL_SEARCH_SERVER);

            return new TextGroup(list, NostalgicUtil.Text.combine(new Component[] {
                help, newTag, conflictTag, resetTag, clientTag, serverTag
            })).getRows();
        };

        list.addRow(new ConfigRowList.CategoryRow(list, new TranslatableComponent(NostalgicLang.Gui.GENERAL_SEARCH_TITLE), searchTags).add());

        /* Keyboard Shortcuts */

        Supplier<ArrayList<ConfigRowList.Row>> shortcuts = () -> {
            Component help = new TranslatableComponent(NostalgicLang.Gui.GENERAL_SHORTCUT_HELP);
            Component find = new TranslatableComponent(NostalgicLang.Gui.GENERAL_SHORTCUT_FIND);
            Component save = new TranslatableComponent(NostalgicLang.Gui.GENERAL_SHORTCUT_SAVE);
            Component exit = new TranslatableComponent(NostalgicLang.Gui.GENERAL_SHORTCUT_EXIT);
            Component group = new TranslatableComponent(NostalgicLang.Gui.GENERAL_SHORTCUT_GROUP);

            return new TextGroup(list, NostalgicUtil.Text.combine(new Component[]{ help, find, save, exit, group })).getRows();
        };

        list.addRow(new ConfigRowList.CategoryRow(list, new TranslatableComponent(NostalgicLang.Gui.GENERAL_SHORTCUT_TITLE), shortcuts).add());
    }

    public void generateGroupList(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (this.parent.getConfigTab() == ConfigScreen.ConfigTab.SWING)
            this.parent.getWidgets().getSwingSpeedPrefix().render(poseStack, mouseX, mouseY, partialTick);
        else if (this.parent.getConfigTab() == ConfigScreen.ConfigTab.SEARCH && this.parent.search.isEmpty())
        {
            boolean isInvalidTag = this.parent.getWidgets().getSearchInput().getValue().startsWith("@");
            for (ConfigScreen.SearchTag tag : ConfigScreen.SearchTag.values())
            {
                if (tag.toString().equals(this.parent.getWidgets().getSearchInput().getValue().replaceAll("@", "")))
                    isInvalidTag = false;
            }

            if (isInvalidTag)
                this.parent.renderLast.add(() -> Screen.drawCenteredString(
                    poseStack,
                    this.parent.getFont(),
                    new TranslatableComponent(NostalgicLang.Gui.SEARCH_INVALID, this.parent.getWidgets().getSearchInput().getValue()),
                    this.parent.width / 2,
                    this.parent.height / 2,
                    0xFFFFFF
                ));
            else
            {
                this.parent.renderLast.add(() -> Screen.drawCenteredString(
                    poseStack,
                    this.parent.getFont(),
                    new TranslatableComponent(NostalgicLang.Gui.SEARCH_EMPTY),
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
            case ANIMATION -> addRows(GroupType.ANIMATION);
            case SWING -> addRows(GroupType.SWING);
            case SEARCH -> addFound();
        }
    }
}
