package gakusei.gakujelli;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.midnightdust.lib.config.MidnightConfig;
import gakusei.gakujelli.effect.Guts;
import gakusei.gakujelli.effect.Nocturnal;
import gakusei.gakujelli.mixin.BrewingRegistry;
import gakusei.gakujelli.networking.Kakapo;
import gakusei.gakujelli.screen.PerkTreeScreen;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.FabricGameRuleVisitor;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gakujelli implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("gakujelli");
	public static String ID = "gakujelli";

	public static final StatusEffect VULN = Registry.register(Registries.STATUS_EFFECT,
			new Identifier(ID, "vulnerability"),
			new Guts(StatusEffectCategory.HARMFUL, 4205355));
	public static final StatusEffect GUTS = Registry.register(Registries.STATUS_EFFECT,
			new Identifier(ID, "guts"),
			new Guts(StatusEffectCategory.BENEFICIAL, 13750737));
	public static final StatusEffect NOCTURNAL = Registry.register(Registries.STATUS_EFFECT,
			new Identifier(ID, "nocturnal"),
			new Nocturnal(StatusEffectCategory.BENEFICIAL, 4205355));

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

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal("addPerkPoints")
								.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
								.then(CommandManager.argument("target", EntityArgumentType.entity())
										.then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
												.executes(context -> executeAddPointCommand(context.getSource(), EntityArgumentType.getEntity(context, "target"), IntegerArgumentType.getInteger(context, "amount"))
										)))));

		RegisterPotionRecipes();

		LoadPerks();
	}

	public static void LoadPerks()
	{
		try {
			List<PerkTreeScreen.Perk> p = new ArrayList<>();
			for (String s : ModConfig.perkDirectories)
			{
				p.addAll(Arrays.stream(PerkTreeScreen.Perk.loadPerksFromFile("/data/" + s + "/perks.json")).toList());
			}
			Perks = p.toArray(PerkTreeScreen.Perk[]::new);
		} catch (IOException e) {
			Gakujelli.Log("failed to load perks");
			throw new RuntimeException(e);
		}
	}

	public static int executeAddPointCommand(ServerCommandSource source, Entity target, int amount)
	{
		int i = 0;
		if (target instanceof PlayerEntity)
		{
			target.getComponent(GakuComponents.PERKS).setPoints(target.getComponent(GakuComponents.PERKS).getPoints() + amount);
		}
		else
		{
			i = -1;
		}
		int finalI = i;
		source.sendFeedback(() -> {
			if (finalI == 0)
			{
				return Text.of("Gave " + amount + " perk points to " + target.getName());
			}
			else return Text.of("Command failed");
		}, true);
		return i;
	}

	public static void RegisterPotionRecipes()
	{
		BrewingRegistry.registerRecipe(Potions.THICK, Items.RAW_GOLD, GUTS_POT);
		BrewingRegistry.registerRecipe(GUTS_POT, Items.REDSTONE, GUTS_POT_EXT);
		BrewingRegistry.registerRecipe(GUTS_POT, Items.GLOWSTONE_DUST, GUTS_POT_STR);
		BrewingRegistry.registerRecipe(GUTS_POT_STR, Items.RAW_GOLD, GUTS_POT_STR2);

		BrewingRegistry.registerRecipe(Potions.MUNDANE, Items.RAW_COPPER, VULN_POT);
		BrewingRegistry.registerRecipe(VULN_POT, Items.REDSTONE, VULN_POT_EXT);
		BrewingRegistry.registerRecipe(VULN_POT, Items.GLOWSTONE_DUST, VULN_POT_STR);
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