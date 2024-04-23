package mod.adrenix.nostalgic.mixin.tweak.candy.grass_texture;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin
{
    /**
     * Dynamically changes the block model location for the vanilla grass block.
     */
    @ModifyVariable(
        method = "loadBlockModel",
        argsOnly = true,
        ordinal = 0,
        at = @At("HEAD")
    )
    private ResourceLocation nt_grass_texture$modifyBlockModelLocation(ResourceLocation resourceLocation)
    {
        String minecraft = "minecraft";
        String namespace = resourceLocation.getNamespace();
        String path = resourceLocation.getPath();
        String modId = NostalgicTweaks.MOD_ID;
        boolean isOldGrass = CandyTweak.OLD_GRASS_SIDE_TEXTURE.get();

        if (!namespace.equals(minecraft))
            return resourceLocation;

        if (path.equals("block/grass_block"))
            namespace = isOldGrass ? modId : minecraft;

        return new ResourceLocation(namespace, path);
    }
}
