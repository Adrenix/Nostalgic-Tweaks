package mod.adrenix.nostalgic.mixin.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.client.RunUtil;
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

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Original missing texture logic contributed by forkiesassds on GitHub.
 *
 * Parts of the merged pull request has been modified. Notably; fixing typos, renaming variables, and creating
 * functional programming methods to prevent the need of restarting the game when the tweak changes.
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
     * @return A texture size that is dependent on the old missing texture tweak.
     */
    private static int getTextureSize()
    {
        TweakVersion.MissingTexture missingTexture = ModConfig.Candy.oldMissingTexture();
        boolean is64 = missingTexture == TweakVersion.MissingTexture.BETA || missingTexture == TweakVersion.MissingTexture.R15;
        return is64 ? 64 : 16;
    }

    /**
     * Generates a missing texture image based on the old missing texture tweak.
     * @return A new native image instance.
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private static NativeImage getImage()
    {
        int textureSize = MissingTextureAtlasSpriteMixin.getTextureSize();
        NativeImage nativeImage = new NativeImage(textureSize, textureSize, false);
        TweakVersion.MissingTexture missingTexture = ModConfig.Candy.oldMissingTexture();

        if (missingTexture == TweakVersion.MissingTexture.BETA || missingTexture == TweakVersion.MissingTexture.R15)
        {
            BufferedImage missingTex = new BufferedImage(64, 64, 2);
            Graphics graphics = missingTex.getGraphics();

            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, 64, 64);
            graphics.setColor(Color.BLACK);

            if (missingTexture == TweakVersion.MissingTexture.BETA)
                graphics.drawString("missingtex", 1, 10);
            else
            {
                int curX = 10;
                int curY = 0;

                while (curX < 64)
                {
                    String stringToDraw = curY++ % 2 == 0 ? "missing" : "texture";
                    graphics.drawString(stringToDraw, 1, curX);
                    curX += graphics.getFont().getSize();

                    if (curY % 2 == 0)
                        curX += 5;
                }
            }

            graphics.dispose();

            for (int xPos = 0; xPos < 64; xPos++)
            {
                for (int yPos = 0; yPos < 64; yPos++)
                    nativeImage.setPixelRGBA(xPos, yPos, missingTex.getRGB(xPos, yPos));
            }
        }
        else
        {
            int col1 = -16777216;
            int col2 = -524040;

            for (int xPos = 0; xPos < 16; xPos++)
            {
                for (int yPos = 0; yPos < 16; yPos++)
                {
                    if (xPos < 8 ^ yPos < 8)
                    {
                        nativeImage.setPixelRGBA(yPos, xPos, col1);
                        continue;
                    }

                    nativeImage.setPixelRGBA(yPos, xPos, col2);
                }
            }
        }

        nativeImage.untrack();

        return nativeImage;
    }

    /**
     * Instructions for changing the missing texture image.
     * Dependent on the current old missing texture tweak.
     */
    private static void update()
    {
        if (ModConfig.Candy.oldMissingTexture() != TweakVersion.MissingTexture.MODERN)
        {
            int textureSize = MissingTextureAtlasSpriteMixin.getTextureSize();

            ImmutableList<AnimationFrame> frames = ImmutableList.of(new AnimationFrame(0, -1));
            AnimationMetadataSection section = new AnimationMetadataSection(frames, textureSize, textureSize, 1, false);

            MISSING_IMAGE_DATA = new LazyLoadedValue<>(MissingTextureAtlasSpriteMixin::getImage);
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
     * Changes the missing texture image.
     * Controlled by the old missing texture tweak.
     */
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void NT$onStaticInit(CallbackInfo callback)
    {
        NT$MODERN_MISSING_IMAGE_DATA = MISSING_IMAGE_DATA;
        NT$MODERN_INFO = INFO;

        MissingTextureAtlasSpriteMixin.update();
        RunUtil.onSave.add(MissingTextureAtlasSpriteMixin::update);
    }
}
