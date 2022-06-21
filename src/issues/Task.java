package issues;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    private int id = 0;
    private String title = "Empty";
    private String description = "Empty";
    private IssueStatus status = IssueStatus.NONE;
    private IssueType type = IssueType.TASK;
    public ZonedDateTime startDate = null;
    private Duration issueDuration = null;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    //"id,type,name,status,description,epic"
    public Task(int id, IssueType type, String title, IssueStatus status, String description, ZonedDateTime startDate, Duration issueDuration) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.status = status;
        this.description = description;
        this.issueDuration = issueDuration;
        this.startDate = startDate;
    }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", type='" + getType() + '\'' +
                ", startDate='" + getStartDate() + '\'' +
                ", issueDurationInMinutes='" + getIssueDuration() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status && type == task.type && Objects.equals(startDate, task.startDate) && Objects.equals(issueDuration, task.issueDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, type, startDate, issueDuration);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public Duration getIssueDuration() {
        return issueDuration;
    }

    public ZonedDateTime getEndTime() {
        if (issueDuration != null && startDate != null) {
            return getStartDate().plus(getIssueDuration());
        }
        return null;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public void setIssueDuration(Duration issueDuration) {
        this.issueDuration = issueDuration;
    }

}
