package gakusei.gakujelli.screen;

import gakusei.gakujelli.GakujelliClient;
import io.wispforest.owo.ui.component.ButtonComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class PerkButton extends ButtonComponent {
    public PerkTreeScreen.Perk perk;

    @Override
    public Text getMessage() {
        String prefix = "§c";
        if (GakujelliClient.obtainedPerks.contains(perk.name)) prefix = "§a";
        return Text.of(prefix + super.getMessage().getString());
    }

    public PerkButton(String message, Consumer<ButtonComponent> onPress, PerkTreeScreen.Perk perk) {
        super(Text.of(message), onPress);
        this.perk = perk;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        setTooltip(Tooltip.of(GetPerkTooltip(perk)));
    }

    public Text GetPerkTooltip(PerkTreeScreen.Perk perk)
    {
        String description = I18n.translate("perk.tooltip.gakujelli." + perk.name);
        String prerequisites = "";
        String formatThing = "";
        for (String p : perk.prerequisites)
        {
            if (GakujelliClient.obtainedPerks.contains(p)) formatThing = "§a§l";
            else formatThing = "§c§l";
            if (prerequisites == "") prerequisites = formatThing + I18n.translate("perk.gakujelli." + p) + "§r";
            else
            {
                prerequisites = prerequisites + ", " + formatThing + I18n.translate("perk.gakujelli." + p) + "§r";
            }
        }
        return Text.of(description + "\n"+ I18n.translate("perks.gakujelli.requires") + ": [" + prerequisites + "]" + "\n" + I18n.translate("perks.gakujelli.costs") + ": " + perk.cost);
    }
}
