package mod.adrenix.nostalgic.util.common.asset;

import mod.adrenix.nostalgic.util.common.sprite.GuiSprite;
import mod.adrenix.nostalgic.util.common.sprite.SpriteAtlas;

public interface GameSprite
{
    GuiSprite BUTTON = GuiSprite.nineSlice(SpriteAtlas.fromGui("widgets"), 0, 66, 200, 20, 3);
    GuiSprite BUTTON_DISABLED = GuiSprite.nineSlice(SpriteAtlas.fromGui("widgets"), 0, 46, 200, 20, 1);
    GuiSprite BUTTON_HIGHLIGHTED = GuiSprite.nineSlice(SpriteAtlas.fromGui("widgets"), 0, 86, 200, 20, 3);
    GuiSprite SLIDER = GuiSprite.nineSlice(SpriteAtlas.fromGui("slider"), 0, 0, 200, 20, 1);
    GuiSprite SLIDER_HANDLE = GuiSprite.nineSlice(SpriteAtlas.fromGui("slider"), 0, 40, 200, 20, 3);
    GuiSprite SLIDER_HANDLE_HIGHLIGHTED = GuiSprite.nineSlice(SpriteAtlas.fromGui("slider"), 0, 60, 200, 20, 3);
    GuiSprite FULL_HEART = GuiSprite.stretch(SpriteAtlas.fromGui("icons"), 52, 0, 9, 9);
    GuiSprite HALF_HEART = GuiSprite.stretch(SpriteAtlas.fromGui("icons"), 61, 0, 9, 9);
    GuiSprite EMPTY_HEART = GuiSprite.stretch(SpriteAtlas.fromGui("icons"), 16, 0, 9, 9);
}
