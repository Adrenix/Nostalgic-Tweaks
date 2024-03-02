package mod.adrenix.nostalgic.util.common.asset;

public interface GameSprite
{
    TextureLocation BUTTON = new TextureLocation(GameAsset.widget("button.png"), 200, 20);
    TextureLocation BUTTON_DISABLED = new TextureLocation(GameAsset.widget("button_disabled.png"), 200, 20);
    TextureLocation BUTTON_HIGHLIGHTED = new TextureLocation(GameAsset.widget("button_highlighted.png"), 200, 20);
    TextureLocation SLIDER = new TextureLocation(GameAsset.widget("slider.png"), 200, 20);
    TextureLocation SLIDER_HANDLE = new TextureLocation(GameAsset.widget("slider_handle.png"), 8, 20);
    TextureLocation SLIDER_HANDLE_HIGHLIGHTED = new TextureLocation(GameAsset.widget("slider_handle_highlighted.png"), 8, 20);
    TextureLocation FULL_HEART = new TextureLocation(GameAsset.heart("full.png"), 9, 9);
    TextureLocation HALF_HEART = new TextureLocation(GameAsset.heart("half.png"), 9, 9);
    TextureLocation EMPTY_HEART = new TextureLocation(GameAsset.heart("container.png"), 9, 9);
}
