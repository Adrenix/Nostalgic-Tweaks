package mod.adrenix.nostalgic.mixin.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.client.RunUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Old missing texture tweak was originally contributed by forkiesassds on GitHub.
 * <br><br>
 * The original merged pull request has been completely changed. All references to {@code java.awt} classes have been
 * removed since the vanilla game does not use anything from that package due to compatibility issues.
 */

@SuppressWarnings("deprecation")
@Mixin(MissingTextureAtlasSprite.class)
public abstract class MissingTextureAtlasSpriteMixin
{
    /* Modern Cache */

    @Unique private static LazyLoadedValue<NativeImage> NT$MODERN_MISSING_IMAGE_DATA;
    @Unique private static TextureAtlasSprite.Info NT$MODERN_INFO;

    /* Shadows */

    @Shadow @Final @Mutable private static LazyLoadedValue<NativeImage> MISSING_IMAGE_DATA;
    @Shadow @Final @Mutable private static TextureAtlasSprite.Info INFO;
    @Shadow @Final private static ResourceLocation MISSING_TEXTURE_LOCATION;

    /* Mixin Utility */

    /**
     * Loads a missing texture image based on the old missing texture tweak.
     *
     * @return An optional native image.
     */
    private static Optional<NativeImage> NT$getImage() throws Exception
    {
        String path = switch (ModConfig.Candy.oldMissingTexture())
        {
            case BETA -> TextureLocation.MISSING_BETA;
            case R15 -> TextureLocation.MISSING_1_5;
            case R16_R112 -> TextureLocation.MISSING_1_6_1_12;
            default -> "";
        };

        Optional<Path> resource = TextureLocation.findResource.apply(path);

        if (resource.isPresent())
            return Optional.of(NativeImage.read(Files.newInputStream(resource.get())));

        return Optional.empty();
    }

    /**
     * Instructions for changing the missing texture image. Controlled by the old missing texture tweak.
     */
    private static void NT$update()
    {
        Optional<NativeImage> image = Optional.empty();

        if (ModConfig.Candy.oldMissingTexture() != TweakVersion.MissingTexture.MODERN)
        {
            try
            {
                image = NT$getImage();
            }
            catch (Exception exception)
            {
                NostalgicTweaks.LOGGER.error("Could not generate missing texture\n%s", exception);
            }
        }

        if (image.isPresent() && ModConfig.Candy.oldMissingTexture() != TweakVersion.MissingTexture.MODERN)
        {
            int textureSize = switch (ModConfig.Candy.oldMissingTexture())
            {
                case BETA, R15 -> 64;
                default -> 16;
            };

            ImmutableList<AnimationFrame> frames = ImmutableList.of(new AnimationFrame(0, -1));
            AnimationMetadataSection section = new AnimationMetadataSection(frames, textureSize, textureSize, 1, false);

            MISSING_IMAGE_DATA = new LazyLoadedValue<>(image::get);
            INFO = new TextureAtlasSprite.Info(MISSING_TEXTURE_LOCATION, textureSize, textureSize, section);
        }
        else
        {
            MISSING_IMAGE_DATA = NT$MODERN_MISSING_IMAGE_DATA;
            INFO = NT$MODERN_INFO;
        }
    }

    /* Injections */

    /**
     * Changes the missing texture image. Controlled by the old missing texture tweak.
     */
    @Inject(
        method = "<clinit>",
        at = @At("TAIL")
    )
    private static void NT$onStaticInit(CallbackInfo callback)
    {
        NT$MODERN_MISSING_IMAGE_DATA = MISSING_IMAGE_DATA;
        NT$MODERN_INFO = INFO;

        MissingTextureAtlasSpriteMixin.NT$update();
        RunUtil.onSave.add(MissingTextureAtlasSpriteMixin::NT$update);
    }
}
