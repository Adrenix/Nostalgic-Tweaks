package mod.adrenix.nostalgic.forge.mixin.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerForgeMixin
{
    /* Shadows */

    @Shadow private ClientLevel level;

    /* Injections */

    /**
     * Fixes micro stutters and lag spikes when crossing chunk borders. The prevents the updating of both sky and block
     * light engines from updating sections since this is not necessary.
     *
     * Controlled by the fix chunk border lag tweak.
     */
    @Inject
    (
        method = "queueLightUpdate(Lnet/minecraft/network/protocol/game/ClientboundForgetLevelChunkPacket;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onEnableChunkLight(ClientboundForgetLevelChunkPacket packet, CallbackInfo callback)
    {
        if (ModConfig.Candy.fixChunkBorderLag())
        {
            this.level.queueLightUpdate(() ->
            {
                this.level.getLightEngine().enableLightSources(new ChunkPos(packet.getX(), packet.getZ()), false);
                this.level.setLightReady(packet.getX(), packet.getZ());
            });

            callback.cancel();
        }
    }
}
