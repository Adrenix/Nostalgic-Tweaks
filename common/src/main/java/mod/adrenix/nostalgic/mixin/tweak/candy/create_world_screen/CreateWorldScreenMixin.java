package mod.adrenix.nostalgic.mixin.tweak.candy.create_world_screen;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen
{
    /* Fake Constructor */

    private CreateWorldScreenMixin(Component title)
    {
        super(title);
    }

    /* Injections */

    /**
     * Disables the rendering of the footer bar.
     */
    @WrapWithCondition(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
        )
    )
    private boolean nt_create_world_screen$shouldRenderFooter(GuiGraphics graphics, Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight)
    {
        return !CandyTweak.REMOVE_CREATE_WORLD_FOOTER.get();
    }

    @ModifyArg(
        index = 2,
        method = "renderMenuBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;IIII)V"
        )
    )
    private int nt_create_world_screen$shouldRenderTabHeaderBackground(int y)
    {
        return CandyTweak.OLD_STYLE_CREATE_WORLD_TABS.get() ? 0 : y;
    }

    /**
     * Renders a full dirt background if the old style world tabs are enabled.
     */
    @WrapWithCondition(
        method = "renderMenuBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
        )
    )
    private boolean nt_create_world_screen$shouldRenderNewDirtBackground(GuiGraphics graphics, Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight)
    {
        return !CandyTweak.OLD_STYLE_CREATE_WORLD_TABS.get();
    }
}
