package gakusei.gakujelli.networking.packets;

import gakusei.gakujelli.GakuComponents;
import gakusei.gakujelli.Gakujelli;
import gakusei.gakujelli.networking.Kakapo;
import gakusei.gakujelli.util.JFunc;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class RequestPerkDataPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender)
    {
        PacketByteBuf b = PacketByteBufs.create();
        b.writeString(JFunc.ArrayToString(player.getComponent(GakuComponents.PERKS).getPerks()));
        b.writeInt(player.getComponent(GakuComponents.PERKS).getPoints());
        Gakujelli.Log("sent data");
        responseSender.sendPacket(Kakapo.RECEIVE_PERK_DATA, b);
    }
}
