package gakusei.gakujelly.networking;

import gakusei.gakujelly.Gakujelly;
import gakusei.gakujelly.networking.packets.ResetPerksPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class Kakapo{
    public static Identifier RESET_PERKS = new Identifier(Gakujelly.ID, "reset_perks");
    public static Identifier C2S_SELF_PACKET_ID = new Identifier(Gakujelly.ID, "c2s_self_packet");

    public static void RegisterS2CPackets()
    {

    }
    public static void RegisterC2SPackets()
    {
        ServerPlayNetworking.registerGlobalReceiver(RESET_PERKS, ResetPerksPacket::receive);
    }
}
