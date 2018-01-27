package bythecake;

public class Cake {
    private String name;
    private double price;

    public Cake(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    @Override
    public String toString() {
        return this.name + "," + this.price;
    }
}
