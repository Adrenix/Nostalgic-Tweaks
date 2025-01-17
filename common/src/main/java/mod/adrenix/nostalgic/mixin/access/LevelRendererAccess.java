package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccess
{
    @Accessor("visibleEntityCount")
    int nt$getVisibleEntityCount();
}
