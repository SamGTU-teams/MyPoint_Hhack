package DataBaseQueries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class ConnectionDB implements AutoCloseable{

    protected Connection connection;

    protected ConnectionDB(String url, String login, String password){
        log().info("Try connect to DB.");
        try{
            connection = DriverManager.getConnection(url, login, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            log().severe("Connection lost.");
        }
    }

    protected ConnectionDB(String url, Properties info){
        log().info("Try connect to DB.");
        try{
            connection = DriverManager.getConnection(url, info);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            log().severe("Connection lost.");
        }
    }

    public abstract Logger log();

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
