package engine;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import model.CriticCustomer;
import model.Customer;
import model.VIPCustomer;

/** Maps each turn to the type of customer that arrives on it. */
public class ArrivalSchedule {

    private final Map<Integer, BiFunction<Integer, Integer, Customer>> arrivals = new TreeMap<>();

    /** Registers the customer type arriving at the start of the given turn. */
    public ArrivalSchedule add(int turn, BiFunction<Integer, Integer, Customer> constructor) {
        arrivals.put(turn, constructor);
        return this;
    }

    /** Creates the customer arriving on this turn, or returns null if nobody arrives. */
    public Customer createArrival(int turn, int customerId) {
        BiFunction<Integer, Integer, Customer> constructor = arrivals.get(turn);
        return (constructor == null) ? null : constructor.apply(customerId, turn);
    }

    /** Default schedule: six arrivals, with a VIP on turn 4 and a Critic on turn 7. */
    public static ArrivalSchedule baseSetting() {
        return new ArrivalSchedule()
                .add(1, Customer::new)
                .add(4, VIPCustomer::new)
                .add(7, CriticCustomer::new)
                .add(10, Customer::new)
                .add(13, Customer::new)
                .add(16, Customer::new);
    }
}
