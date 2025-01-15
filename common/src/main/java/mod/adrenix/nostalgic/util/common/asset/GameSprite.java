package mod.adrenix.nostalgic.util.common.asset;

import net.minecraft.resources.ResourceLocation;

public interface GameSprite
{
    ResourceLocation BUTTON = GameAsset.sprite("widget/button");
    ResourceLocation BUTTON_DISABLED = GameAsset.sprite("widget/button_disabled");
    ResourceLocation BUTTON_HIGHLIGHTED = GameAsset.sprite("widget/button_highlighted");
    ResourceLocation SLIDER = GameAsset.sprite("widget/slider");
    ResourceLocation SLIDER_HANDLE = GameAsset.sprite("widget/slider_handle");
    ResourceLocation SLIDER_HANDLE_HIGHLIGHTED = GameAsset.sprite("widget/slider_handle_highlighted");
    ResourceLocation FULL_HEART = GameAsset.sprite("hud/heart/full");
    ResourceLocation HALF_HEART = GameAsset.sprite("hud/heart/half");
    ResourceLocation EMPTY_HEART = GameAsset.sprite("hud/heart/container");
}
