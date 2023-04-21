# Implementation of MOM Paradigm using JavaRMI (Pràctica 1 CDA)
## How to run it?
To run and test this you'll need three terminals for basic elements and x terminals depending on the workers processess you want.
* - On the first terminal run `rmiregistry`.
* - On the second one run the server file after compile it `java Server`.
* - On the third one run the `Java DisSumMaster <range-compute> <num-workers> <ip-server>`.
* - Lastly, open as many terminals with `java DisSumWorker <ip-server>` as workers you want.
