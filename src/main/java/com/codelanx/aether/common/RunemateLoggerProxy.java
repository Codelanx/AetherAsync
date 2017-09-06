package com.codelanx.aether.common;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.logging.Logger;

public class RunemateLoggerProxy extends Logger {

    private final BotLogger logger;

    public RunemateLoggerProxy(BotLogger logger) {
        super(Environment.getBot() == null ? "Bot" : Environment.getBot().getMetaData().getName(), null);
        this.logger = logger;
    }
}
