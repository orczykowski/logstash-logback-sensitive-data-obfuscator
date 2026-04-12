package io.github.orczykowski.logstash.logback.obfuscator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TimeoutRegexCharSequence implements CharSequence {

    static final int NO_TIMEOUT = -1;

    private final CharSequence inner;
    private final int timeoutMillis;
    private final long timeoutTime;
    private final Pattern pattern;

    TimeoutRegexCharSequence(final CharSequence inner, final int timeoutMillis, final Pattern pattern) {
        this.inner = inner;
        this.timeoutMillis = timeoutMillis;
        this.pattern = pattern;
        this.timeoutTime = System.currentTimeMillis() + timeoutMillis;
    }

    private TimeoutRegexCharSequence(final CharSequence inner, final int timeoutMillis, final Pattern pattern, final long timeoutTime) {
        this.inner = inner;
        this.timeoutMillis = timeoutMillis;
        this.pattern = pattern;
        this.timeoutTime = timeoutTime;
    }

    Matcher matcher() {
        return pattern.matcher(this);
    }

    @Override
    public char charAt(final int index) {
        if (System.currentTimeMillis() > timeoutTime) {
            throw new RegexProcessingTimeoutException(timeoutMillis, pattern.pattern());
        }
        return inner.charAt(index);
    }

    @Override
    public int length() {
        return inner.length();
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return new TimeoutRegexCharSequence(inner.subSequence(start, end), timeoutMillis, pattern, timeoutTime);
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}
