package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldSelectionList.class)
public interface WorldSelectionListAccess
{
    @Accessor("loadingHeader")
    WorldSelectionList.LoadingHeader nt$getLoadingHeader();
}
