package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.ModTracker;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Helper methods were adapted from the Item-Model-Fix mod by
 * <a href="https://github.com/PepperCode1/Item-Model-Fix/">Pepper_Bell</a>.
 */

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin
{
    /* Mixin Utility */

    private static float reduce(float start, float end, float delta) { return (start - delta * end) / (1 - delta); }

    private static void reduceUVs(float[] uvs, float delta)
    {
        float centerU = (uvs[0] + uvs[2]) / 2.0F;
        float centerV = (uvs[1] + uvs[3]) / 2.0F;

        uvs[0] = reduce(uvs[0], centerU, delta);
        uvs[1] = reduce(uvs[1], centerV, delta);
        uvs[2] = reduce(uvs[2], centerU, delta);
        uvs[3] = reduce(uvs[3], centerV, delta);
    }

    /* Unique */

    @Unique private TextureAtlasSprite NT$atlasSprite = null;

    /* Injections */

    /**
     * Attempts to grab the texture atlas sprite associated with the sprite contents before its frames are processed.
     * A texture atlas sprite instance is needed so we can get the UV shrink ratio.
     *
     * This method no longer receives an atlas sprite after 1.19.2.
     */
    @Inject
    (
        locals = LocalCapture.CAPTURE_FAILSOFT,
        method = "generateBlockModel",
        slice = @Slice
        (
            from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockModel;getMaterial(Ljava/lang/String;)Lnet/minecraft/client/resources/model/Material;"),
            to = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z")
        ),
        at = @At
        (
            value = "INVOKE",
            target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"
        )
    )
    private void NT$onGenerateBlockModel
    (
        Function<Material, TextureAtlasSprite> spriteGetter,
        BlockModel model,
        CallbackInfoReturnable<BlockModel> callback,
        Map<?,?> map,
        List<?> list,
        int i,
        String name,
        Material material
    )
    {
        this.NT$atlasSprite = spriteGetter.apply(material);
    }

    /**
     * Fixes the transparent gaps seen in held item models.
     * Controlled by the fix item gap tweak.
     */
    @Inject(method = "processFrames", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void NT$onProcessFrames
    (
        int tintIndex,
        String texture,
        SpriteContents sprite,
        CallbackInfoReturnable<List<BlockElement>> callback,
        Map<Direction, BlockElementFace> faceMap,
        List<BlockElement> blockElements
    )
    {
        try
        {
            if (!ModConfig.Candy.fixItemModelGaps())
                return;
        }
        // Captures NoClassDefFoundError if config dependency is missing which allows forge to reach missing dependency screen
        catch (Error ignored) {}

        boolean isForge = NostalgicTweaks.isForge() && !ModTracker.OPTIFINE.isInstalled();

        if (this.NT$atlasSprite != null)
        {
            for (BlockElement element : blockElements)
            {
                for (BlockElementFace face : element.faces.values())
                    reduceUVs(face.uv.uvs, this.NT$atlasSprite.uvShrinkRatio() + (isForge ? 0.00193F : 0.0F));
            }

            this.NT$atlasSprite = null;
        }
    }
}
