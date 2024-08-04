package gakusei.gakujelli.mixin;

import gakusei.gakujelli.GakuComponents;
import gakusei.gakujelli.Gakujelli;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class NocturnalMixin extends LivingEntity {

    @Inject(method="tick", at=@At("TAIL"))
    public void Nocturnal(CallbackInfo ci)
    {
        if (!getWorld().isClient() && getComponent(GakuComponents.PERKS).hasPerk("nocturnal") && getWorld().isNight()) {
            addStatusEffect(new StatusEffectInstance(Gakujelli.NOCTURNAL, 100));
        }
    }

    protected NocturnalMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Shadow
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return null;
    }

    @Shadow
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Shadow
    public Arm getMainArm() {
        return null;
    }
}
