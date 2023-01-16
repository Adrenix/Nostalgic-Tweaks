package mod.adrenix.nostalgic.fabric.mixin.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerFabricMixin
{
    /**
     * Fixes micro stutters and lag spikes when crossing chunk borders. The prevents the updating of both sky and block
     * light engines from updating sections since this is not necessary.
     *
     * Controlled by the fix chunk border lag tweak.
     */
    @Redirect(method = "method_38546", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/LevelLightEngine;updateSectionStatus(Lnet/minecraft/core/SectionPos;Z)V"))
    private void NT$onEnableChunkLight(LevelLightEngine engine, SectionPos pos, boolean isQueueEmpty)
    {
        if (!ModConfig.Candy.fixChunkBorderLag())
            engine.updateSectionStatus(pos, isQueueEmpty);
    }
}
