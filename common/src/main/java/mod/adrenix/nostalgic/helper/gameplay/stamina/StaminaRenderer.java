package mod.adrenix.nostalgic.helper.gameplay.stamina;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.asset.ModSprite;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.timer.FlagTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.TimeUnit;

/**
 * This utility class is used only by the client.
 */
public abstract class StaminaRenderer
{
    /**
     * Tracks if the stamina bar has begun to drain.
     */
    public static final FlagHolder HAS_BEGUN_TO_DRAIN = FlagHolder.off();

    /**
     * A timer for fully refilled stamina flash delay.
     */
    public static final FlagTimer FULL_FLASH_TIMER = FlagTimer.create(150, TimeUnit.MILLISECONDS)
        .maxRepeat(4)
        .startWith(true)
        .build();

    /**
     * A timer for low stamina flash delay.
     */
    public static final FlagTimer LOW_FLASH_TIMER = FlagTimer.create(200, TimeUnit.MILLISECONDS)
        .startWith(true)
        .build();

    /**
     * @return Whether the stamina bar is visible on the heads-up display.
     */
    public static boolean isVisible()
    {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || CandyTweak.HIDE_STAMINA_BAR.get() || !GameplayTweak.STAMINA_SPRINT.get())
            return false;

        if (CandyTweak.HIDE_STAMINA_BAR_INACTIVE.get() && !StaminaHelper.get(player).isTiringOrExhausted())
            return false;

        Entity vehicle = player.getVehicle();
        boolean isMounted = vehicle != null && vehicle.showVehicleHealth();
        boolean isInSurvival = minecraft.gameMode != null && minecraft.gameMode.canHurtPlayer();
        boolean isPlayerCamera = minecraft.getCameraEntity() instanceof Player;

        return !minecraft.options.hideGui && !isMounted && isInSurvival && isPlayerCamera;
    }

    /**
     * @param stamina The {@link PlayerStamina} instance.
     * @param icon    The icon that is to be rendered.
     * @return The {@link ResourceLocation} of the stamina sprite to use.
     */
    public static ResourceLocation getSprite(PlayerStamina stamina, int icon)
    {
        ResourceLocation sprite = stamina.data.isExhausted() ? ModSprite.STAMINA_RECHARGE : ModSprite.STAMINA_LEVEL;

        if (stamina.hasPositiveEffect())
            sprite = ModSprite.STAMINA_POSITIVE;

        if (stamina.isNotRegainable() && !CandyTweak.HIDE_STAMINA_BAR_MOVING.get())
            sprite = ModSprite.STAMINA_NEGATIVE;

        if (stamina.hasNegativeEffect())
            sprite = ModSprite.STAMINA_NEGATIVE;

        if (stamina.isCoolingDown() && !CandyTweak.HIDE_STAMINA_BAR_COOLDOWN.get())
            sprite = ModSprite.STAMINA_COOLING;

        int level = stamina.data.getStamina();

        if (MathUtil.isOdd(level) && icon == level)
        {
            sprite = stamina.data.isExhausted() ? ModSprite.STAMINA_RECHARGE_HALF : ModSprite.STAMINA_LEVEL_HALF;

            if (stamina.hasPositiveEffect())
                sprite = ModSprite.STAMINA_POSITIVE_HALF;

            if (stamina.isNotRegainable() && !CandyTweak.HIDE_STAMINA_BAR_MOVING.get())
                sprite = ModSprite.STAMINA_NEGATIVE_HALF;

            if (stamina.hasNegativeEffect())
                sprite = ModSprite.STAMINA_NEGATIVE_HALF;

            if (stamina.isCoolingDown() && !CandyTweak.HIDE_STAMINA_BAR_COOLDOWN.get())
                sprite = ModSprite.STAMINA_COOLING_HALF;
        }
        else if (icon > level)
            sprite = ModSprite.STAMINA_EMPTY;

        return sprite;
    }

    /**
     * @param stamina The {@link PlayerStamina} instance.
     * @return Whether the stamina sprite should use its highlighted variant.
     */
    public static boolean shouldHighlight(PlayerStamina stamina)
    {
        boolean shouldHighlight = false;
        int level = stamina.data.getStamina();
        int maximum = stamina.data.getMaximum();

        if (CandyTweak.HIGHLIGHT_STAMINA_BAR.get() && level != maximum)
            shouldHighlight = stamina.isAtFullSprint();

        if (HAS_BEGUN_TO_DRAIN.get() && !stamina.data.isExhausted() && level == maximum)
            shouldHighlight = FULL_FLASH_TIMER.getFlag();
        else
            FULL_FLASH_TIMER.reset();

        if (FULL_FLASH_TIMER.hasReachedMax())
        {
            FULL_FLASH_TIMER.reset();
            HAS_BEGUN_TO_DRAIN.disable();

            shouldHighlight = false;
        }

        int flashAt = CandyTweak.FLASH_STAMINA_BAR_AT.get();

        if (flashAt > 0 && flashAt >= level && stamina.isAtFullSprint())
            shouldHighlight = LOW_FLASH_TIMER.getFlag();

        return shouldHighlight;
    }

    /**
     * Render the heads-up display stamina elements.
     *
     * @param graphics    The {@link GuiGraphics} instance.
     * @param rightHeight The right side height offset of the heads-up display.
     * @param offsetLeft  The amount to offset the stamina icons from the left side of the screen.
     * @return The y-offset to apply to the next HUD element row. Will be 10, 20, 30, etc.
     */
    public static int render(GuiGraphics graphics, int rightHeight, int offsetLeft)
    {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || !isVisible())
            return 10;

        RenderUtil.beginBatching();

        PlayerStamina stamina = StaminaHelper.get(player);
        int width = GuiUtil.getGuiWidth();
        int height = GuiUtil.getGuiHeight();
        int left = width / 2 + 91 + offsetLeft;
        int top = height - rightHeight;
        int level = stamina.data.getStamina();
        int maximum = stamina.data.getMaximum();
        int rows = Math.max((int) Math.ceil(maximum / 20.0D), 1);

        if (CandyTweak.FLASH_STAMINA_BAR_WHEN_FULL.get() && level < maximum)
            HAS_BEGUN_TO_DRAIN.enable();

        for (int j = 0; j < rows; j++)
        {
            int y = top - (10 * j);

            for (int i = 0; i < 10; i++)
            {
                int x = left - i * 8 - 9;
                int icon = (i + (10 * j)) * 2 + 1;

                if (icon > maximum)
                    break;

                ResourceLocation sprite = getSprite(stamina, icon);

                RenderUtil.blitSprite(ModSprite.STAMINA_EMPTY, graphics, x, y, 9, 9);
                RenderUtil.blitSprite(sprite, graphics, x, y, 9, 9);

                if (shouldHighlight(stamina))
                {
                    graphics.pose().pushPose();
                    graphics.pose().translate(0.0F, 0.0F, 1.0F);
                    RenderUtil.blitSprite(ModSprite.STAMINA_HIGHLIGHT, graphics, x, y, 9, 9);
                    graphics.pose().popPose();
                }
            }
        }

        RenderUtil.endBatching();

        return 10 * rows;
    }
}
