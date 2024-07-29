package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface GuiAccess
{
    @Accessor("GUI_ICONS_LOCATION")
    static ResourceLocation NT$GUI_ICONS_LOCATION()
    {
        throw new AssertionError();
    }

    @Accessor("displayHealth")
    int nt$getDisplayHealth();

    @Accessor("tickCount")
    int nt$getTickCount();

    @Accessor("healthBlinkTime")
    void nt$setHealthBlinkTime(long blinkTime);
}
