package mod.adrenix.nostalgic.mixin.util.candy;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public abstract class ArmorMixinHelper
{
    /**
     * @return Whether an entity should be considered hurt.
     */
    public static boolean isEntityHurt(@Nullable LivingEntity entity)
    {
        if (entity == null)
            return false;

        return entity.hurtTime > 0 || entity.deathTime > 0;
    }

    /**
     * Get the vertex consumer to use when an entity is hurt.
     *
     * @param entity        The {@link LivingEntity} to check.
     * @param armorConsumer The standard armor {@link VertexConsumer}.
     * @param bufferSource  The {@link MultiBufferSource} to use.
     * @param armorLocation The armor's {@link ResourceLocation}.
     * @return A {@link VertexConsumer} to use for damaged armor.
     */
    public static VertexConsumer getDamagedConsumer(@Nullable LivingEntity entity, VertexConsumer armorConsumer, MultiBufferSource bufferSource, ResourceLocation armorLocation)
    {
        if (CandyTweak.OLD_DAMAGE_ARMOR_TINT.get() && isEntityHurt(entity))
            return bufferSource.getBuffer(RenderType.entityCutoutNoCullZOffset(armorLocation));

        return armorConsumer;
    }

    /**
     * Get the packed overlay to use when an entity is hurt.
     *
     * @param entity        The {@link LivingEntity} to check.
     * @param packedOverlay The packed overlay to use when the entity is not hurt.
     * @return The packed overlay to use for armor rendering.
     */
    public static int getDamagedPackedOverlay(@Nullable LivingEntity entity, int packedOverlay)
    {
        if (CandyTweak.OLD_DAMAGE_ARMOR_TINT.get() && isEntityHurt(entity))
            return OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(true));

        return packedOverlay;
    }
}
