package mod.adrenix.nostalgic.init;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.init.listener.common.InteractionListener;
import mod.adrenix.nostalgic.init.listener.common.PlayerListener;
import mod.adrenix.nostalgic.mixin.util.gameplay.MobLootMixinHelper;
import mod.adrenix.nostalgic.network.PacketRegistry;
import mod.adrenix.nostalgic.util.server.ServerTimer;

public abstract class ModInitializer
{
    /**
     * Registers common mod events.
     */
    public static void register()
    {
        PacketRegistry.register();
        InteractionListener.register();
        PlayerListener.register();

        LifecycleEvent.SERVER_BEFORE_START.register(NostalgicTweaks::setServer);
        TickEvent.SERVER_PRE.register(server -> ServerTimer.getInstance().onTick());

        EnvExecutor.runInEnv(Env.CLIENT, () -> ClientInitializer::register);

        MobLootMixinHelper.init();
    }
}
