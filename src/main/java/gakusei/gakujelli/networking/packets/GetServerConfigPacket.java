package gakusei.gakujelli.networking.packets;

import gakusei.gakujelli.ModConfig;
import gakusei.gakujelli.networking.Kakapo;
import gakusei.gakujelli.util.JFunc;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class GetServerConfigPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender)
    {
        PacketByteBuf sendBack = PacketByteBufs.create();
        sendBack.writeBoolean(ModConfig.enablePerks);
        sendBack.writeBoolean(ModConfig.freePerkEditing);
        sendBack.writeString(JFunc.ArrayToString(ModConfig.perkDirectories));
        responseSender.sendPacket(Kakapo.SEND_SERVER_CONFIG, sendBack);
    }
}
