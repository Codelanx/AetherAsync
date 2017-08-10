package com.codelanx.aether.common.statebot;

import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.AbstractBot;
import com.runemate.game.api.script.framework.task.Task;

import java.util.Objects;

public abstract class StateBot<T> extends AbstractBot {

    private static final int TICK_RATE = 50; //50ms tick, 20tps
    private T previousState;

    //our logical loop, this is purely bot runtime and thus not run when State#UNSTARTED
    private void onLoop() {
        T state = this.getBotState();
        if (!Objects.equals(this.previousState, state)) {
            this.previousState = state;
        } else if (this.ignoreSimilarStates()) {
            return;
        }
        this.onLoop(state);
    }

    public abstract T getBotState();

    public abstract void onLoop(T state);

    protected boolean ignoreSimilarStates() {
        return true;
    }

    public final void run() {
        //if we want to support this we'd need to extend treebot/looping bot directly
        //which has its own consequences. See if we can hook these handlers manually
        //(possibly via reflection on the obfuscation?)
        /*if (this.getMetaData().isLocal()) {
            if (!this.IiiiIiiiIIIiI) {
                this.getLogger().warn((new StringBuilder()).insert(0, iiIIiiiIiiIII.this("lbVTCHXO\r\u0001gDEGXSZ@YBR\u0001~RDTR|\u0017")).append(this.getMetaData().getName()).append(iIIiiiiIIIiii.this("\taZ(\\{@fN(]`L(MmOi\\d](EgFx\tlLdHq\tgO(r")).append(this.IIIiIiiiIiIII).append(',').append(this.iiiiiiiiIiiIi).append(iiIIiiiIiiIII.this("|\u0019")).toString());
                this.getLogger().warn(iIIiiiiIIIiii.this("rKH}]aFf\u0013(jgG|@f\\mMU\tA](@{\t@`OaDp(Z}NoL{]mM(]`H|\tqF}\tkAiGoL(]`@{\t~Hd\\m\t\u007f@|AaG(Ffz|Hz] z|[aGo\u0007&\u0007(HzN{\u0000(]g\tx[m_mG|\tdHo\u0005([mZg\\zJm\t\u007fH{]m\u0005(HfM(]g\teHcL(Pg\\z\tjF|Z(Ai_m\teFzL(\\f@y\\m\t|@e@fN{\u0007"));
            }

            List var1;
            if ((var1 = this.getEventDispatcher().getListeners()).size() == 1) {
                this.getLogger().warn(iiIIiiiIiiIII.this("lbVTCHXO\r\u0001{HZHCDS\u0001vq~\u0001BRVFR|\u0017xXT\u0010SR\u0001XO[X\u0017TDHYF\u0017@\u0017RZ@[M\u0017QXSCHXO\u0017NQ\u0001CIR\u0001VQ^\r\u0017VR\u0001EDTNZLROS\u0001NNB\u0001[NXJ\u0017HYUX\u0001DNZD\u0017NQ\u0001XTE\u0001rWROCm^RCDYDER\u0017RBB_\u0001VR\u0017U_D\u0017hYWROCNEX{HDURORS\u0019"));
            }

            if (this instanceof EventListener && !var1.contains(this)) {
                long var2 = Arrays.stream(this.getClass().getInterfaces()).filter((var0) -> {
                    return var0.isAssignableFrom(EventListener.class);
                }).count();
                this.getLogger().warn((new StringBuilder()).insert(0, iIIiiiiIIIiii.this("Sji\\|@gG2\t]GlL{@zLl\tjL`H~@g[U\t_L/_m\tlL|Lk]mM(]`H|\tqF}[(Di@f\tkEiZ{\t`H{\t")).append(var2).append(iiIIiiiIiiIII.this("\u0001RWROC\u0001[HDURORSD\u0001@I^B_\u0001_@AD\u0017OXU\u0017CRDY\u0001EDPHDURSRE\u0019")).toString());
                this.getLogger().warn(iIIiiiiIIIiii.this("rKH}]aFf\u0013(jgG|@f\\mMU\t\\F([mNaZ|Lz\t|AmD$\tmQmJ}]m\toL|l~Lf]L@{Yi]kAm[ \u0000&HlMD@{]mGm[ ]`@{\u0000(OzFe\tIK{]zHk][Jz@x]+Ffz|Hz] z|[aGo\u0007&\u0007(HzN{\u0000(^`Lf\tqF}\tg_m[z@lL(@|\u0007"));
            }
        }*/

        tickloop:
        while(true) {
            //some old task handlers, see above
            /*if (this.iIiiiiiiIIiII.validate()) {
                    this.iIiiiiiiIIiII.execute();
                }

                if (this.IiIiIiiiIiiiI.validate()) {
                    this.IiIiIiiiIiiiI.execute();
                }*/
            State state = this.getState();
            switch (state) {
                case PAUSED:
                    //lengthen our timeout
                    break;
                case STOPPED:
                case RESTARTING:
                    //current loopingbot behavior
                    break tickloop;
                case UNSTARTED:
                    //init values
                case RUNNING:
                default:
                    Task t = this.getGameEventController();
                    if (t.validate()) {
                        t.execute();
                        Execution.delay(200, 400); //TODO: remove old delay, kept in case of services
                    }
                    long start = System.currentTimeMillis();
                    this.onLoop();
                    long rem = TICK_RATE - (System.currentTimeMillis() - start);
                    if (rem > 0) {
                        try {
                            Thread.sleep(rem);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); //appears the bot interrupts itself on failure
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

}
