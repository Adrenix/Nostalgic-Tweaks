package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.text.DateFormat;

@Mixin(WorldSelectionList.class)
public interface WorldSelectionListAccess
{
    @Accessor("DATE_FORMAT")
    static DateFormat NT$DATE_FORMAT()
    {
        throw new AssertionError();
    }

    @Accessor("loadingHeader")
    WorldSelectionList.LoadingHeader nt$getLoadingHeader();
}
