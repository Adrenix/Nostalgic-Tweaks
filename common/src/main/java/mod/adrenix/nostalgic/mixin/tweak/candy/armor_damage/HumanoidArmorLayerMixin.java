package mod.adrenix.nostalgic.mixin.tweak.candy.armor_damage;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.mixin.duck.ArmorLayerState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<S extends HumanoidRenderState>
{
    /* Shadows */

    @Shadow @Final private EquipmentLayerRenderer equipmentRenderer;

    /* Injections */

    /**
     * Tracks the entity wearing armor.
     */
    @Inject(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
        at = @At("HEAD")
    )
    private void nt_armor_damage$onRenderHead(PoseStack poseStack, MultiBufferSource buffer, int packedLight, S renderState, float yRot, float xRot, CallbackInfo callback)
    {
        ((ArmorLayerState) this.equipmentRenderer).nt$setRenderState(renderState);
    }

    /**
     * Clears entity tracking field.
     */
    @Inject(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
        at = @At("RETURN")
    )
    private void nt_armor_damage$onRenderReturn(PoseStack poseStack, MultiBufferSource buffer, int packedLight, S renderState, float yRot, float xRot, CallbackInfo callback)
    {
        ((ArmorLayerState) this.equipmentRenderer).nt$setRenderState(null);
    }
}
