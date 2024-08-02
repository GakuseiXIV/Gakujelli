package gakusei.gakujelli.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

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
    void submitPerks(List<String> perks);
}