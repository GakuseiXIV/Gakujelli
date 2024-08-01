package gakusei.gakujelly;

import eu.midnightdust.lib.config.MidnightConfig;
import gakusei.gakujelly.effect.Guts;
import gakusei.gakujelly.effect.Vulnerability;
import gakusei.gakujelly.mixin.BrewingRecipeRegistryMixin;
import gakusei.gakujelly.networking.Kakapo;
import gakusei.gakujelly.screen.PerkTreeScreen;
import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gakujelly implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("gakujelly");
	public static String ID = "gakujelly";

	public static final StatusEffect VULN = Registry.register(Registries.STATUS_EFFECT,
			new Identifier(ID, "vulnerability"),
			new Guts(StatusEffectCategory.HARMFUL, 4205355));
	public static final StatusEffect GUTS = Registry.register(Registries.STATUS_EFFECT,
			new Identifier(ID, "guts"),
			new Guts(StatusEffectCategory.BENEFICIAL, 13750737));

	public static final Potion GUTS_POT = Registry.register(Registries.POTION,
			new Identifier(ID, "guts_potion"),
			new Potion(new StatusEffectInstance(GUTS, 3600)));
	public static final Potion GUTS_POT_EXT = Registry.register(Registries.POTION,
			new Identifier(ID, "long_guts_potion"),
			new Potion("guts_potion", new StatusEffectInstance(GUTS, 9600)));
	public static final Potion GUTS_POT_STR = Registry.register(Registries.POTION,
			new Identifier(ID, "strong_guts_potion"),
			new Potion("guts_potion", new StatusEffectInstance(GUTS, 2400, 1)));

	public static final Potion GUTS_POT_STR2 = Registry.register(Registries.POTION,
			new Identifier(ID, "stronger_guts_potion"),
			new Potion("guts_potion", new StatusEffectInstance(GUTS, 600, 2)));

	public static final Potion VULN_POT = Registry.register(Registries.POTION,
			new Identifier(ID, "vuln_potion"),
			new Potion(new StatusEffectInstance(VULN, 3600)));
	public static final Potion VULN_POT_EXT = Registry.register(Registries.POTION,
			new Identifier(ID, "long_vuln_potion"),
			new Potion("vuln_potion", new StatusEffectInstance(VULN, 9600)));
	public static final Potion VULN_POT_STR = Registry.register(Registries.POTION,
			new Identifier(ID, "strong_vuln_potion"),
			new Potion("vuln_potion", new StatusEffectInstance(VULN, 2400, 1)));

	public static PerkTreeScreen.Perk[] Perks;

	@Override
	public void onInitialize() {
		Kakapo.RegisterS2CPackets();
		MidnightConfig.init(ID, ModConfig.class);

		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.THICK, Items.RAW_GOLD, GUTS_POT);
		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(GUTS_POT, Items.REDSTONE, GUTS_POT_EXT);
		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(GUTS_POT, Items.GLOWSTONE_DUST, GUTS_POT_STR);
		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(GUTS_POT_STR, Items.RAW_GOLD, GUTS_POT_STR2);

		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.MUNDANE, Items.RAW_COPPER, VULN_POT);
		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(VULN_POT, Items.REDSTONE, VULN_POT_EXT);
		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(VULN_POT, Items.GLOWSTONE_DUST, VULN_POT_STR);

		try {
			List<PerkTreeScreen.Perk> p = new ArrayList<>();
			for (String s : ModConfig.perkDirectories)
			{
				p.addAll(Arrays.stream(PerkTreeScreen.Perk.loadPerksFromFile("/data/" + s + "/perks.json")).toList());
			}
			Perks = p.toArray(PerkTreeScreen.Perk[]::new);
		} catch (IOException e) {
			Gakujelly.Log("failed to load perks");
			throw new RuntimeException(e);
		}
	}

	public static void Log(String message)
	{
		LOGGER.info(message);
	}

	public static PerkTreeScreen.Perk[] GetPerks()
	{
		return Perks;
	}
}