package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(OptionsScreen.class)
public interface IMixinOptionsScreen
{
    @Invoker("updatePackList") void invokeUpdatePackList(PackRepository packRepository);
}
