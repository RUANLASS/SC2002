# Restaurant Rush

SC2002 group assignment: a turn-based restaurant management game in Java with a command-line interface. The player is the manager. Staff time is limited, so every turn the manager decides which customer or order comes first.

- **Repository:** https://github.com/RUANLASS/SC2002
- **Lab group:** TODO (for example FCS1_Grp1)
- **Members:** TODO (names and matric numbers)

## Contents

1. [Requirements](#requirements)
2. [Stages, folders and tags](#stages-folders-and-tags)
3. [Build and run](#build-and-run)
4. [How a turn is played](#how-a-turn-is-played)
5. [Rules as implemented](#rules-as-implemented)
6. [Stage comparisons](#stage-comparisons)
7. [Reproducible runs](#reproducible-runs)
8. [Design evidence](#design-evidence)
9. [Video timestamps](#video-timestamps)

## Requirements

- JDK 17 or newer (the Cashier uses a Java `record`). Tested with JDK 21.
- No libraries and no network access. Everything runs offline.

## Stages, folders and tags

Each stage is a complete, runnable version tagged in GitHub. Stage 1 is the repository root; Stages 2 and 3 are full copies in their own folders so that all three can be read side by side.

| Stage | Adds | Folder | Git tag |
| --- | --- | --- | --- |
| 1 Basic operation | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, victory and defeat | repository root (`Main.java`, `engine/`, `model/`, `staff/`, `ui/`) | `stage-1` |
| 2 Customer behaviour | VIPCustomer and CriticCustomer | `Stage-2/` | `stage-2` |
| 3 New role and menu policy | Host, ComboMeal, Happy Hour | `Stage-3/` | `stage-3` (final) |

Every stage has the same package layout: `engine` (game controller), `model` (entities), `staff` (roles) and `ui` (command-line boundary).

## Build and run

From the repository root:

```bash
# Stage 1
javac -d out/stage-1 Stage-1/Main.java Stage-1/engine/*.java Stage-1/model/*.java Stage-1/staff/*.java Stage-1/ui/*.java
java -cp out/stage-1 Main

# Stage 2
(cd Stage-2 && javac -d ../out/stage-2 Main.java engine/*.java model/*.java staff/*.java ui/*.java)
java -cp out/stage-2 Main

# Stage 3 (final version)
(cd Stage-3 && javac -d ../out/stage-3 Main.java engine/*.java model/*.java staff/*.java ui/*.java)
java -cp out/stage-3 Main
```

To replay a recorded run instead of typing, redirect an input file: `java -cp out/stage-3 Main < test-runs/stage-3/victory.in`. To check all runs at once: `bash test-runs/run_tests.sh`.

## How a turn is played

The game always offers the required base setting: 18 turns, 2 tables, one Waiter (one task per turn), one Chef (one preparation unit per turn), one Cashier (one payment per turn), and one arrival at the start of turns 1, 4, 7, 10, 13 and 16. The objective, checked after turn 18, is at least 4 paid customers, revenue of at least $45.00 and an average satisfaction of paid customers of at least 60.

Each turn the game shows the turn number, revenue, average satisfaction of paid customers, the waiting queue, both tables with each customer's satisfaction and order status, and then asks for the manager's choices in this order:

| Order | Prompt | Choices |
| --- | --- | --- |
| 1 | Happy Hour (Stage 3, until used) | activate now, or not now |
| 2 | Host (Stage 3) | seat a queued customer at a chosen table, or skip |
| 3 | Waiter | seat a queued customer, take an order, serve a READY dish, or wait |
| 4 | Chef | cook one unit of an ordered dish at a chosen table, or wait |
| 5 | Cashier | automatic: collects from the READY_TO_PAY customer who arrived earliest |

An invalid choice (out of range, not a number, an empty table, a dish that is not ready) prints why it failed and asks again without using up the staff task. At the end of each turn the game prints the served count, unhappy departures, revenue, average satisfaction of paid customers (N/A until someone pays) and turns used.

## Rules as implemented

**Customer food choice** (deterministic, no randomness; the choice is shown in the queue and again when the order is placed):

- Regular and VIP customers cycle through the menu by customer id: #1 Salad, #2 Burger, #3 Pasta, #4 Salad, and so on.
- A Critic always orders the most demanding dish: most preparation units, then highest price (Pasta on the base menu).
- Stage 3 combo rule: a customer with an **even id** upgrades to a combo of their preferred dish plus the next dish on the menu (wrapping round). Odd ids keep the single dish.

**Arrivals on the base setting**

| Turn | Customer | Stage 1 | Stage 2 and 3 type | Dish in Stage 1 and 2 | Dish in Stage 3 |
| --- | --- | --- | --- | --- | --- |
| 1 | #1 | Regular | Regular | Salad | Salad |
| 4 | #2 | Regular | **VIP** | Burger | Burger + Pasta Combo |
| 7 | #3 | Regular | **Critic** | Pasta | Pasta |
| 10 | #4 | Regular | Regular | Salad | Salad + Burger Combo |
| 13 | #5 | Regular | Regular | Burger | Burger |
| 16 | #6 | Regular | Regular | Pasta | Pasta + Salad Combo |

**Satisfaction.** Every customer who has not yet been served (waiting for a table, seated without an order, ORDERED or READY) loses points at the end of each turn: Regular 8, VIP 5, Critic 12, minimum 0. A customer who reaches 0 leaves without paying: the order is cancelled and the table is freed. Served, eating and ready-to-pay customers lose nothing.

**Timing.** A dish finished this turn is served next turn at the earliest. A customer served on turn T eats during turn T+1, becomes READY_TO_PAY at the end of that turn and pays on turn T+2 at the earliest. Satisfaction is recorded when the customer pays.

**Critic behaviour** (Stage 2): a Critic always orders the most demanding dish. If the dish is served within 3 turns of the Critic's arrival (the fastest possible service), the Critic is impressed and gains 10 satisfaction (maximum 100). Slower service earns no bonus. Both behaviours live in `CriticCustomer`; the controller never checks the customer type.

**Host** (Stage 3): seats at most one queued customer per turn in addition to the Waiter's task. The Host acts before the Waiter, so the Waiter can take the new customer's order in the same turn.

**ComboMeal** (Stage 3): two existing menu items. Preparation units are the sum of the components; the price is 90% of the combined price rounded to the nearest cent (Salad + Pasta = $24.30, 3 units).

**Happy Hour** (Stage 3): can be activated once per game at the start of a turn and lasts for that turn and the next; the remaining duration is shown. Orders taken while it is active get 20% off, applied after the customer's own discount (a VIP's Burger is $15.00 x 0.9 x 0.8 = $10.80). The price is locked when the order is taken. Customers still waiting for a table or food recover 15 satisfaction (maximum 100) at the start of each active turn.

**Money** is stored as integer cents everywhere and shown as dollars with two decimals.

## Stage comparisons

Because Stages 2 and 3 are full copies in their own folders, GitHub's compare view shows each folder as new files. To see only what changed, use a local diff:

```bash
diff -ru engine  Stage-2/engine ; diff -ru model Stage-2/model ; diff -ru staff Stage-2/staff ; diff -ru ui Stage-2/ui   # 1 -> 2
diff -ru Stage-2 Stage-3                                                                                              # 2 -> 3
```

### Stage 1 to Stage 2

Compare view: https://github.com/RUANLASS/SC2002/compare/stage-1...stage-2 (commit: TODO hash)

| File | Change | Reason |
| --- | --- | --- |
| `Stage-2/engine/ArrivalSchedule.java` | **new** | Says which customer type arrives on which turn using constructor references, so the controller creates customers without any `if` or `switch` on type |
| `Stage-2/model/VIPCustomer.java` | **new** | VIP: decay 5, bill 10% off |
| `Stage-2/model/CriticCustomer.java` | **new** | Critic: decay 12, Pasta preference, impressed bonus |
| `Stage-2/model/Customer.java` | changed | Everything that differs by customer type became an overridable method (`getDecayRate`, `applyDiscount`, `selectItem`, `onServed`, `getServiceReaction`, `getTypeName`), so subclasses override behaviour instead of the controller testing types |
| `Stage-2/engine/RestaurantGame.java` | changed | Arrivals now come from an injected `ArrivalSchedule` instead of hard-coded turn numbers |
| `Stage-2/staff/Waiter.java` | changed | `serveDish` calls `customer.onServed(turn)`, one line that lets each customer react in its own way |
| `Stage-2/ui/GameCLI.java` | changed | Order confirmation shows the dish and the locked bill; serving prints the customer's reaction |
| `Order`, `Table`, `Chef`, `Cashier`, `Staff`, `MenuItem`, `SingleMenuItem`, `OrderStatus`, `InputValidator`, `Main` | unchanged | Stage 1 design already isolated them from customer type |

Nothing was deleted.

### Stage 2 to Stage 3

Compare view: https://github.com/RUANLASS/SC2002/compare/stage-2...stage-3 (commit: TODO hash)

| File | Change | Reason |
| --- | --- | --- |
| `Stage-3/staff/Host.java` | **new** | New role that seats one customer per turn |
| `Stage-3/model/ComboMeal.java` | **new** | Holds two `MenuItem`s by reference (aggregation) and implements `MenuItem` itself, so `Order`, `Chef`, `Waiter` and `Cashier` handle it unchanged |
| `Stage-3/model/ComboPolicy.java` | **new** | Holds the documented rule a customer follows to choose a combo |
| `Stage-3/model/HappyHour.java` | **new** | Encapsulates the Happy Hour state, duration, discount arithmetic and recovery points |
| `Stage-3/engine/RestaurantGame.java` | changed | Owns the Host, ComboPolicy and HappyHour; adds `hostSeatCustomer`, `activateHappyHour` and the recovery step; counts Happy Hour down at turn end |
| `Stage-3/model/Customer.java` | changed | New overload `chooseFood(menu, comboPolicy)` lets the preferred dish be upgraded to a combo |
| `Stage-3/staff/Waiter.java` | changed | `takeOrder` takes the `HappyHour` so the bill is `happyHour.applyTo(customer.applyDiscount(price))`: customer discount first, then Happy Hour |
| `Stage-3/ui/GameCLI.java` | changed | Happy Hour and Host prompts, Happy Hour status line |
| everything else, including `ArrivalSchedule`, `VIPCustomer`, `CriticCustomer`, `Order`, `Table`, `Chef` and `Cashier` | unchanged | |

### Corrections applied to every stage

While writing the test notes two rule errors were found and fixed (commit: TODO hash), because the brief's worked example and objective did not match the code:

| File | Fix | Reason |
| --- | --- | --- |
| `engine/RestaurantGame.java` (Stage 1 only) | `isVictory()` now checks at least 4 paid, revenue of at least $45.00 and average satisfaction of at least 60 | Stage 1 had the satisfaction test inverted, an extra abandonment limit and no paid-count check; Stages 2 and 3 already had the correct rule |
| `model/Order.java` (all three stages) | A dish served this turn is not ticked at this turn's end | Customers were paying one turn too early; the fix reproduces the brief's section 3.3 timeline |

## Reproducible runs

`TEST_NOTES.md` documents one victory and one defeat per stage, plus a run showing that invalid input never uses up a staff task and a Stage 2 what-if showing what one decision is worth. Each run is an input file in `test-runs/` with the full expected console output, and `bash test-runs/run_tests.sh` replays all of them.

## Design evidence

- Class diagram (final design, matches Stage 3): `docs/class-diagram.png` (source `docs/class-diagram.puml`)
- Sequence diagram (Critic scenario, Stage 3): `docs/sequence-diagram.png` (source `docs/sequence-diagram.puml`)
- Design discussion, functional evidence and reflections: the PDF report submitted with the project

## Video timestamps

Video: TODO (file name or link). Timestamps below are the plan; replace them with the real times after recording.

| # | Segment | Presenter | Time |
| --- | --- | --- | --- |
| 1 | Game launching from the submitted project | TODO | 0:00 |
| 2 | Meaningful manager choices, including a staff-constrained decision | TODO | 1:30 |
| 3 | A dish going READY, SERVED, then payment | TODO | 4:00 |
| 4 | VIP and Critic behaviour | TODO | 6:00 |
| 5 | Stage 3 feature: Host, combo, Happy Hour | TODO | 8:00 |
| 6 | One victory and one defeat | TODO | 10:30 |
| 7 | Stage tag comparisons and the diagrams | TODO | 12:30 |
