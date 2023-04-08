package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin
{
    /**
     * Dynamically changes the block state JSON for vanilla chests. Controlled by various old chest tweaks.
     */
    @Redirect(
        method = "loadModel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resources/ResourceLocation;getNamespace()Ljava/lang/String;"
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/block/model/BlockModelDefinition$Context;setDefinition(Lnet/minecraft/world/level/block/state/StateDefinition;)V"
            )
        )
    )
    private String NT$onLoadModel(ResourceLocation blockStateLocation)
    {
        String minecraft = "minecraft";
        String namespace = blockStateLocation.getNamespace();
        String path = blockStateLocation.getPath();

        if (!namespace.equals(minecraft))
            return namespace;

        namespace = switch (path)
        {
            case "chest" -> ModConfig.Candy.oldChest() ? NostalgicTweaks.MOD_ID : minecraft;
            case "ender_chest" -> ModConfig.Candy.oldEnderChest() ? NostalgicTweaks.MOD_ID : minecraft;
            case "trapped_chest" -> ModConfig.Candy.oldTrappedChest() ? NostalgicTweaks.MOD_ID : minecraft;
            default -> namespace;
        };

        return namespace;
    }

    /**
     * Dynamically changes the model to be loaded for vanilla chests. Controlled by various old chest tweaks.
     */
    @ModifyVariable(
        method = "loadBlockModel",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private ResourceLocation NT$onLoadBlockModel(ResourceLocation vanilla)
    {
        String minecraft = "minecraft";
        String namespace = vanilla.getNamespace();
        String path = vanilla.getPath();
        String nt = NostalgicTweaks.MOD_ID;

        if (!namespace.equals(minecraft))
            return vanilla;

        namespace = switch (path)
        {
            case "block/chest", "item/chest" -> ModConfig.Candy.oldChest() ? nt : minecraft;
            case "block/ender_chest", "item/ender_chest" -> ModConfig.Candy.oldEnderChest() ? nt : minecraft;
            case "block/trapped_chest", "item/trapped_chest" -> ModConfig.Candy.oldTrappedChest() ? nt : minecraft;
            default -> namespace;
        };

        if (ModConfig.Candy.oldChest() && !ModConfig.Candy.oldEnderChest() && path.equals("item/ender_chest"))
            return new ResourceLocation(NostalgicTweaks.MOD_ID, "item/vanilla_ender_chest");
        else if (ModConfig.Candy.oldChest() && !ModConfig.Candy.oldTrappedChest() && path.equals("item/trapped_chest"))
            return new ResourceLocation(NostalgicTweaks.MOD_ID, "item/vanilla_trapped_chest");

        return new ResourceLocation(namespace, path);
    }
}
