FCFS_REYES - FIRST-COME, FIRST-SERVED DISK SCHEDULING

FILES
src/FCFSDiskScheduler.java       Calculates FCFS head movements.
src/FCFSDiskScheduleResult.java Stores the calculated results.
src/FCFSPanel.java              Contains the Java Swing interface.
src/FCFSSimulatorApp.java       Opens the application window.
tests/FCFSTest.java              Checks the FCFS calculations.

REQUIREMENTS
- Java JDK installed (java and javac available)

RUN IN VS CODE (WINDOWS)
1. Extract FCFS_REYES.zip.
2. Open the FCFS_REYES folder in VS Code.
3. Open Terminal > New Terminal.
4. Run these commands:

   New-Item -ItemType Directory -Force -Path out

   javac -encoding UTF-8 -d out src\FCFSDiskScheduler.java src\FCFSDiskScheduleResult.java src\FCFSPanel.java src\FCFSSimulatorApp.java tests\FCFSTest.java

   java -cp out FCFSTest

   java -cp out FCFSSimulatorApp

