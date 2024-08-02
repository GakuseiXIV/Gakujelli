package gakusei.gakujelli.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import gakusei.gakujelli.GakuComponents;
import gakusei.gakujelli.Gakujelli;
import gakusei.gakujelli.util.JFunc;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class InternalPerkComponent implements PerkComponent, AutoSyncedComponent {
    private List<String> perks;
    private final PlayerEntity provider;
    public int points;

    public InternalPerkComponent(PlayerEntity provider) {
        this.provider = provider;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.perks = JFunc.StringToArray(tag.getString("perks"));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putString("perks", JFunc.ArrayToString(perks));
    }

    public List<String> getPerks() {
        return perks;
    }

    public boolean hasPerk(String perk)
    {
        return perks.contains(perk);
    }

    public int getNPerks(){
        return perks.size();
    }

    public void addPerk(String perk) {
        perks.add(perk);
        GakuComponents.PERKS.sync(this.provider);
    }

    public void removePerk(String perk) {
        perks.remove(perk);
        GakuComponents.PERKS.sync(this.provider);
    }

    public void resetPerks() {
        perks = new ArrayList<>();
        Gakujelli.Log("balling");
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
        this.perks = perks;
        GakuComponents.PERKS.sync(this.provider);
    }
}
