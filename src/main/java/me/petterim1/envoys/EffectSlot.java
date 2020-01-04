package me.petterim1.envoys;

import cn.nukkit.potion.Effect;

class EffectSlot {

    public final Effect effect;
    public final double chance;
    public final boolean su;

    EffectSlot(double ch, boolean su, int id, int am, int du) {
        this.effect = Effect.getEffect(id).setAmplifier(am).setDuration(du);
        this.chance = ch;
        this.su = su;
    }
}
