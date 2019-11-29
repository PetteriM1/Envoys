package me.petterim1.envoys;

public class Task extends Thread {

    private Envoys pl;

    Task(Envoys pl) {
        this.pl = pl;
        setName("Envoys Task");
    }

    @Override
    public void run() {
        pl.c.nextEnvoy--;
        if (pl.c.nextEnvoy < 0) {
            if (!pl.c.now) {
                pl.c.doEnvoy();
            } else {
                pl.c.nowTicks--;
                pl.c.hintTicks--;
                if (pl.c.nowTicks < 0) {
                    pl.c.endEnvoy();
                    return;
                }
                if (pl.c.hintTicks < 0) {
                    pl.c.e.spawnRandomEffects();
                    pl.c.hintTicks = pl.c.hint;
                }
            }
        }
    }
}
