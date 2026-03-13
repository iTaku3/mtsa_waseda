//Constants 
//directions
const North	= 0
const East	= 1
const South	= 2
const West	= 3
range Dir = North..West
const Dirs = 4

//Map
const Rows  = 2 
const Cols  = 2 
const Cells = Rows*Cols
range Loc = 0..Cells-1

//Loading
const Unloaded = 0
const Loaded = 1
range LoadUnload = Unloaded..Loaded

//Alphabets
set Actions = {left,right,follow}
set ActionCommands = {try.{Actions},relocate, tryPickupFrom.{in,r}, in.try.putdownAt.r, r.try.putdownAt.out}
set ControllableActions = {ActionCommands}
set FailActions = {in.putdownAt.fail.r, r.putdownAt.fail.out, {in, r}.pickup.fail}
set UncontrollableActions = {FailActions,Actions.{suc},{in,r}.pickup.suc, {in,r}.loaded}
set Alphabet = {ControllableActions,UncontrollableActions}

//ARMs
ARM(A='in, R='robot) = ([A].loaded -> LOADED),
LOADED = (tryPickupFrom.[A] -> TRY_PICK), 
TRY_PICK = ([A].pickup.suc -> PICKED  
			|[A].pickup.fail -> LOADED),
PICKED = ([A].try.putdownAt.[R] -> TRY_PUT), 
TRY_PUT = ([A].putdownAt.suc.[R] -> [R].loaded-> ARM
		| [A].putdownAt.fail.[R]->PICKED).

||ARMS = (ARM('in,'r) || ARM('r,'out)).

MAP	= MAP[0][0][South],
MAP[x:0..Cols][y:0..Rows][facing:Dir] = 
	( try.right -> MAP[x][y][(facing + 1) % Dirs]
	| try.left -> MAP[x][y][(facing + (Dirs - 1)) % Dirs]
	| when (x > 0 && facing == East) 
					try.follow -> MAP[x - 1][y][facing]
	| when (x < (Cols - 1) && facing == West) 
					try.follow -> MAP[x + 1][y][facing]
	| when (y > 0 && facing == North) 
					try.follow -> MAP[x][y - 1][facing]
	| when (y < (Rows - 1) && facing == South) 
					try.follow -> MAP[x][y + 1][facing]
	| when (x == 0 && y == 0) 
					tryPickupFrom.in -> MAP[x][y][facing]
	| when ((x == (Rows - 1)) && (y == (Cols - 1))) 
					r.try.putdownAt.out -> MAP[x][y][facing]).

ROBOT = MOVE[Unloaded],
MOVE[loadStatus:LoadUnload]= 
	(when (loadStatus == Unloaded) tryPickupFrom.in -> MOVE[loadStatus]
	|when (loadStatus == Loaded) r.try.putdownAt.out -> MOVE[loadStatus]
	| in.putdownAt.suc.r -> MOVE[Loaded]
	| r.putdownAt.suc.out -> MOVE[Unloaded]
	|when (loadStatus == Loaded) try[action:Actions]->TRY[loadStatus][action]),
TRY[loadStatus:LoadUnload][action:Actions] = 
			([action].lost->RELOCATE[loadStatus]
			|[action].suc->MOVE[loadStatus]),
RELOCATE[loadStatus:LoadUnload] = 
			(relocate->MOVE[loadStatus]).

//Composed Environment Model
||Scenario = ( MAP || ROBOT || ARMS).

//Fluent definitions
fluent INTRAY_FULL = <in.loaded, r.loaded> 
fluent ADDED_TO_OUTTRAY = <out.loaded, Alphabet\{out.loaded}> 
fluent F_FAILURES = <FailActions,Alphabet\{FailActions}>



assert TESTGOAL = ([]<>IntrayFull -> []<>UnloadOut)
//Problem Assertions
assert IntrayFull = INTRAY_FULL
assert UnloadOut = ADDED_TO_OUTTRAY
assert Failures = F_FAILURES

//Goal Specifications
controllerSpec Goal = {
	failure = {Failures}	
	assumption = {IntrayFull}
	liveness = {UnloadOut}
	controllable = {ControllableActions}
}

//Controller Definitions
controller ||C = (Scenario)~{Goal}.


