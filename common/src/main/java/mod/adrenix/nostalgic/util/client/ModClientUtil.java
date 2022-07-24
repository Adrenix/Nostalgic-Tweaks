package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.mixin.duck.IReequipSlot;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 * For a server safe mixin utility use {@link mod.adrenix.nostalgic.util.server.ModServerUtil}.
 */

public abstract class ModClientUtil
{
    /**
     * Config Runnables
     *
     * Some tweaks require more work for a change to take place.
     *
     * For example, the star buffer needs to be redone if the old stars tweak is toggled.
     * A runnable is defined in the LevelRendererMixin class that provides instructions on what to do when a change is
     * made to the tweak.
     */

    public static class Run
    {
        // On-save Runnables
        public static final ArrayList<Runnable> onSave = new ArrayList<>();

        // Reload States

        public static boolean reloadChunks = false;
        public static boolean reloadResources = false;

        static
        {
            onSave.add(() -> {
                Minecraft minecraft = Minecraft.getInstance();

                if (reloadResources)
                {
                    reloadResources = false;
                    reloadChunks = false;
                    minecraft.reloadResourcePacks();
                }
                else if (reloadChunks)
                {
                    reloadChunks = false;
                    minecraft.levelRenderer.allChanged();
                }
            });
        }
    }

    /* Rendering Helpers */

    public static class Render
    {
        public static void fill(BufferBuilder buffer, Matrix4f matrix, float leftX, float rightX, float topY, float bottomY, int rgba)
        {
            float z = 0.0F;
            buffer.vertex(matrix, leftX, bottomY, z).color(rgba).endVertex();
            buffer.vertex(matrix, rightX, bottomY, z).color(rgba).endVertex();
            buffer.vertex(matrix, rightX, topY, z).color(rgba).endVertex();
            buffer.vertex(matrix, leftX, topY, z).color(rgba).endVertex();
        }
    }

    /* Gui Helpers */

    public static class Gui
    {
        // A mod screen supplier (defined in mod loaders)
        @Nullable
        public static Function<Screen, Screen> modScreen = null;

        // Gets right side x position for the given text
        private static int getRightX(String text)
        {
            Minecraft mc = Minecraft.getInstance();
            return mc.getWindow().getGuiScaledWidth() - mc.font.width(text) - 2;
        }

        // Renders in-game HUD text overlays - game version, food, experience, etc.
        public static void renderOverlays(PoseStack poseStack)
        {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.options.renderDebug)
                return;

            Font font = minecraft.font;
            Player player = minecraft.player;

            if (player == null)
                return;

            TweakType.Corner expCorner = ModConfig.Gameplay.alternativeExperienceCorner();
            TweakType.Corner foodCorner = ModConfig.Gameplay.alternativeHungerCorner();

            boolean isVersion = ModConfig.Candy.oldVersionOverlay();
            boolean isExperience = ModConfig.Gameplay.alternativeExperienceBar();
            boolean isFood = ModConfig.Gameplay.alternativeHungerBar();

            boolean isExpTop = expCorner.equals(TweakType.Corner.TOP_LEFT) || expCorner.equals(TweakType.Corner.TOP_RIGHT);
            boolean isExpRight = expCorner.equals(TweakType.Corner.TOP_RIGHT) || expCorner.equals(TweakType.Corner.BOTTOM_RIGHT);
            boolean isFoodTop = foodCorner.equals(TweakType.Corner.TOP_LEFT) || foodCorner.equals(TweakType.Corner.TOP_RIGHT);
            boolean isFoodRight = foodCorner.equals(TweakType.Corner.TOP_RIGHT) || foodCorner.equals(TweakType.Corner.BOTTOM_RIGHT);

            float foodSat = player.getFoodData().getSaturationLevel();
            int foodLevel = player.getFoodData().getFoodLevel();
            int xpPercent = (int) (player.experienceProgress * 100.0F);
            int satPercent = (int) ((foodSat / 20.0F) * 100.0F);

            int white = 0xFFFFFF;
            float height = (float) minecraft.getWindow().getGuiScaledHeight();
            float leftX = 2.0F;
            float topLeftY = 0.0F;
            float topRightY = 0.0F;
            float bottomLeftY = height - 10.0F;
            float bottomRightY = bottomLeftY;
            float dy = 10.0F;

            String foodColor = "a";
            String satColor = "a";
            String xpColor = "a";

            if (foodLevel <= 2) foodColor = "4";
            else if (foodLevel <= 6) foodColor = "c";
            else if (foodLevel <= 10) foodColor = "6";
            else if (foodLevel <= 15) foodColor = "e";
            else if (foodLevel < 20) foodColor = "2";

            if (xpPercent < 20) xpColor = "c";
            else if (xpPercent < 40) xpColor = "6";
            else if (xpPercent < 60) xpColor = "e";
            else if (xpPercent < 80) xpColor = "2";

            if (satPercent < 20) satColor = "c";
            else if (satPercent < 40) satColor = "6";
            else if (satPercent < 60) satColor = "e";
            else if (satPercent < 80) satColor = "2";

            String food = Component.translatable(NostalgicLang.Gui.HUD_FOOD, foodColor, foodLevel).getString();
            String sat = Component.translatable(NostalgicLang.Gui.HUD_SATURATION, satColor, satPercent).getString();
            String xp = Component.translatable(NostalgicLang.Gui.HUD_EXPERIENCE, xpColor, xpPercent).getString();
            String level = Component.translatable(NostalgicLang.Gui.HUD_LEVEL, player.experienceLevel).getString();

            if (isVersion)
                font.drawShadow(poseStack, ModConfig.Candy.getOverlayText(), 2.0F, topLeftY += 2.0F, white);

            if (isExperience)
            {
                float xpX = isExpRight ? getRightX(xp) : leftX;
                float levelX = isExpRight ? getRightX(level) : leftX;
                float levelY;
                float xpY;

                if (isExpRight)
                {
                    xpY = isExpTop ? topRightY += 2.0F : bottomRightY;
                    levelY = isExpTop ? (topRightY += topRightY == 0.0F ? 2.0F : dy) : (bottomRightY -= 10.0F);
                }
                else
                {
                    xpY = isExpTop ? (topLeftY += topLeftY == 0.0F ? 2.0F : dy) : bottomLeftY;
                    levelY = isExpTop ? (topLeftY += topLeftY == 0.0F ? 2.0F : dy) : (bottomLeftY -= 10.0F);
                }

                font.drawShadow(poseStack, xp, xpX, xpY, white);
                font.drawShadow(poseStack, level, levelX, levelY, white);
            }

            if (isFood)
            {
                float foodX = isFoodRight ? getRightX(food) : leftX;
                float satX = isFoodRight ? getRightX(sat) : leftX;
                float satY;
                float foodY;

                if (isFoodRight)
                {
                    foodY = isFoodTop ? (topRightY += topRightY == 0.0F ? 2.0F : dy) : (isExpTop ? bottomRightY : (bottomRightY -= 10.0F));
                    satY = isFoodTop ? topRightY + dy : isExpTop ? bottomRightY - 10.0F : bottomRightY + 10.0F;
                }
                else
                {
                    foodY = isFoodTop ? (topLeftY += topLeftY == 0.0F ? 2.0F : dy) : (bottomLeftY -= 10.0F);
                    satY = isFoodTop ? topLeftY + dy : bottomLeftY;
                }

                if (isExperience && !isExpTop && !isFoodTop)
                {
                    satY = height - 30.0F;
                    foodY = satY - 10.0F;
                }
                else if (!isExperience && !isExpTop && !isFoodTop)
                {
                    satY = height - 10.0F;
                    foodY = satY - 10.0F;
                }

                font.drawShadow(poseStack, food, foodX, foodY, white);
                font.drawShadow(poseStack, sat, satX, satY, white);
            }
        }

        // Render an inverse half-armor texture
        public static void renderInverseArmor(PoseStack poseStack, float offset, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
        {
            // Flip the vertex’s u texture coordinates so the half armor texture rendering goes from right to left
            Matrix4f matrix = poseStack.last().pose();
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.vertex(matrix, x, y + vHeight, offset).uv((uOffset + uWidth) / 256.0F, (vOffset + vHeight) / 256.0F).endVertex();
            bufferBuilder.vertex(matrix, x + uWidth, y + vHeight, offset).uv(uOffset / 256.0F, (vOffset + vHeight) / 256.0F).endVertex();
            bufferBuilder.vertex(matrix, x + uWidth, y, offset).uv(uOffset / 256.0F, vOffset / 256.0F).endVertex();
            bufferBuilder.vertex(matrix, x, y, offset).uv((uOffset + uWidth) / 256.0F, vOffset / 256.0F).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
        }
    }

    /* Block Helpers */

    public static class Block
    {
        public static boolean isBlockOldChest(net.minecraft.world.level.block.Block block)
        {
            boolean isOldChest = ModConfig.Candy.oldChest() && block.getClass().equals(ChestBlock.class);
            boolean isOldEnder = ModConfig.Candy.oldEnderChest() && block.getClass().equals(EnderChestBlock.class);
            boolean isOldTrap = ModConfig.Candy.oldTrappedChest() && block.getClass().equals(TrappedChestBlock.class);

            return isOldChest || isOldEnder || isOldTrap;
        }

        public static boolean isBlockFullShape(net.minecraft.world.level.block.Block block)
        {
            boolean isChest = isBlockOldChest(block);
            boolean isAOFixed = ModConfig.Candy.fixAmbientOcclusion();
            boolean isSoulSand = isAOFixed && block.getClass().equals(SoulSandBlock.class);
            boolean isPowderedSnow = isAOFixed && block.getClass().equals(PowderSnowBlock.class);
            boolean isComposter = isAOFixed && block.getClass().equals(ComposterBlock.class);
            boolean isPiston = isAOFixed && block.getClass().equals(PistonBaseBlock.class);

            return isChest || isSoulSand || isPowderedSnow || isComposter || isPiston;
        }
    }

    /* World Candy Helpers */

    public static class World
    {
        // Determines where the sun/moon should be rotated when rendering it.
        public static float getSunriseRotation(float vanilla)
        {
            return ModConfig.Candy.oldSunriseAtNorth() ? 0.0F : vanilla;
        }

        // Builds a sky disc for the far plane.
        public static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder builder, float y)
        {
            float x = Math.signum(y) * 512.0F;
            RenderSystem.setShader(GameRenderer::getPositionShader);
            builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
            builder.vertex(0.0, y, 0.0).endVertex();

            for (int i = -180; i <= 180; i += 45)
                builder.vertex(x * Mth.cos((float) i * ((float) Math.PI / 180)), y, 512.0F * Mth.sin((float) i * ((float) Math.PI / 180))).endVertex();
            return builder.end();
        }

        // Caches the model view matrix and the projection matrix so the sky can be overlaid with the blue void correctly.
        public static Matrix4f blueModelView = new Matrix4f();
        public static Matrix4f blueProjection = new Matrix4f();

        // Creates the correct blue color for the void based on the environment.
        public static void setBlueVoidColor()
        {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level == null)
                return;

            float weatherModifier;
            float partialTick = minecraft.getDeltaFrameTime();
            float timeOfDay = level.getTimeOfDay(partialTick);
            float boundedTime = Mth.cos(timeOfDay * ((float) Math.PI * 2)) * 2.0F + 0.5F;
            boundedTime = Mth.clamp(boundedTime, 0.0F, 1.0F);

            float r = boundedTime;
            float g = boundedTime;
            float b = boundedTime;

            float rainLevel = level.getRainLevel(partialTick);
            float thunderLevel = level.getThunderLevel(partialTick);

            if (rainLevel > 0.0F)
            {
                thunderLevel = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.6F;
                weatherModifier = 1.0F - rainLevel * 0.75F;

                r = r * weatherModifier + thunderLevel * (1.0F - weatherModifier);
                g = g * weatherModifier + thunderLevel * (1.0F - weatherModifier);
                b = b * weatherModifier + thunderLevel * (1.0F - weatherModifier);
            }

            if (thunderLevel > 0.0F)
            {
                float thunderModifier = 1.0F - thunderLevel * 0.75F;
                weatherModifier = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.2F;

                r = r * thunderModifier + weatherModifier * (1.0F - thunderModifier);
                g = g * thunderModifier + weatherModifier * (1.0F - thunderModifier);
                b = b * thunderModifier + weatherModifier * (1.0F - thunderModifier);
            }

            r = Mth.clamp(r, 0.1F, 1.0F);
            g = Mth.clamp(g, 0.1F, 1.0F);
            b = Mth.clamp(b, 0.1F, 1.0F);

            RenderSystem.setShader(GameRenderer::getPositionShader);
            RenderSystem.setShaderColor(0.13F * r, 0.17F * g, 0.7F * b, 1.0F);
        }
    }

    /* Animation Helpers */

    public static class Animation
    {
        // Makes arm models parallel to the ground like in the old days.
        public static void setStaticArms(ModelPart rightArm, ModelPart leftArm)
        {
            rightArm.xRot = -1.57F;
            rightArm.yRot = 0.0F;
            rightArm.zRot = 0.0F;

            leftArm.xRot = -1.57F;
            leftArm.yRot = 0.0F;
            leftArm.zRot = 0.0F;
        }
    }

    /* Fog Helpers */

    public static class Fog
    {
        public static boolean isMobEffectActive = false;
        public static boolean isOverworld(Camera camera) { return camera.getEntity().getLevel().dimension() == Level.OVERWORLD; }
        public static boolean isNether(Camera camera) { return camera.getEntity().getLevel().dimension() == Level.NETHER; }
        private static boolean isFluidFog(Camera camera) { return camera.getFluidInCamera() != FogType.NONE; }
        private static boolean isEntityBlind(Camera camera)
        {
            return camera.getEntity() instanceof LivingEntity && ((LivingEntity) camera.getEntity()).hasEffect(MobEffects.BLINDNESS);
        }

        private static int getRenderDistance()
        {
            int renderDistance = Minecraft.getInstance().options.renderDistance().get();
            int multiplier = renderDistance <= 6 ? 16 : 32;
            return renderDistance * multiplier;
        }

        private static void setTerrainFog(FogRenderer.FogMode fogMode)
        {
            if (fogMode != FogRenderer.FogMode.FOG_TERRAIN)
                return;

            float distance = Math.min(1024, getRenderDistance());
            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(distance * 0.8F);
        }

        private static void setHorizonFog(FogRenderer.FogMode fogMode)
        {
            if (fogMode != FogRenderer.FogMode.FOG_SKY)
                return;

            float distance = Math.min(512, getRenderDistance());
            RenderSystem.setShaderFogStart(distance * 0.25F);
            RenderSystem.setShaderFogEnd(distance);
        }

        private static void renderFog(FogRenderer.FogMode fogMode)
        {
            if (ModConfig.Candy.oldTerrainFog())
                setTerrainFog(fogMode);
            if (ModConfig.Candy.oldHorizonFog())
                setHorizonFog(fogMode);
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
        }

        // Overrides fog in the overworld
        public static void setupFog(Camera camera, FogRenderer.FogMode fogMode)
        {
            if (isFluidFog(camera) || isEntityBlind(camera) || !isOverworld(camera))
                return;
            else if (isMobEffectActive)
            {
                isMobEffectActive = false;
                return;
            }

            renderFog(fogMode);
        }

        // Overrides fog in the nether
        public static void setupNetherFog(Camera camera, FogRenderer.FogMode fogMode)
        {
            if (!ModConfig.Candy.oldNetherFog() || isFluidFog(camera) || isEntityBlind(camera) || !isNether(camera))
                return;
            else if (isMobEffectActive)
            {
                isMobEffectActive = false;
                return;
            }

            renderFog(fogMode);
            RenderSystem.setShaderFogStart(0.0F);
        }
    }

    /* Item Helpers */

    public static class Item
    {
        // Used to enhance the old reequipping logic
        public static ItemStack getLastItem(ItemStack originalItemStack, ItemStack rendererItemStack, ItemStack playerItemStack, IReequipSlot player)
        {
            // Item from main hand turns to air as soon as the player pulls it out. When this happens, the following strings appear in each property respectively.
            boolean isUnequipped = rendererItemStack.toString().equals("0 air") && playerItemStack.toString().equals("1 air");
            if (!ModConfig.Animation.oldItemReequip() || !isUnequipped)
                return originalItemStack;

            return player.NT$getLastItem();
        }

        // Tells the item renderer if we're rendering a flat item.
        private static boolean isRenderingFlat = false;

        // Used to cache the current level pose stack position matrix for re-enabling diffused lighting after flat rendering.
        @Nullable
        public static PoseStack.Pose levelPoseStack;

        // Used to cache the current buffer source during the entity render cycle.
        // This is needed so we can end the batch early to apply flat lighting to vertices.
        public static MultiBufferSource.BufferSource levelBufferSource;

        // Used to check if a model should be rendered in 2D.
        public static boolean isModelFlat(BakedModel model) { return !model.usesBlockLight(); }

        // Flattens an item to be as close to 2D as possible via scaling.
        public static void flatten(PoseStack poseStack) { poseStack.scale(1.0F, 1.0F, 0.001F); }

        // Getter for checking if diffused lighting is disabled.
        public static boolean isLightingFlat() { return isRenderingFlat; }

        // Turns off diffused lighting.
        public static void disableDiffusedLighting()
        {
            levelBufferSource.endBatch();
            Lighting.setupForFlatItems();
            isRenderingFlat = true;
        }

        // Turns on diffused lighting.
        public static void enableDiffusedLighting()
        {
            isRenderingFlat = false;
            levelBufferSource.endBatch();

            if (Minecraft.getInstance().level == null || levelPoseStack == null)
                return;

            if (Minecraft.getInstance().level.effects().constantAmbientLight())
                Lighting.setupNetherLevel(levelPoseStack.pose());
            else
                Lighting.setupLevel(levelPoseStack.pose());
        }

        // Used to change the normal based on which quad side we're rendering.
        public static void setNormalQuad(PoseStack.Pose pose, BakedQuad quad)
        {
            pose.normal().setIdentity();
            if (quad.getDirection() == Direction.NORTH)
                pose.normal().mul(-1.0F);
        }
    }
}
