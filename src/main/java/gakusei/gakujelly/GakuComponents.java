package gakusei.gakujelly;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import gakusei.gakujelly.component.InternalPerkComponent;
import gakusei.gakujelly.component.PerkComponent;
import net.minecraft.util.Identifier;

public final class GakuComponents implements EntityComponentInitializer {
    public static final ComponentKey<PerkComponent> PERKS =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(Gakujelly.ID, "perks"), PerkComponent.class);
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(PERKS, InternalPerkComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
