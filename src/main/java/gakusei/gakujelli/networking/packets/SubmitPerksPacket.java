package gakusei.gakujelli.networking.packets;

import gakusei.gakujelli.GakuComponents;
import gakusei.gakujelli.util.JFunc;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class SubmitPerksPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender)
    {
        player.getComponent(GakuComponents.PERKS).submitPerks(JFunc.StringToArray(buf.readString()));
    }
}