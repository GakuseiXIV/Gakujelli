package gakusei.gakujelli;

import gakusei.gakujelli.networking.Kakapo;
import gakusei.gakujelli.screen.PerkTreeScreen;
import gakusei.gakujelli.util.JFunc;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.List;

public class GakujelliClient implements ClientModInitializer {

    public static final KeyBinding OPEN_PERKS_TREE_KEYBIND = registerBind("key.gakujelly.open_perk_tree", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_V);

    public static List<String> obtainedPerks = new ArrayList<>();

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

    public static void SubmitPerks()
    {
        if (!obtainedPerks.isEmpty())
        {
            ClientPlayNetworking.send(Kakapo.SUBMIT_PERKS, PacketByteBufs.create().writeString(JFunc.ArrayToString(obtainedPerks)));
            Gakujelli.Log("Closed perks menu and attempted to submit");
        }
        else
        {
            ClientPlayNetworking.send(Kakapo.RESET_PERKS, PacketByteBufs.empty());
            Gakujelli.Log("Closed perks menu and attempted to submit clear perks command");
        }
    }

    public static void ResetPerks()
    {
        obtainedPerks = new ArrayList<>();
        ClientPlayNetworking.send(Kakapo.RESET_PERKS, PacketByteBufs.empty());
    }
}
