package mod.adrenix.nostalgic.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DiffuseLighting.class)
public abstract class LightingMixin
{
    /**
     * Brings back the old inverted inventory player lighting.
     * Controlled by the inverted player lighting tweak.
     */
    @Inject(method = "setupForEntityInInventory", at = @At("HEAD"), cancellable = true)
    private static void NT$onSetupForEntityInInventory(CallbackInfo callback)
    {
        if (ModConfig.Candy.invertPlayerLight())
        {
            RenderSystem.setShaderLights(new Vec3f(-2.0F, -3.0F, -1.0F), new Vec3f(-0.8F, -5.0F, -1.5F));
            callback.cancel();
        }
    }

    /**
     * Brings back the old inverted inventory block lighting.
     * Controlled by the inverted block lighting tweak.
     */
    @Inject(method = "setupFor3DItems", at = @At("HEAD"), cancellable = true)
    private static void NT$onSetupFor3DItems(CallbackInfo callback)
    {
        if (ModConfig.Candy.invertBlockLight())
        {
            RenderSystem.setupGui3DDiffuseLighting(new Vec3f(0.0F, 2.0F, 1.0F), new Vec3f(-1.7F, 4.0F, 1.5F));
            callback.cancel();
        }
    }
}
