package gakusei.gakujelli.mixin;

import gakusei.gakujelli.Gakujelli;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.Closeable;

@Mixin(GameRenderer.class)
public class NocturnalEffectMixin implements AutoCloseable{

    @Inject(method="getNightVisionStrength", at=@At("HEAD"), cancellable = true)
    private static void NocturnalNV(LivingEntity entity, float tickDelta, CallbackInfoReturnable<Float> cir)
    {
        if (entity.hasStatusEffect(Gakujelli.NOCTURNAL))
        {
            cir.setReturnValue(1.0F);
            cir.cancel();
        }
    }


    @Shadow
    public void close() throws Exception {
    }
}
