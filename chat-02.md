**Me:** go through the README file now. check if any grammar improvements can be made, rewording, etc. don’t make any changes for now, just suggest

**Claude Code:** I haven’t changed anything. These are my suggestions, with the most important first.

**Errors to fix**

1. **Broken contents list (lines 15–16).** “How a turn is played” and “Rules as implemented” no longer exist in the README. The sections were deleted in `a0fa0b3`. Either restore them or drop items 4 and 5 and renumber the list.  
2. **Stage 1 file list (line 139).** Line 140 says “in each stage”, but this row says “(Stage 1 only)”. Both are right, but the table reads oddly. Suggested: “`Stage-1/engine/RestaurantGame.java`” with the “(Stage 1 only)” note moved into the Reason column.  
3. **Line 113: “(one whitespace change)”.** It only makes sense for `Main`, but it is placed after the whole list. Suggested: “`Main` (whitespace change only)” as a separate note, or “... `InputValidator` and `Main` (whitespace change only)”.

**Wording and grammar**

| Line | Current | Suggestion |
| ----- | ----- | ----- |
| 3 | “Staff time is limited, so every turn the manager decides which customer or order comes first.” | “Staff time is limited, so each turn the manager decides which customer or order to deal with first.” |
| 24 | “Tested with JDK 21.” | “Developed and tested with JDK 21.” Only change this if it’s accurate. |
| 25 | “No libraries and no network needed.” | “No external libraries or network access needed.” |
| 26 | “Commands are given for macOS and Linux (bash) and for Windows (PowerShell, which is built into Windows 10 and 11).” | “Commands are given for macOS and Linux (bash) and Windows (PowerShell, built into Windows 10 and 11).” |
| 30 | “Each stage is a complete, runnable version with its own tag. Each one is a full copy in its own folder, so all three can be read side by side.” | “Each stage is a complete, runnable version with its own tag and its own folder, so all three can be read side by side.” |
| 42 | “From the repository root:” then “macOS and Linux (bash):” | Merge into “Run these from the repository root.” Two separate lines in a row read awkwardly. |
| 76 | “feed in an input file.” | “pipe in an input file.” Also, the two replay lines (78 and 80\) would look better as a bash block and a PowerShell block, matching the rest of the section. |
| 84 | “...shows every folder as new files.” | “...shows each stage folder as all-new files.” |
| 106 | “Says which customer type arrives on which turn, using constructor references. The controller creates...” | “Chooses which customer type arrives on each turn, using constructor references, so the controller creates customers without any `if` or `switch` on type.” Several cells use the fragment-then-sentence pattern. Pick one style. |
| 107–108 | “VIP: decay 5, bill 10% off” | “VIP customer: loses 5 satisfaction per turn, 10% off the bill”. Spell out “decay” and the other terms in plain words. |
| 113 | “They never depended on customer type” | “They do not depend on customer type” (present tense). |
| 115 | “No file was deleted.” | Fine. Alternatively “No files were deleted.” |
| 131 | The Reason cell is empty. | Fill it in, for example “These already work with the new features.” |
| 135 | “We fixed two rule bugs in all three stages because our code did not match the brief’s worked example and objective:” | “We fixed two rule bugs in all three stages, because our code did not match the brief’s worked example and objective.” |
| 139 | “Stage 1 had the satisfaction test inverted, an extra abandonment limit and no paid-count check.” | Add commas for clarity: “the satisfaction test was inverted, there was an extra abandonment limit, and the paid-customer count was not checked.” |
| 140 | “A dish served this turn is not ticked at the end of this turn” | “A dish served this turn does not start eating until the next turn.” “Ticked” is jargon. |
| 144 | “...a Stage 2 what-if. Each run is an input file in `test-runs/` with its full expected console output.” | Add a link: “See TEST\_NOTES.md for...” |
| 152 | One very long bullet with three links | Split it into three sub-bullets. |
| 153 | “Design discussion, functional evidence and reflections: the PDF report submitted with the project” | “The PDF report submitted with the project covers the design discussion, functional evidence and reflections.” |
| 157 | “The video is 16:36 long and has five parts. Times are minutes:seconds from the start of the video.” | “The video is 16:36 long and has five parts. Times are minutes:seconds.” |
| 161 | “Shows the decision where the Waiter can serve the Critic or seat the next customer, not both, with a replay of the other choice.” | “Shows the decision where the Waiter can either serve the Critic or seat the next customer, not both, and replays the other choice.” |
| 162 | “One dish from kitchen to payment (READY, SERVED, READY\_TO\_PAY, paid). The Stage 1 victory...” | Reword the pieces into a more parallel list: “One dish from kitchen to payment; Stage 1 victory and defeat; Stage 3 Host, combo meal and Happy Hour; Stage 3 victory and defeat.” |
| 163 | “The three stages and the class diagrams ..., showing how... The git tag comparisons.” | Same fix: split the sentence fragments, or write them as one parallel list. |
| 165 | “none” | “None” (capitalise it, to match the other cells). |
| 167 | “The segment numbers match the 7 required segments” | “The segment numbers refer to the 7 required segments in section 6.2 of the brief: ...” |

**Formatting**

* The member names are in all caps, which is fine if it matches your matric records. Otherwise use title case.  
* Line 134 says “Corrections applied to every stage”, but the Stage 1 row is Stage 1 only. The heading is fine, but the table intro should make that clear.  
* The video table’s last column is very long. Consider splitting it into two lines per row, or shortening the cells.

**Me:** Make these changes, and commit it yourself.

**Claude Code:** I made the README edits and committed them as `5f2034d` “README wording and grammar edits”. The commit contains only `README.md`. I didn’t push, and the working tree is clean.

I applied every suggestion from my list, with these choices:

* **Broken contents list:** I removed the two dead entries (“How a turn is played” and “Rules as implemented”) and renumbered the list to 7 items. I did not restore those sections. To bring them back, copy them from `2e6a670:README.md`.  
* **Empty reason cell:** The empty cell in the Stage 3 “everything else” row now reads “They already work with the new features”.  
* **Critic row:** The Critic row now says “loses 12 satisfaction per turn, prefers Pasta, gains a bonus if impressed”.  
* **Corrections intro:** The intro now says the first fix affects Stage 1 only and the second affects all three stages.  
* **Title case for member names:** I left the member names in capitals, as you had them.

This commit is separate from the earlier README commits. If you still want to squash them, run `git reset --soft HEAD~N` as before, counting this commit too.

