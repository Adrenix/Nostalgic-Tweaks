package mod.adrenix.nostalgic.util.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class InternetTexture extends SimpleTexture
{
    /* Fields */

    private final String address;
    private final ResourceLocation resourceLocation;
    @Nullable private final Runnable onDownloaded;
    @Nullable private TextureLocation textureLocation = null;
    @Nullable private CompletableFuture<Void> future = null;

    /* Constructors */

    /**
     * Create a new internet texture with a download callback.
     *
     * @param address          A valid URL address to download the image from.
     * @param resourceLocation The resource location that identifies this image (does not need to be on filesystem).
     * @param onDownloaded     A {@link Runnable} to run when the image is downloaded.
     */
    public InternetTexture(String address, ResourceLocation resourceLocation, @Nullable Runnable onDownloaded)
    {
        super(resourceLocation);

        this.address = address;
        this.resourceLocation = resourceLocation;
        this.onDownloaded = onDownloaded;
    }

    /**
     * Create a new internet texture.
     *
     * @param address          A valid URL address to download the image from.
     * @param resourceLocation The resource location that identifies this image (does not need to be on filesystem).
     */
    public InternetTexture(String address, ResourceLocation resourceLocation)
    {
        this(address, resourceLocation, null);
    }

    /* Methods */

    /**
     * @return A unique {@link ResourceLocation} that identifies this texture.
     */
    @PublicAPI
    public ResourceLocation getResourceLocation()
    {
        return this.resourceLocation;
    }

    /**
     * The texture location is not defined until the image is downloaded. Once downloaded, the dimensions will be known
     * which will define the texture.
     *
     * @return An {@link Optional} {@link TextureLocation}.
     */
    @PublicAPI
    public Optional<TextureLocation> getTextureLocation()
    {
        return Optional.ofNullable(this.textureLocation);
    }

    /**
     * Once the image is loaded, it will be prepared and callback instructions will be run if applicable.
     *
     * @param image A {@link NativeImage} instance.
     */
    private void loadCallback(NativeImage image)
    {
        if (this.onDownloaded != null)
            this.onDownloaded.run();

        Minecraft.getInstance().execute(() -> {
            if (RenderSystem.isOnRenderThread())
                this.upload(image);
            else
                RenderSystem.recordRenderCall(() -> this.upload(image));
        });
    }

    /**
     * Uploads and updates image data.
     *
     * @param image A {@link NativeImage} instance.
     */
    private void upload(NativeImage image)
    {
        TextureUtil.prepareImage(this.getId(), image.getWidth(), image.getHeight());
        image.upload(0, 0, 0, true);

        this.textureLocation = new TextureLocation(this.resourceLocation, image.getWidth(), image.getHeight());
    }

    @Nullable
    private NativeImage load(InputStream stream)
    {
        NativeImage image = null;

        try
        {
            image = NativeImage.read(stream);
        }
        catch (Exception exception)
        {
            NostalgicTweaks.LOGGER.error("Error while loading native internet image\n%s", exception);
        }

        return image;
    }

    @Override
    public void load(@NotNull ResourceManager resourceManager) throws IOException
    {
        if (this.future != null)
            return;

        this.future = CompletableFuture.runAsync(() -> {
            Proxy proxy = Minecraft.getInstance().getProxy();
            HttpURLConnection connection = null;

            NostalgicTweaks.LOGGER.debug("Downloading internet texture from: %s", this.address);

            try
            {
                connection = (HttpURLConnection) new URI(this.address).toURL().openConnection(proxy);
                connection.connect();

                if (connection.getResponseCode() / 100 != 2)
                    return;

                NativeImage image = this.load(connection.getInputStream());

                if (image != null)
                    this.loadCallback(image);
            }
            catch (Exception exception)
            {
                NostalgicTweaks.LOGGER.error("Couldn't download internet texture\n%s", exception);
            }
            finally
            {
                if (connection != null)
                    connection.disconnect();
            }
        }, Util.backgroundExecutor());
    }
}
