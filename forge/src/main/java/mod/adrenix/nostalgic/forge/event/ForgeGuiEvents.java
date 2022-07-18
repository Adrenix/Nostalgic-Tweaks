package mod.adrenix.nostalgic.forge.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.common.ForgeMod;

public abstract class ForgeGuiEvents
{
    // In Game Overlay Override
    public static void overlayOverride(RenderGameOverlayEvent.PreLayer event)
    {
        // Old Version & Alternative HUD Elements

        ModClientUtil.Gui.renderOverlays(event.getPoseStack());

        // Overlay Overrides

        IIngameOverlay overlay = event.getOverlay();
        boolean isArmor = overlay.equals(ForgeIngameGui.ARMOR_LEVEL_ELEMENT);
        boolean isFood = overlay.equals(ForgeIngameGui.FOOD_LEVEL_ELEMENT);
        boolean isAir = overlay.equals(ForgeIngameGui.AIR_LEVEL_ELEMENT);
        boolean isOverlay = isArmor || isFood || isAir;
        boolean isVehiclePresent = false;
        boolean isSurvivalMode = true;

        if (Minecraft.getInstance().getCameraEntity() instanceof Player player)
        {
            if (player.isCreative() || player.isSpectator())
                isSurvivalMode = false;

            if (player.getVehicle() instanceof LivingEntity)
                isVehiclePresent = true;
        }

        // Run Modified Vanilla Overlays and Cancel Event

        if (isOverlay && isSurvivalMode && !isVehiclePresent)
        {
            PoseStack poseStack = event.getPoseStack();
            ForgeIngameGui gui = (ForgeIngameGui) Minecraft.getInstance().gui;
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

    private static void renderFood(ForgeIngameGui gui, boolean isHungerDisabled, boolean isExperienceDisabled, int width, int height, PoseStack poseStack)
    {
        if (isHungerDisabled)
            return;

        RandomSource random = RandomSource.create();
        Minecraft minecraft = Minecraft.getInstance();

        Player player = (Player) minecraft.getCameraEntity();
        if (player == null)
            return;

        minecraft.getProfiler().push("food");
        RenderSystem.enableBlend();

        int left = width / 2 + 91;
        int top = height - gui.right_height;
        gui.right_height += 10;

        FoodData stats = player.getFoodData();
        int level = stats.getFoodLevel();

        for (int i = 0; i < 10; ++i)
        {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top + (isExperienceDisabled ? 7 : 0);
            int icon = 16;
            byte background = 0;

            if (player.hasEffect(MobEffects.HUNGER))
            {
                icon += 36;
                background = 13;
            }

            if (player.getFoodData().getSaturationLevel() <= 0.0F && gui.getGuiTicks() % (level * 3 + 1) == 0)
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

    private static void renderArmor(ForgeIngameGui gui, boolean isHungerDisabled, boolean isExperienceDisabled, int width, int height, PoseStack poseStack)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null)
            return;

        minecraft.getProfiler().push("armor");
        RenderSystem.enableBlend();

        int left = width / 2 - 91;
        int top = height - gui.left_height;
        int dy = 0;
        int level = minecraft.player.getArmorValue();
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

        gui.left_height += 10;

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    private static void renderAir(ForgeIngameGui gui, boolean isHungerDisabled, boolean isExperienceDisabled, int width, int height, PoseStack poseStack)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = (Player) minecraft.getCameraEntity();
        if (player == null)
            return;

        minecraft.getProfiler().push("air");
        RenderSystem.enableBlend();

        int left = width / 2 + 91;
        if (left % 2 != 0 && isHungerDisabled)
            left -= 1;

        int top = height - gui.right_height;
        int y = top - (isExperienceDisabled ? 2 : 9);
        int dy = isExperienceDisabled ? 7 : 0;
        int air = player.getAirSupply();
        if (player.getAbsorptionAmount() > 0.0F)
            y -= 10;

        if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) || air < 300)
        {
            int full = Mth.ceil((double) (air - 2) * 10.0D / 300.0D);
            int partial = Mth.ceil((double) air * 10.0D / 300.0D) - full;

            for (int i = 0; i < full + partial; ++i)
            {
                int x = left - i * 8 - 9;
                int mirrorX = width - x - 10;

                if (isHungerDisabled)
                    gui.blit(poseStack, mirrorX, y, (i < full ? 16 : 25), 18, 9, 9);
                else
                    gui.blit(poseStack, x, top + dy, (i < full ? 16 : 25), 18, 9, 9);
            }

            gui.right_height += 10;
        }

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }
}
