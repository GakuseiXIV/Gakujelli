package gakusei.gakujelly;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {
    @Comment(category = "text", centered = true) public static Comment gutsSec;
    @Entry(category = "text") public static boolean enableGuts = true;
    @Entry(category = "text", isSlider = true, min=0, max=5) public static int defaultPlayerGuts = 2;
    @Entry(category = "text", isSlider = true, min=0, max=5) public static int defaultMobGuts = 1;
    @Entry(category = "text", min=0) public static float minimumDamage = 1;
}