package mod.adrenix.nostalgic.mixin.tweak.candy.missing_texture;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.platform.NativeImage;
import dev.architectury.platform.Platform;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.MissingTexture;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Old missing texture tweak was originally contributed by forkiesassds on GitHub.
 */
@Mixin(MissingTextureAtlasSprite.class)
public abstract class MissingTextureAtlasSpriteMixin
{
    /* Shadow */

    @Shadow private static @Nullable DynamicTexture missingTexture;

    /* Injections */

    /**
     * Instructions for changing the missing texture image.
     */
    @ModifyReturnValue(
        method = "generateMissingImage",
        at = @At("RETURN")
    )
    private static NativeImage nt_missing_texture$modifyMissingImage(NativeImage nativeImage)
    {
        if (CandyTweak.OLD_MISSING_TEXTURE.get() == MissingTexture.MODERN)
            return nativeImage;

        Optional<NativeImage> image = Optional.empty();

        try
        {
            image = nt_missing_texture$get();
        }
        catch (Exception exception)
        {
            NostalgicTweaks.LOGGER.error("Could not generate missing texture\n%s", exception);
        }

        return image.orElse(nativeImage);
    }

    /**
     * Changes the sprite dimensions for the missing texture image. This is important since not changing sprite
     * dimensions will cause visual issues for modified textures.
     */
    @ModifyExpressionValue(
        method = "create",
        at = @At(
            value = "CONSTANT",
            args = "intValue=16"
        )
    )
    private static int nt_missing_texture$modifyImageDimension(int vanilla)
    {
        return switch (CandyTweak.OLD_MISSING_TEXTURE.get())
        {
            case BETA, R15 -> 64;
            default -> vanilla;
        };
    }

    /**
     * Adds config runnable that changes the missing texture image after the config is saved.
     */
    @Inject(
        method = "<clinit>",
        at = @At("TAIL")
    )
    private static void nt_missing_texture$addOnSave(CallbackInfo callback)
    {
        AfterConfigSave.addInstruction(MissingTextureAtlasSpriteMixin::nt_missing_texture$clearMissingTexture);
    }

    /**
     * Resets the missing texture so that the game regenerates the missing texture image after a change to the old
     * missing texture tweak is made.
     */
    @Unique
    private static void nt_missing_texture$clearMissingTexture()
    {
        if (AfterConfigSave.areChunksGoingToReload())
            missingTexture = null;
    }

    /**
     * Loads a missing texture image based on the old missing texture tweak.
     *
     * @return An {@link Optional} {@link NativeImage}.
     * @throws IOException If the old missing texture location can't be read.
     */
    @Unique
    private static Optional<NativeImage> nt_missing_texture$get() throws IOException
    {
        String path = switch (CandyTweak.OLD_MISSING_TEXTURE.get())
        {
            case BETA -> TextureLocation.MISSING_BETA;
            case R15 -> TextureLocation.MISSING_1_5;
            case R16_R112 -> TextureLocation.MISSING_1_6_1_12;
            default -> "";
        };

        Optional<Path> resource = Platform.getMod(NostalgicTweaks.MOD_ID).findResource(path);

        if (resource.isPresent())
            return Optional.of(NativeImage.read(Files.newInputStream(resource.get())));

        return Optional.empty();
    }
}
