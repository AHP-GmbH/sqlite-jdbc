package org.sqlite;

import java.sql.Connection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import org.sqlite.date.FastDateFormat;

import static org.sqlite.SQLiteConfig.*;

/** Connection local configurations */
public class SQLiteConnectionConfig implements Cloneable {
    private SQLiteConfig.DateClass dateClass = SQLiteConfig.DateClass.INTEGER;
    private SQLiteConfig.DatePrecision datePrecision =
            SQLiteConfig.DatePrecision.MILLISECONDS; // Calendar.SECOND or Calendar.MILLISECOND
    private String dateStringFormat = DEFAULT_DATE_STRING_FORMAT;
    private String timeStringFormat = DEFAULT_TIME_STRING_FORMAT;
    private String timestampStringFormat = DEFAULT_TIMESTAMP_STRING_FORMAT;
    private FastDateFormat dateFormat = FastDateFormat.getInstance(dateStringFormat);
    private FastDateFormat timeFormat = FastDateFormat.getInstance(timeStringFormat);
    private FastDateFormat timestampFormat = FastDateFormat.getInstance(timestampStringFormat);

    private int transactionIsolation = Connection.TRANSACTION_SERIALIZABLE;
    private SQLiteConfig.TransactionMode transactionMode = SQLiteConfig.TransactionMode.DEFERRED;
    private boolean autoCommit = true;

    public static SQLiteConnectionConfig fromPragmaTable(Properties pragmaTable) {
        return new SQLiteConnectionConfig(
                SQLiteConfig.DateClass.getDateClass(
                        pragmaTable.getProperty(
                                SQLiteConfig.Pragma.DATE_CLASS.pragmaName,
                                SQLiteConfig.DateClass.INTEGER.name())),
                SQLiteConfig.DatePrecision.getPrecision(
                        pragmaTable.getProperty(
                                SQLiteConfig.Pragma.DATE_PRECISION.pragmaName,
                                SQLiteConfig.DatePrecision.MILLISECONDS.name())),
                pragmaTable.getProperty(
                        SQLiteConfig.Pragma.DATE_STRING_FORMAT.pragmaName,
                        DEFAULT_DATE_STRING_FORMAT),
                pragmaTable.getProperty(
                        SQLiteConfig.Pragma.TIME_STRING_FORMAT.pragmaName,
                        DEFAULT_TIME_STRING_FORMAT),
                pragmaTable.getProperty(
                        SQLiteConfig.Pragma.TIMESTAMP_STRING_FORMAT.pragmaName,
                        DEFAULT_TIMESTAMP_STRING_FORMAT),
                Connection.TRANSACTION_SERIALIZABLE,
                SQLiteConfig.TransactionMode.getMode(
                        pragmaTable.getProperty(
                                SQLiteConfig.Pragma.TRANSACTION_MODE.pragmaName,
                                SQLiteConfig.TransactionMode.DEFERRED.name())),
                true);
    }

    public SQLiteConnectionConfig(
            SQLiteConfig.DateClass dateClass,
            SQLiteConfig.DatePrecision datePrecision,
            String dateStringFormat,
            String timeStringFormat,
            String timestampStringFormat,
            int transactionIsolation,
            SQLiteConfig.TransactionMode transactionMode,
            boolean autoCommit) {
        setDateClass(dateClass);
        setDatePrecision(datePrecision);
        setDateStringFormat(dateStringFormat);
        setTimeStringFormat(timeStringFormat);
        setTimestampStringFormat(timestampStringFormat);
        setTransactionIsolation(transactionIsolation);
        setTransactionMode(transactionMode);
        setAutoCommit(autoCommit);
    }

    public SQLiteConnectionConfig copyConfig() {
        return new SQLiteConnectionConfig(
                dateClass,
                datePrecision,
                dateStringFormat,
                timeStringFormat,
                timestampStringFormat,
                transactionIsolation,
                transactionMode,
                autoCommit);
    }

    public long getDateMultiplier() {
        return (datePrecision == SQLiteConfig.DatePrecision.MILLISECONDS) ? 1L : 1000L;
    }

    public SQLiteConfig.DateClass getDateClass() {
        return dateClass;
    }

    public void setDateClass(SQLiteConfig.DateClass dateClass) {
        this.dateClass = dateClass;
    }

    public SQLiteConfig.DatePrecision getDatePrecision() {
        return datePrecision;
    }

    public void setDatePrecision(SQLiteConfig.DatePrecision datePrecision) {
        this.datePrecision = datePrecision;
    }

    public String getDateStringFormat() {
        return dateStringFormat;
    }

    public void setDateStringFormat(String dateStringFormat) {
        this.dateStringFormat = dateStringFormat;
        this.dateFormat = FastDateFormat.getInstance(dateStringFormat);
    }

    public String getTimeStringFormat() {
        return timeStringFormat;
    }

    public void setTimeStringFormat(String timeStringFormat) {
        this.timeStringFormat = timeStringFormat;
        this.timeFormat = FastDateFormat.getInstance(timeStringFormat);
    }

    public String getTimestampStringFormat() {
        return timestampStringFormat;
    }

    public void setTimestampStringFormat(String timestampStringFormat) {
        this.timestampStringFormat = timestampStringFormat;
        this.timestampFormat = FastDateFormat.getInstance(timestampStringFormat);
    }

    public FastDateFormat getDateFormat() {
        return dateFormat;
    }

    public FastDateFormat getTimeFormat() {
        return timeFormat;
    }

    public FastDateFormat getTimestampFormat() {
        return timestampFormat;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public int getTransactionIsolation() {
        return transactionIsolation;
    }

    public void setTransactionIsolation(int transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }

    public SQLiteConfig.TransactionMode getTransactionMode() {
        return transactionMode;
    }

    @SuppressWarnings("deprecation")
    public void setTransactionMode(SQLiteConfig.TransactionMode transactionMode) {
        this.transactionMode = transactionMode;
    }

    private static final Map<SQLiteConfig.TransactionMode, String> beginCommandMap =
            new EnumMap<SQLiteConfig.TransactionMode, String>(SQLiteConfig.TransactionMode.class);

    static {
        beginCommandMap.put(SQLiteConfig.TransactionMode.DEFERRED, "begin;");
        beginCommandMap.put(SQLiteConfig.TransactionMode.IMMEDIATE, "begin immediate;");
        beginCommandMap.put(SQLiteConfig.TransactionMode.EXCLUSIVE, "begin exclusive;");
    }

    String transactionPrefix() {
        return beginCommandMap.get(transactionMode);
    }
}
