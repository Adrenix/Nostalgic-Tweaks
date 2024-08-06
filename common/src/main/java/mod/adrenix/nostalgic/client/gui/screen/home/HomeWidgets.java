package mod.adrenix.nostalgic.client.gui.screen.home;

import com.mojang.blaze3d.vertex.BufferBuilder;
import mod.adrenix.nostalgic.client.gui.screen.WidgetManager;
import mod.adrenix.nostalgic.client.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.gui.screen.home.overlay.DebugOverlay;
import mod.adrenix.nostalgic.client.gui.screen.home.overlay.SetupOverlay;
import mod.adrenix.nostalgic.client.gui.screen.home.overlay.supporter.SupporterOverlay;
import mod.adrenix.nostalgic.client.gui.screen.packs.PacksListScreen;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconFactory;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.gui.LinkUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderPass;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.LinkLocation;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.timer.FlagTimer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class HomeWidgets implements WidgetManager
{
    /* Fields */

    private final HomeScreen homeScreen;
    private final IconFactory heartOutline;
    private final FlagTimer flashTimer;

    /* Constructor */

    HomeWidgets(HomeScreen homeScreen)
    {
        Panorama.unpause();

        this.homeScreen = homeScreen;
        this.flashTimer = FlagTimer.create(1L, TimeUnit.SECONDS).build();
        this.heartOutline = IconWidget.create(Icons.HEART_OUTLINE).cannotFocus().renderWhen(RenderPass.LAST).size(12);
    }

    /* Methods */

    @Override
    public void init()
    {
        int sectionMargin = 0;
        int sectionOffset = 6;

        /* Menus & Links */

        ButtonWidget config = ButtonWidget.create(Lang.Enum.SCREEN_CONFIG)
            .icon(Icons.MECHANICAL_TOOLS)
            .useTextWidth()
            .alignLeft(sectionOffset)
            .onPress(() -> Minecraft.getInstance().setScreen(new ConfigScreen(this.homeScreen)))
            .backgroundRenderer(this::renderLeftTransparent)
            .build(this.homeScreen::addWidget);

        ButtonWidget presets = ButtonWidget.create(Lang.Enum.SCREEN_PACKS)
            .icon(Icons.ZIP_FILE)
            .alignFlushTo(config)
            .below(config, sectionMargin)
            .useTextWidth()
            .alignLeft(sectionOffset)
            .onPress(() -> Minecraft.getInstance().setScreen(new PacksListScreen(this.homeScreen)))
            .backgroundRenderer(this::renderLeftTransparent)
            .build(this.homeScreen::addWidget);

        ButtonWidget discord = ButtonWidget.create(Lang.Home.DISCORD)
            .icon(Icons.DISCORD)
            .alignFlushTo(presets)
            .below(presets, sectionMargin)
            .useTextWidth()
            .alignLeft(sectionOffset)
            .onPress(LinkUtil.onPress(LinkLocation.DISCORD))
            .backgroundRenderer(this::renderLeftTransparent)
            .build(this.homeScreen::addWidget);

        ButtonWidget kofi = ButtonWidget.create(Lang.Home.KOFI)
            .icon(Icons.KOFI)
            .alignFlushTo(discord)
            .below(discord, sectionMargin)
            .useTextWidth()
            .alignLeft(sectionOffset)
            .onPress(LinkUtil.onPress(LinkLocation.KOFI))
            .backgroundRenderer(this::renderLeftTransparent)
            .build(this.homeScreen::addWidget);

        ButtonWidget golden = ButtonWidget.create(Lang.Home.GOLDEN_DAYS)
            .icon(Icons.GOLDEN_DAYS)
            .alignFlushTo(kofi)
            .below(kofi, sectionMargin)
            .useTextWidth()
            .alignLeft(sectionOffset)
            .onPress(LinkUtil.onPress(LinkLocation.GOLDEN_DAYS))
            .backgroundRenderer(this::renderLeftTransparent)
            .build(this.homeScreen::addWidget);

        SeparatorWidget separator = SeparatorWidget.create(Color.WHITE)
            .height(1)
            .below(golden, 2)
            .build(this.homeScreen::addWidget);

        ButtonWidget.create(Lang.Affirm.QUIT_CANCEL)
            .icon(Icons.GO_BACK)
            .alignFlushTo(golden)
            .below(separator, 1)
            .useTextWidth()
            .alignLeft(sectionOffset)
            .onPress(this.homeScreen::onClose)
            .backgroundRenderer(this::renderLeftTransparent)
            .build(this.homeScreen::addWidget);

        List<DynamicWidget<?, ?>> extended = this.homeScreen.getWidgets().stream().toList();

        this.homeScreen.getWidgets().stream().map(DynamicWidget::getBuilder).forEach(builder -> {
            if (builder instanceof LayoutBuilder<?, ?> layout)
                layout.extendWidthToLargest(extended);
        });

        config.getBuilder().posY(() -> {
            int spacing = (extended.size() - 1) * (20 + sectionMargin);
            return Math.round(MathUtil.center(spacing - sectionMargin, GuiUtil.getGuiHeight()));
        });

        /* Panorama */

        Supplier<Component> pauseOrUnpauseText = () -> {
            if (Panorama.isPaused())
                return Component.literal("▶");

            return Component.literal("⏸");
        };

        ButtonWidget panoramaLast = ButtonWidget.create(Lang.literal("⏮"))
            .size(16, 13)
            .cannotFocus()
            .tooltip(Lang.Home.PREV_PANORAMA, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Home.PREV_PANORAMA_INFO, 35)
            .visibleIf(() -> Minecraft.getInstance().level == null)
            .onPress(Panorama::backward)
            .backgroundRenderer(this::renderTopTransparent)
            .build(this.homeScreen::addWidget);

        ButtonWidget panoramaCycle = ButtonWidget.create(pauseOrUnpauseText)
            .size(16, 13)
            .cannotFocus()
            .rightOf(panoramaLast, 0)
            .tooltip(Lang.Home.CYCLE_PANORAMA, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Home.CYCLE_PANORAMA_INFO, 35)
            .visibleIf(() -> Minecraft.getInstance().level == null)
            .onPress(() -> {
                if (Panorama.isPaused())
                    Panorama.unpause();
                else
                    Panorama.pause();
            })
            .backgroundRenderer(this::renderTopTransparent)
            .build(this.homeScreen::addWidget);

        ButtonWidget.create(Lang.literal("⏭"))
            .size(16, 13)
            .cannotFocus()
            .rightOf(panoramaCycle, 0)
            .tooltip(Lang.Home.NEXT_PANORAMA, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Home.NEXT_PANORAMA_INFO, 35)
            .visibleIf(() -> Minecraft.getInstance().level == null)
            .onPress(Panorama::forward)
            .backgroundRenderer(this::renderTopTransparent)
            .build(this.homeScreen::addWidget);

        /* Extras */

        ButtonWidget debug = ButtonWidget.create()
            .icon(Icons.BUG)
            .tooltip(Lang.Home.DEBUG, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.HOME_DEBUG, 35)
            .fromScreenEndX(1)
            .fromScreenEndY(1)
            .tabOrderGroup(4)
            .onPress(() -> new DebugOverlay().open())
            .build(this.homeScreen::addWidget);

        ButtonWidget heart = ButtonWidget.create()
            .icon(this::getHeart, 12)
            .tooltip(Lang.Home.SUPPORTERS, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.HOME_SUPPORTERS, 35)
            .leftOf(debug, 1)
            .tabOrderGroup(3)
            .onPress(() -> new SupporterOverlay().open())
            .build(this.homeScreen::addWidget);

        this.heartOutline.pos(heart::getIconX, heart::getIconY)
            .visibleIf(heart::isHoveredOrFocused)
            .build(this.homeScreen::addWidget);

        ButtonWidget.create()
            .icon(Icons.MECHANICAL_TOOLS)
            .tooltip(Lang.Home.INIT_CONFIG, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.HOME_INIT, 35)
            .leftOf(heart, 1)
            .tabOrderGroup(2)
            .onPress(SetupOverlay::open)
            .build(this.homeScreen::addWidget);

        /* Copyright */

        TextWidget.create("Made by Adrenix\nMMPL - 2.0.3-Modified © 2024")
            .onPress(LinkUtil.onPress(LinkLocation.LICENSE))
            .color(Color.fromFormatting(ChatFormatting.GRAY))
            .useTextWidth()
            .centerAligned()
            .centerInScreenX()
            .fromScreenEndY(1)
            .tabOrderGroup(1)
            .build(this.homeScreen::addWidget);
    }

    /**
     * @return A {@link TextureIcon} heart.
     */
    private TextureIcon getHeart()
    {
        if (this.flashTimer.getFlag() && !ModTweak.OPENED_SUPPORTER_SCREEN.get())
            return Icons.HEART_EMPTY;

        return Icons.HEART;
    }

    /**
     * Handler method for rendering a transparent section button background.
     *
     * @param button      A {@link ButtonWidget} instance.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The current x-position of the mouse.
     * @param mouseY      The current y-position of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F-1.0F].
     */
    private void renderLeftTransparent(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        final Color bar = Color.BLACK.fromAlpha(0.0F);
        final Color fill = Color.BLACK.fromAlpha(0.6F);
        final BufferBuilder builder = RenderUtil.getAndBeginFill();

        if (button.isHoveredOrFocused())
        {
            bar.set(Color.LEMON_YELLOW);
            bar.setAlpha(1.0F);

            RenderUtil.fill(builder, graphics, button.getX(), button.getY(), button.getEndX(), button.getEndY(), fill.get());
        }

        RenderUtil.fill(builder, graphics, button.getX(), button.getY(), button.getX() + 2, button.getEndY(), bar.get());
        RenderUtil.endFill(builder);
    }

    /**
     * Handler method for rendering a transparent top button background.
     *
     * @param button      A {@link ButtonWidget} instance.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The current x-position of the mouse.
     * @param mouseY      The current y-position of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F-1.0F].
     */
    private void renderTopTransparent(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        final Color bar = Color.BLACK.fromAlpha(0.0F);
        final Color fill = Color.BLACK.fromAlpha(0.6F);
        final BufferBuilder builder = RenderUtil.getAndBeginFill();

        if (button.isHoveredOrFocused())
        {
            bar.set(Color.ORANGE);
            bar.setAlpha(1.0F);

            RenderUtil.fill(builder, graphics, button.getX(), button.getY(), button.getEndX(), button.getEndY(), fill.get());
        }

        RenderUtil.fill(builder, graphics, button.getX(), button.getEndY() - 1, button.getEndX(), button.getEndY(), bar.get());
        RenderUtil.endFill(builder);
    }
}
