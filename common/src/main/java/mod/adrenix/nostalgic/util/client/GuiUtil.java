package mod.adrenix.nostalgic.util.client;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.api.ClientEventFactory;
import mod.adrenix.nostalgic.api.event.HudEvent;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.mixin.duck.WidgetManager;
import mod.adrenix.nostalgic.mixin.widen.AbstractContainerScreenAccessor;
import mod.adrenix.nostalgic.mixin.widen.ScreenAccessor;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.joml.Matrix4f;

import java.util.function.Function;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 */

public abstract class GuiUtil
{
    /**
     * A mod screen supplier (defined in mod loaders)
     */
    public static Function<Screen, Screen> modScreen = null;

    /* Recipe Button Helpers */

    /**
     * Get a new image button that represents a 'large' recipe button but with a square border.
     * @param screen The current container screen.
     * @param parent The original image button to get data from.
     * @return A new recipe image button as a large square button.
     */
    public static ImageButton getLargeBook(AbstractContainerScreenAccessor screen, ImageButton parent)
    {
        return new ImageButton
        (
            screen.NT$getLeftPos() + 151,
            screen.NT$getTopPos() + 7,
            18,
            18,
            178,
            20,
            TextureLocation.INVENTORY,
            (button) ->
            {
                parent.onPress();
                button.setPosition(screen.NT$getLeftPos() + 151, screen.NT$getTopPos() + 7);
            }
        );
    }

    /**
     * Get a new image button that represents a 'small' recipe button as a square with a question mark in it.
     * @param screen The current container screen.
     * @param parent The original image button to get data from.
     * @return A new recipe image button as a small button with a question mark in it.
     */
    public static ImageButton getSmallBook(AbstractContainerScreenAccessor screen, ImageButton parent)
    {
        return new ImageButton
        (
            screen.NT$getLeftPos() + 160,
            screen.NT$getTopPos() + 7,
            9,
            10,
            178,
            0,
            TextureLocation.INVENTORY,
            (button) ->
            {
                parent.onPress();
                button.setPosition(screen.NT$getLeftPos() + 160, screen.NT$getTopPos() + 7);
            }
        );
    }

    /**
     * Create a new recipe button instance based on the status of recipe button tweaks.
     * @param screen The current container screen.
     * @param book The type of recipe button the user wants.
     */
    public static void createRecipeButton(AbstractContainerScreenAccessor screen, TweakType.RecipeBook book)
    {
        ImageButton recipeButton = null;
        WidgetManager injector = (WidgetManager) Minecraft.getInstance().screen;

        for (Renderable widget : ((ScreenAccessor) screen).NT$getRenderables())
        {
            if (widget instanceof ImageButton imageButton)
            {
                recipeButton = imageButton;
                break;
            }
        }

        if (injector != null && recipeButton != null)
        {
            switch (book)
            {
                case DISABLED -> recipeButton.setPosition(-9999, -9999);
                case LARGE ->
                {
                    injector.NT$removeWidget(recipeButton);
                    injector.NT$addRenderableWidget(getLargeBook(screen, recipeButton));
                }
                case SMALL ->
                {
                    injector.NT$removeWidget(recipeButton);
                    injector.NT$addRenderableWidget(getSmallBook(screen, recipeButton));
                }
            }
        }
    }

    /* In-game HUD Overlays */

    /**
     * Calculates where text rendering from the right side of the screen should start.
     * @param text The text to render.
     * @return The starting point of where the given text should render.
     */
    private static int getRightX(String text)
    {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth() - Minecraft.getInstance().font.width(text) - 2;
    }

    /**
     * Gets a color based on the current food status of the player.
     * @param food The current food level.
     * @return A color code based on the given food level.
     */
    private static String getFoodColor(int food)
    {
        if (food <= 2)
            return "§4";
        else if (food <= 6)
            return "§c";
        else if (food <= 10)
            return "§6";
        else if (food <= 15)
            return "§e";
        else if (food < 20)
            return "§2";

        return "§a";
    }

    /**
     * Gets a color based on a given percentage.
     * @param percent The current percentage.
     * @return A color code based on the given percentage.
     */
    private static String getPercentColor(int percent)
    {
        if (percent < 20)
            return "§c";
        else if (percent < 40)
            return "§6";
        else if (percent < 60)
            return "§e";
        else if (percent < 80)
            return "§2";

        return "§a";
    }

    /**
     * The class will manage and keep track of where given text should render.
     * This greatly simplifies the rendering process of in-game HUD text.
     */
    public static class CornerManager
    {
        private final float height = (float) Minecraft.getInstance().getWindow().getGuiScaledHeight();
        private final AtomicDouble topLeft = new AtomicDouble(2.0D);
        private final AtomicDouble topRight = new AtomicDouble(2.0D);
        private final AtomicDouble bottomLeft = new AtomicDouble(this.height - 10.0D);
        private final AtomicDouble bottomRight = new AtomicDouble(this.height - 10.0D);

        public float getAndAdd(TweakType.Corner corner)
        {
            return switch (corner)
            {
                case TOP_LEFT -> (float) topLeft.getAndAdd(10.0D);
                case TOP_RIGHT -> (float) topRight.getAndAdd(10.0D);
                case BOTTOM_LEFT -> (float) bottomLeft.getAndAdd(-10.0D);
                case BOTTOM_RIGHT -> (float) bottomRight.getAndAdd(-10.0D);
            };
        }
    }

    /**
     * Draws the given text to the in-game HUD.
     * @param graphics The current GuiGraphics object.
     * @param text The text to render.
     * @param corner The corner to render the text to.
     * @param manager The corner manager for this render cycle.
     */
    public static void drawText(GuiGraphics graphics, String text, TweakType.Corner corner, CornerManager manager)
    {
        Minecraft minecraft = Minecraft.getInstance();
        boolean isLeft = corner.equals(TweakType.Corner.TOP_LEFT) || corner.equals(TweakType.Corner.BOTTOM_LEFT);

        graphics.drawString(minecraft.font, text, isLeft ? 2 : getRightX(text), (int) manager.getAndAdd(corner), 0xFFFFFF, true);
    }

    /**
     * Renders in-game HUD text overlays - game version, food, experience, etc.
     * @param graphics The current GuiGraphics object.
     */
    public static void renderOverlays(GuiGraphics graphics)
    {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.options.renderDebug || minecraft.options.hideGui)
            return;

        Player player = minecraft.player;

        if (player == null)
            return;

        CornerManager manager = new CornerManager();

        boolean isCreative = player.isCreative() || player.isSpectator();
        boolean isLevelCreative = isCreative && ModConfig.Gameplay.displayAlternativeLevelCreative();
        boolean isProgressCreative = isCreative && ModConfig.Gameplay.displayAlternativeProgressCreative();

        int foodLevel = player.getFoodData().getFoodLevel();
        int xpPercent = (int) (player.experienceProgress * 100.0F);
        int satPercent = (int) ((player.getFoodData().getSaturationLevel() / 20.0F) * 100.0F);

        if (ModConfig.Candy.oldVersionOverlay())
            drawText(graphics, ModConfig.Candy.getOverlayText(), ModConfig.Candy.oldOverlayCorner(), manager);

        if (ModConfig.Gameplay.displayAlternativeLevelText() && (!isCreative || isLevelCreative))
        {
            TweakType.Corner levelCorner = ModConfig.Gameplay.alternativeLevelCorner();
            String level = ModConfig.Gameplay.getAlternativeLevelText(Integer.toString(player.experienceLevel));
            drawText(graphics, level, levelCorner, manager);
        }

        if (ModConfig.Gameplay.displayAlternativeProgressText() && (!isCreative || isProgressCreative))
        {
            boolean useColor = ModConfig.Gameplay.useDynamicProgressColor();
            TweakType.Corner xpCorner = ModConfig.Gameplay.alternativeProgressCorner();
            String xp = ModConfig.Gameplay.getAlternativeProgressText((useColor ? getPercentColor(xpPercent) : "") + xpPercent);
            drawText(graphics, xp, xpCorner, manager);
        }

        if (ModConfig.Gameplay.displayAlternativeFoodText() && !isCreative)
        {
            boolean useColor = ModConfig.Gameplay.useDynamicFoodColor();
            TweakType.Corner foodCorner = ModConfig.Gameplay.alternativeFoodCorner();
            String food = ModConfig.Gameplay.getAlternativeFoodText((useColor ? getFoodColor(foodLevel) : "") + foodLevel);
            drawText(graphics, food, foodCorner, manager);
        }

        if (ModConfig.Gameplay.displayAlternativeSatText() && !isCreative)
        {
            boolean useColor = ModConfig.Gameplay.useDynamicSatColor();
            TweakType.Corner satCorner = ModConfig.Gameplay.alternativeSaturationCorner();
            String sat = ModConfig.Gameplay.getAlternativeSaturationText((useColor ? getPercentColor(satPercent) : "") + satPercent);
            drawText(graphics, sat, satCorner, manager);
        }
    }

    /**
     * A y-position tracker for where heart icons are rendered on the screen.
     * This is used to add support to mods such as AppleSkin.
     */
    public static int heartY = 0;

    /**
     * A y-position tracker for where food icons are rendered on the screen.
     * This is used to add support to mods such as AppleSkin.
     */
    public static int foodY = 0;

    /**
     * Renders an inverse half-armor texture.
     * @param graphics The current GuiGraphics object.
     * @param x The x-position.
     * @param y The y-position.
     * @param uOffset The u-coordinate (horizontal) on the texture sheet.
     * @param vOffset The v-coordinate (vertical) on the texture sheet.
     * @param uWidth The horizontal width of the texture.
     * @param vHeight The vertical height of the texture.
     */
    public static void renderInverseArmor(GuiGraphics graphics, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        // Flip the vertex’s u texture coordinates so the half armor texture rendering goes from right to left
        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, x, y + vHeight, 0.0F).uv((uOffset + uWidth) / 256.0F, (vOffset + vHeight) / 256.0F).endVertex();
        bufferBuilder.vertex(matrix, x + uWidth, y + vHeight, 0.0F).uv(uOffset / 256.0F, (vOffset + vHeight) / 256.0F).endVertex();
        bufferBuilder.vertex(matrix, x + uWidth, y, 0.0F).uv(uOffset / 256.0F, vOffset / 256.0F).endVertex();
        bufferBuilder.vertex(matrix, x, y, 0.0F).uv((uOffset + uWidth) / 256.0F, vOffset / 256.0F).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    /**
     * Renders the armor icon texture on the HUD depending on the state of various tweaks.
     * @param graphics The current GuiGraphics object.
     * @param player A player instance.
     * @param width The current screen width.
     * @param height The current screen height.
     * @param leftHeight The left side GUI offset.
     * @param rightHeight The right side GUI offset.
     */
    public static void renderArmor(GuiGraphics graphics, Player player, int width, int height, int leftHeight, int rightHeight)
    {
        boolean isHungerDisabled = ModConfig.Gameplay.disableHungerBar();

        int left = width / 2 - 91;
        int top = height - (isHungerDisabled ? rightHeight : leftHeight);
        int level = player.getArmorValue();
        int index = -1;

        for (int i = 1; level > 0 && i < 20; i += 2)
        {
            boolean isXChanged = false;
            int x = isHungerDisabled ? width - left - 10 : left;
            int y = top;

            HudEvent event = ClientEventFactory.RENDER_ARMOR.create(x, y, ++index, graphics);
            event.emit();

            if (event.isCanceled())
                continue;

            if (x != event.getX())
                isXChanged = true;

            x = event.getX();
            y = event.getY();

            if (i == level)
            {
                // Half armor
                if (isHungerDisabled && !isXChanged)
                    GuiUtil.renderInverseArmor(graphics, x, y, 25, 9, 9, 9);
                else
                    graphics.blit(Gui.GUI_ICONS_LOCATION, x, y, 25, 9, 9, 9);
            }
            else if (i < level)
            {
                // Full armor
                graphics.blit(Gui.GUI_ICONS_LOCATION, x, y, 34, 9, 9, 9);
            }
            else
            {
                // No armor
                graphics.blit(Gui.GUI_ICONS_LOCATION, x, y, 16, 9, 9, 9);
            }

            left += 8;
        }
    }

    /**
     * Renders the food icon texture on the HUD depending on the state of various tweaks.
     * @param graphics The current GuiGraphics object.
     * @param player A player instance.
     * @param width The current screen width.
     * @param height The current screen height.
     * @param rightHeight The right side GUI offset.
     */
    public static void renderFood(GuiGraphics graphics, Player player, int width, int height, int rightHeight)
    {
        Gui gui = Minecraft.getInstance().gui;
        RandomSource random = RandomSource.create();

        int left = width / 2 + 91;
        int top = height - rightHeight;

        FoodData stats = player.getFoodData();

        int level = stats.getFoodLevel();
        int index = -1;

        for (int i = 0; i < 10; i++)
        {
            int iconX = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;
            int icon = 16;
            int background = 0;

            if (player.hasEffect(MobEffects.HUNGER))
            {
                icon += 36;
                background = 13;
            }

            if (player.getFoodData().getSaturationLevel() <= 0.0F && gui.getGuiTicks() % (level * 3 + 1) == 0)
                y = top + (random.nextInt(3) - 1);

            HudEvent event = ClientEventFactory.RENDER_FOOD.create(x, y, ++index, graphics);
            event.emit();

            GuiUtil.foodY = event.getY();

            if (event.isCanceled())
                continue;

            x = event.getX();
            y = event.getY();

            graphics.blit(Gui.GUI_ICONS_LOCATION, x, y, 16 + background * 9, 27, 9, 9);

            if (iconX < level)
                graphics.blit(Gui.GUI_ICONS_LOCATION, x, y, icon + 36, 27, 9, 9);
            else if (iconX == level)
                graphics.blit(Gui.GUI_ICONS_LOCATION, x, y, icon + 45, 27, 9, 9);
        }
    }

    /**
     * Renders the air bubble icon texture on the HUD depending on the state of various tweaks.
     * @param isPlayerLosingAir A function that accepts a player instance and returns whether the player is losing air.
     * @param graphics The current GuiGraphics object.
     * @param player A player instance.
     * @param width The current screen width.
     * @param height The current screen height.
     * @param leftHeight The left side GUI offset.
     * @param rightHeight The right side GUI offset.
     */
    public static void renderAir(Function<Player, Boolean> isPlayerLosingAir, GuiGraphics graphics, Player player, int width, int height, int leftHeight, int rightHeight)
    {
        boolean isHungerDisabled = ModConfig.Gameplay.disableHungerBar();

        int index = -1;
        int left = width / 2 + 91;

        if (left % 2 != 0 && isHungerDisabled)
            left -= 1;

        int y = height - (isHungerDisabled ? leftHeight : rightHeight);
        int air = player.getAirSupply();

        if (isPlayerLosingAir.apply(player))
        {
            int full = Mth.ceil((double) (air - 2) * 10.0D / 300.0D);
            int partial = Mth.ceil((double) air * 10.0D / 300.0D) - full;

            for (int i = 0; i < full + partial; i++)
            {
                int shiftX = left - i * 8 - 9;
                int mirrorX = width - shiftX - 10;
                int x = isHungerDisabled ? mirrorX : shiftX;

                HudEvent event = ClientEventFactory.RENDER_BUBBLE.create(x, y, ++index, graphics);
                event.emit();

                if (event.isCanceled())
                    return;

                x = event.getX();
                y = event.getY();

                graphics.blit(Gui.GUI_ICONS_LOCATION, x, y, (i < full ? 16 : 25), 18, 9, 9);
            }
        }
    }
}
