package mod.adrenix.nostalgic.mixin.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.awt.image.BufferedImage;

@Mixin(MissingTextureAtlasSprite.class)
public class MissingTextureAtlasSpriteMixin
{
    private static TweakVersion.OldMissingTexture missingTextureType = ModConfig.Candy.oldMissingTexture();
    private static int textureSize = missingTextureType == TweakVersion.OldMissingTexture.B14 || missingTextureType == TweakVersion.OldMissingTexture.R15 ? 64 : 16;

    @Shadow private static TextureAtlasSprite.Info INFO;
    @Shadow private static LazyLoadedValue<NativeImage> MISSING_IMAGE_DATA;
    @Shadow private static final ResourceLocation MISSING_TEXTURE_LOCATION = new ResourceLocation("missingno");

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void injectStaticInit(CallbackInfo ci)
    {
        if (missingTextureType != TweakVersion.OldMissingTexture.MODERN)
        {
            MISSING_IMAGE_DATA = new LazyLoadedValue<NativeImage>(() ->
            {
                NativeImage nativeImage = new NativeImage(textureSize, textureSize, false);
                if (missingTextureType == TweakVersion.OldMissingTexture.B14 || missingTextureType == TweakVersion.OldMissingTexture.R15)
                {
                    BufferedImage missingtex = new BufferedImage(64, 64, 2);
                    Graphics graphics = missingtex.getGraphics();
                    graphics.setColor(Color.WHITE);
                    graphics.fillRect(0, 0, 64, 64);
                    graphics.setColor(Color.BLACK);
                    if (missingTextureType == TweakVersion.OldMissingTexture.B14)
                    {
                        graphics.drawString("missingtex", 1, 10);
                    }
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
                            {
                                curX += 5;
                            }
                        }
                    }
                    graphics.dispose();
                    for (int xPos = 0; xPos < 64; xPos++)
                    {
                        for (int yPos = 0; yPos < 64; yPos++)
                        {
                            nativeImage.setPixelRGBA(xPos, yPos, missingtex.getRGB(xPos, yPos));
                        }
                    }
                }
                else
                {
                    int col1 = -16777216;
                    int col2 = -524040;
                    for (int xPos = 0; xPos < 16; ++xPos)
                    {
                        for (int yPos = 0; yPos < 16; ++yPos)
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
            });
            INFO = new TextureAtlasSprite.Info(MISSING_TEXTURE_LOCATION, textureSize, textureSize, new AnimationMetadataSection(ImmutableList.of(new AnimationFrame(0, -1)), textureSize, textureSize, 1, false));
        }
    }
}
