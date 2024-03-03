package mod.adrenix.nostalgic.util.common.asset;

import net.minecraft.resources.ResourceLocation;

public interface ModSprite
{
    ResourceLocation OVERLAY = ModAsset.sprite("overlay/border");
    ResourceLocation RECIPE_BUTTON_SMALL = ModAsset.sprite("recipe_book/button_small");
    ResourceLocation RECIPE_BUTTON_LARGE = ModAsset.sprite("recipe_book/button_large");
    ResourceLocation RECIPE_BUTTON_SMALL_HIGHLIGHTED = ModAsset.sprite("recipe_book/button_small_highlighted");
    ResourceLocation RECIPE_BUTTON_LARGE_HIGHLIGHTED = ModAsset.sprite("recipe_book/button_large_highlighted");

    static ResourceLocation icon(String path)
    {
        return ModAsset.sprite("icon/" + path);
    }
}
