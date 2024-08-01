package gakusei.gakujelly.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import gakusei.gakujelly.util.JFunc;
import net.minecraft.nbt.NbtCompound;

import javax.swing.text.html.parser.Entity;
import java.util.Arrays;
import java.util.List;

public interface PerkComponent extends ComponentV3 {
    List<String> getPerks();
    void resetPerks();
    int getPoints();
    void setPoints(int value);
    void removePerk(String perk);
    void addPerk(String perk);
    int getNPerks();
    boolean hasPerk(String perk);
}