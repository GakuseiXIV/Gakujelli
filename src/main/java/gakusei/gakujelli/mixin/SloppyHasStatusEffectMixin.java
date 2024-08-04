package gakusei.gakujelli.mixin;

import gakusei.gakujelli.Gakujelli;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class SloppyHasStatusEffectMixin extends Entity implements Attackable {

    @Shadow @Final private Map<StatusEffect, StatusEffectInstance> activeStatusEffects;

    public SloppyHasStatusEffectMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method="hasStatusEffect", at=@At("HEAD"), cancellable = true)
    public void hasStatusEffect(StatusEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (effect == StatusEffects.NIGHT_VISION && this.activeStatusEffects.containsKey(Gakujelli.NOCTURNAL))
        {
            cir.setReturnValue(true);
        }
    }

    @Shadow
    protected void initDataTracker() {

    }

    @Shadow
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Shadow
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Nullable
    @Shadow
    public LivingEntity getLastAttacker() {
        return null;
    }
}
