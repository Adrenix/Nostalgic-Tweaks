package mod.adrenix.nostalgic.mixin.tweak.candy.uncap_title_fps;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.platform.Window;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /* Shadows */

    @Shadow
    public abstract Window getWindow();

    @Shadow @Final public Options options;
    @Shadow @Nullable public ClientLevel level;

    /* Injections */

    /**
     * Uncaps the framerate limit imposed on the title screen.
     */
    @ModifyReturnValue(
        method = "runTick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/platform/FramerateLimitTracker;getFramerateLimit()I"
        )
    )
    private int nt_uncap_title_fps$modifyLimit(int framerate)
    {
        if (this.level != null)
            return framerate;

        return CandyTweak.UNCAP_TITLE_FPS.get() ? Math.max(this.options.framerateLimit().get(), 60) : framerate;
    }
}
