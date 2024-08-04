package gakusei.gakujelli;

import gakusei.gakujelli.networking.Kakapo;
import gakusei.gakujelli.networking.packets.SubmitPerksPacket;
import gakusei.gakujelli.networking.packets.SyncPerksPacket;
import gakusei.gakujelli.screen.PerkTreeScreen;
import gakusei.gakujelli.util.JFunc;
import io.wispforest.owo.config.annotation.Sync;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.realms.Request;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GakujelliClient implements ClientModInitializer {

    public static final KeyBinding OPEN_PERKS_TREE_KEYBIND = registerBind("key.gakujelly.open_perk_tree", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_V);

    public static List<String> obtainedPerks = new ArrayList<>();

    public static int perkPoints = 0;

    public static PerkTreeScreen.Perk[] PERKS;

    @Override
    public void onInitializeClient() {
        Kakapo.RegisterC2SPackets();
        PERKS = Gakujelli.GetPerks();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_PERKS_TREE_KEYBIND.wasPressed() && ModConfig.enablePerks)
            {
                client.setScreen(new PerkTreeScreen());
            }
        });
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

    public static void ResetPerks()
    {
        for (String s : obtainedPerks)
        {
            PerkTreeScreen.Perk p = GetPerkFromString(s);
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
        if (ModConfig.freePerkEditing)
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
        if (ModConfig.freePerkEditing || byPassFPE)
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

    public static PerkTreeScreen.Perk GetPerkFromString(String perk)
    {
        for (PerkTreeScreen.Perk p: PERKS)
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

    public static void SyncPerkData()
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(JFunc.ArrayToString(obtainedPerks));
        buf.writeInt(perkPoints);
        ClientPlayNetworking.send(Kakapo.SYNC_PERKS, buf);
    }
}
