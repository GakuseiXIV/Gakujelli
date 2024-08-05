package gakusei.gakujelli.networking;

import gakusei.gakujelli.Gakujelli;
import gakusei.gakujelli.GakujelliClient;
import gakusei.gakujelli.networking.packets.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class Kakapo{
    public static Identifier RESET_PERKS = new Identifier(Gakujelli.ID, "reset_perks");
    public static Identifier SUBMIT_PERKS = new Identifier(Gakujelli.ID, "submit_perks");
    public static Identifier REQUEST_PERK_DATA = new Identifier(Gakujelli.ID, "request_perk_data");
    public static Identifier RECEIVE_PERK_DATA = new Identifier(Gakujelli.ID, "receive_perk_data");
    public static Identifier SYNC_PERKS = new Identifier(Gakujelli.ID, "sync_perks");
    public static Identifier GET_SERVER_CONFIG = new Identifier(Gakujelli.ID, "get_server_config");
    public static Identifier SEND_SERVER_CONFIG = new Identifier(Gakujelli.ID, "send_server_config");

    public static void RegisterS2CPackets()
    {
        ClientPlayNetworking.registerGlobalReceiver(RECEIVE_PERK_DATA, GakujelliClient::receivePerkData);
        ClientPlayNetworking.registerGlobalReceiver(SEND_SERVER_CONFIG, GakujelliClient::receiveModConfig);
    }
    public static void RegisterC2SPackets()
    {
        ServerPlayNetworking.registerGlobalReceiver(RESET_PERKS, ResetPerksPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SUBMIT_PERKS, SubmitPerksPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_PERK_DATA, RequestPerkDataPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SYNC_PERKS, SyncPerksPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(GET_SERVER_CONFIG, GetServerConfigPacket::receive);
    }
}
