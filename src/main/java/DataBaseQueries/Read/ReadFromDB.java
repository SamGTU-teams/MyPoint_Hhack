package DataBaseQueries.Read;

import DataBaseQueries.ConnectionDB;
import DataStreams.DataStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

public abstract class ReadFromDB<T> extends ConnectionDB implements DataStream<T> {
    protected ReadFromDB(String url, String login, String password) {
        super(url, login, password);
    }

    @Override
    public Stream<T> generateStream() {
        return list().stream();
    }

    protected abstract T collectData(ResultSet set);

    public List<T> list(){
        LinkedList<T> result = new LinkedList<>();
        try(PreparedStatement statement = connection.prepareStatement(select())){
            ResultSet set = statement.executeQuery();
            while(set.next()){
                result.add(collectData(set));
            }
            set.close();
        } catch (SQLException throwables) {
            log().log(Level.SEVERE, "SQLError", throwables);
        }
        return result;
    }

    public abstract String select();
}
