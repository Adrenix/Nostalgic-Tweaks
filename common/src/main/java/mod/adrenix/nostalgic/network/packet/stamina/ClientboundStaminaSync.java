package mod.adrenix.nostalgic.network.packet.stamina;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaData;
import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaHelper;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record ClientboundStaminaSync(int stamina, int maximum, int remaining, int cooldown, boolean exhausted)
    implements ModPacket
{
    public static final Type<ClientboundStaminaSync> TYPE = ModPacket.createType(ClientboundStaminaSync.class);

    /**
     * Helper for quickly creating a stamina synchronization packet from a player instance.
     *
     * @param player The {@link ServerPlayer} instance to get stamina data for.
     * @return A new {@link ClientboundStaminaSync} instance with stamina data from the given player.
     */
    public static ClientboundStaminaSync create(ServerPlayer player)
    {
        StaminaData data = StaminaHelper.get(player).getData();

        int stamina = data.getStamina();
        int maximum = data.getMaximum();
        int remaining = data.getRemaining();
        int cooldown = data.getCooldown();
        boolean exhausted = data.isExhausted();

        return new ClientboundStaminaSync(stamina, maximum, remaining, cooldown, exhausted);
    }

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundStaminaSync(final FriendlyByteBuf buffer)
    {
        this(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readBoolean());
    }

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeInt(this.stamina);
        buffer.writeInt(this.maximum);
        buffer.writeInt(this.remaining);
        buffer.writeInt(this.cooldown);
        buffer.writeBoolean(this.exhausted);
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        ExecuteOnClient.handleStaminaSync(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
