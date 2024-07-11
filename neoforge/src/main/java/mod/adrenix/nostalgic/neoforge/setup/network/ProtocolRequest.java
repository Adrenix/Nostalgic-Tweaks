package mod.adrenix.nostalgic.neoforge.setup.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ProtocolRequest() implements CustomPacketPayload
{
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(NostalgicTweaks.MOD_ID, "protocol_request");

    public ProtocolRequest(final FriendlyByteBuf buffer)
    {
        this();
    }

    @Override
    public void write(FriendlyByteBuf buffer)
    {
    }

    @Override
    public ResourceLocation id()
    {
        return IDENTIFIER;
    }
}
