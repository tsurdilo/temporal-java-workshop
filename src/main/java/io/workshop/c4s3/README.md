## 1.  External Workflow Stub (sending signals from one workflow to another)
## 2.  Show that signals are recorded even if they are not handled
## 3. Show one possible solutions - disable signals via interceptor
## 4. Talk about possible "attacks" with signals and workflow history
## 5. Talks about how clients sending signals at a very high rate can cause problems, even with continueAsNew

## Future solution
The future solution would be using Update feature that would 
support rejecting an update without writing it to a history.