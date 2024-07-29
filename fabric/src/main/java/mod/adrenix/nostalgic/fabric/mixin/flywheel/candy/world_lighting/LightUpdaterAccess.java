package mod.adrenix.nostalgic.fabric.mixin.flywheel.candy.world_lighting;

import com.jozufozu.flywheel.light.LightListener;
import com.jozufozu.flywheel.light.LightUpdater;
import com.jozufozu.flywheel.light.WeakContainmentMultiMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LightUpdater.class)
public interface LightUpdaterAccess
{
    @Accessor("chunks")
    WeakContainmentMultiMap<LightListener> nt$getChunks();
}