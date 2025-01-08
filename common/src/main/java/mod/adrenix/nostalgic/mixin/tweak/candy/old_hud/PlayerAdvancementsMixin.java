package mod.adrenix.nostalgic.mixin.tweak.candy.old_hud;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.server.PlayerAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin
{
    /**
     * Prevents the advancement messages that appear in server chat after a player gets a new achievement.
     */
    @ModifyExpressionValue(
        method = "method_53637",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancements/DisplayInfo;shouldAnnounceChat()Z"
        )
    )
    private boolean nt_old_hud$shouldAnnounceAdvancementInChat(boolean shouldAnnounceChat)
    {
        return !CandyTweak.HIDE_ADVANCEMENT_CHATS.get() && shouldAnnounceChat;
    }
}
