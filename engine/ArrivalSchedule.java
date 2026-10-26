package engine;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import model.CriticCustomer;
import model.Customer;
import model.VIPCustomer;

/**
 * Stage 2: says which type of customer arrives on which turn.
 *
 * Each entry stores a constructor reference (for example VIPCustomer::new),
 * so the game creates customers without any if/switch on customer type.
 * Adding a customer type means adding a class and one line here.
 */
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

    /**
     * Required base setting: six arrivals on turns 1, 4, 7, 10, 13, 16.
     * Stage 2 replaces two Regular arrivals: a VIP on turn 4, a Critic on turn 7.
     */
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
