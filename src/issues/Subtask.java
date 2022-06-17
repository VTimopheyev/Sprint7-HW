package issues;

import java.time.Duration;
import java.time.ZonedDateTime;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        setEpicId(epicId);
        setType(IssueType.SUBTASK);
    }
    //"id,type,name,status,description,startDate,duration,endDate,epic"
    public Subtask (int id, IssueType type, String title, IssueStatus status, String description, ZonedDateTime startDate, Duration issueDuration, int epicId){
        super(id, type, title, status, description, startDate, issueDuration);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", type='" + getType() + '\'' +
                ", epicId='" + getEpicId() + '\'' +
                ", startDate='" + getStartDate() + '\'' +
                ", issueDurationInMinutes='" + getIssueDuration() + '\'' +
                '}';
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
