package fr.miage.fsgbd;

public class TestInteger implements Executable<Integer>, java.io.Serializable {
    public boolean execute(Integer int1, Integer int2) {
        return (int1 < int2);
    }

    @Override
    public int compare(Integer type, Integer type1) {
        return type.compareTo(type1);
    }
}