package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    /**
     * Brings back the old nether shading.
     * Controlled by the old lighting tweak.
     */
    @Redirect
    (
        method = "getShade",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;constantAmbientLight()Z"
        )
    )
    private boolean NT$onGetNetherShade(DimensionSpecialEffects instance)
    {
        if (MixinConfig.Candy.oldNetherLighting() && Minecraft.getInstance().level != null && Minecraft.getInstance().level.dimension() == Level.NETHER)
            return false;
        return instance.constantAmbientLight();
    }
}
