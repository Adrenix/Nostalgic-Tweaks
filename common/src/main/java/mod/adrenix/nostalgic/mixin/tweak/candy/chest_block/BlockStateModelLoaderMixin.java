package mod.adrenix.nostalgic.mixin.tweak.candy.chest_block;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.CalendarUtil;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BlockStateModelLoader.class)
public abstract class BlockStateModelLoaderMixin
{
    /**
     * Dynamically changes the block state location for the vanilla chests.
     */
    @ModifyArg(
        method = "loadBlockStateDefinitions",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resources/FileToIdConverter;idToFile(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/resources/ResourceLocation;"
        )
    )
    private ResourceLocation nt_chest_block$modifyBlockStateLocation(ResourceLocation resourceLocation)
    {
        String minecraft = "minecraft";
        String namespace = resourceLocation.getNamespace();
        String path = resourceLocation.getPath();

        if (!namespace.equals(minecraft))
            return resourceLocation;

        namespace = switch (path)
        {
            case "chest" -> CandyTweak.OLD_CHEST.get() ? NostalgicTweaks.MOD_ID : minecraft;
            case "ender_chest" -> CandyTweak.OLD_ENDER_CHEST.get() ? NostalgicTweaks.MOD_ID : minecraft;
            case "trapped_chest" -> CandyTweak.OLD_TRAPPED_CHEST.get() ? NostalgicTweaks.MOD_ID : minecraft;
            default -> namespace;
        };

        if (namespace.equals(minecraft))
            return resourceLocation;

        if (CalendarUtil.isChristmasTime() && path.equals("chest"))
            return ResourceLocation.fromNamespaceAndPath(namespace, "christmas_chest");

        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
