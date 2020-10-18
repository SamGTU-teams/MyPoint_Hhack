package DataBaseQueries;

import GoogleAPI.DirectionCreator;
import GoogleAPI.GeoCoding;
import Data.*;

import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

public class DBListener extends ConnectionDB {

    public long sleep = 5000;

    private static final Logger LOG = Logger.getLogger(DBListener.class.getName());
    private boolean status = true;
    private static final String LISTEN_TEMP = "select id, finish_address from tempOrder where !ready;";
    private static final String LISTEN_ORDER = "select id from checkOrder where !checked";
    private static final String SQL = "call getOrderData(?);";
    private Timer timer = new Timer();

    public DBListener(String url, String login, String password) {
        super(url, login, password);
    }

    public DBListener(String url, Properties info) {
        super(url, info);
    }

    public void stopListen() {
        timer.cancel();
    }

    @Override
    public Logger log() {
        return LOG;
    }

    public void run() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listen();
            }
        }, sleep, sleep);
    }

    private void listen() {
        try (PreparedStatement statement = connection.prepareStatement(LISTEN_TEMP)) {
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                updateGeoCoding(set.getInt(1), set.getString(2));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try (PreparedStatement statement = connection.prepareCall(LISTEN_ORDER)) {
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                checkOrder(set.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static final String UPDATE_TEMP_ORDER = "update tempOrder set end_lat = ?, end_lon = ?, ready = 1 where id = ?;";

    private void updateGeoCoding(int id_order, String address) {
        System.out.println(id_order);
        Point point = GeoCoding.geoCodePoint(address);
        double lat = point.lat, lon = point.lon;

        try (PreparedStatement statement = connection.prepareCall(UPDATE_TEMP_ORDER)) {
            statement.setDouble(1, lat);
            statement.setDouble(2, lon);
            statement.setInt(3, id_order);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static final String UPDATE_CHECK_ORDER = "update checkOrder set checked = 1, distance = ?, travel_time = ? where id =?;";

    private void checkOrder(int id_order) {
        try (CallableStatement statement = connection.prepareCall(SQL)) {
            statement.setInt(1, id_order);
            ResultSet set = statement.executeQuery();
            int id_courier = set.getInt(2);
            int id_building = set.getInt(3);
            String vehicle = set.getString(4);
            Point courier_point = new Point(set.getDouble(5), set.getDouble(6));
            Point building_point = new Point(set.getDouble(7), set.getDouble(8));
            Point finish_point = new Point(set.getDouble(9), set.getDouble(10));
            boolean order_taken = set.getBoolean(11);
            Trial trial = new Trial(finish_point);
            if (!order_taken) {
                trial.addWayPoint(building_point);
            }
            Courier courier = new Courier(courier_point, id_courier).initTrial(trial).rideMode(vehicle);

            long distance = 0;
            Instant duration = Instant.ofEpochSecond(0);
            try {
                ResultTrial result = DirectionCreator.getResult(courier);
                distance = result.getDistance().getMetres() / 1000;
                duration = Instant.ofEpochSecond(result.getDuration().getSeconds());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (PreparedStatement load = connection.prepareStatement(UPDATE_CHECK_ORDER)) {
                load.setDouble(1, distance);
                load.setTimestamp(2, Timestamp.from(duration));
                load.setInt(3, id_order);
                load.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
