package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin
{
    /**
     * Helper methods were adapted from the Item-Model-Fix mod by Pepper_Bell.
     * https://github.com/PepperCode1/Item-Model-Fix/
     */

    private static float reduce(float start, float end, float delta)
    {
        return (start - delta * end) / (1 - delta);
    }

    private static void reduceUVs(float[] uvs, float delta)
    {
        float centerU = (uvs[0] + uvs[2]) / 2.0F;
        float centerV = (uvs[1] + uvs[3]) / 2.0F;

        uvs[0] = reduce(uvs[0], centerU, delta);
        uvs[1] = reduce(uvs[1], centerV, delta);
        uvs[2] = reduce(uvs[2], centerU, delta);
        uvs[3] = reduce(uvs[3], centerV, delta);
    }

    /**
     * Fixes the transparent gaps seen in held item models.
     * Controlled by the fix item gap toggle.
     */

    @Inject(method = "processFrames", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void onProcessFrames
    (
        int tintIndex,
        String texture,
        TextureAtlasSprite sprite,
        CallbackInfoReturnable<List<BlockElement>> callback,
        Map<Direction, BlockElementFace> faceMap,
        List<BlockElement> blockElements
    )
    {
        try
        {
            if (!MixinConfig.Candy.fixItemModelGaps())
                return;
        }
        // Captures NoClassDefFoundError if config dependency is missing which allows forge to finish loading
        catch (Error ignored) {}


        for (BlockElement element : blockElements)
        {
            for (BlockElementFace face : element.faces.values())
                reduceUVs(face.uv.uvs, sprite.uvShrinkRatio());
        }
    }
}
