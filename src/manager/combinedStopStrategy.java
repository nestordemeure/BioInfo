package manager;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.StopStrategy;
import java.util.concurrent.TimeUnit;

public final class combinedStopStrategy implements StopStrategy
{
    private final int maxAttemptNumber;
    private final long maxDelay;

    // a mix of the stop after delay and the stop after attemp strategies
    public combinedStopStrategy(int maxAttemptNumber, long duration, TimeUnit timeUnit)
    {
        this.maxDelay = timeUnit.toMillis(duration);
        this.maxAttemptNumber = maxAttemptNumber;
    }

    @Override
    // stops if the time is spend or after the given number of attemps
    public boolean shouldStop(Attempt failedAttempt)
    {
        return (failedAttempt.getAttemptNumber() >= maxAttemptNumber) || (failedAttempt.getDelaySinceFirstAttempt() >= maxDelay);
    }
}
