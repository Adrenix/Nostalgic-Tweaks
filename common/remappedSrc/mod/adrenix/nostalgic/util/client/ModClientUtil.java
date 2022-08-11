package mod.adrenix.nostalgic.util.client;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.mixin.duck.IReequipSlot;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.renderer.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.level.block.*;
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
                MinecraftClient minecraft = MinecraftClient.getInstance();

                if (reloadResources)
                {
                    reloadResources = false;
                    reloadChunks = false;
                    minecraft.reloadResources();
                }
                else if (reloadChunks)
                {
                    reloadChunks = false;
                    minecraft.worldRenderer.reload();
                }
            });
        }
    }

    /* Rendering Helpers */

    public static class Render
    {
        public static void fill(BufferBuilder builder, Matrix4f matrix, float leftX, float rightX, float topY, float bottomY, int rgba)
        {
            float z = 0.0F;
            builder.vertex(matrix, leftX, bottomY, z).color(rgba).next();
            builder.vertex(matrix, rightX, bottomY, z).color(rgba).next();
            builder.vertex(matrix, rightX, topY, z).color(rgba).next();
            builder.vertex(matrix, leftX, topY, z).color(rgba).next();
        }
    }

    /* Gui Helpers */

    public static class Gui
    {
        // A mod screen supplier (defined in mod loaders)
        @Nullable
        public static Function<Screen, Screen> modScreen = null;

        /* In-game HUD Overlays */

        private static int getRightX(String text)
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            return mc.getWindow().getScaledWidth() - mc.textRenderer.getWidth(text) - 2;
        }

        private static String getFoodColor(int food)
        {
            if (food <= 2) return "§4";
            else if (food <= 6) return "§c";
            else if (food <= 10) return "§6";
            else if (food <= 15) return "§e";
            else if (food < 20) return "§2";
            return "§a";
        }

        private static String getPercentColor(int percent)
        {
            if (percent < 20) return "§c";
            else if (percent < 40) return "§6";
            else if (percent < 60) return "§e";
            else if (percent < 80) return "§2";
            return "§a";
        }

        private static class CornerManager
        {
            private final float height = (float) MinecraftClient.getInstance().getWindow().getScaledHeight();
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

        private static void drawText(MatrixStack poseStack, String text, TweakType.Corner corner, CornerManager manager)
        {
            boolean isLeft = corner.equals(TweakType.Corner.TOP_LEFT) || corner.equals(TweakType.Corner.BOTTOM_LEFT);
            MinecraftClient.getInstance().textRenderer.drawWithShadow(poseStack, text, isLeft ? 2.0F : getRightX(text), manager.getAndAdd(corner), 0xFFFFFF);
        }

        // Renders in-game HUD text overlays - game version, food, experience, etc.
        public static void renderOverlays(MatrixStack poseStack)
        {
            MinecraftClient minecraft = MinecraftClient.getInstance();
            if (minecraft.options.debugEnabled || minecraft.options.hudHidden)
                return;

            PlayerEntity player = minecraft.player;
            if (player == null)
                return;

            CornerManager manager = new CornerManager();

            int foodLevel = player.getHungerManager().getFoodLevel();
            int xpPercent = (int) (player.experienceProgress * 100.0F);
            int satPercent = (int) ((player.getHungerManager().getSaturationLevel() / 20.0F) * 100.0F);

            if (ModConfig.Candy.oldVersionOverlay())
                drawText(poseStack, ModConfig.Candy.getOverlayText(), ModConfig.Candy.oldOverlayCorner(), manager);

            if (ModConfig.Gameplay.displayAlternativeLevelText())
            {
                TweakType.Corner levelCorner = ModConfig.Gameplay.alternativeLevelCorner();
                String level = ModConfig.Gameplay.getAlternativeLevelText(Integer.toString(player.experienceLevel));
                drawText(poseStack, level, levelCorner, manager);
            }

            if (ModConfig.Gameplay.displayAlternativeProgressText())
            {
                boolean useColor = ModConfig.Gameplay.useDynamicProgressColor();
                TweakType.Corner xpCorner = ModConfig.Gameplay.alternativeProgressCorner();
                String xp = ModConfig.Gameplay.getAlternativeProgressText((useColor ? getPercentColor(xpPercent) : "") + xpPercent);
                drawText(poseStack, xp, xpCorner, manager);
            }

            if (ModConfig.Gameplay.displayAlternativeFoodText())
            {
                boolean useColor = ModConfig.Gameplay.useDynamicFoodColor();
                TweakType.Corner foodCorner = ModConfig.Gameplay.alternativeFoodCorner();
                String food = ModConfig.Gameplay.getAlternativeFoodText((useColor ? getFoodColor(foodLevel) : "") + foodLevel);
                drawText(poseStack, food, foodCorner, manager);
            }

            if (ModConfig.Gameplay.displayAlternativeSatText())
            {
                boolean useColor = ModConfig.Gameplay.useDynamicSatColor();
                TweakType.Corner satCorner = ModConfig.Gameplay.alternativeSaturationCorner();
                String sat = ModConfig.Gameplay.getAlternativeSaturationText((useColor ? getPercentColor(satPercent) : "") + satPercent);
                drawText(poseStack, sat, satCorner, manager);
            }
        }

        // Render an inverse half-armor texture
        public static void renderInverseArmor(MatrixStack poseStack, float offset, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
        {
            // Flip the vertex’s u texture coordinates so the half armor texture rendering goes from right to left
            Matrix4f matrix = poseStack.peek().getPositionMatrix();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix, x, y + vHeight, offset).texture((uOffset + uWidth) / 256.0F, (vOffset + vHeight) / 256.0F).next();
            bufferBuilder.vertex(matrix, x + uWidth, y + vHeight, offset).texture(uOffset / 256.0F, (vOffset + vHeight) / 256.0F).next();
            bufferBuilder.vertex(matrix, x + uWidth, y, offset).texture(uOffset / 256.0F, vOffset / 256.0F).next();
            bufferBuilder.vertex(matrix, x, y, offset).texture((uOffset + uWidth) / 256.0F, vOffset / 256.0F).next();
            BufferRenderer.drawWithShader(bufferBuilder.end());
        }
    }

    /* Block Helpers */

    public static class Block
    {
        public static boolean isBlockOldChest(net.minecraft.block.Block block)
        {
            boolean isOldChest = ModConfig.Candy.oldChest() && block.getClass().equals(ChestBlock.class);
            boolean isOldEnder = ModConfig.Candy.oldEnderChest() && block.getClass().equals(EnderChestBlock.class);
            boolean isOldTrap = ModConfig.Candy.oldTrappedChest() && block.getClass().equals(TrappedChestBlock.class);

            return isOldChest || isOldEnder || isOldTrap;
        }

        public static boolean isBlockFullShape(net.minecraft.block.Block block)
        {
            boolean isChest = isBlockOldChest(block);
            boolean isAOFixed = ModConfig.Candy.fixAmbientOcclusion();
            boolean isSoulSand = isAOFixed && block.getClass().equals(SoulSandBlock.class);
            boolean isPowderedSnow = isAOFixed && block.getClass().equals(PowderSnowBlock.class);
            boolean isComposter = isAOFixed && block.getClass().equals(ComposterBlock.class);
            boolean isPiston = isAOFixed && block.getClass().equals(PistonBlock.class);

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
            builder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
            builder.vertex(0.0, y, 0.0).next();

            for (int i = -180; i <= 180; i += 45)
                builder.vertex(x * MathHelper.cos((float) i * ((float) Math.PI / 180)), y, 512.0F * MathHelper.sin((float) i * ((float) Math.PI / 180))).next();
            return builder.end();
        }

        // Caches the model view matrix and the projection matrix so the sky can be overlaid with the blue void correctly.
        public static Matrix4f blueModelView = new Matrix4f();
        public static Matrix4f blueProjection = new Matrix4f();

        // Creates the correct blue color for the void based on the environment.
        public static void setBlueVoidColor()
        {
            MinecraftClient minecraft = MinecraftClient.getInstance();
            ClientWorld level = minecraft.world;
            if (level == null)
                return;

            float weatherModifier;
            float partialTick = minecraft.getLastFrameDuration();
            float timeOfDay = level.getSkyAngle(partialTick);
            float boundedTime = MathHelper.cos(timeOfDay * ((float) Math.PI * 2)) * 2.0F + 0.5F;
            boundedTime = MathHelper.clamp(boundedTime, 0.0F, 1.0F);

            float r = boundedTime;
            float g = boundedTime;
            float b = boundedTime;

            float rainLevel = level.getRainGradient(partialTick);
            float thunderLevel = level.getThunderGradient(partialTick);

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

            r = MathHelper.clamp(r, 0.1F, 1.0F);
            g = MathHelper.clamp(g, 0.1F, 1.0F);
            b = MathHelper.clamp(b, 0.1F, 1.0F);

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
            rightArm.pitch = -1.57F;
            rightArm.yaw = 0.0F;
            rightArm.roll = 0.0F;

            leftArm.pitch = -1.57F;
            leftArm.yaw = 0.0F;
            leftArm.roll = 0.0F;
        }
    }

    /* Fog Helpers */

    public static class Fog
    {
        public static boolean isMobEffectActive = false;
        public static boolean isOverworld(Camera camera) { return camera.getFocusedEntity().getWorld().getRegistryKey() == net.minecraft.world.World.OVERWORLD; }
        public static boolean isNether(Camera camera) { return camera.getFocusedEntity().getWorld().getRegistryKey() == net.minecraft.world.World.NETHER; }
        private static boolean isFluidFog(Camera camera) { return camera.getSubmersionType() != CameraSubmersionType.NONE; }
        private static boolean isEntityBlind(Camera camera)
        {
            return camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity) camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS);
        }

        private static int getRenderDistance()
        {
            int renderDistance = MinecraftClient.getInstance().options.renderDistance().get();
            int multiplier = renderDistance <= 6 ? 16 : 32;
            return renderDistance * multiplier;
        }

        private static void setTerrainFog(BackgroundRenderer.FogType fogMode)
        {
            if (fogMode != BackgroundRenderer.FogType.FOG_TERRAIN)
                return;

            float distance = Math.min(1024, getRenderDistance());
            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(distance * 0.8F);
        }

        private static void setHorizonFog(BackgroundRenderer.FogType fogMode)
        {
            if (fogMode != BackgroundRenderer.FogType.FOG_SKY)
                return;

            float distance = Math.min(512, getRenderDistance());
            RenderSystem.setShaderFogStart(distance * 0.25F);
            RenderSystem.setShaderFogEnd(distance);
        }

        private static void renderFog(BackgroundRenderer.FogType fogMode)
        {
            if (ModConfig.Candy.oldTerrainFog())
                setTerrainFog(fogMode);
            if (ModConfig.Candy.oldHorizonFog())
                setHorizonFog(fogMode);
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
        }

        // Overrides fog in the overworld
        public static void setupFog(Camera camera, BackgroundRenderer.FogType fogMode)
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
        public static void setupNetherFog(Camera camera, BackgroundRenderer.FogType fogMode)
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
        public static MatrixStack.Entry levelPoseStack;

        // Used to cache the current buffer source during the entity render cycle.
        // This is needed so we can end the batch early to apply flat lighting to vertices.
        public static VertexConsumerProvider.Immediate levelBufferSource;

        // Used to check if a model should be rendered in 2D.
        public static boolean isModelFlat(BakedModel model) { return !model.isSideLit(); }

        // Flattens an item to be as close to 2D as possible via scaling.
        public static void flatten(MatrixStack poseStack) { poseStack.scale(1.0F, 1.0F, 0.001F); }

        // Getter for checking if diffused lighting is disabled.
        public static boolean isLightingFlat() { return isRenderingFlat; }

        // Turns off diffused lighting.
        public static void disableDiffusedLighting()
        {
            levelBufferSource.draw();
            DiffuseLighting.disableGuiDepthLighting();
            isRenderingFlat = true;
        }

        // Turns on diffused lighting.
        public static void enableDiffusedLighting()
        {
            isRenderingFlat = false;
            levelBufferSource.draw();

            if (MinecraftClient.getInstance().world == null || levelPoseStack == null)
                return;

            if (MinecraftClient.getInstance().world.getDimensionEffects().isDarkened())
                DiffuseLighting.enableForLevel(levelPoseStack.getPositionMatrix());
            else
                DiffuseLighting.disableForLevel(levelPoseStack.getPositionMatrix());
        }

        // Used to change the normal based on which quad side we're rendering.
        public static void setNormalQuad(MatrixStack.Entry pose, BakedQuad quad)
        {
            pose.getNormalMatrix().loadIdentity();
            if (quad.getFace() == Direction.NORTH)
                pose.getNormalMatrix().multiply(-1.0F);
        }
    }
}
