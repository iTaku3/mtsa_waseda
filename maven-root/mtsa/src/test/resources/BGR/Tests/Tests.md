These lts files are tests designed for the BGR integration testing.

For non controllable problems place a new lts file in the NonControllable directory.

For controllable problems a "Test" assertion needs to be defined in the file:

`assert Test = ([]<> Buchi && ([]<>Assumption -> []<> Guarantee))`

This ltl property will be checked against the composition on the tests.