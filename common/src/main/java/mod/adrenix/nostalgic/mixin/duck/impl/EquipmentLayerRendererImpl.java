package mod.adrenix.nostalgic.mixin.duck.impl;

import mod.adrenix.nostalgic.mixin.duck.ArmorLayerState;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererImpl implements ArmorLayerState
{
    @Unique @Nullable private HumanoidRenderState nt$renderState = null;

    @Override
    public void nt$setRenderState(HumanoidRenderState state)
    {
        this.nt$renderState = state;
    }

    @Override
    @Nullable
    public HumanoidRenderState nt$getRenderState()
    {
        return this.nt$renderState;
    }
}
