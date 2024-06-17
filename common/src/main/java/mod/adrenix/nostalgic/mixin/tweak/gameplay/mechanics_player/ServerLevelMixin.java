package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_player;

import mod.adrenix.nostalgic.mixin.util.gameplay.NightmareMixinHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin
{
    /* Shadows */

    @Shadow @Final List<ServerPlayer> players;

    /* Injections */

    /**
     * Checks if the player should have a nightmare when sleeping.
     */
    @Inject(
        method = "tick",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/server/players/SleepStatus;areEnoughDeepSleeping(ILjava/util/List;)Z"
        )
    )
    private void nt_mechanics_player$onUpdateIsSleepingCheck(BooleanSupplier hasTimeLeft, CallbackInfo callback)
    {
        if (GameplayTweak.OLD_NIGHTMARES.get())
            this.players.forEach(NightmareMixinHelper::tick);
    }
}
