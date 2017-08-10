package com.codelanx.aether.common.statebot;


import com.codelanx.aether.common.statebot.MyStateBot.MyState;

public class MyStateBot extends StateBot<MyState> {

    @Override
    public MyState getBotState() {
        return MyState.TEST;
    }

    @Override
    public void onLoop(MyState state) {
        switch (state) {
            case TEST:
                System.out.println("test success!");
        }
    }

    enum MyState {
        TEST,
        ;
    }


}
