package mod.adrenix.nostalgic.mixin.tweak.candy.chest_block;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.CalendarUtil;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin
{
    /**
     * Dynamically changes the block state location for the vanilla chests.
     */
    @ModifyArg(
        method = "loadModel",
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
            return new ResourceLocation(namespace, "christmas_chest");

        return new ResourceLocation(namespace, path);
    }

    /**
     * Dynamically changes the block model location for the vanilla chests.
     */
    @ModifyVariable(
        method = "loadBlockModel",
        argsOnly = true,
        ordinal = 0,
        at = @At("HEAD")
    )
    private ResourceLocation nt_chest_block$modifyBlockModelLocation(ResourceLocation resourceLocation)
    {
        String minecraft = "minecraft";
        String namespace = resourceLocation.getNamespace();
        String path = resourceLocation.getPath();
        String modId = NostalgicTweaks.MOD_ID;

        boolean isOldChest = CandyTweak.OLD_CHEST.get();
        boolean isOldEnderChest = CandyTweak.OLD_ENDER_CHEST.get();
        boolean isOldTrappedChest = CandyTweak.OLD_TRAPPED_CHEST.get();

        if (!namespace.equals(minecraft))
            return resourceLocation;

        namespace = switch (path)
        {
            case "block/chest", "item/chest" -> isOldChest ? modId : minecraft;
            case "block/ender_chest", "item/ender_chest" -> isOldEnderChest ? modId : minecraft;
            case "block/trapped_chest", "item/trapped_chest" -> isOldTrappedChest ? modId : minecraft;
            default -> namespace;
        };

        if (isOldChest && !isOldEnderChest && path.equals("item/ender_chest"))
            return new ResourceLocation(NostalgicTweaks.MOD_ID, "item/vanilla_ender_chest");

        if (isOldChest && !isOldTrappedChest && path.equals("item/trapped_chest"))
            return new ResourceLocation(NostalgicTweaks.MOD_ID, "item/vanilla_trapped_chest");

        if (isOldChest && CalendarUtil.isChristmasTime())
        {
            if (path.equals("block/chest"))
                return new ResourceLocation(namespace, "block/christmas_chest");

            if (path.equals("item/chest"))
                return new ResourceLocation(namespace, "item/christmas_chest");
        }

        return new ResourceLocation(namespace, path);
    }
}
