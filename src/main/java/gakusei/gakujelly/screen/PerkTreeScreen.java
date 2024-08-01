package gakusei.gakujelly.screen;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import gakusei.gakujelly.GakuComponents;
import gakusei.gakujelly.Gakujelly;
import gakusei.gakujelly.GakujellyClient;
import gakusei.gakujelly.networking.Kakapo;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PerkTreeScreen extends BaseOwoScreen<FlowLayout> {

    public static float radius_diff = 50;

    public Perk[] perks;

    public List<Perk> renderedPerks = new ArrayList<>();

    public List<String> obtainedPerks = new ArrayList<>();

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        perks = Gakujelly.Perks;
        rootComponent
                .surface(Surface.blur(5,5))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        //reset
        var reset = rootComponent.child(
                Components.button(
                        Text.literal("Reset"),
                        button -> {
                            ClientPlayNetworking.send(Kakapo.RESET_PERKS, PacketByteBufs.create());
                        }
                )
        );
        for (Perk p : perks) {
            if (renderedPerks.contains(p)) Gakujelly.Log("wtf this shouldnt happen");
            //positioning
            var a = (
                    Components.button(
                            Text.translatable("perk.gakujelly." + p.name),
                            buttonComponent -> {
                                Gakujelly.Log(p.name);
                            }
                    ));
            a.setTooltip(Tooltip.of(GetPerkTooltip(p)));
            /*yeah this isnt ideal BUT without this there will constantly be two instances
            * of the tooltip on screen when you hover so this is the best I can do
            *
            * my other solution is to mixin with ButtonWidget to hide tooltips but that will probably
            * break a few things that shouldn't be broken*/
            a.setTooltipDelay(999999999);

            Vec2f pos = GetButtonLocation(p);

            int w = textRenderer.getWidth(a.getMessage())+7;
            int h = 21;

            a.positioning().set(Positioning.absolute((int)pos.x - w/2, (int)pos.y - h/2));

            renderedPerks.add(p);

            rootComponent.child(a);
        }
    }

    public Text GetPerkTooltip(Perk perk)
    {
        Text description = Text.translatable("perk.tooltip.gakujelly." + perk.name);
        Text prerequisites = Text.empty();
        for (String p : perk.prerequisites)
        {
            if (prerequisites.getContent() == TextContent.EMPTY) prerequisites = Text.translatable("perk.gakujelly." + p);
            else
            {
                prerequisites = Text.of(prerequisites.getString() + ", " + Text.translatable("perk.gakujelly." + p));
            }
        }
        return Text.of(description.getString() + "\nRequires: [" + prerequisites.getString() + "]");
    }

    public Vec2f GetButtonLocation(Perk perk)
    {
        float d = perk.ring * radius_diff;
        int perksn = GetNPerksInRing(perk.ring);
        int ringIndex = GetIndexInRing(perk);
        //ringIndex = perk.ring % 2 == 0 ? ringIndex+1: ringIndex;
        double cos = Math.cos(Math.toRadians(ringIndex * (360f / perksn)));
        double sin = Math.sin(Math.toRadians(ringIndex * (360f / perksn)));
        Vec2f v = new Vec2f(d*(float) sin,d*(float) cos);
        Gakujelly.Log("n perks in ring: " + perksn);
        Gakujelly.Log("index in ring: "+ ringIndex);
        Gakujelly.Log("ButtonLoc: " + sin + ", " + cos);
        v = v.add(new Vec2f(width/2f, height/2f));
        return v;
    }

    public int GetNPerksInRing(int ring)
    {
        int i = 0;
        for (Perk p : perks) {
            if (p.ring == ring) i++;
        }
        return i;
    }

    public int GetIndexInRing(Perk perk)
    {
        int i = 0;
        for (Perk p : renderedPerks) {
            if (p.ring == perk.ring) i++;
        }
        return i;
    }

    public class Perk
    {
        public String name;
        public int ring;
        public List<String> prerequisites;
        public int cost;
        public Perk (String name, int ring, List<String> prerequisites, int cost)
        {
            this.name = name;
            this.ring = ring;
            this.prerequisites = prerequisites;
            this.cost = cost;
        }
        public static Perk[] loadPerksFromFile(String filePath) throws IOException {
            Gson gson = new GsonBuilder().create();
            try (InputStream inputStream = Perk.class.getResourceAsStream(filePath);
                 InputStreamReader reader = new InputStreamReader(inputStream)) {
                if (inputStream == null) {
                    throw new IOException("Resource not found: " + filePath);
                }
                Type perkArrayType = new TypeToken<Perk[]>() {}.getType();
                return gson.fromJson(reader, perkArrayType);
            } catch (IOException e) {
                Gakujelly.Log("Failed to load perks from resource " + e);
                return new Perk[0];
            }
        }
    }
}
