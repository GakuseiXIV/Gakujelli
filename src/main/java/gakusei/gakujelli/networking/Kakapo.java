package gakusei.gakujelli.networking;

import gakusei.gakujelli.Gakujelli;
import gakusei.gakujelli.networking.packets.ResetPerksPacket;
import gakusei.gakujelli.networking.packets.SubmitPerksPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class Kakapo{
    public static Identifier RESET_PERKS = new Identifier(Gakujelli.ID, "reset_perks");
    public static Identifier C2S_SELF_PACKET_ID = new Identifier(Gakujelli.ID, "c2s_self_packet");
    public static Identifier SUBMIT_PERKS = new Identifier(Gakujelli.ID, "submit_perks");

    public static void RegisterS2CPackets()
    {

    }
    public static void RegisterC2SPackets()
    {
        ServerPlayNetworking.registerGlobalReceiver(RESET_PERKS, ResetPerksPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SUBMIT_PERKS, SubmitPerksPacket::receive);
    }
}
