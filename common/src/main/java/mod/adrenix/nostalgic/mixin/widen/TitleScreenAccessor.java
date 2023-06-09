package mod.adrenix.nostalgic.mixin.widen;

import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TitleScreen.class)
public interface TitleScreenAccessor
{
    @Accessor("splash") SplashRenderer NT$getSplash();
    @Accessor("realmsNotificationsScreen") RealmsNotificationsScreen NT$getRealmsNotificationsScreen();
    @Invoker("realmsNotificationsEnabled") boolean NT$getRealmsNotificationsEnabled();
}
