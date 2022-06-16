package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin
{
    /**
     * Dynamically changes the model to be loaded for vanilla chests.
     * Controlled by various old chest tweaks.
     */
    @ModifyVariable(method = "loadBlockModel", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private ResourceLocation NT$onLoadBlockModel(ResourceLocation vanilla)
    {
        String minecraft = "minecraft";
        String namespace = vanilla.getNamespace();
        String path = vanilla.getPath();

        if (!namespace.equals(minecraft))
            return vanilla;

        namespace = switch (path)
        {
            case "block/chest", "item/chest" -> MixinConfig.Candy.oldChest() ? NostalgicTweaks.MOD_ID : minecraft;
            case "block/ender_chest", "item/ender_chest" -> MixinConfig.Candy.oldEnderChest() ? NostalgicTweaks.MOD_ID : minecraft;
            case "block/trapped_chest", "item/trapped_chest" -> MixinConfig.Candy.oldTrappedChest() ? NostalgicTweaks.MOD_ID : minecraft;
            default -> namespace;
        };

        if (MixinConfig.Candy.oldChest() && !MixinConfig.Candy.oldEnderChest() && path.equals("item/ender_chest"))
            return new ResourceLocation(NostalgicTweaks.MOD_ID, "item/vanilla_ender_chest");
        else if (MixinConfig.Candy.oldChest() && !MixinConfig.Candy.oldTrappedChest() && path.equals("item/trapped_chest"))
            return new ResourceLocation(NostalgicTweaks.MOD_ID, "item/vanilla_trapped_chest");
        return new ResourceLocation(namespace, path);
    }
}
