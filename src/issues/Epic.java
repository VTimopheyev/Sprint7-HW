package issues;

import java.time.Duration;
import java.time.ZonedDateTime;

public class Epic extends Task {

    private ZonedDateTime endTime = null;


    public Epic(String title, String description) {
        super(title, description);
        setType(IssueType.EPIC);
    }

    public Epic(int id, IssueType type, String title, IssueStatus status, String description, ZonedDateTime startDate, Duration issueDuration, ZonedDateTime endTime) {
        super(id, type, title, status, description, startDate, issueDuration);
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEpicEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", type='" + getType() + '\'' +
                ", startDate='" + getStartDate() + '\'' +
                ", issueDuration='" + getIssueDuration() + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
