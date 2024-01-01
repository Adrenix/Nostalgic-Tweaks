package mod.adrenix.nostalgic.client.gui.screen.home.overlay.supporter;

import mod.adrenix.nostalgic.util.client.renderer.InternetTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

record PlayerFace(ResourceLocation location, InternetTexture texture)
{
    public void register()
    {
        Minecraft.getInstance().getTextureManager().register(this.location, this.texture);
    }
}
