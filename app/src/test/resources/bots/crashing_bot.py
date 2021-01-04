import sys
import json
import os

sys.stdin = os.fdopen(sys.stdin.fileno(), 'rb', buffering=0)

turn = 1
while True:
    game_state_string = sys.stdin.readline()
    game_state = json.loads(game_state_string)
    print(f"Turn {turn}", file=sys.stderr, flush=True)

    if len(sys.argv) > 1:
        # uses command line argument to determine when to crash
        if turn == int(sys.argv[1]):
            a = [1, 2, 3]
            b = a[4]  # index out of bounds exception

    # sending decision
    print("move 1 1", flush=True)
    print("done", flush=True)

    turn += 1
