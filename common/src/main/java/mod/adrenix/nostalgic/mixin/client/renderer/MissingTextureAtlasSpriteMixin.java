package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import dev.architectury.platform.Platform;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.client.RunUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Old missing texture tweak was originally contributed by forkiesassds on GitHub.
 * <br><br>
 * The original merged pull request has been completely changed. All references to {@code java.awt} classes have been
 * removed since the vanilla game does not use anything from that package due to compatibility issues.
 */

@Mixin(MissingTextureAtlasSprite.class)
public abstract class MissingTextureAtlasSpriteMixin
{
    /* Shadows */

    @Shadow @Nullable private static DynamicTexture missingTexture;

    /* Injections */

    /**
     * Instructions for changing the missing texture image. Controlled by the old missing texture tweak.
     */
    @Inject(
        method = "generateMissingImage",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void NT$onGenerateMissingImage(int width, int height, CallbackInfoReturnable<NativeImage> callback)
    {
        if (ModConfig.Candy.oldMissingTexture() != TweakVersion.MissingTexture.MODERN)
        {
            Optional<NativeImage> image = Optional.empty();

            try
            {
                image = MissingTextureAtlasSpriteMixin.getImage();
            }
            catch (IOException exception)
            {
                NostalgicTweaks.LOGGER.error("Could not generate missing texture", exception);
            }

            image.ifPresent(callback::setReturnValue);
        }
    }

    /**
     * Changes the sprite dimensions for the missing texture image. This is important since not changing sprite
     * dimensions will cause visual issues for modified textures. Controlled by the old missing texture tweak.
     */
    @ModifyConstant(
        method = "create",
        constant = @Constant(intValue = 16)
    )
    private static int NT$onCreate(int vanilla)
    {
        if (ModConfig.Candy.oldMissingTexture() == TweakVersion.MissingTexture.MODERN)
            return vanilla;

        return switch (ModConfig.Candy.oldMissingTexture())
        {
            case BETA, R15 -> 64;
            default -> 16;
        };
    }

    /**
     * Adds config runnable that changes the missing texture image after the config is saved. Not controlled by any
     * tweak.
     */
    @Inject(
        method = "<clinit>",
        at = @At("TAIL")
    )
    private static void NT$onStaticInit(CallbackInfo callback)
    {
        RunUtil.onSave.add(MissingTextureAtlasSpriteMixin::update);
    }

    /* Mixin Utility */

    /**
     * Resets the missing texture so that the game regenerates the missing texture image after a change to the old
     * missing texture tweak is made.
     */
    private static void update()
    {
        missingTexture = null;
    }

    /**
     * Loads a missing texture image based on the old missing texture tweak.
     *
     * @return An optional native image.
     */
    private static Optional<NativeImage> getImage() throws IOException
    {
        String path = switch (ModConfig.Candy.oldMissingTexture())
        {
            case BETA -> TextureLocation.MISSING_BETA;
            case R15 -> TextureLocation.MISSING_1_5;
            case R16_R112 -> TextureLocation.MISSING_1_6_1_12;
            default -> "";
        };

        Optional<Path> resource = Platform.getMod(NostalgicTweaks.MOD_ID).findResource(path);

        if (resource.isPresent())
            return Optional.of(NativeImage.read(resource.get().toUri().toURL().openStream()));

        return Optional.empty();
    }
}
