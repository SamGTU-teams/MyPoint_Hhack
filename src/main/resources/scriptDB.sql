create table couriers
(
    id             int primary key auto_increment,
    name           text,
    last_time      timestamp,
    last_latitude  float,
    last_longitude float,
    telegram_id    int,
    busy           bool,
    online         bool,
    rating         float,
    vehicle        int references vehicles (id),
    session        text,
    has_geo        bool,
    geo_point      text,
    got_order      boolean
);

create table orders
(
    id             int primary key auto_increment,
    latitude       float,
    longitude      float,
    address        text,
    accepted       timestamp,
    acceptance     timestamp,
    order_taken    boolean default false,
    order_finished boolean default false,
    courier_id     int references couriers (id),
    stock          int references buildings (id)
);

create table checkOrder
(
    id          int references orders (id),
    checked     boolean default false,
    distance    float,
    travel_time timestamp,
    path        text
);

create table organizations
(
    id       int primary key auto_increment,
    name     text,
    products text
);

create table buildings
(
    id           int primary key auto_increment,
    latitude     float,
    longitude    float,
    address      text,
    organization int references organizations (id)
);

create table tempOrder
(
    id             int primary key auto_increment,
    finish_address text,
    stock          int references buildings (id),
    ready          boolean default 0,
    end_lat        float,
    end_lon        float
);


create table vehicles
(
    id    int primary key auto_increment,
    name  text,
    speed float
);

create function calculateDistance(lat1 float, lat2 float, lon1 float, lon2 float) returns float
begin
    declare temp float;
    declare distance float;
    select pow(sin(radians(lon2 - lon1) / 2), 2) * cos(radians(lat2)) * cos(radians(lat1)) +
           pow(sin(radians(lat2 - lat1) / 2), 2)
    into temp;
    select 2 * atan2(sqrt(temp), sqrt(1 - temp)) * 6372795 into distance;
    return distance;
end;

create procedure selectPreviewCouriers(temp_order int)
begin
    declare lat_order float;
    declare lon_order float;
    declare id_building int;
    declare address_order text;
    set lat_order = (select end_lat from tempOrder where id = temp_order);
    set lon_order = (select end_lon from tempOrder where id = temp_order);
    set id_building = (select stock from tempOrder where id = temp_order);
    set address_order = (select finish_address from tempOrder where id = temp_order);

    select result.temp_order                                 as id_order,
           result.id_building                                as id_building,
           result.id_courier                                 as id_courier,
           result.address_building                           as address_building,
           result.courier_name                               as courier_name,
           result.courier_rating                             as courier_rating,
           result.distance / 1000                            as distance,
           sec_to_time(result.distance / result.speed * 3.6) as travel_time,
           lat_order,
           lon_order,
           address_order
    from (select temp_order,
                 buildings_set.id                              as id_building,
                 couriers_vehicles.id                          as id_courier,
                 buildings_set.address                         as address_building,
                 couriers_vehicles.name                        as courier_name,
                 couriers_vehicles.rating                      as courier_rating,
                 buildings_set.distance +
                 min(calculateDistance(buildings_set.lat, couriers_vehicles.lat, buildings_set.lon,
                                       couriers_vehicles.lon)) as distance,
                 couriers_vehicles.speed                       as speed
          from (select id,
                       latitude                                                     as lat,
                       longitude                                                    as lon,
                       address,
                       calculateDistance(lat_order, latitude, lon_order, longitude) as distance
                from buildings
                where id = id_building) as buildings_set,
               (select c.id             as id,
                       c.name           as name,
                       c.rating         as rating,
                       c.last_longitude as lon,
                       c.last_latitude  as lat,
                       c.busy           as busy,
                       c.online         as online,
                       v.speed          as speed
                from couriers c
                         inner join vehicles v on v.id = c.vehicle) as couriers_vehicles
          where !couriers_vehicles.busy
            and couriers_vehicles.online
          group by couriers_vehicles.id) as result
    order by travel_time;
end;

create procedure deleteTempData(id_temp int)
begin
    delete from tempOrder where id = id_temp;
end;

create procedure deleteCheckOrder(id_order int)
begin
    delete from checkOrder where id = id_order;
end;

create procedure checkOrder(id_order int)
begin
    select o.id             as id_order,
           c.id             as id_courier,
           c.last_latitude  as lat_courier,
           c.last_longitude as lon_courier,
           o.order_taken    as order_taken,
           o.acceptance     as acceptance,
           o.distance       as distance,
           o.travel_time    as travel_time,
           o.path           as path
    from (select o.id          as id,
                 o.acceptance  as acceptance,
                 o.courier_id  as courier_id,
                 o.order_taken as order_taken,
                 c.distance    as distance,
                 c.travel_time as travel_time,
                 c.path        as path
          from orders o
                   inner join checkOrder c on o.id = c.id
          where id = id_order) as o
             inner join couriers c on c.id = o.courier_id;
end;

create procedure getOrderData(id_order int)
begin
    select o.id_order     as id_order,
           o.id_courier   as id_courier,
           o.id_building  as id_building,
           v.name         as vehicle,
           o.courier_lat  as courier_lat,
           o.courier_lon  as courier_lon,
           o.building_lat as building_lat,
           o.building_lon as building_lon,
           o.finish_lat   as finish_lat,
           o.finish_lon   as finish_lon,
           o.order_taken  as order_taken
    from (select o.id_order    as id_order,
                 o.id_courier  as id_courier,
                 o.id_building as id_building,
                 o.vehicle     as vehicle,
                 o.courier_lat as courier_lat,
                 o.courier_lon as courier_lon,
                 b.latitude    as building_lat,
                 b.longitude   as building_lon,
                 o.finish_lat  as finish_lat,
                 o.finish_lon  as finish_lon,
                 o.order_taken as order_taken
          from (select o.id_order       as id_order,
                       o.id_courier     as id_courier,
                       o.id_building    as id_building,
                       o.finish_lat     as finish_lat,
                       o.finish_lon     as finish_lon,
                       c.vehicle        as vehicle,
                       c.last_longitude as courier_lon,
                       c.last_latitude  as courier_lat,
                       o.order_taken    as order_taken
                from (select o.id          as id_order,
                             o.courier_id  as id_courier,
                             o.stock       as id_building,
                             o.latitude    as finish_lat,
                             o.longitude   as finish_lon,
                             o.order_taken as order_taken
                      from checkOrder c
                               inner join orders o on c.id = o.id
                      where c.id = id_order) o
                         inner join couriers c on c.id = o.id_courier) o
                   inner join buildings b on o.id_building = b.id) o
             inner join vehicles v on o.vehicle = v.id;
end;

create procedure countUsers()
begin
    select o.count as online, b.count as not_busy
    from (select count(id) as count from couriers where online) o,
         (select count(id) as count from couriers where !busy and online) b;
end;

create procedure getOrders()
begin
    select o.id          as id_order,
           o.courier_id  as id_courier,
           c.name        as name_courier,
           o.accepted    as accepted,
           o.order_taken as order_taken
    from (select id,
                 courier_id,
                 accepted,
                 order_taken
          from orders
          where !order_finished) o
             inner join couriers c on o.courier_id = c.id;
end;


