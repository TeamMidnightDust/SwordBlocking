package eu.midnightdust.swordblocking.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class SwordBlockingConfig extends MidnightConfig {
    @Entry public static boolean enabled = true;
    @Entry public static boolean hideShield = true;
    @Entry public static boolean alwaysHideShield = true;
    @Entry public static boolean hideOffhandSlot = false;
}
