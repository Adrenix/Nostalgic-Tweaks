package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchGroup;
import mod.adrenix.nostalgic.client.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.RowProvider;
import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetHolder;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.client.search.SearchTag;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.function.ForEachWithPrevious;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

        record FilterTag(SearchTag tag, FlagHolder holder, SwitchGroup group)
        {
            public static FilterTag create(WidgetHolder parent, SearchTag tag)
            {
                FlagHolder holder = FlagHolder.off();
                Translation title = Lang.literal(TextUtil.toTitleCase(tag.toString()));
                Translation description = tag.getDescription();
                SwitchGroup group = SwitchGroup.create(parent, title, description, holder::get, holder::set);

                return new FilterTag(tag, holder, group);
            }

            public boolean isActive()
            {
                return this.holder.get();
            }
        }

        record FilterCategory(Container category, FlagHolder holder)
        {
            public static FilterCategory create(Container category)
            {
                if (!category.isCategory())
                    throw new AssertionError("Given container is not a category!");

                return new FilterCategory(category, FlagHolder.off());
            }

            public void checkbox(ManageOverlay overlay, Group group, @Nullable DynamicWidget<?, ?> below)
            {
                ButtonWidget checkbox = ButtonTemplate.checkbox(Lang.EMPTY, this.holder::get)
                    .skipFocusOnClick()
                    .below(below, overlay.padding * 2)
                    .onPress(this.holder::toggle)
                    .build(group::addWidget);

                BlankWidget guide = BlankWidget.create()
                    .pos(checkbox::getX, checkbox::getY)
                    .size(Icons.CHECKBOX::getWidth, Icons.CHECKBOX::getHeight)
                    .build(group::addWidget);

                TextWidget text = TextWidget.create(this.category.toString())
                    .icon(this.category.getIcon())
                    .color(this.category.getColor())
                    .brightenIconOnHover(1.2F)
                    .useTextWidth()
                    .centerVertical()
                    .rightOf(guide, 4)
                    .height(Icons.CHECKBOX::getHeight)
                    .hoverOrFocusSync(checkbox, this.category.getColor().brighten(0.35D))
                    .centerInWidgetY(checkbox)
                    .build(group::addWidget);

                checkbox.getBuilder().extendWidthToEnd(text, -1);
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

        NullableHolder<DynamicWidget<?, ?>> below = NullableHolder.empty();
        LinkedHashSet<FilterTag> tagFilters = new LinkedHashSet<>();
        LinkedHashSet<FilterCategory> categoryFilters = new LinkedHashSet<>();

        for (Container category : Container.CATEGORIES)
            categoryFilters.add(FilterCategory.create(category));

        ForEachWithPrevious.create(categoryFilters)
            .applyToFirst(category -> category.checkbox(manager, toggles, null))
            .forEach((prev, next) -> next.checkbox(manager, toggles, toggles.getWidgets().getLast()))
            .run();

        SeparatorWidget separateToggles = SeparatorWidget.create(toggles.getColor())
            .height(1)
            .below(toggles.getWidgets().getLast(), manager.padding * 2)
            .width(toggles::getInsideWidth)
            .build(toggles::addWidget);

        below.set(separateToggles);

        for (SearchTag tag : SearchTag.values())
        {
            FilterTag filter = FilterTag.create(toggles, tag);

            TextWidget info = filter.group()
                .getDescription()
                .extendWidthToEnd(toggles, toggles.getInsidePaddingX())
                .below(below.get(), 4)
                .build();

            filter.group().getToggle().getBuilder().below(below.get(), 4);

            toggles.addWidget(filter.group().getToggle());
            toggles.addWidget(info);

            tagFilters.add(filter);
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

        Grid grid = Grid.create(manager.overlay, 2)
            .columnSpacing(1)
            .extendWidthToEnd(apply, apply.getInsidePaddingX())
            .below(sideSeparator, manager.padding * 2)
            .build(apply::addWidget);

        BooleanSupplier isToggleEnabled = () -> {
            boolean isStatePicked = isEnabled.get() || isDisabled.get();
            boolean isCachePicked = isLocal.get() || isNetwork.get();

            return isStatePicked && isCachePicked;
        };

        Runnable onToggleAll = () -> {
            List<Predicate<Tweak<?>>> tagPredicates = tagFilters.stream()
                .filter(FilterTag::isActive)
                .map(FilterTag::tag)
                .map(SearchTag::getPredicate)
                .toList();

            UniqueArrayList<Predicate<Tweak<?>>> predicates = new UniqueArrayList<>(tagPredicates);

            if (categoryFilters.stream().anyMatch(FilterCategory::isActive))
            {
                predicates.add(tweak -> categoryFilters.stream()
                    .filter(FilterCategory::isActive)
                    .map(FilterCategory::category)
                    .anyMatch(category -> category == tweak.getCategory()));
            }

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
                TweakPool.automated(predicates).forEach(tweak -> consumer.accept(tweak, tweak.getDefault()));

            if (isDisabled.get())
                TweakPool.automated(predicates).forEach(tweak -> consumer.accept(tweak, tweak.getDisabled()));
        };

        Consumer<ConfigScreen> onReviewAll = (config) -> {
            RowProvider.ALL.use();

            config.getWidgetManager().setQuery("");
            config.getWidgetManager().setQuery(SearchTag.SAVE.query());
            config.getWidgetManager().populateFromQuery();

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
