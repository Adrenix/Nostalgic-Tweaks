package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{

    @ModifyReturnValue(
            method = "useAmbientOcclusion",
            at = @At("RETURN")
    )
    private static boolean nt_world_lighting$overrideSmoothLighting(boolean original)
    {
        if (CandyTweak.DISABLE_SMOOTH_LIGHTING.get())
            return false;
        return original;
    }
}
