# Test notes: Restaurant Rush

These notes give one reproducible **victory** and one reproducible **defeat** for every stage. They also have a Stage 2 what-if about the turn 10 decision. Nothing is random. Arrivals are fixed by the brief, each customer's food choice follows a rule, and each run replays a fixed list of manager choices, so the console output is the same every time.

## 1. Replaying the runs

You need a JDK, version 17 or newer (the Cashier uses a Java `record`). From the repository root, replay one run by hand (each stage is in its own folder):

```bash
# Stage 1
(cd Stage-1 && javac -d ../out/stage-1 Main.java engine/*.java model/*.java staff/*.java ui/*.java)
java -cp out/stage-1 Main < test-runs/stage-1/victory.in

# Stage 2
(cd Stage-2 && javac -d ../out/stage-2 Main.java engine/*.java model/*.java staff/*.java ui/*.java)
java -cp out/stage-2 Main < test-runs/stage-2/victory.in

# Stage 3
(cd Stage-3 && javac -d ../out/stage-3 Main.java engine/*.java model/*.java staff/*.java ui/*.java)
java -cp out/stage-3 Main < test-runs/stage-3/victory.in
```

Run without `< file.in` to play interactively. To check a run, save the output and compare it with the run's `.expected` file, which holds the complete console output.

## 2. Reading an input file

An `.in` file is the manager's choices as numbers, in the order the game asks for them. Each turn is:

| Prompt | Numbers typed |
| --- | --- |
| Happy Hour (Stage 3 only, asked each turn until it has been used) | `1` activate, `2` not now |
| Host (Stage 3 only) | `1 <table>` seat a queued customer, `2` skip |
| Waiter | `1 <table>` seat, `2 <table>` take order, `3 <table>` serve, `4` wait |
| Chef | `1 <table>` cook one unit, `2` wait |

The Cashier needs no input. The game collects from the READY_TO_PAY customer who arrived earliest.

## 3. Results at a glance

| Stage | Run | Input file | Result | Paid | Revenue | Avg satisfaction | What decides it |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Victory | `test-runs/stage-1/victory.in` | Victory | 5 | $66.00 | 79 | Prompt service: all targets met |
| 1 | Defeat | `test-runs/stage-1/defeat.in` | Defeat | 4 | $51.00 | 50 | Revenue and paid count are fine, but average satisfaction 50 is below 60 |
| 2 | Victory | `test-runs/stage-2/victory.in` | Victory | 5 | $64.50 | 80 | Same decisions as Stage 1 victory, now with a VIP and a Critic |
| 2 | Defeat | `test-runs/stage-2/defeat.in` | Defeat | 2 | $22.50 | 48 | Late start and the Critic leaves, so paid count, revenue and satisfaction all miss |
| 2 | What-if | `test-runs/stage-2/whatif-seat-first.in` | Victory | 5 | $64.50 | 75 | Same as the victory run except turn 10: the Waiter seats customer 4 and serves the Critic a turn late |
| 3 | Victory | `test-runs/stage-3/victory.in` | Victory | 5 | $84.98 | 83 | Host and Happy Hour used |
| 3 | Defeat | `test-runs/stage-3/defeat.in` | Defeat | 3 | $53.73 | 43 | Late start, Host and Happy Hour never used, so paid count and satisfaction miss |

Targets: at least 4 paid customers, revenue at least $45.00, average satisfaction of paid customers at least 60.

The required runs are `stage-1/victory.in` (Stage 1 victory), `stage-1/defeat.in` (Stage 1 defeat) and `stage-2/victory.in` (Stage 2 base setting with a VIP and a Critic and still six arrivals). The other runs are extra.

## 4. Stage 1

### 4.1 Victory (`stage-1/victory.in`)

The Waiter serves a ready dish when one can be served, otherwise takes the next order, otherwise seats the next customer. The Chef cooks every order the Waiter has taken. Result: 5 paid, $66.00, average satisfaction 79 (84, 84, 76, 76, 76).

| Turn | Arrival | Waiter | Chef | Cashier (automatic) |
| --- | --- | --- | --- | --- |
| 1 | #1 regular (Salad) | seat T1 | wait |  |
| 2 |  | take order T1 | cook T1 |  |
| 3 |  | serve T1 | wait |  |
| 4 | #2 regular (Burger) | seat T2 | wait |  |
| 5 |  | take order T2 | cook T2 | collects $9.00 from #1 (sat 84) |
| 6 |  | serve T2 | wait |  |
| 7 | #3 regular (Pasta) | seat T1 | wait |  |
| 8 |  | take order T1 | cook T1 | collects $15.00 from #2 (sat 84) |
| 9 |  | wait | cook T1 |  |
| 10 | #4 regular (Salad) | serve T1 | wait |  |
| 11 |  | seat T2 | wait |  |
| 12 |  | take order T2 | cook T2 | collects $18.00 from #3 (sat 76) |
| 13 | #5 regular (Burger) | serve T2 | wait |  |
| 14 |  | seat T1 | wait |  |
| 15 |  | take order T1 | cook T1 | collects $9.00 from #4 (sat 76) |
| 16 | #6 regular (Pasta) | serve T1 | wait |  |
| 17 |  | seat T2 | wait |  |
| 18 |  | wait | wait | collects $15.00 from #5 (sat 76) |

What to look at:

- **Customer 1 (Salad, 1 unit)** follows the brief's section 3.3 timeline: seated turn 1 (satisfaction 92 at turn end), order and cook turn 2 (84, dish READY), served turn 3 (84, no more loss), READY_TO_PAY at the end of turn 4, paid turn 5.
- **Customer 6** arrives on turn 16 and is seated on turn 17 but cannot pay within 18 turns. At most five customers can pay.

### 4.2 Defeat (`stage-1/defeat.in`)

The manager does nothing until turn 5, then serves first, takes orders second and seats third. Result: 4 paid, $51.00, average satisfaction 50 (52, 52, 44, 52). The game is lost on the satisfaction target alone.

| Turn | Arrival | Waiter | Chef | Cashier (automatic) |
| --- | --- | --- | --- | --- |
| 1 | #1 regular (Salad) | wait | wait |  |
| 2 |  | wait | wait |  |
| 3 |  | wait | wait |  |
| 4 | #2 regular (Burger) | wait | wait |  |
| 5 |  | seat T1 | wait |  |
| 6 |  | take order T1 | cook T1 |  |
| 7 | #3 regular (Pasta) | serve T1 | wait |  |
| 8 |  | seat T2 | wait |  |
| 9 |  | take order T2 | cook T2 | collects $9.00 from #1 (sat 52) |
| 10 | #4 regular (Salad) | serve T2 | wait |  |
| 11 |  | seat T1 | wait |  |
| 12 |  | take order T1 | cook T1 | collects $15.00 from #2 (sat 52) |
| 13 | #5 regular (Burger) | seat T2 | cook T1 |  |
| 14 |  | serve T1 | wait |  |
| 15 |  | take order T2 | cook T2 |  |
| 16 | #6 regular (Pasta) | serve T2 | wait | collects $18.00 from #3 (sat 44) |
| 17 |  | seat T1 | wait |  |
| 18 |  | take order T1 | cook T1 | collects $9.00 from #4 (sat 52) |

## 5. Stage 2

### 5.1 Victory (`stage-2/victory.in`)

The manager makes the same decisions as in the Stage 1 victory. Only the customer types differ: customer 2 is a VIP (loses 5 per waiting turn, 10% off) and customer 3 is a Critic (loses 12 per waiting turn, always orders Pasta). Result: 5 paid, $64.50, average satisfaction 80 (84, 90, 74, 76, 76).

| Turn | Arrival | Waiter | Chef | Cashier (automatic) |
| --- | --- | --- | --- | --- |
| 1 | #1 Regular (Salad) | seat T1 | wait |  |
| 2 |  | take order T1 | cook T1 |  |
| 3 |  | serve T1 | wait |  |
| 4 | #2 VIP (Burger) | seat T2 | wait |  |
| 5 |  | take order T2 | cook T2 | collects $9.00 from #1 (sat 84) |
| 6 |  | serve T2 | wait |  |
| 7 | #3 Critic (Pasta) | seat T1 | wait |  |
| 8 |  | take order T1 | cook T1 | collects $13.50 from #2 (sat 90) |
| 9 |  | wait | cook T1 |  |
| 10 | #4 Regular (Salad) | serve T1 | wait |  |
| 11 |  | seat T2 | wait |  |
| 12 |  | take order T2 | cook T2 | collects $18.00 from #3 (sat 74) |
| 13 | #5 Regular (Burger) | serve T2 | wait |  |
| 14 |  | seat T1 | wait |  |
| 15 |  | take order T1 | cook T1 | collects $9.00 from #4 (sat 76) |
| 16 | #6 Regular (Pasta) | serve T1 | wait |  |
| 17 |  | seat T2 | wait |  |
| 18 |  | wait | wait | collects $15.00 from #5 (sat 76) |

What to look at:

- **VIP bill:** customer 2 orders a $15.00 Burger and pays $13.50. Satisfaction is 95 at the end of turn 4 and 90 when paid, because the VIP loses 5 per waiting turn, not 8.
- **Critic reaction, turn 10:** the Waiter can either serve the Critic's ready Pasta or seat customer 4. Serving the Critic prints "Critic #3 is impressed by the fast service! +10 satisfaction (now 74)", because the dish is served within 3 turns of arrival. Section 5.3 shows the other choice.

### 5.2 Defeat (`stage-2/defeat.in`)

The manager does nothing until turn 7. Customers 1 and 2 pay late (satisfaction 36 and 60). The Critic arrives on turn 7, loses 12 per turn in the queue and is seated on turn 13 at satisfaction 28. It loses 12 more at the end of turns 13, 14 and 15, reaches 0 and leaves without paying ("Unhappy Departures: 1"). Its order is cancelled and its table is freed. Result: 2 paid, $22.50, average satisfaction 48, so all three targets are missed.

| Turn | Arrival | Waiter | Chef | Cashier (automatic) |
| --- | --- | --- | --- | --- |
| 1 | #1 Regular (Salad) | wait | wait |  |
| 2 |  | wait | wait |  |
| 3 |  | wait | wait |  |
| 4 | #2 VIP (Burger) | wait | wait |  |
| 5 |  | wait | wait |  |
| 6 |  | wait | wait |  |
| 7 | #3 Critic (Pasta) | seat T1 | wait |  |
| 8 |  | take order T1 | cook T1 |  |
| 9 |  | serve T1 | wait |  |
| 10 | #4 Regular (Salad) | seat T2 | wait |  |
| 11 |  | take order T2 | cook T2 | collects $9.00 from #1 (sat 36) |
| 12 |  | serve T2 | wait |  |
| 13 | #5 Regular (Burger) | seat T1 | wait |  |
| 14 |  | take order T1 | cook T1 | collects $13.50 from #2 (sat 60) |
| 15 |  | seat T2 | cook T1 |  |
| 16 | #6 Regular (Pasta) | take order T2 | cook T2 |  |
| 17 |  | serve T2 | wait |  |
| 18 |  | seat T1 | wait |  |

### 5.3 What-if (`stage-2/whatif-seat-first.in`)

This replays the victory decisions with one change at turn 10. The Waiter seats customer 4 instead of serving the Critic, serves the Critic on turn 11, takes customer 4's order on turn 12 (the Chef cooks it) and serves customer 4 on turn 13. Every other turn is the same as in the victory run.

- Turn 10 header: Critic satisfaction 64 and its Pasta is READY, as in the victory run.
- The Critic's dish stays READY through turn 10, so the Critic loses another 12 and is at 52 at the start of turn 11.
- On turn 11 the game prints "Critic #3 was kept waiting 4 turns and is unimpressed. No bonus."
- The Critic is paid on turn 13 at satisfaction 52 (74 in the victory run). Result: still a victory, 5 paid, $64.50, but the average satisfaction is 75 instead of 80.

We also tried this by hand in Stage 3 (Host skipped, Waiter seats customer 4 on turn 10). The Critic was also served at satisfaction 52. This run is not saved in `test-runs/`. In Stage 3 the Host normally removes the problem by seating customer 4 while the Waiter serves the Critic.

## 6. Stage 3

### 6.1 Victory (`stage-3/victory.in`)

Result: 5 paid, $84.98, average satisfaction 83 (92, 90, 74, 76, 84).

| Turn | Arrival | Happy Hour | Host | Waiter | Chef | Cashier (automatic) |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | #1 Regular (Salad) |  | seat T1 | take order T1 | cook T1 |  |
| 2 |  |  | skip | serve T1 | wait |  |
| 3 |  |  | skip | wait | wait |  |
| 4 | #2 VIP (Burger + Pasta Combo) | **ACTIVATE** | seat T2 | take order T2 | cook T2 | collects $9.00 from #1 (sat 92) |
| 5 |  |  | skip | wait | cook T2 |  |
| 6 |  |  | skip | wait | cook T2 |  |
| 7 | #3 Critic (Pasta) |  | seat T1 | serve T2 | wait |  |
| 8 |  |  | skip | take order T1 | cook T1 |  |
| 9 |  |  | skip | wait | cook T1 | collects $21.38 from #2 (sat 90) |
| 10 | #4 Regular (Salad + Burger Combo) |  | seat T2 | serve T1 | wait |  |
| 11 |  |  | skip | take order T2 | cook T2 |  |
| 12 |  |  | skip | wait | cook T2 | collects $18.00 from #3 (sat 74) |
| 13 | #5 Regular (Burger) |  | seat T1 | serve T2 | wait |  |
| 14 |  |  | skip | take order T1 | cook T1 |  |
| 15 |  |  | skip | serve T1 | wait | collects $21.60 from #4 (sat 76) |
| 16 | #6 Regular (Pasta + Salad Combo) |  | seat T2 | take order T2 | cook T2 |  |
| 17 |  |  | skip | wait | cook T2 | collects $15.00 from #5 (sat 84) |
| 18 |  |  | skip | wait | cook T2 |  |

What to look at:

- **Host in addition to the Waiter:** on turns 1, 4, 7, 10, 13 and 16 the Host seats the new arrival and the Waiter still takes an order or serves a dish in the same turn.
- **Happy Hour, turn 4:** Happy Hour is activated at the start of turn 4 and the VIP recovers 15 satisfaction (95 to 100). The VIP's Burger + Pasta combo is $29.70 (90% of $33.00), then the VIP discount gives $26.73, then Happy Hour gives $21.38. The price is locked when the order is taken.

### 6.2 Defeat (`stage-3/defeat.in`)

The manager does nothing until turn 5, never uses the Host and never activates Happy Hour. Result: 3 paid, $53.73, average satisfaction 43 (52, 28, 50). The game is lost on both the paid count and satisfaction.

| Turn | Arrival | Happy Hour | Host | Waiter | Chef | Cashier (automatic) |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | #1 Regular (Salad) |  | skip | wait | wait |  |
| 2 |  |  | skip | wait | wait |  |
| 3 |  |  | skip | wait | wait |  |
| 4 | #2 VIP (Burger + Pasta Combo) |  | skip | wait | wait |  |
| 5 |  |  | skip | seat T1 | wait |  |
| 6 |  |  | skip | take order T1 | cook T1 |  |
| 7 | #3 Critic (Pasta) |  | skip | serve T1 | wait |  |
| 8 |  |  | skip | seat T2 | wait |  |
| 9 |  |  | skip | take order T2 | cook T2 | collects $9.00 from #1 (sat 52) |
| 10 | #4 Regular (Salad + Burger Combo) |  | skip | seat T1 | cook T2 |  |
| 11 |  |  | skip | take order T1 | cook T1 |  |
| 12 |  |  | skip | wait | cook T1 |  |
| 13 | #5 Regular (Burger) |  | skip | serve T1 | cook T2 |  |
| 14 |  |  | skip | serve T2 | wait |  |
| 15 |  |  | skip | wait | wait | collects $18.00 from #3 (sat 28) |
| 16 | #6 Regular (Pasta + Salad Combo) |  | skip | seat T1 | wait | collects $26.73 from #2 (sat 50) |
| 17 |  |  | skip | take order T1 | cook T1 |  |
| 18 |  |  | skip | seat T2 | cook T1 |  |

## 7. Where each rule is demonstrated

| Rule (assignment brief) | Where to see it |
| --- | --- |
| Regular customer loses 8 per turn while waiting, and nothing once served (3.2, step 5) | Stage 1 victory, customer 1: 100, 92, 84, then stays at 84 from turn 3 |
| A dish finished this turn cannot be served until next turn (3.2, step 3) | Stage 1 victory, customer 1: READY at the end of turn 2, served turn 3 |
| Eating takes a full turn, payment is the turn after READY_TO_PAY (3.2, step 4) | Stage 1 victory, customer 1: served turn 3, READY_TO_PAY end of turn 4, paid turn 5 |
| Cashier collects one payment per turn, automatically, from the READY_TO_PAY customer who arrived earliest (3.2, step 4) | Stage 3 victory, turns 4, 9, 12, 15, 17 |
| Abandonment cancels the order and frees the table (3.2, step 5) | Stage 2 defeat, Critic, end of turn 15 |
| At most five customers can pay (3.4) | All victory runs: customer 6 never pays |
| Money in integer cents, two decimals on screen (2.2) | Stage 3 victory: $21.38 and $24.30 |
| VIP loses 5 per turn and gets 10% off; Critic loses 12 per turn with its own order and reaction (4.2) | Stage 2 victory, customer 2 and turn 10; Stage 2 defeat |
| Host seats one customer per turn in addition to the Waiter (4.3) | Stage 3 victory, turns 1, 4, 7, 10, 13, 16 |
| Combo price is 90% of the components, units add (4.3) | Stage 3 victory: $21.60 for 2 units, $24.30 for 3 units |
| Happy Hour: once per game, two turns, +15, 20% off after the VIP discount (4.3) | Stage 3 victory, turns 4 and 5 |
