BUFF = (in?[i:0..3]->out[i]-> BUFF).
BUFF2 = (in[i:0..4]->out[i]-> BUFF2).

WEIRD = (a? -> (b -> STOP | hidden->c -> STOP)).
WEIRD_IMPL = (a -> c -> STOP).

FIG8A = (hidden? -> a -> STOP).
FIG8B = (a? -> STOP).


