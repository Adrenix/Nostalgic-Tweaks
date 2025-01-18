package mod.adrenix.nostalgic.mixin.duck;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.jetbrains.annotations.Nullable;

public interface ArmorLayerState
{
    void nt$setRenderState(@Nullable HumanoidRenderState state);

    @Nullable
    HumanoidRenderState nt$getRenderState();
}
