# Restaurant Rush

SC2002 group assignment: a turn-based restaurant management game in Java with a command-line interface. The player is the manager. Staff time is limited, so each turn the manager decides which customer or order to deal with first.

- **Repository:** https://github.com/RUANLASS/SC2002
- **Lab group:** SCED, group 2
- **Members:** YANG YONGJIA, HARISH BALAMURALI MYTHILI, SHANMUGANATHAN VISHAL, SARIN LAKSHYA ANUJ
- **Video:** https://youtu.be/kMqX5hegKAs

## Contents

1. [Requirements](#requirements)
2. [Stages, folders and tags](#stages-folders-and-tags)
3. [Build and run](#build-and-run)
4. [Stage comparisons](#stage-comparisons)
5. [Reproducible runs](#reproducible-runs)
6. [Design evidence](#design-evidence)
7. [Video timestamps](#video-timestamps)

## Requirements

- JDK 17 or newer (the Cashier uses a Java `record`). Developed and tested with JDK 21.
- No external libraries or network access needed.
- Commands are given for macOS and Linux (bash) and and Windows (PowerShell, built into Windows 10 and 11).

## Stages, folders and tags

Each stage is a complete, runnable version with its own tag and its own folder, so all three can be read side by side.

| Stage | Adds | Folder | Git tag |
| --- | --- | --- | --- |
| 1 Basic operation | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, victory and defeat | `Stage-1/` | `stage-1` |
| 2 Customer behaviour | VIPCustomer and CriticCustomer | `Stage-2/` | `stage-2` |
| 3 New role and menu policy | Host, ComboMeal, Happy Hour | `Stage-3/` | `stage-3` (final) |

Every stage has the same package layout: `engine` (game controller), `model` (entities), `staff` (roles) and `ui` (command-line boundary).

## Build and run

Run these from the repository root.

macOS and Linux (bash):

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

Windows (PowerShell):

```powershell
# Stage 1
javac -d out\stage-1 (Get-ChildItem -Path Stage-1 -Recurse -Filter *.java | ForEach-Object FullName)
java -cp out\stage-1 Main

# Stage 2
javac -d out\stage-2 (Get-ChildItem -Path Stage-2 -Recurse -Filter *.java | ForEach-Object FullName)
java -cp out\stage-2 Main

# Stage 3 (final version)
javac -d out\stage-3 (Get-ChildItem -Path Stage-3 -Recurse -Filter *.java | ForEach-Object FullName)
java -cp out\stage-3 Main
```

To replay a recorded run instead of typing, pipe in an input file.

macOS and Linux (bash):

```bash
java -cp out/stage-3 Main < test-runs/stage-3/victory.in
```

Windows (PowerShell):

```powershell
Get-Content test-runs\stage-3\victory.in | java -cp out\stage-3 Main
```

## Stage comparisons

Each stage is a full copy in its own folder, so GitHub's compare view shows each stage folder as all-new files. To see only what changed, use a local diff from the repository root:

macOS and Linux (bash):

```bash
diff -ru Stage-1 Stage-2   # 1 -> 2
diff -ru Stage-2 Stage-3   # 2 -> 3
```

Windows (PowerShell, needs Git for Windows):

```powershell
git diff --no-index Stage-1 Stage-2   # 1 -> 2
git diff --no-index Stage-2 Stage-3   # 2 -> 3
```

### Stage 1 to Stage 2

Compare view: https://github.com/RUANLASS/SC2002/compare/stage-1...stage-2

| File | Change | Reason |
| --- | --- | --- |
| `Stage-2/engine/ArrivalSchedule.java` | **new** | Chooses which customer type arrives on each turn, using constructor references, so the controller creates customers without any `if` or `switch` on type |
| `Stage-2/model/VIPCustomer.java` | **new** | VIP customer: loses 5 satisfaction per turn, 10% off the bill |
| `Stage-2/model/CriticCustomer.java` | **new** | Critic customer: loses 12 satisfaction per turn, prefers Pasta, gains a bonus if impressed |
| `Stage-2/model/Customer.java` | changed | Added the overridable methods `selectItem`, `onServed` and `getServiceReaction`. Subclasses now override these and the existing `getDecayRate`, `applyDiscount` and `getTypeName`, so the controller never tests the type |
| `Stage-2/engine/RestaurantGame.java` | changed | Arrivals come from an `ArrivalSchedule` instead of hard-coded turn numbers |
| `Stage-2/staff/Waiter.java` | changed | `serveDish` calls `customer.onServed(turn)` so each customer reacts in its own way |
| `Stage-2/ui/GameCLI.java` | changed | Order confirmation shows the dish and the locked bill. Serving prints the customer's reaction |
| `Order`, `Table`, `Chef`, `Cashier`, `Staff`, `MenuItem`, `SingleMenuItem`, `OrderStatus`, `InputValidator`, `Main` (whitespace change only) | unchanged | They do not depend on customer type |

No file was deleted.

### Stage 2 to Stage 3

Compare view: https://github.com/RUANLASS/SC2002/compare/stage-2...stage-3

| File | Change | Reason |
| --- | --- | --- |
| `Stage-3/staff/Host.java` | **new** | New role that seats one customer per turn |
| `Stage-3/model/ComboMeal.java` | **new** | Holds two `MenuItem`s (aggregation) and implements `MenuItem` itself, so `Order`, `Chef`, `Waiter` and `Cashier` handle it unchanged |
| `Stage-3/model/ComboPolicy.java` | **new** | Holds the rule a customer follows to choose a combo |
| `Stage-3/model/HappyHour.java` | **new** | Holds the Happy Hour state, duration, discount and recovery points |
| `Stage-3/engine/RestaurantGame.java` | changed | Owns the Host, ComboPolicy and HappyHour. Adds `hostSeatCustomer`, `activateHappyHour` and the recovery step, and counts Happy Hour down at turn end |
| `Stage-3/model/Customer.java` | changed | New overload `chooseFood(menu, comboPolicy)` can upgrade the preferred dish to a combo |
| `Stage-3/staff/Waiter.java` | changed | `takeOrder` takes the `HappyHour`. The bill is `happyHour.applyTo(customer.applyDiscount(price))`, so the customer discount comes first, then Happy Hour |
| `Stage-3/ui/GameCLI.java` | changed | Happy Hour and Host prompts, Happy Hour status line |
| everything else, including `ArrivalSchedule`, `VIPCustomer`, `CriticCustomer`, `Order`, `Table`, `Chef` and `Cashier` | unchanged | They already work with the new features |

### Corrections applied to every stage

We fixed two rule bugs, because our code did not match the brief's worked example and objective. The first affects Stage 1 only and the second affects all three stages.

| File | Fix | Reason |
| --- | --- | --- |
| `Stage-1/engine/RestaurantGame.java` | `isVictory()` now checks at least 4 paid, revenue of at least $45.00 and average satisfaction of at least 60 | Stage 1 had the satisfaction test inverted, had an extra abandonment limit, and did not check the paid-customer count. Stages 2 and 3 already had the correct rule |
| `model/Order.java` in each stage | A dish served this turn does not start eating until the next turn | Customers paid one turn too early. The fix matches the brief's section 3.3 timeline |

## Reproducible runs

[TEST_NOTES.md](TEST_NOTES.md) has one victory and one defeat per stage and a Stage 2 what-if. Each run is an input file in `test-runs/` with its full expected console output.

## Design evidence

- Stage 1 class diagram: [docs/class-diagram-stage-1.png](docs/class-diagram-stage-1.png)
- Stage 2 class diagram: [docs/class-diagram-stage-2.png](docs/class-diagram-stage-2.png)
- Stage 3 class diagram (final design): [docs/class-diagram-stage-3.png](docs/class-diagram-stage-3.png)
- Sequence diagram (Critic scenario): [docs/sequence-diagram-critic.png](docs/sequence-diagram-critic.png)
- Editable Lucid versions:
  - [Stage 3 class diagram](https://lucid.app/lucidchart/df858552-20ae-40fd-ad71-3b8ac1896168/view)
  - [Stage 1 and 2 class diagrams](https://lucid.app/lucidchart/c2a458fd-7789-4c5e-bdd0-a143e05d2a3a/view)
  - [Sequence diagram](https://lucid.app/lucidchart/03ad86f7-b2db-4a51-9f7d-16c6f6acedbc/edit)
- The PDF report submitted with the project covers the design discussion, functional evidence and reflections.

## Video timestamps

Video: https://youtu.be/kMqX5hegKAs. The video is 16:36 long and has five parts. Times are minutes:seconds.

| Part | Presenter | Starts | Length | Brief segments | What it covers |
| --- | --- | --- | --- | --- | --- |
| 1 | YANG YONGJIA | 0:00 | 3:56 | 1, 2, 4 | Launching the game from the unzipped project, the rules and the manager's choices (a VIP at turn 4, the Critic at turn 10). Shows the decision where the Waiter can either serve the Critic or seat the next customer, not both, and replays the other choice. |
| 2 | HARISH BALAMURALI MYTHILI | 3:56 | 3:39 | 3, 5, 6 | One dish from kitchen to payment (READY, SERVED, READY_TO_PAY, paid); the Stage 1 victory and defeat; the Stage 3 Host, combo meal and Happy Hour; and the Stage 3 victory and defeat. |
| 3 | SHANMUGANATHAN VISHAL | 7:35 | 3:58 | 4, 7 | The three stages and their class diagrams, showing how the VIP, the Critic and the Stage 3 features fit into the code; and the git tag comparisons. |
| 4 | SARIN LAKSHYA ANUJ | 11:33 | 3:33 | 7 | The Critic use case followed step by step in the sequence diagram. |
| 5 | SARIN LAKSHYA ANUJ | 15:06 | 1:30 | None | Design principles in the code: Open-Closed, Liskov Substitution, encapsulation and high cohesion, and Single Responsibility. |

The segment numbers refer to the 7 required segments in section 6.2 of the brief: 1 game launching, 2 manager choices, 3 dish to payment, 4 VIP or Critic behaviour, 5 Stage 3 feature, 6 victory and defeat, 7 stage tag comparisons and diagrams.
