package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.client.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionSpecialEffects.class)
public abstract class DimensionSpecialEffectsMixin
{
    /**
     * Changes the cloud height, which is dynamically set by the user.
     * Controlled by the old cloud height tweak.
     */
    @Inject(method = "getCloudHeight", at = @At("HEAD"), cancellable = true)
    private void NT$onGetCloudHeight(CallbackInfoReturnable<Float> callback)
    {
        Minecraft minecraft = Minecraft.getInstance();
        int height = ModConfig.Candy.getCloudHeight();

        boolean isOverworld = minecraft.level != null && minecraft.level.dimension().equals(Level.OVERWORLD);
        boolean isOldHeight = height != 192;

        if (isOverworld && isOldHeight)
            callback.setReturnValue((float) height);
    }
}
