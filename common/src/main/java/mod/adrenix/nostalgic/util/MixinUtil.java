package mod.adrenix.nostalgic.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.mixin.duck.IReequipSlot;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class MixinUtil
{
    /* Mixin Utility Constants */

    public static final int PRIORITY = 999;

    /* World Candy Injection Helpers */

    public static class World
    {
        // Determines where the sun/moon should be rotated when rendering it.
        public static float getSunriseRotation(float vanilla)
        {
            return MixinConfig.Candy.oldSunriseAtNorth() ? 0.0F : vanilla;
        }

        // Checks if a chunk is on the edge of a square border render distance.
        public static int squareDistance(int chunkX, int chunkZ, int secX, int secZ)
        {
            int diffX = chunkX - secX;
            int diffY = chunkZ - secZ;
            return Math.max(Math.abs(diffX), Math.abs(diffY));
        }

        // Builds a sky disc for the far plane.
        public static void buildSkyDisc(BufferBuilder builder, float y)
        {
            float x = Math.signum(y) * 512.0F;
            RenderSystem.setShader(GameRenderer::getPositionShader);
            builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
            builder.vertex(0.0, y, 0.0).endVertex();

            for (int i = -180; i <= 180; i += 45)
                builder.vertex(x * Mth.cos((float) i * ((float) Math.PI / 180)), y, 512.0F * Mth.sin((float) i * ((float) Math.PI / 180))).endVertex();
            builder.end();
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

    /* Common Animation Injection Helpers */

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

    /* Common Fog Injection Helpers */

    public static class Fog
    {
        public static boolean isOverworld(Camera camera) { return camera.getEntity().getLevel().dimension() == Level.OVERWORLD; }
        public static boolean isNether(Camera camera) { return camera.getEntity().getLevel().dimension() == Level.NETHER; }
        private static boolean isFluidFog(Camera camera) { return camera.getFluidInCamera() != FogType.NONE; }
        private static boolean isEntityBlind(Camera camera)
        {
            return camera.getEntity() instanceof LivingEntity && ((LivingEntity) camera.getEntity()).hasEffect(MobEffects.BLINDNESS);
        }

        private static int getRenderDistance()
        {
            int renderDistance = Minecraft.getInstance().options.renderDistance;
            int multiplier = renderDistance <= 6 ? 16 : 32;
            return renderDistance * multiplier;
        }

        private static void setTerrainFog(FogRenderer.FogMode fogType)
        {
            if (fogType != FogRenderer.FogMode.FOG_TERRAIN)
                return;

            float distance = Math.min(1024, getRenderDistance());
            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(distance * 0.8F);
        }

        private static void setHorizonFog(FogRenderer.FogMode fogType)
        {
            if (fogType != FogRenderer.FogMode.FOG_SKY)
                return;

            float distance = Math.min(512, getRenderDistance());
            RenderSystem.setShaderFogStart(distance * 0.25F);
            RenderSystem.setShaderFogEnd(distance);
        }

        private static void renderFog(FogRenderer.FogMode fogType)
        {
            if (MixinConfig.Candy.oldTerrainFog())
                setTerrainFog(fogType);
            if (MixinConfig.Candy.oldHorizonFog())
                setHorizonFog(fogType);
        }

        // Overrides fog in the overworld
        public static void setupFog(Camera camera, FogRenderer.FogMode fogType)
        {
            if (isFluidFog(camera) || isEntityBlind(camera) || !isOverworld(camera))
                return;
            renderFog(fogType);
        }

        // Overrides fog in the nether
        public static void setupNetherFog(Camera camera, FogRenderer.FogMode fogType)
        {
            if (!MixinConfig.Candy.oldNetherFog() || isFluidFog(camera) || isEntityBlind(camera) || !isNether(camera))
                return;

            renderFog(fogType);
            RenderSystem.setShaderFogStart(0.0F);
        }
    }

    /* Common Item Injection Helpers */

    public static class Item
    {
        // Used to handle differences between forge and fabric when separating merged items
        public static Consumer<ItemStack> explodeStack(Consumer<ItemStack> consumer)
        {
            if (!MixinConfig.Candy.oldItemMerging())
                return consumer;
            return stack -> {
                ItemStack instance = stack.copy();
                instance.setCount(1);

                for (int i = 0; i < stack.getCount(); i++)
                    consumer.accept(instance);
            };
        }

        // Used to enhance the old reequipping logic
        public static ItemStack getLastItem(ItemStack originalItemStack, ItemStack rendererItemStack, ItemStack playerItemStack, IReequipSlot player)
        {
            // Item from main hand turns to air as soon as the player pulls it out. When this happens, the following strings appear in each property respectively.
            boolean isUnequipped = rendererItemStack.toString().equals("0 air") && playerItemStack.toString().equals("1 air");
            if (!MixinConfig.Animation.oldItemReequip() || !isUnequipped)
                return originalItemStack;

            return player.getLastItem();
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
