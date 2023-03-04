package mod.adrenix.nostalgic.fabric.mixin.flywheel;

import com.jozufozu.flywheel.light.LightListener;
import com.jozufozu.flywheel.light.LightUpdater;
import com.jozufozu.flywheel.light.WeakContainmentMultiMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LightUpdater.class)
public interface LightUpdaterAccessor
{
    @Accessor("chunks") WeakContainmentMultiMap<LightListener> NT$getChunks();
}
