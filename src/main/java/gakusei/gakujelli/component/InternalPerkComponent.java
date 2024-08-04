package gakusei.gakujelli.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import gakusei.gakujelli.GakuComponents;
import gakusei.gakujelli.Gakujelli;
import gakusei.gakujelli.util.JFunc;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class InternalPerkComponent implements PerkComponent, AutoSyncedComponent {
    private String perks;
    private final PlayerEntity provider;
    public int points;

    public InternalPerkComponent(PlayerEntity provider) {
        this.provider = provider;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.perks = tag.getString("gakujelliperks");
        this.points = tag.getInt("gakujellipoints");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putString("gakujelliperks", perks);
        tag.putInt("gakujellipoints", points);
    }

    public List<String> getPerks() {
        return JFunc.StringToArray(perks);
    }

    public boolean hasPerk(String perk)
    {
        Gakujelli.Log(perks);
        Gakujelli.Log(String.valueOf(perks.contains(perk)));
        return perks.contains(perk);
    }

    public int getNPerks(){
        return JFunc.StringToArray(perks).size();
    }

    public void addPerk(String perk) {
        perks = JFunc.AddToFalseArray(perks, perk);
        GakuComponents.PERKS.sync(this.provider);
    }

    public void removePerk(String perk) {
        perks = JFunc.RemoveFromFalseArray(perks, perk);
        GakuComponents.PERKS.sync(this.provider);
    }

    public void resetPerks() {
        perks = "";
        GakuComponents.PERKS.sync(this.provider);
    }

    public int getPoints()
    {
        return points;
    }

    public void setPoints(int value)
    {
        points = value;
        GakuComponents.PERKS.sync(this.provider);
    }

    @Override
    public void submitPerks(List<String> perks) {
        Gakujelli.Log("Perks submitted");
        this.perks = JFunc.ArrayToString(perks);
        GakuComponents.PERKS.sync(this.provider, (this));
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeString(perks);
        buf.writeInt(points);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        this.perks = buf.readString();
        this.points = buf.readInt();
    }
}
