package mod.adrenix.nostalgic.network.packet.stamina;

import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaData;
import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaHelper;
import net.minecraft.client.Minecraft;

final class ExecuteOnClient
{
    static void handleStaminaSync(ClientboundStaminaSync packet)
    {
        if (Minecraft.getInstance().player == null)
            return;

        StaminaData data = StaminaHelper.get(Minecraft.getInstance().player).getData();

        data.setStaminaRaw(packet.stamina());
        data.setRemaining(packet.remaining());
        data.setCooldown(packet.cooldown());
        data.setExhausted(packet.exhausted());
    }
}
