package gakusei.gakujelli.util;

import gakusei.gakujelli.GakuComponents;
import gakusei.gakujelli.Gakujelli;
import gakusei.gakujelli.ModConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

public class GutsRater {
    public static int GetGutsRating(LivingEntity entity)
    {
        int f = 0;
        if (entity.getType() == EntityType.PLAYER) f = ModConfig.defaultPlayerGuts;
        if (entity.getType() == EntityType.PLAYER && entity.getComponent(GakuComponents.PERKS).hasPerk("tank")) f += 1;
        if (entity instanceof MobEntity) f = ModConfig.defaultMobGuts;
        if (entity.getMaxHealth()>=100f) f= Math.round(f+(entity.getMaxHealth()/100f));

        if (entity.hasStatusEffect(Gakujelli.GUTS)) f += Objects.requireNonNull(entity.getStatusEffect(Gakujelli.GUTS)).getAmplifier()+1;

        f = Math.min(5, f);
        f = Math.max(0, f);
        return f;
    }
}
