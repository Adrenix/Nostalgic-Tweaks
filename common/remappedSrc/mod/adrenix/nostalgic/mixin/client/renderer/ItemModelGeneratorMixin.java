package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

/**
 * Helper methods were adapted from the Item-Model-Fix mod by
 * <a href="https://github.com/PepperCode1/Item-Model-Fix/">Pepper_Bell</a>.
 */

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin
{
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

    /**
     * Fixes the transparent gaps seen in held item models.
     * Controlled by the fix item gap tweak.
     */

    @Inject(method = "processFrames", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void NT$onProcessFrames
    (
        int tintIndex,
        String texture,
        Sprite sprite,
        CallbackInfoReturnable<List<ModelElement>> callback,
        Map<Direction, ModelElementFace> faceMap,
        List<ModelElement> blockElements
    )
    {
        try
        {
            if (!ModConfig.Candy.fixItemModelGaps())
                return;
        }
        // Captures NoClassDefFoundError if config dependency is missing which allows forge to reach missing dependency screen
        catch (Error ignored) {}


        for (ModelElement element : blockElements)
        {
            for (ModelElementFace face : element.faces.values())
                reduceUVs(face.textureData.uvs, sprite.getAnimationFrameDelta() + (NostalgicTweaks.isForge() ? 0.00193F : 0.0F));
        }
    }
}
