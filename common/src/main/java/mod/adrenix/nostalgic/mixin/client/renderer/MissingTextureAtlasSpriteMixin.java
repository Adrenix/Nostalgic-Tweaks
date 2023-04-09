package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.client.RunUtil;
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

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Original missing texture logic contributed by forkiesassds on GitHub.
 * <p>
 * Parts of the merged pull request has been modified. Notably, renaming variables and creating functional programming
 * methods to prevent the need of restarting the game when the tweak changes.
 */

@Mixin(MissingTextureAtlasSprite.class)
public abstract class MissingTextureAtlasSpriteMixin
{
    /* Injections */

    @Shadow @Nullable private static DynamicTexture missingTexture;

    /**
     * Instructions for changing the missing texture image. Dependent on the current old missing texture tweak.
     */
    @Inject(
        method = "generateMissingImage",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void NT$onGenerateMissingImage(int width, int height, CallbackInfoReturnable<NativeImage> callback)
    {
        if (ModConfig.Candy.oldMissingTexture() != TweakVersion.MissingTexture.MODERN)
            callback.setReturnValue(MissingTextureAtlasSpriteMixin.getImage());
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

        return MissingTextureAtlasSpriteMixin.getTextureSize();
    }

    /**
     * Adds a config runnable that changes the missing texture image after the config is saved. Not controlled by any
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
     * @return A texture size that is dependent on the old missing texture tweak.
     */
    private static int getTextureSize()
    {
        TweakVersion.MissingTexture missingTexture = ModConfig.Candy.oldMissingTexture();
        boolean is64 = missingTexture == TweakVersion.MissingTexture.BETA ||
                       missingTexture == TweakVersion.MissingTexture.R15;
        return is64 ? 64 : 16;
    }

    /**
     * Generates a missing texture image based on the old missing texture tweak.
     *
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

        return nativeImage;
    }
}
