package gakusei.gakujelli.screen;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import gakusei.gakujelli.Gakujelli;
import gakusei.gakujelli.GakujelliClient;
import gakusei.gakujelli.ModConfig;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import net.minecraft.client.resource.language.I18n;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
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
        GakujelliClient.OpenPerkMenu();
        perks = Gakujelli.Perks;
        rootComponent
                .surface(Surface.blur(5,5))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        //reset
        if (ModConfig.freePerkEditing == true)
        {
            var reset = rootComponent.child(
                    Components.button(
                            Text.literal("Reset"),
                            button -> {
                                GakujelliClient.ResetPerks(perks);
                            }
                    )
            );
        }

        var points = new PerkPointsDisplay(Text.of("§l"+
                I18n.translate("perks.gakujelli.points")));
        points.shadow(true);
        points.positioning().set(Positioning.relative(50,80));
        rootComponent.child(points);

        for (Perk p : perks) {
            var a = (
                    new PerkButton(
                            I18n.translate("perk.gakujelli." + p.name),
                            buttonComponent -> {
                                if (!GakujelliClient.obtainedPerks.contains(p.name) && new HashSet<>(GakujelliClient.obtainedPerks).containsAll(p.prerequisites)) {
                                    GakujelliClient.AddPerk(p);
                                }
                                else if (GakujelliClient.obtainedPerks.contains(p.name)){
                                    GakujelliClient.RemovePerk(p);
                                }
                            },
                            p
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

    @Override
    public void close() {
        GakujelliClient.SubmitPerks();
        super.close();
    }

    public Text GetPerkTooltip(Perk perk)
    {
        String description = I18n.translate("perk.tooltip.gakujelli." + perk.name);
        String prerequisites = "";
        String formatThing;
        for (String p : perk.prerequisites)
        {
            if (obtainedPerks.contains(p)) formatThing = "§a§l";
            else formatThing = "§c§l";
            if (prerequisites == "") prerequisites = formatThing + I18n.translate("perk.gakujelli." + p) + "§r";
            else
            {
                prerequisites = prerequisites + ", " + formatThing + I18n.translate("perk.gakujelli." + p) + "§r";
            }
        }
        return Text.of(description + "\n"+ I18n.translate("perks.gakujelli.requires") + ": [" + prerequisites + "]" + "\n" + I18n.translate("perks.gakujelli.costs") + ": " + perk.cost);
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
        Gakujelli.Log("n perks in ring: " + perksn);
        Gakujelli.Log("index in ring: "+ ringIndex);
        Gakujelli.Log("ButtonLoc: " + sin + ", " + cos);
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
        public Perk empty()
        {
            return new Perk("", 0, new ArrayList<>(), 0);
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
                Gakujelli.Log("Failed to load perks from resource " + e);
                return new Perk[0];
            }
        }
    }
}
