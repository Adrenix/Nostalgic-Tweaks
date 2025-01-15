package mod.adrenix.nostalgic.fabric.mixin.tweak.candy.model_gap_fix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.fabric.mixin.util.FabricModelGapFix;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Function;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin
{
    /* Unique */

    @Unique @Nullable private TextureAtlasSprite nt$sprite = null;

    /* Injections */

    /**
     * Gets and stores the model's texture atlas sprite so its atlas uv shrink ratio can be retrieved.
     */
    @ModifyExpressionValue(
        method = "generateBlockModel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/model/BlockModel;getMaterial(Ljava/lang/String;)Lnet/minecraft/client/resources/model/Material;"
        )
    )
    private Material nt_fabric_model_gap_fix$onGenerateBlockModel(Material material, Function<Material, TextureAtlasSprite> spriteGetter)
    {
        this.nt$sprite = spriteGetter.apply(material);

        return material;
    }

    /**
     * Fixes the gaps seen in held item models.
     */
    @ModifyReturnValue(
        method = "processFrames",
        at = @At("RETURN")
    )
    private List<BlockElement> nt_fabric_model_gap_fix$onProcessFrames(List<BlockElement> blockElements)
    {
        if (!CandyTweak.FIX_ITEM_MODEL_GAP.get() || this.nt$sprite == null)
            return blockElements;

        for (BlockElement element : blockElements)
        {
            for (BlockElementFace face : element.faces.values())
                FabricModelGapFix.apply(face.uv().uvs, this.nt$sprite.uvShrinkRatio());
        }

        this.nt$sprite = null;

        return blockElements;
    }
}
