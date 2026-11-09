# Test notes: Restaurant Rush

These notes give one reproducible **victory** and one reproducible **defeat** for every stage, plus a run that proves invalid input never uses up a staff task and a Stage 2 what-if that shows what the turn 10 decision is worth. Nothing is random: arrivals are fixed by the brief, every customer's food choice follows a documented rule, and each run replays a fixed list of manager choices, so the console output is identical every time.

Expected outputs were produced after the eating-timing fix: a dish served on turn T becomes READY_TO_PAY at the end of turn T+1 and is paid on turn T+2 at the earliest.

## 1. Replaying the runs

You need a JDK, version 17 or newer (the Cashier uses a Java `record`). From the repository root:

```bash
bash test-runs/run_tests.sh        # builds all three stages, replays every run, diffs the full console output
```

To replay one run by hand (Stage 1 is the repository root; Stages 2 and 3 are the `Stage-2/` and `Stage-3/` folders):

```bash
# Stage 1
javac -d out/stage-1 Main.java engine/*.java model/*.java staff/*.java ui/*.java
java -cp out/stage-1 Main < test-runs/stage-1/victory.in

# Stage 2
(cd Stage-2 && javac -d ../out/stage-2 Main.java engine/*.java model/*.java staff/*.java ui/*.java)
java -cp out/stage-2 Main < test-runs/stage-2/victory.in

# Stage 3
(cd Stage-3 && javac -d ../out/stage-3 Main.java engine/*.java model/*.java staff/*.java ui/*.java)
java -cp out/stage-3 Main < test-runs/stage-3/victory.in
```

Run without `< file.in` to play interactively. Each run also has a `.expected` file holding the complete console output to compare against.

## 2. Reading an input file

An `.in` file is the manager's choices as numbers, in the order the game asks for them. Each turn is:

| Prompt | Numbers typed |
| --- | --- |
| Happy Hour (Stage 3 only, asked each turn until it has been used) | `1` activate, `2` not now |
| Host (Stage 3 only) | `1 <table>` seat a queued customer, `2` skip |
| Waiter | `1 <table>` seat, `2 <table>` take order, `3 <table>` serve, `4` wait |
| Chef | `1 <table>` cook one unit, `2` wait |

The Cashier needs no input: the game collects payment automatically, oldest customer first.

## 3. Results at a glance

| Stage | Run | Input file | Result | Paid | Revenue | Avg satisfaction | What decides it |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Victory | `test-runs/stage-1/victory.in` | Victory | 5 | $66.00 | 79 | Prompt service: all targets met |
| 1 | Defeat | `test-runs/stage-1/defeat.in` | Defeat | 4 | $51.00 | 50 | Revenue and paid count are fine; average satisfaction 50 is below 60 |
| 1 | Invalid inputs | `test-runs/stage-1/invalid-inputs.in` | Victory | 5 | $66.00 | 79 | Same result as the clean victory despite six bad inputs |
| 2 | Victory | `test-runs/stage-2/victory.in` | Victory | 5 | $64.50 | 80 | Same decisions as Stage 1 victory, now with a VIP and a Critic |
| 2 | Defeat | `test-runs/stage-2/defeat.in` | Defeat | 2 | $22.50 | 48 | Late start; the Critic abandons; paid count, revenue and satisfaction all miss |
| 2 | What-if | `test-runs/stage-2/whatif-seat-first.in` | Victory | 5 | $64.50 | 75 | Same as the victory run except turn 10: the Waiter seats customer 4 and serves the Critic a turn late |
| 3 | Victory | `test-runs/stage-3/victory.in` | Victory | 5 | $84.98 | 83 | Host and Happy Hour used |
| 3 | Defeat | `test-runs/stage-3/defeat.in` | Defeat | 3 | $53.73 | 43 | Late start, Host and Happy Hour never used; paid count and satisfaction miss |

Targets: at least 4 paid customers, revenue at least $45.00, average satisfaction of paid customers at least 60.

## 4. Stage 1

### 4.1 Victory (`stage-1/victory.in`)

The Waiter serves a ready dish as soon as one can be served, otherwise takes the next order, otherwise seats the next customer. The Chef cooks every order the Waiter has taken. Result: 5 paid, $66.00, average satisfaction 79 (84, 84, 76, 76, 76).

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

- **Customer 1 (Salad, 1 unit)** follows the brief's section 3.3 timeline for a one-unit dish: seated turn 1 (satisfaction 92 at turn end), order and first cook turn 2 (84, dish READY), served turn 3 (84, no further loss), READY_TO_PAY at the end of turn 4, paid turn 5.
- **Customer 3 (Pasta, 2 units)** needs two Chef turns (8 and 9), so it is served on turn 10 at satisfaction 76.
- **Customer 6** arrives on turn 16 and is seated on turn 17, but cannot finish paying within 18 turns. At most five customers can pay, as the brief says.

### 4.2 Defeat (`stage-1/defeat.in`)

The manager does nothing until turn 5, then plays "serve first, take orders second, seat third". Result: 4 paid, $51.00, average satisfaction 50 (52, 52, 44, 52), so the game is lost on the satisfaction target alone.

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

### 4.3 Invalid inputs (`stage-1/invalid-inputs.in`)

This replays the victory decisions with deliberately bad input on turns 1 to 3. None of it uses up a staff task, and the final result is identical to the clean victory.

| Turn | Input | What the game does |
| --- | --- | --- |
| 1 | Waiter menu: `9` | Rejects it: "Invalid choice! Please enter an integer from 1 to 4." |
| 1 | Waiter menu: `x` | Rejects the non-number the same way |
| 1 | Waiter: serve table 1, which is empty | "Action Failed ... Re-select!" and the Waiter may still choose again |
| 1 | Chef: cook table 1, which has no order | "Action Failed: No pending ORDERED dish at that table. Re-select!" |
| 2 | Waiter: take order, table `7` | Rejects the table number: "enter an integer from 1 to 2" |
| 3 | Waiter: serve table 2, which is empty | "Action Failed ... Re-select!" and the Waiter then serves table 1 |

## 5. Stage 2

### 5.1 Victory (`stage-2/victory.in`)

These are exactly the Stage 1 victory decisions. Only the customer types differ: customer 2 is a VIP (loses 5 per waiting turn, 10% off) and customer 3 is a Critic (loses 12 per waiting turn, always orders the most demanding dish, which is Pasta). Result: 5 paid, $64.50, average satisfaction 80 (84, 90, 74, 76, 76).

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

- **VIP bill:** customer 2 orders a $15.00 Burger and is billed $13.50. Satisfaction is 95 at the end of turn 4 and 90 when paid, because the VIP loses 5 per waiting turn, not 8.
- **Critic reaction, turn 10:** the Waiter has two useful tasks, serving the Critic's ready Pasta or seating customer 4 from the queue. Serving the Critic prints "Critic #3 is impressed by the fast service! +10 satisfaction (now 74)", because the dish is served within 3 turns of arrival.
- **What the other choice would cost:** the run in section 5.3 swaps the two tasks. Seating customer 4 first means the Critic's ready dish waits another turn and still counts as waiting. The Critic is served on turn 11 at satisfaction 52 with no bonus, 22 points lower, and the average satisfaction falls from 80 to 75.

### 5.2 Defeat (`stage-2/defeat.in`)

The manager does nothing until turn 7. Customers 1 and 2 pay late (satisfaction 36 and 60). The Critic arrives on turn 7, loses 12 per turn in the queue and is seated on turn 13 at satisfaction 28. It then loses 12 more at the end of turns 13, 14 and 15, reaches 0 and leaves without paying: "Unhappy Departures: 1", its order is cancelled and its table is freed. Result: 2 paid, $22.50, average satisfaction 48, so all three targets are missed.

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

This replays the victory decisions with one change, so the effect of the turn 10 choice can be seen directly. From turn 10: turn 10 the Waiter seats customer 4 (instead of serving the Critic), turn 11 the Waiter serves the Critic, turn 12 takes customer 4's order (the Chef cooks it), turn 13 serves customer 4. Every other turn is identical to the victory run.

- Turn 10 header: Critic satisfaction 64 and its Pasta is READY, as in the victory run.
- The Critic's dish stays READY through turn 10, so the Critic loses another 12 and is 52 at the start of turn 11.
- On turn 11 the game prints "Critic #3 was kept waiting 4 turns and is unimpressed. No bonus."
- The Critic is paid on turn 13 at satisfaction 52 (74 in the victory run). Result: still a victory, 5 paid, $64.50, but the average satisfaction is 75 instead of 80.

The same check in the Stage 3 code (Host skipped, Waiter seats customer 4 on turn 10) gives the same Critic satisfaction of 52. In Stage 3 the Host normally removes this dilemma by seating customer 4 while the Waiter serves the Critic.

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
- **Happy Hour, turn 4:** activated at the start of turn 4, shown as "AVAILABLE" before and "ACTIVE - 1 turn(s) remaining" in the turn 5 header, then "ENDED (already used this game)" from turn 6. The activation message reports the +15 satisfaction recovery, which lifts the VIP from 95 to 100 at the start of turn 5.
- **Discount order and rounding:** the VIP's Burger + Pasta combo has a base price of $29.70 (90% of $33.00). The VIP discount comes first ($26.73), then Happy Hour 20% off ($21.38). The price is locked when the order is taken, so it does not change when Happy Hour ends.
- **Combos by documented rule:** customers with an even id take a combo of their preferred dish plus the next dish on the menu. Customer 4 orders Salad + Burger ($21.60, 2 units); customer 6 orders Pasta + Salad ($24.30, 3 units, never finished in time).
- **Critic bonus** works unchanged through the Stage 3 code on turn 10 (+10, now 74).

### 6.2 Defeat (`stage-3/defeat.in`)

The manager does nothing until turn 5, never uses the Host and never activates Happy Hour. Result: 3 paid, $53.73, average satisfaction 43 (52, 28, 50), so the game is lost on both the paid count and satisfaction.

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
| Regular customer loses 8 per turn while waiting (3.2, step 5) | Stage 1 victory, customer 1: 100, then 92, then 84 |
| A dish finished this turn cannot be served until next turn (3.2, step 3) | Stage 1 victory, customer 1: READY at the end of turn 2, served turn 3 |
| Served customers lose nothing more (3.2, step 5) | Stage 1 victory, customer 1 stays at 84 from turn 3 |
| Eating takes a full turn, payment is the turn after READY_TO_PAY (3.2, step 4) | Stage 1 victory, customer 1: served turn 3, READY_TO_PAY end of turn 4, paid turn 5 |
| Cashier collects one payment per turn, automatically (3.2, step 4) | Stage 3 victory, turns 4, 9, 12, 15, 17 |
| Abandonment cancels the order and frees the table (3.2, step 5) | Stage 2 defeat, Critic, end of turn 15 |
| Invalid choice explains the failure and does not use the task (3.4) | Stage 1 invalid inputs |
| At most five customers can pay (3.4) | All victory runs: customer 6 never pays |
| Money in integer cents, two decimals on screen (2.2) | Stage 3 victory: $21.38 and $24.30 |
| VIP: 5 per turn, 10% off (4.2) | Stage 2 victory, customer 2 |
| Critic: 12 per turn, distinct order and reaction (4.2) | Stage 2 victory turn 10; Stage 2 defeat |
| Host seats one customer per turn in addition to the Waiter (4.3) | Stage 3 victory, turns 1, 4, 7, 10, 13, 16 |
| Combo price is 90% of the components, units add (4.3) | Stage 3 victory: $21.60 for 2 units, $24.30 for 3 units |
| Happy Hour: once per game, two turns, +15, 20% off after the VIP discount (4.3) | Stage 3 victory, turns 4 and 5 |
