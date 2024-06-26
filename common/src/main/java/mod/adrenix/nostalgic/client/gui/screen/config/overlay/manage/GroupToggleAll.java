package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchGroup;
import mod.adrenix.nostalgic.client.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.RowProvider;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetHolder;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.factory.*;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.client.search.SearchTag;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.text.TextUtil;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GroupToggleAll extends ManageGroup
{
    @Override
    void define(ManageOverlay manager)
    {
        /* Header */

        Group heading = Group.create(manager.overlay)
            .forceRelativeY()
            .icon(Icons.LIGHTNING)
            .title(Lang.Button.TOGGLE_ALL_TWEAKS)
            .border(Color.SCHOOL_BUS)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget.create(Lang.Manage.TOGGLE_ALL_HEADER).width(heading::getInsideWidth).build(heading::addWidget);

        /* Filters */

        record Filter(SearchTag tag, FlagHolder holder, SwitchGroup group)
        {
            public static Filter create(WidgetHolder parent, SearchTag tag)
            {
                FlagHolder holder = FlagHolder.off();
                Translation title = Lang.literal(TextUtil.toTitleCase(tag.toString()));
                Translation description = tag.getDescription();
                SwitchGroup group = SwitchGroup.create(parent, title, description, holder::get, holder::set);

                return new Filter(tag, holder, group);
            }

            public boolean isActive()
            {
                return this.holder.get();
            }
        }

        Group toggles = Group.create(manager.overlay)
            .icon(Icons.FILTER)
            .title(Lang.Button.FILTER)
            .border(Color.NOSTALGIC_GRAY)
            .below(heading, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        FlagHolder isOverride = FlagHolder.off();
        NullableHolder<TextWidget> below = NullableHolder.empty();
        LinkedHashSet<Filter> filters = new LinkedHashSet<>();

        for (SearchTag tag : SearchTag.values())
        {
            Filter filter = Filter.create(toggles, tag);

            TextWidget info = filter.group()
                .getDescription()
                .extendWidthToEnd(toggles, toggles.getInsidePaddingX())
                .below(below.get(), 4)
                .build();

            filter.group().getToggle().getBuilder().disableIf(isOverride::get).below(below.get(), 4);

            toggles.addWidget(filter.group().getToggle());
            toggles.addWidget(info);

            filters.add(filter);
            below.set(info);
        }

        /* Apply */

        Group apply = Group.create(manager.overlay)
            .icon(Icons.CLIPBOARD)
            .title(Lang.Button.APPLY)
            .border(Color.DEER_BROWN)
            .below(toggles, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget applyInfo = TextWidget.create(Lang.Manage.TOGGLE_ALL_APPLY_INFO)
            .width(apply::getInsideWidth)
            .build(apply::addWidget);

        /* Toggle All */

        FlagHolder isEnabled = FlagHolder.off();

        ButtonWidget enable = ButtonTemplate.checkbox(Lang.Manage.TOGGLE_ALL_ENABLE, isEnabled::get)
            .skipFocusOnClick()
            .tooltip(Lang.Button.ENABLE_ALL, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Manage.TOGGLE_ALL_ENABLE_INFO, 35)
            .extendWidthToEnd(apply, apply.getInsidePaddingX())
            .below(applyInfo, manager.padding * 2)
            .onPress(isEnabled::toggle)
            .build(apply::addWidget);

        FlagHolder isDisabled = FlagHolder.off();

        ButtonWidget disable = ButtonTemplate.checkbox(Lang.Manage.TOGGLE_ALL_DISABLE, isDisabled::get)
            .skipFocusOnClick()
            .tooltip(Lang.Button.DISABLE_ALL, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Manage.TOGGLE_ALL_DISABLE_INFO, 35)
            .extendWidthToEnd(apply, apply.getInsidePaddingX())
            .below(enable, manager.padding)
            .onPress(isDisabled::toggle)
            .build(apply::addWidget);

        FlagHolder.radio(isEnabled, isDisabled);

        /* Toggle Sided */

        SeparatorWidget stateSeparator = SeparatorWidget.create(apply.getColor())
            .height(1)
            .below(disable, manager.padding * 2)
            .width(apply::getInsideWidth)
            .build(apply::addWidget);

        FlagHolder isLocal = FlagHolder.on();

        ButtonWidget local = ButtonTemplate.checkbox(Lang.Manage.TOGGLE_ALL_LOCAL, isLocal::get)
            .skipFocusOnClick()
            .tooltip(Lang.Button.LOCAL, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Manage.TOGGLE_ALL_LOCAL_INFO, 35)
            .extendWidthToEnd(apply, apply.getInsidePaddingX())
            .below(stateSeparator, manager.padding * 2)
            .onPress(isLocal::toggle)
            .build(apply::addWidget);

        FlagHolder isNetwork = FlagHolder.off();

        ButtonWidget network = ButtonTemplate.checkbox(Lang.Manage.TOGGLE_ALL_NETWORK, isNetwork::get)
            .skipFocusOnClick()
            .tooltip(Lang.Button.NETWORK, 35, 500L, TimeUnit.MILLISECONDS)
            .disabledTooltip(Lang.Tooltip.NOT_CONNECTED_OR_OPERATOR, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Manage.TOGGLE_ALL_NETWORK_INFO, 35)
            .enableIf(NetUtil::isConnectedAndOperator)
            .extendWidthToEnd(apply, apply.getInsidePaddingX())
            .below(local, manager.padding)
            .onPress(isNetwork::toggle)
            .build(apply::addWidget);

        FlagHolder.radio(isLocal, isNetwork);

        /* Toggle Overrides */

        SeparatorWidget sideSeparator = SeparatorWidget.create(apply.getColor())
            .height(1)
            .below(network, manager.padding * 2)
            .width(apply::getInsideWidth)
            .build(apply::addWidget);

        ButtonWidget override = ButtonTemplate.checkbox(Lang.Manage.TOGGLE_ALL_OVERRIDE, isOverride::get)
            .skipFocusOnClick()
            .tooltip(Lang.Button.OVERRIDE, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Manage.TOGGLE_ALL_OVERRIDE_INFO, 35)
            .extendWidthToEnd(apply, apply.getInsidePaddingX())
            .below(sideSeparator, manager.padding * 2)
            .onPress(isOverride::toggle)
            .build(apply::addWidget);

        Grid grid = Grid.create(manager.overlay, 2)
            .columnSpacing(1)
            .extendWidthToEnd(apply, apply.getInsidePaddingX())
            .below(override, manager.padding * 2)
            .build(apply::addWidget);

        BooleanSupplier isToggleEnabled = () -> {
            boolean isStatePicked = isEnabled.get() || isDisabled.get();
            boolean isCachePicked = isLocal.get() || isNetwork.get();
            boolean isReady = isStatePicked && isCachePicked;

            if (isOverride.get())
                return isReady;

            return filters.stream().map(Filter::holder).anyMatch(FlagHolder::get) && isReady;
        };

        Runnable onToggleAll = () -> {
            List<Predicate<Tweak<?>>> predicates = filters.stream()
                .filter(Filter::isActive)
                .map(Filter::tag)
                .map(SearchTag::getPredicate)
                .toList();

            Stream<Tweak<Object>> tweaks = TweakPool.filter(predicates)
                .filter(tweak -> ClassUtil.isNotInstanceOf(tweak, TweakBinding.class))
                .filter(tweak -> tweak.getCategory() != Category.ROOT)
                .filter(tweak -> tweak.getCategory() != Category.MOD)
                .filter(Tweak::isNotIgnored)
                .filter(Tweak::isNotInternal)
                .map(TweakMeta::wildcard);

            BiConsumer<Tweak<Object>, Object> consumer = (tweak, value) -> {
                TweakListing<?, ?> listing = ClassUtil.cast(tweak, TweakListing.class).orElse(null);

                if (listing != null)
                {
                    if (isLocal.get())
                        listing.fromLocal().setDisabled(isDisabled.get());
                    else if (isNetwork.get())
                        listing.fromNetwork().setDisabled(isDisabled.get());
                }
                else if (isLocal.get())
                    tweak.setLocal(value);
                else if (isNetwork.get())
                    tweak.setNetwork(value);
            };

            if (isEnabled.get())
                tweaks.forEach(tweak -> consumer.accept(tweak, tweak.getDefault()));
            else if (isDisabled.get())
                tweaks.forEach(tweak -> consumer.accept(tweak, tweak.getDisabled()));
        };

        Consumer<ConfigScreen> onReviewAll = (config) -> {
            RowProvider.ALL.use();
            config.getWidgetManager().setQuery(SearchTag.SAVE.query());

            if (manager.overlay.getParentScreen() instanceof ConfigScreen screen)
                screen.setFocused(config.getWidgetManager().getSearch());

            GuiUtil.getScreenAs(Overlay.class).ifPresent(Overlay::close);
        };

        ButtonWidget.create(Lang.Button.TOGGLE_ALL_TWEAKS)
            .icon(Icons.LIGHTNING)
            .tooltip(Lang.Button.TOGGLE_ALL_TWEAKS, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Manage.TOGGLE_ALL_APPLY_INFO, 35)
            .enableIf(isToggleEnabled)
            .onPress(onToggleAll)
            .build(grid::addCell);

        ButtonWidget.create(Lang.Button.REVIEW_CHANGES)
            .icon(Icons.SEARCH)
            .tooltip(Lang.Button.REVIEW_CHANGES, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.REVIEW_CHANGES, 35)
            .onPress(() -> ClassUtil.cast(manager.overlay.getParentScreen(), ConfigScreen.class).ifPresent(onReviewAll))
            .build(grid::addCell);
    }
}
