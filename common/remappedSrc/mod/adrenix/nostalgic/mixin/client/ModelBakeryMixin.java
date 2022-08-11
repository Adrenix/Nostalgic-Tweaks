package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelLoader.class)
public abstract class ModelBakeryMixin
{
    /**
     * Dynamically changes the model to be loaded for vanilla chests.
     * Controlled by various old chest tweaks.
     */
    @ModifyVariable(method = "loadBlockModel", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Identifier NT$onLoadBlockModel(Identifier vanilla)
    {
        String minecraft = "minecraft";
        String namespace = vanilla.getNamespace();
        String path = vanilla.getPath();

        if (!namespace.equals(minecraft))
            return vanilla;

        namespace = switch (path)
        {
            case "block/chest", "item/chest" -> ModConfig.Candy.oldChest() ? NostalgicTweaks.MOD_ID : minecraft;
            case "block/ender_chest", "item/ender_chest" -> ModConfig.Candy.oldEnderChest() ? NostalgicTweaks.MOD_ID : minecraft;
            case "block/trapped_chest", "item/trapped_chest" -> ModConfig.Candy.oldTrappedChest() ? NostalgicTweaks.MOD_ID : minecraft;
            default -> namespace;
        };

        if (ModConfig.Candy.oldChest() && !ModConfig.Candy.oldEnderChest() && path.equals("item/ender_chest"))
            return new Identifier(NostalgicTweaks.MOD_ID, "item/vanilla_ender_chest");
        else if (ModConfig.Candy.oldChest() && !ModConfig.Candy.oldTrappedChest() && path.equals("item/trapped_chest"))
            return new Identifier(NostalgicTweaks.MOD_ID, "item/vanilla_trapped_chest");
        return new Identifier(namespace, path);
    }
}
