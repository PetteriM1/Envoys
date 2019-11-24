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
                if (pl.c.nowTicks == pl.c.effect) { //TODO
                    pl.c.e.spawnRandomEffects();
                } else if (pl.c.nowTicks < 0) {
                    pl.c.endEnvoy();
                }
            }
        }
    }
}
