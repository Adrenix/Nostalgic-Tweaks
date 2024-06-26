package mod.adrenix.nostalgic.neoforge.setup.network;

import mod.adrenix.nostalgic.util.common.LocateResource;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ProtocolRequest() implements CustomPacketPayload
{
    public static final ResourceLocation IDENTIFIER = LocateResource.mod("protocol_request");
    public static final CustomPacketPayload.Type<ProtocolRequest> TYPE = new CustomPacketPayload.Type<>(IDENTIFIER);
    public static final StreamCodec<FriendlyByteBuf, ProtocolRequest> CODEC = CustomPacketPayload.codec(ProtocolRequest::encode, ProtocolRequest::new);

    public ProtocolRequest(final FriendlyByteBuf buffer)
    {
        this();
    }

    public void encode(FriendlyByteBuf buffer)
    {
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
