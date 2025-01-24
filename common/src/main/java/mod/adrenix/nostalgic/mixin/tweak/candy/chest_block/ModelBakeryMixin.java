package mod.adrenix.nostalgic.mixin.tweak.candy.chest_block;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.CalendarUtil;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net/minecraft/client/resources/model/ModelBakery$ModelBakerImpl")
public abstract class ModelBakeryMixin
{
    /**
     * Dynamically changes the block model location for the vanilla chests.
     */
    @ModifyVariable(
        method = "getModel",
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
            return ResourceLocation.fromNamespaceAndPath(NostalgicTweaks.MOD_ID, "item/vanilla_ender_chest");

        if (isOldChest && !isOldTrappedChest && path.equals("item/trapped_chest"))
            return ResourceLocation.fromNamespaceAndPath(NostalgicTweaks.MOD_ID, "item/vanilla_trapped_chest");

        if (isOldChest && CalendarUtil.isChristmasTime())
        {
            if (path.equals("block/chest"))
                return ResourceLocation.fromNamespaceAndPath(namespace, "block/christmas_chest");

            if (path.equals("item/chest"))
                return ResourceLocation.fromNamespaceAndPath(namespace, "item/christmas_chest");
        }

        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
