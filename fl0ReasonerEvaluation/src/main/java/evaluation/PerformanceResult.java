package evaluation;

import java.time.Duration;

public class PerformanceResult {
    private Duration duration;

    public PerformanceResult(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }
}
