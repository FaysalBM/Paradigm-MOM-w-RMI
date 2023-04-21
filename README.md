# Implementation of MOM Paradigm using JavaRMI
## How to run it?
To run and test this you'll need three terminals for basic elements and x terminals depending on the workers processess you want.
1 - On the first terminal run `rmiregistry`.
2 - On the second one run the server file after compile it `java Server`.
3 - On the third one run the `Java DisSumMaster <range-compute> <num-workers> <ip-server>`.
4 - Lastly, open as many terminals with `java DisSumWorker <ip-server>` as workers you want.
