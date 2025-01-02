package mod.adrenix.nostalgic.helper.gameplay.stamina;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.asset.ModSprite;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.sprite.GuiSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * This utility is only used by the client.
 */
public abstract class StaminaRenderer
{
    /**
     * @return Whether the stamina bar is visible on the heads-up display.
     */
    public static boolean isVisible()
    {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || CandyTweak.HIDE_STAMINA_BAR.get() || !GameplayTweak.STAMINA_SPRINT.get())
            return false;

        if (CandyTweak.HIDE_STAMINA_BAR_INACTIVE.get() && !StaminaHelper.isActiveFor(player))
            return false;

        Entity vehicle = player.getVehicle();
        boolean isMounted = vehicle != null && vehicle.showVehicleHealth();
        boolean isInSurvival = minecraft.gameMode != null && minecraft.gameMode.canHurtPlayer();
        boolean isPlayerCamera = minecraft.getCameraEntity() instanceof Player;

        return !minecraft.options.hideGui && !isMounted && isInSurvival && isPlayerCamera;
    }

    /**
     * Render the heads-up display stamina elements.
     *
     * @param graphics    The {@link GuiGraphics} instance.
     * @param rightHeight The right side height offset of the heads-up display.
     */
    public static void render(GuiGraphics graphics, int rightHeight)
    {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || !isVisible())
            return;

        RenderUtil.beginBatching();

        StaminaData data = StaminaHelper.get(player);
        int width = GuiUtil.getGuiWidth();
        int height = GuiUtil.getGuiHeight();
        int left = width / 2 + 91;
        int top = height - rightHeight;
        int stamina = data.getStaminaLevel();
        boolean isExhausted = data.isExhausted();

        for (int i = 0; i < 10; i++)
        {
            GuiSprite sprite = isExhausted ? ModSprite.STAMINA_RECHARGE : ModSprite.STAMINA_LEVEL;
            int x = left - i * 8 - 9;
            int icon = i * 2 + 1;

            RenderUtil.blitSprite(ModSprite.STAMINA_EMPTY, graphics, x, top);

            if (icon > stamina)
                sprite = ModSprite.STAMINA_EMPTY;
            else if (icon == stamina && MathUtil.isOdd(stamina))
                sprite = isExhausted ? ModSprite.STAMINA_RECHARGE_HALF : ModSprite.STAMINA_LEVEL_HALF;

            RenderUtil.blitSprite(sprite, graphics, x, top);
        }

        RenderUtil.endBatching();
    }
}
