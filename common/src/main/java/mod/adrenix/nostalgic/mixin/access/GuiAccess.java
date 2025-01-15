package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface GuiAccess
{
    @Accessor("ARMOR_FULL_SPRITE")
    static ResourceLocation NT$ARMOR_FULL_SPRITE()
    {
        throw new AssertionError();
    }

    @Accessor("ARMOR_HALF_SPRITE")
    static ResourceLocation NT$ARMOR_HALF_SPRITE()
    {
        throw new AssertionError();
    }

    @Accessor("ARMOR_EMPTY_SPRITE")
    static ResourceLocation NT$ARMOR_EMPTY_SPRITE()
    {
        throw new AssertionError();
    }

    @Accessor("AIR_SPRITE")
    static ResourceLocation NT$AIR_SPRITE()
    {
        throw new AssertionError();
    }

    @Accessor("AIR_BURSTING_SPRITE")
    static ResourceLocation NT$AIR_BURSTING_SPRITE()
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
