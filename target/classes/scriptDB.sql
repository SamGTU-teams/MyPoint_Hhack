create table couriers
(
    id             int primary key,
    name           text,
    last_time      timestamp,
    last_latitude  float,
    last_longitude float,
    telegram_id    int,
    busy           bool,
    online         bool,
    rating         float
);

create table orders
(
    id           int primary key,
    latitude     float,
    longitude    float,
    address      text,
    accepted     timestamp,
    acceptance   timestamp,
    organization int references organizations (id),
    courier_id   int references couriers (id),
    stock        int references buildings (id)
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

create table acceptOrder
(
    id_order          int references orders (id),
    id_courier        int references couriers (id),
    accepted_operator bool,
    accepted_courier  bool
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

select calculateDistance(55, 55, 55, 55.000002);

create procedure selectCouriers(id_order int)
begin
    declare lat_order float;
    declare lon_order float;
    declare id_org int;
    set lat_order = (select latitude from orders where id = id_order);
    set lon_order = (select longitude from orders where id = id_order);
    set id_org = (select organization from orders where id = id_order);

    select id_order,
           buildings_set.id                                as id_building,
           couriers.id                                     as id_courier,
           buildings_set.distance_order_to_building +
           min(calculateDistance(buildings_set.lat, couriers.last_latitude, buildings_set.lon,
                                 couriers.last_longitude)) as distance
    from couriers,
         (select id,
                 latitude                                                     as lat,
                 longitude                                                    as lon,
                 calculateDistance(lat_order, latitude, lon_order, longitude) as distance_order_to_building
          from buildings
          where organization = id_org) as buildings_set
    where !couriers.busy
      and couriers.online
    group by id_courier
    order by distance;
end;