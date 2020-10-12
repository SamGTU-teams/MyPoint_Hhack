create table couriers
(
    id             int primary key not null,
    name           text,
    last_time      timestamp,
    last_latitude  float,
    last_longitude float,
    is_busy        bool default false
);

create table orders
(
    id         int primary key              not null,
    accepted   boolean default false,
    latitude   float                        not null,
    longitude  float                        not null,
    address    text,
    courier_id int references couriers (id) null,
    finished   bool    default false
);

create table buildings
(
    id           int primary key not null,
    latitude     float           not null,
    longitude    float           not null,
    organization text,
    address      text
);

drop function calculateDistance;

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