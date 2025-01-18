package mod.adrenix.nostalgic.helper.candy;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public abstract class ArmorHelper
{
    /**
     * Check if the current tweak and entity context allows for the old damage armor tint.
     *
     * @param renderState The {@link HumanoidRenderState} to check.
     * @return Whether to use the old damage armor tint effect.
     */
    public static boolean useOldTint(@Nullable HumanoidRenderState renderState)
    {
        if (renderState == null)
            return false;

        return CandyTweak.OLD_DAMAGE_ARMOR_TINT.get() && renderState.hasRedOverlay;
    }

    /**
     * Get the vertex consumer to use when an entity is hurt.
     *
     * @param renderState   The {@link HumanoidRenderState} to check.
     * @param armorConsumer The standard armor {@link VertexConsumer}.
     * @param bufferSource  The {@link MultiBufferSource} to use.
     * @param armorLocation The armor's {@link ResourceLocation}.
     * @return A {@link VertexConsumer} to use for damaged armor.
     */
    public static VertexConsumer getDamagedConsumer(@Nullable HumanoidRenderState renderState, VertexConsumer armorConsumer, MultiBufferSource bufferSource, ResourceLocation armorLocation)
    {
        if (useOldTint(renderState))
            return bufferSource.getBuffer(RenderType.entityCutoutNoCullZOffset(armorLocation));

        return armorConsumer;
    }

    /**
     * Get the packed overlay to use when an entity is hurt.
     *
     * @param renderState   The {@link HumanoidRenderState} to check.
     * @param packedOverlay The packed overlay to use when the entity is not hurt.
     * @return The packed overlay to use for armor rendering.
     */
    public static int getDamagedPackedOverlay(@Nullable HumanoidRenderState renderState, int packedOverlay)
    {
        if (useOldTint(renderState))
            return OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(true));

        return packedOverlay;
    }
}
