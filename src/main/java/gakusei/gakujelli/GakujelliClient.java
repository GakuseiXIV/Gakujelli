package gakusei.gakujelli;

import gakusei.gakujelli.networking.Kakapo;
import gakusei.gakujelli.screen.PerkTreeScreen;
import gakusei.gakujelli.util.JFunc;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GakujelliClient implements ClientModInitializer {

    public static final KeyBinding OPEN_PERKS_TREE_KEYBIND = registerBind("key.gakujelly.open_perk_tree", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_V);

    public static List<String> obtainedPerks = new ArrayList<>();

    public static int perkPoints = 0;

    public static PerkTreeScreen.Perk[] PERKS;

    public static boolean perksEnabled = false;
    public static boolean fpe = false;
    public static List<String> directories = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        Kakapo.RegisterC2SPackets();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_PERKS_TREE_KEYBIND.wasPressed())
            {
                OpenPerkMenu();
                if (perksEnabled) client.setScreen(new PerkTreeScreen());
            }
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ClientPlayNetworking.send(Kakapo.GET_SERVER_CONFIG, PacketByteBufs.empty());
        });
    }

    public static PerkTreeScreen.Perk[] LoadPerks()
    {
        try {
            List<PerkTreeScreen.Perk> p = new ArrayList<>();
            for (String s : directories)
            {
                p.addAll(Arrays.stream(PerkTreeScreen.Perk.loadPerksFromFile("/data/" + s + "/perks.json")).toList());
            }
            return p.toArray(PerkTreeScreen.Perk[]::new);
        } catch (IOException e) {
            Gakujelli.Log("failed to load perks");
            throw new RuntimeException(e);
        }
    }

    public static void OpenPerkMenu() {
        obtainedPerks.clear();
        obtainedPerks.addAll(MinecraftClient.getInstance().player.getComponent(GakuComponents.PERKS).getPerks());
        perkPoints = MinecraftClient.getInstance().player.getComponent(GakuComponents.PERKS).getPoints();
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
            MinecraftClient.getInstance().player.getComponent(GakuComponents.PERKS).submitPerks(obtainedPerks);
            MinecraftClient.getInstance().player.getComponent(GakuComponents.PERKS).setPoints(perkPoints);
        }
        else
        {
            MinecraftClient.getInstance().player.getComponent(GakuComponents.PERKS).submitPerks(new ArrayList<>());
            MinecraftClient.getInstance().player.getComponent(GakuComponents.PERKS).setPoints(perkPoints);
        }
        SyncPerkData();
    }

    public static void ResetPerks(PerkTreeScreen.Perk[] perks)
    {
        for (String s : obtainedPerks)
        {
            PerkTreeScreen.Perk p = GetPerkFromString(s, perks);
            if (p != null)
            {
                perkPoints += p.cost;
            }
            else {
                Gakujelli.Log("perk '" + s + "' does not exist!");
            }
        }
        obtainedPerks = new ArrayList<>();
        SubmitPerks();
    }

    public static void RemovePerk(@NotNull PerkTreeScreen.Perk perk)
    {
        if (fpe)
        {
            Gakujelli.Log(JFunc.ArrayToString(obtainedPerks));
            Gakujelli.Log(perk.name);
            obtainedPerks.remove(perk.name);
            perkPoints += perk.cost;
            for (PerkTreeScreen.Perk p : PERKS)
            {
                if (p.prerequisites.contains(perk.name) && obtainedPerks.contains(p.name)) {
                    obtainedPerks.remove(p.name);
                    perkPoints += p.cost;
                }
            }
        }
    }

    public static void RemovePerk(@NotNull PerkTreeScreen.Perk perk, boolean byPassFPE)
    {
        if (fpe || byPassFPE)
        {
            Gakujelli.Log(JFunc.ArrayToString(obtainedPerks));
            Gakujelli.Log(perk.name);
            obtainedPerks.remove(perk.name);
            perkPoints += perk.cost;
            for (PerkTreeScreen.Perk p : PERKS)
            {
                if (p.prerequisites.contains(perk.name) && obtainedPerks.contains(p.name)) {
                    obtainedPerks.remove(p.name);
                    perkPoints += p.cost;
                }
            }
        }
    }

    public static PerkTreeScreen.Perk GetPerkFromString(String perk, PerkTreeScreen.Perk[] perks)
    {
        for (PerkTreeScreen.Perk p : perks)
        {
            if (p.name == perk) return p;
        }
        return null;
    }

    public static void ChangePerkPoints(int value)
    {
        perkPoints = value;
    }

    public static void AddPerk(@NotNull PerkTreeScreen.Perk perk)
    {
        if (perkPoints >= perk.cost)
        {
            obtainedPerks.add(perk.name);
            perkPoints -= perk.cost;
        }
        else
        {
            MinecraftClient.getInstance().player.sendMessage(Text.translatable("perks.gakujelli.not_enough_points"));
        }
    }

    public static void receivePerkData(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender)
    {
        Gakujelli.Log("received data");
        obtainedPerks = JFunc.StringToArray(buf.readString());
        perkPoints = buf.readInt();
        Gakujelli.Log(JFunc.ArrayToString(obtainedPerks) + " | " + perkPoints);
    }

    public static void receiveModConfig(MinecraftClient client, ClientPlayNetworkHandler handler,
                                       PacketByteBuf buf, PacketSender responseSender)
    {
        perksEnabled = buf.readBoolean();
        fpe = buf.readBoolean();
        directories = JFunc.StringToArray(buf.readString());
        PERKS = LoadPerks();
    }

    public static void SyncPerkData()
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(JFunc.ArrayToString(obtainedPerks));
        buf.writeInt(perkPoints);
        ClientPlayNetworking.send(Kakapo.SYNC_PERKS, buf);
    }
}
