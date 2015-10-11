package my.server.dbservice.datasets;

import my.server.base.Results;

import javax.persistence.*;

@Entity
@Table(name="results")
public class ResultsDataSet implements Results {

    @Id
    private int id;
    @Column(name="user_id")
    private int userId;
    @Transient
    private String name;
    @Column(name="attempts")
    private int attempts;
    @Column(name="time")
    private long time;

    public ResultsDataSet() {
    }

    public ResultsDataSet(int userId, int attempts, long time) {
        this.userId = userId;
        this.attempts = attempts;
        this.time = time;
    }

    public ResultsDataSet(String name, int attempts, long time) {
        this.name = name;
        this.attempts = attempts;
        this.time = time;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAttempts() {
        return attempts;
    }

    @Override
    public long getTime() {
        return time;
    }
}
