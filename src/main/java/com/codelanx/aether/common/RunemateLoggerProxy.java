package com.codelanx.aether.common;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

//this breaks some contracts with Logger, mostly out of laziness
public class RunemateLoggerProxy extends Logger {

    private static final Formatter FORMAT = new FMLFormatter();
    private final BotLogger logger;

    public RunemateLoggerProxy(BotLogger logger) {
        super(Environment.getBot() == null ? "Bot" : Environment.getBot().getMetaData().getName(), null);
        this.logger = logger;
    }

    @Override
    public void log(LogRecord record) {
        //delegate into BotLogger
        Environment.getLogger().println(this.mapLevel(record.getLevel()), FORMAT.format(record));
    }

    //gdi
    private BotLogger.Level mapLevel(Level level) {
        if (level == Level.INFO) {
            return BotLogger.Level.INFO;
        } else if (level == Level.SEVERE) {
            return BotLogger.Level.SEVERE;
        } else if (level == Level.WARNING) {
            return BotLogger.Level.WARN;
        } else if (level == Level.CONFIG) {
            return BotLogger.Level.DEBUG;
        } else if (level == Level.OFF) {
            return BotLogger.Level.OFF;
        } else {
            return BotLogger.Level.FINE;
        }
    }

    @Override
    public Handler[] getHandlers() {
        return super.getHandlers();
    }

    //FML, for, fuck my life
    private static class FMLFormatter extends Formatter {

        //default: %1$tH:%1$tM:%1$tS  %2$s%n%4$s: %5$s%6$s%n
        private static final String FORMAT = "[%1$s] %4$s%5$s";//"H:M:S %-6s %s";
        private final Date dat = new Date();

        @Override
        public String format(LogRecord record) {
            dat.setTime(record.getMillis());
            String source = record.getLoggerName();
            /*if (record.getSourceClassName() != null) {
                source = record.getSourceClassName();
                if (record.getSourceMethodName() != null) {
                    source += " " + record.getSourceMethodName();
                }
            } else {
                source = record.getLoggerName();
            }*/
            String message = formatMessage(record);
            String throwable = "";
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.println();
                record.getThrown().printStackTrace(pw);
                pw.close();
                throwable = sw.toString();
            }
            return String.format(FORMAT,
                    source,
                    record.getLoggerName(),
                    record.getLevel().getLocalizedName(),
                    message,
                    throwable);
        }
    }
}
