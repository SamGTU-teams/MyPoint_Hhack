package Data;

public enum DistanceProperties {
    DRIVING("driving"),//Car
    WALKING("walking"),
    BICYCLING("bicycling"),
    TRANSIT("transit")//Bus, train ...
    ;
    String value;


    DistanceProperties(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
