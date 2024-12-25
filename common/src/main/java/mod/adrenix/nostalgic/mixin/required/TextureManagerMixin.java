package mod.adrenix.nostalgic.mixin.required;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.client.renderer.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin
{
    /**
     * On rare occasions, the light map will fail to load when the title screen renders the splash text on the title
     * screen. This mixin tracks when (and if) the light map is registered so further debugging can be performed and
     * determine where and how the light map fails to load.
     */
    @ModifyExpressionValue(
        method = "register(Ljava/lang/String;Lnet/minecraft/client/renderer/texture/DynamicTexture;)Lnet/minecraft/resources/ResourceLocation;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
        )
    )
    private String nt_debug$onRegisterDynamicTexture(String location)
    {
        if (location.contains("light_map"))
            NostalgicTweaks.LOGGER.info("[Dynamic Texture] Registered: " + location);

        return location;
    }
}
