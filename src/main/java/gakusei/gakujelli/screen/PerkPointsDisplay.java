package gakusei.gakujelli.screen;

import gakusei.gakujelli.Gakujelli;
import gakusei.gakujelli.GakujelliClient;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class PerkPointsDisplay extends LabelComponent {

    protected PerkPointsDisplay(Text text) {
        super(text);
    }

    @Override
    public Text text() {
        return Text.of(text.getString() + ": " + GakujelliClient.perkPoints);
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        var matrices = context.getMatrices();

        matrices.push();
        matrices.translate(0, 1 / MinecraftClient.getInstance().getWindow().getScaleFactor(), 0);

        int x = this.x;
        int y = this.y;

        if (this.horizontalSizing.get().isContent()) {
            x += this.horizontalSizing.get().value;
        }
        if (this.verticalSizing.get().isContent()) {
            y += this.verticalSizing.get().value;
        }

        switch (this.verticalTextAlignment) {
            case CENTER -> y += (this.height - ((this.wrappedText.size() * (this.lineHeight() + 2)) - 2)) / 2;
            case BOTTOM -> y += this.height - ((this.wrappedText.size() * (this.lineHeight() + 2)) - 2);
        }

        final int lambdaX = x;
        final int lambdaY = y;

        int renderX = lambdaX;
        switch (this.horizontalTextAlignment) {
            case CENTER -> renderX += (this.width - this.textRenderer.getWidth(text())) / 2;
            case RIGHT -> renderX += this.width - this.textRenderer.getWidth(text());
        }
        int renderY = lambdaY;
        renderY += this.lineHeight() - this.textRenderer.fontHeight;

        context.drawText(this.textRenderer, text(), renderX, renderY, this.color.get().argb(), this.shadow);
        Gakujelli.Log(text().getString());

        matrices.pop();
    }
}
