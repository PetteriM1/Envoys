package me.petterim1.envoys;

import cn.nukkit.potion.Effect;

public class EffectSlot {

    public final Effect effect;
    public final double chance;
    public final boolean su;

    public EffectSlot(double chance, boolean su, int id, int amplifier, int duration) {
        this.effect = Effect.getEffect(id).setAmplifier(amplifier).setDuration(duration);
        this.chance = chance;
        this.su = su;
    }
}
