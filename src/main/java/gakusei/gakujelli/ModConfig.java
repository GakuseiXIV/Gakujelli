package gakusei.gakujelli;

import eu.midnightdust.lib.config.MidnightConfig;

import com.google.common.collect.Lists;

import java.util.List;

public class ModConfig extends MidnightConfig {
    @Comment(category = "text", centered = true) public static Comment serverSide;
    @Comment(category = "text", centered = true) public static Comment gutsSec;
    @Entry(category = "text") public static boolean enableGuts = true;
    @Entry(category = "text", isSlider = true, min=0, max=5) public static int defaultPlayerGuts = 2;
    @Entry(category = "text", isSlider = true, min=0, max=5) public static int defaultMobGuts = 1;
    @Entry(category = "text", min=0) public static float minimumDamage = 1;
    @Comment(category = "text", centered = true) public static Comment perksSec;
    @Comment(category = "text") public static Comment perksDisclaimer;
    @Entry(category = "text") public static boolean enablePerks = false;
    @Entry(category = "text") public static List<String> perkDirectories = Lists.newArrayList("gakujelli");
    @Entry(category = "text") public static boolean freePerkEditing = false;
}