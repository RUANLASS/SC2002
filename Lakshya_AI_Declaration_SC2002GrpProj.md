Me: Can you explain the game to me with a sample run. Do one run for stage 1, stage 2, stage 3\.

Claude:

I’ve read the brief. Next I’ll write a small simulator that follows the turn rules exactly. That way the sample runs are verified rather than hand-waved.

Still working: I’ve simulated the Stage 1 and Stage 2 runs against the brief’s turn rules, and I’m now building the Stage 3 run (Host, combo meal, Happy Hour). Then I’ll write everything up.

The game is a turn-based restaurant where you’re the manager. Each turn you decide what one Waiter and one Chef do. The Cashier acts on its own. I ran each sample below through a small simulator that follows the brief’s turn rules, so the numbers are consistent. The brief leaves a few things open, so I assumed them. They are listed after the three runs.

### **How the game works**

* **Setup:** 18 turns, 2 tables, and one customer arriving at the start of turns 1, 4, 7, 10, 13 and 16\.  
* **Menu:** Salad \$9 (1 prep unit), Burger \$15 (1 unit), Pasta \$18 (2 units).  
* **Each turn:**  
  1. A customer arrives and waits in the queue at satisfaction 100\.  
  2. You pick one Waiter task (seat, take an order, or serve a READY dish) and one Chef task (advance one order by one unit).  
  3. The Waiter acts before the Chef. A dish the Chef finishes this turn can only be served next turn.  
  4. A customer served on an earlier turn finishes eating. The Cashier then collects from one customer who became ready on an earlier turn, and that frees the table.  
  5. Anyone not yet served loses satisfaction (Regular 8, VIP 5, Critic 12). At 0 they leave without paying.  
* **The core tension:** the Waiter can do only one thing per turn, and arrivals collide with serving duties. Every delay costs satisfaction.  
* **Winning:** after turn 18, you need at least 4 paid customers, at least \$45 revenue, and average satisfaction of at least 60\. The satisfaction average uses the value recorded at the moment each customer pays.

In each trace below, a customer ID like C3 means the third arrival. The number after a customer is their satisfaction at the end of that turn.

---

### **Stage 1: Regular customers only**

Customers order in this order: Burger, Pasta, Salad, Burger, Pasta, Salad.

#### **Victory run**

| Turn | Arrival | Waiter | Chef | Cashier / events | End of turn |
| ----- | ----- | ----- | ----- | ----- | ----- |
| 1 | C1 | Seat C1 | – | – | C1 92 |
| 2 | – | Take C1’s order | Burger done, so it’s READY | – | C1 84 |
| 3 | – | Serve C1 | – | – | C1 84, and no further loss |
| 4 | C2 | Seat C2 | – | C1 finishes eating | C2 92 |
| 5 | – | Take C2’s order | Pasta 1/2 | Collects **\$15** from C1 (84) | C2 84 |
| 6 | – | Nothing legal to do | Pasta 2/2, so it’s READY | – | C2 76 |
| 7 | C3 | **Serve C2** | – | – | C2 76, C3 waiting 92 |
| 8 | – | Seat C3 | – | C2 finishes eating | C3 84 |
| 9 | – | Take C3’s order | Salad READY | Collects **\$18** from C2 (76) | C3 76 |
| 10 | C4 | Serve C3 | – | – | C4 waiting 92 |
| 11 | – | Seat C4 | – | C3 finishes eating | C4 84 |
| 12 | – | Take C4’s order | Burger READY | Collects **\$9** from C3 (76) | C4 76 |
| 13 | C5 | Serve C4 | – | – | C5 waiting 92 |
| 14 | – | Seat C5 | – | C4 finishes eating | C5 84 |
| 15 | – | Take C5’s order | Pasta 1/2 | Collects **\$15** from C4 (76) | C5 76 |
| 16 | C6 | Seat C6 | Pasta 2/2, READY | – | C5 68, C6 92 |
| 17 | – | Serve C5 | – | – | C6 84 |
| 18 | – | Take C6’s order | Salad READY | C5 finishes eating | End |

**Result: Victory.** Four customers paid, revenue was \$57.00, and average satisfaction was 78\. C5 would have paid on turn 19, so that revenue never counts.

The manager’s choice mattered on turns 7, 10 and 13\. Each time a new arrival and a READY dish competed for the Waiter, the run served first. That kept customers moving through the pipeline.

#### **Defeat run**

The manager leaves the Waiter on Wait for turns 1–3, so C1 sits in the queue and drops from 100 to 76\. After that, the manager always picks seat, then order, then serve, and the Chef works the oldest order first.

| Turn | What happened |
| ----- | ----- |
| 1–3 | C1 waits in the queue and drops to 76 |
| 4–5 | Waiter seats C1 and C2. Both keep losing 8 per turn |
| 6 | C1’s Burger is READY, but it isn’t served |
| 7 | Waiter takes C2’s order instead of serving the READY Burger, so C1 keeps waiting |
| 8–9 | Waiter finally serves C1 (44), then C2 (60) |
| 10–12 | Cashier collects \$15 from C1 (sat 44\) and \$18 from C2 (sat 60). C3 and C4 sit in the queue and seats are delayed |
| 13–18 | The same lag repeats. C3 pays at 36 and C4 at 52\. C5 is only seated on turn 18 and never pays |

**Result: Defeat.** Four customers paid and revenue was \$57.00, so both of those targets were met. The average satisfaction was only 48, below the 60 needed. A passive start plus a rigid priority order wasted too many waiting turns.

---

### **Stage 2: VIP and Critic added**

* **VIP:** loses 5 satisfaction per waiting turn and gets 10% off.  
* **Critic:** loses 12 per waiting turn. In my version the Critic always orders Pasta, because the brief asks you to define this behaviour yourself.  
* **Arrivals:** C2 (turn 4\) is a VIP ordering Pasta, and C4 (turn 10\) is a Critic. The other four arrivals are Regulars ordering Burger, Salad, Burger and Salad.

| Turn | Arrival | Waiter | Chef | Cashier / events | End of turn |
| ----- | ----- | ----- | ----- | ----- | ----- |
| 1–3 | C1 Regular, Burger | Seat, take order, serve | Burger | – | C1 84 |
| 4 | **C2 VIP** | Seat C2 | – | C1 finishes eating | C2 95 |
| 5 | – | Take C2’s order, **bill \$16.20** (Pasta \$18 × 0.9) | Pasta 1/2 | \$15 from C1 (84) | C2 90 |
| 6 | – | – | Pasta 2/2, READY | – | C2 85 |
| 7 | C3 Regular | **Seat C3** (not serve C2) | – | – | C2 80, C3 92 |
| 8 | – | Serve C2 | – | – | C3 84 |
| 9 | – | Take C3’s order | Salad READY | C2 finishes eating | C3 76 |
| 10 | **C4 Critic** | Serve C3 | – | \$16.20 from C2 (80) | C4 queue 88 |
| 11 | – | Seat C4 | – | C3 finishes eating | C4 76 |
| 12 | – | Take C4’s order (Pasta) | Pasta 1/2 | \$9 from C3 (76) | C4 64 |
| 13 | C5 Regular | Seat C5 | Pasta 2/2, READY | – | C4 52, C5 92 |
| 14 | – | Serve C4 | – | – | C5 84 |
| 15 | – | Take C5’s order | Burger READY | C4 finishes eating | C5 76 |
| 16 | C6 | Serve C5 | – | \$18 from C4 (52) | – |
| 17 | – | Seat C6 | – | C5 finishes eating | – |
| 18 | – | Take C6’s order | Salad READY | \$15 from C5 (76) | End |

**Result: Victory.** Five customers paid, revenue was \$73.20, and average satisfaction was 73.6.

* **Turn 7:** the Waiter could serve the READY VIP dish or seat the new Regular. The VIP loses only 5 per turn and the Regular loses 8, so I seated the Regular first.  
* **Turn 10:** this is a real trade-off. The Critic decays fastest at 12 per turn, but serving C3 first moved C3’s payment forward.  
* **The Critic’s cost:** his low payment satisfaction of 52 shows how costly his fast decay is.  
* **Code design:** the different loss rates and discounts sit inside the customer objects. The turn controller doesn’t check customer types.

---

### **Stage 3: Host, ComboMeal and Happy Hour**

* **Host:** seats one queued customer per turn on top of the Waiter’s task.  
* **Combos:**  
  * Salad \+ Burger costs \$21.60 for 2 units.  
  * Salad \+ Pasta costs \$24.30 for 3 units.  
* **Combo rule (mine):** customers 1 and 5 order a combo and the rest order single items.  
* **Arrivals:** the same customer types as Stage 2, with C1 ordering Salad \+ Burger and C5 ordering Salad \+ Pasta.

| Turn | Arrival | Host / Waiter | Chef | Cashier / events | End of turn |
| ----- | ----- | ----- | ----- | ----- | ----- |
| 1 | C1 | Host seats C1. Waiter takes the **combo order, \$21.60** | Combo 1/2 | – | C1 92 |
| 2 | – | – | Combo 2/2, READY | – | C1 84 |
| 3 | – | Serve C1 | – | – | C1 84 |
| 4 | C2 VIP | Host seats C2. Waiter takes the order (\$16.20) | Pasta 1/2 | C1 finishes eating | C2 95 |
| 5 | – | – | Pasta 2/2, READY | \$21.60 from C1 (84) | C2 90 |
| 6 | – | Serve C2 | – | – | C2 90 |
| 7 | C3 | Host seats C3. Waiter takes the Salad order | Salad READY | C2 finishes eating | C3 92 |
| 8 | – | Serve C3 | – | \$16.20 from C2 (90) | C3 92 |
| 10 | C4 Critic | **Happy Hour ON.** Host seats C4. Waiter takes the order at **\$14.40** (\$18 × 0.8) | Pasta 1/2 | \$9 from C3 (92) | C4 88 |
| 11 | – | – | Pasta 2/2, READY | Happy Hour recovers C4 from 88 to 100 | C4 88 |
| 12 | – | Serve C4 | – | – | C4 88 |
| 13 | C5 | Host seats C5. Waiter takes the **combo order, \$24.30** | Combo 1/3 | C4 finishes eating | C5 92 |
| 14 | – | – | Combo 2/3 | \$14.40 from C4 (88) | C5 84 |
| 15 | – | – | Combo 3/3, READY | – | C5 76 |
| 16 | C6 | Host seats C6. Waiter serves C5 | – | – | C5 76 |
| 17 | – | Take C6’s order | Burger READY | C5 finishes eating | – |
| 18 | – | – | – | \$24.30 from C5 (76) | End |

**Result: Victory.** Five customers paid, revenue was \$85.50, and average satisfaction was 86\. Turns 9 and 14 aren’t listed above. Turn 9 only moves C3 to ready-to-pay, and turn 14’s events are in the row above.

* **Happy Hour:** I switched it on at turn 10 for the Critic. It saved him 12 satisfaction points (he paid at 88 instead of 76). The cost was \$3.60 of revenue on his bill.  
* **Host:** arrivals get seated and ordered in the same turn, which removes the Stage 1 and 2 Waiter bottleneck. The game gets much easier, so a Stage 3 design could tighten the other rules.

---

### **Assumptions I made**

* **Food choices:** I used a fixed script. The brief says to document a deterministic rule, and you’ll define your own.  
* **Critic:** he always orders Pasta, as noted above.  
* **Host timing:** the Host seats before the Waiter acts, so the Waiter can take that customer’s order on the same turn. The brief doesn’t spell this out, so confirm it with your group.  
* **Happy Hour timing:** I applied the \+15 recovery from the turn it is switched on, and it covers that turn and the next.

I can turn any of these into a test-notes section for your report, or a seeded script for your video.

Me:

Make a list of relevant "classes" from this for each stage and their possible functions.

Claude:

Class names below are suggestions, since the brief gives you design freedom. Each stage lists only what is new or changed. Stage 1 already contains the extension points that Stages 2 and 3 need, so those stages mostly add classes instead of rewriting old ones.

### **Stage 1: Basic operation**

**Enums**

| Class | Values / purpose |
| ----- | ----- |
| `CustomerState` | WAITING, SEATED, ORDERED, SERVED, READY\_TO\_PAY, PAID, LEFT |
| `OrderStatus` | PLACED, READY, SERVED, PAID, CANCELLED |
| `GameResult` | VICTORY, DEFEAT |

**Entities**

| Class | Responsibility | Possible methods |
| ----- | ----- | ----- |
| `MenuItem` | Name, price in cents and prep units | `getName()`, `getPriceCents()`, `getPrepUnits()` |
| `Menu` | Holds the available items | `getItems()`, `getItem(name)` |
| `Customer` | Holds satisfaction, state, table and order. Make it abstract now, with `RegularCustomer` as the only subclass, so Stage 2 doesn’t need a rewrite | `loseSatisfaction()`, `getSatisfaction()`, `hasLeft()`, `markServed()`, `markReadyToPay()`, `lossPerWaitingTurn()` (abstract), `computeBill(basePrice)` |
| `RegularCustomer` | Loses 8 per waiting turn, pays full price | overrides `lossPerWaitingTurn()` and `computeBill()` |
| `Order` | One customer’s order, prep progress and locked bill | `advanceOneUnit()`, `isReady()`, `markServed()`, `markPaid()`, `cancel()`, `getRemainingUnits()` |
| `Table` | Occupied or free | `seat(customer)`, `free()`, `isFree()`, `getCustomer()` |
| `WaitingQueue` | FIFO line of arrivals | `add(customer)`, `peekNext()`, `remove(customer)`, `getAll()` |

**Staff**

| Class | Responsibility | Possible methods |
| ----- | ----- | ----- |
| `Waiter` | One task per turn | `seatCustomer(customer, table)`, `takeOrder(customer)`, `serveOrder(order)`, `canAct()` |
| `Chef` | Advances one order by one unit per turn | `prepare(order)`, `canAct()` |
| `Cashier` | Collects from one ready-to-pay customer per turn, oldest first | `processPayment(restaurant)` |

**Control**

| Class | Responsibility | Possible methods |
| ----- | ----- | ----- |
| `Restaurant` | Holds tables, queue, orders, customers and staff | `getFreeTable()`, `getReadyOrders()`, `getSeatedCustomers()`, `addRevenue(cents)`, `recordPayment(satisfaction)` |
| `GameController` / `TurnManager` | Runs the six turn steps in order | `runGame()`, `runTurn()`, `processArrivals()`, `resolveKitchenAndServing()`, `resolveEatingAndPayment()`, `applyWaitingLoss()` |
| `ArrivalSchedule` | Decides who arrives on which turn | `getArrival(turn)` |
| `OrderChoiceRule` | Documented rule for what a customer orders | `chooseItem(customer, menu)` |
| `ObjectiveChecker` | Applies the three win conditions | `evaluate(stats)` |
| `GameStats` | Counters shown at turn end | `getPaidCount()`, `getRevenueCents()`, `getAverageSatisfaction()`, `getUnhappyDepartures()` |

**Boundary**

| Class | Responsibility | Possible methods |
| ----- | ----- | ----- |
| `ConsoleUI` | Shows state and collects choices. It holds no game rules | `showTurnStart(state)`, `promptWaiterAction()`, `promptChefAction()`, `showError(msg)`, `showResult(result)` |
| `InputParser` | Validates input | `readChoice(min, max)` |
| `MoneyFormatter` | Turns cents into “\$18.00” | `format(cents)` |

### **Stage 2: Customer behaviour**

**New classes**

| Class | Responsibility | Possible methods |
| ----- | ----- | ----- |
| `VIPCustomer` | Loses 5 per waiting turn and gets 10% off | overrides `lossPerWaitingTurn()` and `computeBill()` |
| `CriticCustomer` | Loses 12 per waiting turn, pays full price, and has your own defined behaviour (for example always ordering Pasta) | overrides `lossPerWaitingTurn()`, plus `chooseItem()` or `reactToService()` |
| `CustomerFactory` | Builds the right customer for each arrival | `create(type)` |

**Changed classes**

| Class | Change |
| ----- | ----- |
| `ArrivalSchedule` | Returns types as well as turns. Two arrivals become VIP and Critic |
| `OrderChoiceRule` | May ask the customer for its preference, so the Critic’s rule lives in the customer and not in the controller |
| `Customer` | Almost nothing, if you made the hooks (`lossPerWaitingTurn`, `computeBill`) abstract in Stage 1 |

`TurnManager` stays untouched. It calls `customer.lossPerWaitingTurn()` and `customer.computeBill(...)`, so polymorphism replaces any chain of type checks.

### **Stage 3: Host, ComboMeal, Happy Hour**

**New classes**

| Class | Responsibility | Possible methods |
| ----- | ----- | ----- |
| `Host` | Seats at most one queued customer per turn, on top of the Waiter | `seatCustomer(customer, table)`, `canAct()` |
| `ComboMeal` | Two menu items. Its prep units are the sum and its price is 90% of the combined price | `getPriceCents()` (rounded to the nearest cent), `getPrepUnits()`, `getComponents()` |
| `PricingPolicy` | Strategy for how a bill is calculated | `applyTo(baseCents, customer)` |
| `NormalPricing` | Applies the customer’s own discount only | `applyTo()` |
| `HappyHourPricing` | Applies the customer discount first, then 20% off | `applyTo()` |
| `HappyHourManager` | Tracks the single activation, remaining turns and the \+15 recovery | `activate()`, `isActive()`, `remainingTurns()`, `applyTurnStartRecovery(customers)`, `tick()` |

**Changed classes**

| Class | Change |
| ----- | ----- |
| `MenuItem` | Becomes a shared abstraction (an interface or base class) with `MenuItem` and `ComboMeal` as implementations |
| `Menu` | Registers the combo items |
| `OrderChoiceRule` | Gains your documented rule for choosing combos |
| `Order` | Takes the price at order time using the current `PricingPolicy`, then locks it |
| `Restaurant` | Registers the Host, the policy and the manager |
| `TurnManager` | Adds a start-of-turn Happy Hour step and a Host action step |
| `ConsoleUI` | Adds the Host choice, a Happy Hour toggle and a remaining-duration display |

Because the price locks when the order is taken, `Order` stores the final cents. A later policy change then can’t affect existing orders.

### **Design points to annotate in your diagrams**

* **Encapsulation:** `Order` hides its remaining units, and only `advanceOneUnit()` can change them.  
* **Polymorphism:** `Customer.lossPerWaitingTurn()` and `computeBill()`, and `PricingPolicy.applyTo()`.  
* **Composition:** `Restaurant` owns its tables, queue and staff. `ComboMeal` is built from two `MenuItem`s.  
* **Possible extension:** a shared `StaffMember` interface would help Stage 3 (Host, Waiter, Chef and Cashier all act once per turn). The brief says not to add interfaces just to satisfy a checklist, so add it only if you use it.

I can turn this into a class diagram if that helps. The brief asks for one in the final report.

Me:

How can we implement the SOLID design principles in this project

Claude:

Apply each principle only where it solves a real problem in this game. The brief says to discuss SOLID “only where genuinely reflected in your code”, so a short, honest list scores better than forcing all five everywhere.

### **S: Single Responsibility**

Each class should have one reason to change.

| Class | Its one job | What it must not do |
| ----- | ----- | ----- |
| `Order` | Tracks prep progress, status and locked bill | Decide who gets served |
| `Waiter`, `Chef`, `Cashier` | Perform their own actions | Run the turn sequence |
| `TurnManager` | Orders the six steps | Contain pricing or discount rules |
| `ObjectiveChecker` | Applies the win conditions | Count revenue (`GameStats` does that) |
| `ConsoleUI` | Display and input only | Any game rule |

The usual failure is a bloated `GameController` that seats, prices, validates input and prints. The brief’s “keep rules out of the UI” requirement is itself an SRP requirement.

### **O: Open/Closed**

This is your strongest principle, because Stages 2 and 3 are extensions.

* **Stage 2:** add `VIPCustomer` and `CriticCustomer` without editing `TurnManager`. The controller calls the same two methods on every customer:

java

```java
abstract class Customer {
    abstract int lossPerWaitingTurn();
    abstract int computeBill(int baseCents);
}
// controller: c.loseSatisfaction(c.lossPerWaitingTurn());
```

* **Stage 3:** `ComboMeal` and `HappyHourPricing` also plug in without edits to existing logic.  
* **How to prove it:** the README transition table (stage 1 to 2\) should show that `TurnManager` is unchanged.

The anti-pattern to avoid is `if (customer instanceof VIPCustomer)` anywhere in the controller. The brief explicitly forbids it.

### **L: Liskov Substitution**

Any `Customer` subclass must work wherever a `Customer` is expected.

* `VIPCustomer` and `CriticCustomer` must follow the same flow (queue, seat, order, serve, eat, pay). They can change numbers and preferences, but they must not skip steps or add steps the controller must know about.  
* `CriticCustomer.chooseItem()` should still return a valid `MenuItem` from the menu, never null.  
* `ComboMeal` must be orderable and cookable anywhere a `MenuItem` is, using the same `getPriceCents()` and `getPrepUnits()`.

A quick test is to run your whole turn loop with a list of mixed customer types and check that no special cases appear.

### **I: Interface Segregation**

Be selective here, because the brief warns against interfaces added just to satisfy a checklist. Add a small interface only if it keeps a class from depending on methods it doesn’t use. Two possible fits:

* **A read-only game state view.** `ConsoleUI` takes a `GameStateView` with getters only, so it can’t mutate the restaurant.  
* **A small `PricingPolicy` interface** with a single method, `applyTo(baseCents, customer)`.

Skip a big `Staff` interface that forces `Cashier` to implement `seatCustomer()`. If you want a shared type, keep it tiny, for example `canAct()`.

### **D: Dependency Inversion**

High-level logic should depend on abstractions, not concrete classes.

* `Order` depends on a `PricingPolicy`, not on `HappyHourPricing` directly. Stage 3 then swaps the policy at runtime.  
* `TurnManager` depends on `Customer`, not on `RegularCustomer`.  
* Inject dependencies through constructors, with a `Main` or factory class wiring everything:

java

```java
new TurnManager(restaurant, new NormalPricing(), arrivalSchedule, ui);
```

* The game core shouldn’t call `System.out` directly. It depends on a small UI abstraction (or the `GameStateView` above), which also lets you test the rules without the console.

### **Where each principle shows up in your report**

| Principle | Concrete evidence to cite |
| ----- | ----- |
| SRP | `ConsoleUI` vs `TurnManager` vs `ObjectiveChecker` |
| OCP | Stage 2 diff: new customer files, `TurnManager` untouched |
| LSP | Mixed customer list in the same turn loop |
| ISP | `GameStateView` or the single-method `PricingPolicy` |
| DIP | Constructor injection of `PricingPolicy` and the UI |

For the “trade-offs and one alternative design” section, a good alternative is a single `Customer` class with a `type` enum and a switch statement. It is simpler to start with. It breaks OCP, because every new customer type means editing the switch in several places.

I can sketch a skeleton of the Stage 1 classes with these hooks in place if that would help.

Me:

Give me format and UML elements, fragments etc. for sequence diagrams

Claude:

### **Basic elements**

| Element | Notation | Use in your game |
| ----- | ----- | ----- |
| **Lifeline** | A box at the top with a dashed vertical line below it. Label it `name : Class`, for example `:Waiter` or `c1 : VIPCustomer` | One per object that takes part |
| **Actor** | Stick figure | The Manager (the player) |
| **Activation bar** | A thin rectangle on the lifeline while the object is active | Shows when `TurnManager` or `Waiter` is working |
| **Synchronous message** | Solid line with a filled arrowhead, labelled `method(args)` | `waiter.takeOrder(c1)` |
| **Return message** | Dashed line with an open arrowhead | `order`, `true`, `bill` |
| **Asynchronous message** | Solid line with an open arrowhead | Rarely needed in a turn-based game |
| **Self-message** | Arrow that loops back to the same lifeline | `applyWaitingLoss()` inside `TurnManager` |
| **Create message** | Dashed arrow pointing at a new lifeline box, labelled `«create»` | `CustomerFactory` creating a `VIPCustomer`, or creating an `Order` |
| **Destroy** | A large X at the end of a lifeline | A customer who abandons, or a cancelled order |
| **Note** | Folded-corner box | State changes, such as `Order: READY` |

Lifelines are normally laid out left to right in this order: Actor, UI (boundary), Controller, Staff, Domain entities. Time runs downward.

### **Combined fragments**

A fragment is a labelled box with a tag in the top-left corner. The guard goes in square brackets.

| Fragment | Tag | Meaning | Where to use it |
| ----- | ----- | ----- | ----- |
| **Loop** | `loop [turn <= 18]` | Repeats while the guard holds | The 18-turn game loop |
| **Alternative** | `alt [guard]` / `[else]` | If/else, with the branches separated by a dashed line | Order READY vs not READY, win vs defeat |
| **Option** | `opt [guard]` | If with no else | `opt [dish was READY before this turn]` before serving |
| **Break** | `break [input invalid]` | Exits the enclosing fragment | Invalid choice: show the error, re-prompt, don’t use up the task |
| **Parallel** | `par` | Concurrent regions | Not needed, since your turn order is strictly sequential |
| **Reference** | `ref` | Points to another diagram | Use it to keep a long scenario readable, for example `ref: Cashier collects payment` |

Typical nesting is a `loop` for the game, containing the six turn steps, with `alt` or `opt` inside for decisions.

### **What the brief asks for**

The brief wants one complete scenario in which a manager choice changes an outcome. It must include:

* customer arrival and order,  
* Waiter and Chef assignments,  
* READY, then SERVED,  
* eating and payment,  
* the result check,  
* one Stage 2 or 3 behaviour, such as VIP pricing or Happy Hour.

It also wants messages among the CLI, controller, staff and domain objects, with relevant guards and loops. Suggested lifelines:

`Manager` · `:ConsoleUI` · `:GameController` · `:Waiter` · `:Chef` · `:Cashier` · `c1 : VIPCustomer` · `:Order` · `:PricingPolicy` · `:Restaurant`

### **Skeleton you can adapt**

This is written for PlantUML, which is easy to export as an image:

plantuml

```
@startuml
actor Manager
participant ConsoleUI
participant GameController
participant Waiter
participant Chef
participant Cashier
participant "c2 : VIPCustomer" as C2
participant Order

loop turn <= 18
  GameController -> GameController : processArrivals()
  GameController -> ConsoleUI : showTurnStart(state)

  ConsoleUI -> Manager : prompt Waiter action
  Manager --> ConsoleUI : "take order C2"

  alt valid choice
    ConsoleUI -> GameController : waiterAction(TAKE_ORDER, C2)
    GameController -> Waiter : takeOrder(C2)
    Waiter -> C2 : chooseItem(menu)
    C2 --> Waiter : Pasta
    Waiter -> C2 : computeBill(1800)
    C2 --> Waiter : 1620
    create Order
    Waiter -> Order : <<create>>(Pasta, 1620)
  else invalid choice
    ConsoleUI -> Manager : explain error, re-prompt
  end

  GameController -> Chef : prepare(order)
  Chef -> Order : advanceOneUnit()
  opt remainingUnits == 0
    Order -> Order : status = READY
  end

  opt order READY before this turn
    GameController -> Waiter : serveOrder(order)
  end

  GameController -> Cashier : processPayment()
  GameController -> GameController : applyWaitingLoss()
end

GameController -> GameController : evaluateResult()
alt all 3 targets met
  GameController -> ConsoleUI : showResult(VICTORY)
else
  GameController -> ConsoleUI : showResult(DEFEAT)
end
@enduml
```

### **Marking tips**

* **Make the manager’s choice visible.** Use an `alt` where the Manager picks “serve C2” vs “seat C3”. The brief asks for a scenario in which a choice changes the outcome.  
* **Show polymorphism in the messages.** `c2.computeBill()` returns the VIP price without the controller checking a type. Add a note such as `«polymorphic call»`.  
* **Show the READY-vs-SERVED guard.** An `opt [order was READY at start of turn]` before `serveOrder`.  
* **Keep it complete but readable.** Use `ref` frames, for example for payment, if the diagram gets too big.  
* **Match the code.** The class diagram, sequence diagram and Stage 3 code must agree. Method names on arrows should be real methods.

