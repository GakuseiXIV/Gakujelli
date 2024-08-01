package gakusei.gakujelly;

import gakusei.gakujelly.networking.Kakapo;
import gakusei.gakujelly.screen.PerkTreeScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class GakujellyClient implements ClientModInitializer {

    public static final KeyBinding OPEN_PERKS_TREE_KEYBIND = registerBind("key.gakujelly.open_perk_tree", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_V);

    @Override
    public void onInitializeClient() {
        Kakapo.RegisterC2SPackets();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_PERKS_TREE_KEYBIND.wasPressed() && ModConfig.enablePerks)
            {
                client.setScreen(new PerkTreeScreen());
            }
        });
    }

    public static KeyBinding registerBind(String translation_key, InputUtil.Type type, int keycode) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                translation_key,
                type,
                keycode,
                "category.gakujelly.gakujelly."));
    }
    public static KeyBinding registerBind(String translation_key, InputUtil.Type type, int keycode, String category_key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                translation_key,
                type,
                keycode,
                category_key));
    }
}
