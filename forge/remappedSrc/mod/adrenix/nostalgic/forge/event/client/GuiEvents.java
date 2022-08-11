package mod.adrenix.nostalgic.forge.event.client;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.ForgeMod;

public abstract class GuiEvents
{
    // In Game Overlay Override
    public static void overlayOverride(RenderGuiOverlayEvent.Pre event)
    {
        // Old Version & Alternative HUD Elements

        ModClientUtil.Gui.renderOverlays(event.getPoseStack());

        // Overlay Overrides

        NamedGuiOverlay overlay = event.getOverlay();
        boolean isArmor = overlay.equals(VanillaGuiOverlay.ARMOR_LEVEL.type());
        boolean isFood = overlay.equals(VanillaGuiOverlay.FOOD_LEVEL.type());
        boolean isAir = overlay.equals(VanillaGuiOverlay.AIR_LEVEL.type());
        boolean isOverlay = isArmor || isFood || isAir;
        boolean isVehiclePresent = false;
        boolean isSurvivalMode = true;

        if (MinecraftClient.getInstance().getCameraEntity() instanceof PlayerEntity player)
        {
            if (player.isCreative() || player.isSpectator())
                isSurvivalMode = false;

            if (player.getVehicle() instanceof LivingEntity)
                isVehiclePresent = true;
        }

        // Run Modified Vanilla Overlays and Cancel Event

        if (isOverlay && isSurvivalMode && !isVehiclePresent)
        {
            MatrixStack poseStack = event.getPoseStack();
            ForgeGui gui = (ForgeGui) MinecraftClient.getInstance().inGameHud;
            gui.setupOverlayRenderState(true, false);

            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();

            boolean isHungerDisabled = ModConfig.Gameplay.disableHungerBar();
            boolean isExperienceDisabled = ModConfig.Gameplay.disableExperienceBar();

            if (isArmor)
                renderArmor(gui, isHungerDisabled, isExperienceDisabled, width, height, poseStack);
            else if (isFood)
                renderFood(gui, isHungerDisabled, isExperienceDisabled, width, height, poseStack);
            else
                renderAir(gui, isHungerDisabled, isExperienceDisabled, width, height, poseStack);

            event.setCanceled(true);
        }
    }

    private static void renderFood(ForgeGui gui, boolean isHungerDisabled, boolean isExperienceDisabled, int width, int height, MatrixStack poseStack)
    {
        if (isHungerDisabled)
            return;

        RandomSource random = RandomSource.create();
        MinecraftClient minecraft = MinecraftClient.getInstance();

        PlayerEntity player = (PlayerEntity) minecraft.getCameraEntity();
        if (player == null)
            return;

        minecraft.getProfiler().push("food");
        RenderSystem.enableBlend();

        int left = width / 2 + 91;
        int top = height - gui.rightHeight;
        gui.rightHeight += 10;

        HungerManager stats = player.getHungerManager();
        int level = stats.getFoodLevel();

        for (int i = 0; i < 10; ++i)
        {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top + (isExperienceDisabled ? 7 : 0);
            int icon = 16;
            byte background = 0;

            if (player.hasStatusEffect(StatusEffects.HUNGER))
            {
                icon += 36;
                background = 13;
            }

            if (player.getHungerManager().getSaturationLevel() <= 0.0F && gui.getGuiTicks() % (level * 3 + 1) == 0)
                y = top + (isExperienceDisabled ? 7 : 0) + (random.nextInt(3) - 1);

            gui.blit(poseStack, x, y, 16 + background * 9, 27, 9, 9);

            if (idx < level)
                gui.blit(poseStack, x, y, icon + 36, 27, 9, 9);
            else if (idx == level)
                gui.blit(poseStack, x, y, icon + 45, 27, 9, 9);
        }

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    private static void renderArmor(ForgeGui gui, boolean isHungerDisabled, boolean isExperienceDisabled, int width, int height, MatrixStack poseStack)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        if (minecraft.player == null)
            return;

        minecraft.getProfiler().push("armor");
        RenderSystem.enableBlend();

        int left = width / 2 - 91;
        int top = height - gui.leftHeight;
        int dy = 0;
        int level = minecraft.player.getArmor();
        boolean isAbsorbing = minecraft.player.getAbsorptionAmount() > 0.0F;

        if (isExperienceDisabled)
            dy += 7;
        if (isHungerDisabled)
            dy += 10;
        if (isAbsorbing && isHungerDisabled)
            dy += 10;

        for (int i = 1; level > 0 && i < 20; i += 2)
        {
            int x = width - left - 10;

            if (isHungerDisabled)
            {
                if (i == level) // Half armor
                    ModClientUtil.Gui.renderInverseArmor(poseStack, gui.getBlitOffset(), x, top + dy, 25, 9, 9, 9);
                else if (i < level) // Full armor
                    gui.blit(poseStack, x, top + dy, 34, 9, 9, 9);
                else // No armor
                    gui.blit(poseStack, x, top + dy, 16, 9, 9, 9);
            }
            else
            {
                if (i == level) // Half armor
                    gui.blit(poseStack, left, top + dy, 25, 9, 9, 9);
                else if (i < level) // Full armor
                    gui.blit(poseStack, left, top + dy, 34, 9, 9, 9);
                else // No armor
                    gui.blit(poseStack, left, top + dy, 16, 9, 9, 9);
            }

            left += 8;
        }

        gui.leftHeight += 10;

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    private static void renderAir(ForgeGui gui, boolean isHungerDisabled, boolean isExperienceDisabled, int width, int height, MatrixStack poseStack)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        PlayerEntity player = (PlayerEntity) minecraft.getCameraEntity();
        if (player == null)
            return;

        minecraft.getProfiler().push("air");
        RenderSystem.enableBlend();

        int left = width / 2 + 91;
        if (left % 2 != 0 && isHungerDisabled)
            left -= 1;

        int top = height - gui.rightHeight;
        int y = top - (isExperienceDisabled ? 2 : 9);
        int dy = isExperienceDisabled ? 7 : 0;
        int air = player.getAir();
        if (player.getAbsorptionAmount() > 0.0F)
            y -= 10;

        if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) || air < 300)
        {
            int full = MathHelper.ceil((double) (air - 2) * 10.0D / 300.0D);
            int partial = MathHelper.ceil((double) air * 10.0D / 300.0D) - full;

            for (int i = 0; i < full + partial; ++i)
            {
                int x = left - i * 8 - 9;
                int mirrorX = width - x - 10;

                if (isHungerDisabled)
                    gui.blit(poseStack, mirrorX, y, (i < full ? 16 : 25), 18, 9, 9);
                else
                    gui.blit(poseStack, x, top + dy, (i < full ? 16 : 25), 18, 9, 9);
            }

            gui.rightHeight += 10;
        }

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }
}
