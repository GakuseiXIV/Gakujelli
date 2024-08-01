package gakusei.gakujelly.networking.packets;

import dev.onyxstudios.cca.internal.entity.CardinalComponentsEntity;
import gakusei.gakujelly.GakuComponents;
import gakusei.gakujelly.component.InternalPerkComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ResetPerksPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender)
    {
        player.sendMessage(Text.of("Reset perks"));
        player.getComponent(GakuComponents.PERKS).resetPerks();
    }
}
