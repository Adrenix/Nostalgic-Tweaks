package mod.adrenix.nostalgic.mixin.util.candy.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.mixin.access.GuiAccess;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;

/**
 * This utility class is used only by the client.
 */
public abstract class HudMixinHelper
{
    /**
     * Checks if the mod is managing the heads-up display. This flag assists in keeping the graphics state consistent
     * during abnormal game behavior.
     */
    private static final FlagHolder HUD_MANAGED = FlagHolder.off();

    /**
     * Checks if food elements positioning has been modified on the heads-up display.
     */
    private static final FlagHolder FOOD_LEVEL_PUSHED = FlagHolder.off();

    /**
     * Checks if an armor element has modified the heads-up display positioning.
     */
    private static final FlagHolder ARMOR_LEVEL_PUSHED = FlagHolder.off();

    /**
     * Checks if an air bubble element has modified the heads-up display positioning.
     */
    private static final FlagHolder AIR_LEVEL_PUSHED = FlagHolder.off();

    /**
     * A list of all {@link FlagHolder} element pushing trackers.
     */
    private static final List<FlagHolder> PUSHED_ELEMENTS = List.of(FOOD_LEVEL_PUSHED, ARMOR_LEVEL_PUSHED, AIR_LEVEL_PUSHED);

    /**
     * Config reload listener that forces Exordium to update the hotbar component after a config save.
     */
    public static void runAfterSave()
    {
        GuiAccess gui = (GuiAccess) Minecraft.getInstance().gui;

        if (ModTracker.EXORDIUM.isInstalled())
            gui.nt$setHealthBlinkTime(gui.nt$getTickCount() + 20);
    }

    /**
     * @return The {@link Player} data from the camera.
     */
    @Nullable
    private static Player getPlayer()
    {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player player)
            return player;

        return null;
    }

    /**
     * @return Check if the player is currently mounted on a vehicle that has health.
     */
    private static boolean isVehicleHealthShown()
    {
        Player player = getPlayer();

        if (player != null)
            return player.getVehicle() instanceof LivingEntity;

        return false;
    }

    /**
     * @return Check if the player is currently mounted on a vehicle that can jump.
     */
    private static boolean isJumpMeterShown()
    {
        Player player = getPlayer();

        if (player instanceof LocalPlayer local)
            return local.jumpableVehicle() != null;

        return false;
    }

    /**
     * @return The height offset provided by the number of heart rows the player has.
     */
    private static int getHeightOffsetFromHearts()
    {
        int absorptionAmount = Mth.ceil(NullableResult.getOrElse(getPlayer(), 0.0F, Player::getAbsorptionAmount));
        int currentHealth = Mth.ceil(NullableResult.getOrElse(getPlayer(), 0.0F, Player::getHealth));
        int displayHealth = ((GuiAccess) (Minecraft.getInstance().gui)).nt$getDisplayHealth();
        float playerHealth = NullableResult.getOrElse(getPlayer(), 0.0F, player -> (float) player.getAttributeValue(Attributes.MAX_HEALTH));
        float maxHealth = Math.max(playerHealth, (float) Math.max(displayHealth, currentHealth));
        int numberOfRows = Mth.ceil((maxHealth + (float) absorptionAmount) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (numberOfRows - 2), 3);

        return 39 + (numberOfRows - 1) * rowHeight + 10;
    }

    /**
     * Begin managing the heads-up display.
     *
     * @param graphics The {@link GuiGraphics} instance.
     */
    public static void begin(GuiGraphics graphics)
    {
        if (HUD_MANAGED.ifDisabledThenEnable())
            graphics.pose().pushPose();
    }

    /**
     * End managing the heads-up display.
     *
     * @param graphics The {@link GuiGraphics} instance.
     */
    public static void end(GuiGraphics graphics)
    {
        if (HUD_MANAGED.ifEnabledThenDisable())
        {
            while (PUSHED_ELEMENTS.stream().anyMatch(FlagHolder::ifEnabledThenDisable))
                graphics.pose().popPose();

            graphics.pose().popPose();
        }
    }

    /**
     * Pop previous pose changes.
     *
     * @param graphics The {@link GuiGraphics} instance.
     */
    public static void pop(GuiGraphics graphics)
    {
        if (HUD_MANAGED.get() && PUSHED_ELEMENTS.stream().anyMatch(FlagHolder::ifEnabledThenDisable))
            graphics.pose().popPose();
    }

    /**
     * Apply pose changes based on the given arguments.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param overlay  The {@link HudElement} to apply.
     */
    public static void apply(GuiGraphics graphics, HudElement overlay)
    {
        if (!HUD_MANAGED.get())
            return;

        switch (overlay)
        {
            case VEHICLE_HEALTH ->
            {
                if (isVehicleHealthShown())
                    graphics.pose().translate(0.0F, -10.0F, 0.0F);
            }
            case EXPERIENCE_BAR ->
            {
                if (!isJumpMeterShown())
                    graphics.pose().translate(0.0F, 7.0F, 0.0F);
            }
            case FOOD ->
            {
                FOOD_LEVEL_PUSHED.enable();

                graphics.pose().pushPose();
                graphics.pose().translate(0.0F, 9e10F, 0.0F);
            }
            case ARMOR ->
            {
                ARMOR_LEVEL_PUSHED.enable();

                graphics.pose().pushPose();
                graphics.pose().translate((float) (GuiUtil.getGuiWidth() / 2 + 90), 0.0F, 0.0F);

                renderArmor(graphics, 39);
            }
            case AIR ->
            {
                AIR_LEVEL_PUSHED.enable();

                graphics.pose().pushPose();
                graphics.pose().translate((float) (GuiUtil.getGuiWidth() / 2 - 100), 0.0F, 0.0F);

                renderAir(graphics, getHeightOffsetFromHearts());
            }
        }
    }

    /**
     * Flips the half armor's u-texture coordinate so the icon goes from right to left.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param x        The x-coordinate to render at on the screen.
     * @param y        The y-coordinate to render at on the screen.
     */
    private static void renderInverseHalfArmor(GuiGraphics graphics, int x, int y)
    {
        TextureAtlasSprite sprite = Minecraft.getInstance().getGuiSprites().getSprite(GuiAccess.NT$ARMOR_HALF_SPRITE());

        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder builder = Tesselator.getInstance().getBuilder();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(matrix, x, y + 9, 0.0F).uv(sprite.getU1(), sprite.getV1()).endVertex();
        builder.vertex(matrix, x + 9, y + 9, 0.0F).uv(sprite.getU0(), sprite.getV1()).endVertex();
        builder.vertex(matrix, x + 9, y, 0.0F).uv(sprite.getU0(), sprite.getV0()).endVertex();
        builder.vertex(matrix, x, y, 0.0F).uv(sprite.getU1(), sprite.getV0()).endVertex();

        BufferUploader.drawWithShader(builder.end());
    }

    /**
     * Renders old armor on the right side of the heads-up display.
     *
     * @param graphics     The {@link GuiGraphics} instance.
     * @param offsetHeight The offset height that will be subtracted from the bottom of the scaled window height.
     */
    public static void renderArmor(GuiGraphics graphics, int offsetHeight)
    {
        int armor = NullableResult.getOrElse(getPlayer(), 0, Player::getArmorValue);
        int top = GuiUtil.getGuiHeight() - offsetHeight;
        int left = 0;

        for (int i = 1; armor > 0 && i < 20; i += 2)
        {
            left -= 8;

            if (i == armor)
                renderInverseHalfArmor(graphics, left, top);
            else if (i < armor)
                graphics.blitSprite(GuiAccess.NT$ARMOR_FULL_SPRITE(), left, top, 9, 9);
            else
                graphics.blitSprite(GuiAccess.NT$ARMOR_EMPTY_SPRITE(), left, top, 9, 9);
        }
    }

    /**
     * Renders old air bubbles on the left side of the heads-up display above the hearts.
     *
     * @param graphics     The {@link GuiGraphics} instance.
     * @param offsetHeight The offset height that will be subtracted from the bottom of the scaled window height.
     */
    public static void renderAir(GuiGraphics graphics, int offsetHeight)
    {
        int air = NullableResult.getOrElse(getPlayer(), 0, Player::getAirSupply);
        int top = GuiUtil.getGuiHeight() - offsetHeight + 1;
        int left = 0;

        int full = Mth.ceil((double) (air - 2) * 10.0D / 300.0D);
        int partial = Mth.ceil((double) air * 10.0D / 300.0D) - full;

        for (int i = 0; i < full + partial; ++i)
            graphics.blitSprite(i < full ? GuiAccess.NT$AIR_SPRITE() : GuiAccess.NT$AIR_BURSTING_SPRITE(), left + i * 8 + 9, top, 9, 9);
    }
}
