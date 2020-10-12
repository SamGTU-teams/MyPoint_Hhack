package DataBaseQueries.Write;

import DataBaseQueries.ConnectionDB;
import DataStreams.WriteData;

import java.sql.SQLException;
import java.util.logging.Level;

public abstract class WriteToDB<T> extends ConnectionDB implements WriteData<T> {

    protected WriteToDB(String url, String login, String password) {
        super(url, login, password);
    }

    @Override
    public void writeData(T data) {
        try {
            if (select(data)) {
                update(data);
            } else {
                insert(data);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            log().log(Level.SEVERE, "SQLError", throwables);
        }
    }

    protected abstract boolean select(T data) throws SQLException;

    protected abstract void update(T data) throws SQLException;

    protected abstract void insert(T data) throws SQLException;
}
