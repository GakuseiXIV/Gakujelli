package gakusei.gakujelly.mixin;

import gakusei.gakujelly.Gakujelly;
import gakusei.gakujelly.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class GutsMixin {
	@Shadow public abstract float getHealth();

	@Shadow public abstract float getMaxHealth();

	@Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

	@Shadow public abstract Vec3d applyMovementInput(Vec3d movementInput, float slipperiness);

	@ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private float GutsifyDamage(float amount) {
		if (!ModConfig.enableGuts) return amount;
		boolean altered = true;
		int gutsRating = GetGutsRating((LivingEntity)(Object)this);
		float f = amount;
		if (this.getHealth() / this.getMaxHealth() <= 0.1f) f*=0.56f-(gutsRating/100f*3f);
		else if (this.getHealth() / this.getMaxHealth() <= 0.2f) f*=0.66f-(gutsRating/100f*3f);
		else if (this.getHealth() / this.getMaxHealth() <= 0.3f) f*=0.75f-(gutsRating/100f*3f);
		else if (this.getHealth() / this.getMaxHealth() <= 0.4f) f*=0.84f-(gutsRating/50f);
		else if (this.getHealth() / this.getMaxHealth() <= 0.5f) f*=0.89f-(gutsRating/50f);
		else if (this.getHealth() / this.getMaxHealth() <= 0.6f) f*=0.92f-(gutsRating/100f);
		else if (this.getHealth() / this.getMaxHealth() <= 0.7f) f*=0.97f-(gutsRating/100f);
		else altered = false;
		if (altered && f < ModConfig.minimumDamage) f = ModConfig.minimumDamage;
		Gakujelly.LOGGER.info("damage dealt should be "+f);

		f = addVulnerabilityDamage(f, (LivingEntity) (Object) this);
		return f;
	}

	public int GetGutsRating(LivingEntity entity)
	{
		int f = 0;
		if (entity.getType() == EntityType.PLAYER) f = ModConfig.defaultPlayerGuts;
		if (entity instanceof MobEntity) f = ModConfig.defaultMobGuts;
		if (entity.getMaxHealth()>=100f) f= Math.round(f+(entity.getMaxHealth()/100f));

		if (entity.hasStatusEffect(Gakujelly.GUTS)) f += Objects.requireNonNull(entity.getStatusEffect(Gakujelly.GUTS)).getAmplifier()+1;

		f = Math.min(5, f);
		f = Math.max(0, f);
		return f;
	}

	private float addVulnerabilityDamage(float amount, LivingEntity entity)
	{
		float a = 0;
		if (entity.hasStatusEffect(Gakujelly.VULN)) a = Objects.requireNonNull(entity.getStatusEffect(Gakujelly.VULN)).getAmplifier()+1;

		return a == 0 ? amount: amount * (1+(a/2f));
	}
}