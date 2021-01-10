import sys
import json
import os


def process_decision(game_state):
    return ["move 1 1", "done"]


if __name__ == "__main__":
    # needed to make sure stdin can be read from without buffering
    sys.stdin = os.fdopen(sys.stdin.fileno(), 'rb', buffering=0)

    print("Sending \"NONE\" and \"NONE\" for Item and Upgrade", file=sys.stderr, flush=True)

    # Item
    print("NONE")

    # Upgrade
    print("NONE")

    # all logging and errors should be redirected to sys.stderr
    # while all commands sent back to the game engine as decision should
    # be sent in stdout using print
    while True:
        game_state_string = sys.stdin.readline()
        game_state = json.loads(game_state_string)
        # print(f"================ Turn {game_state['turn']} begin ================", file=sys.stderr, flush=True)
        print(f"I received: {str(game_state):.30s}...", file=sys.stderr, flush=True)

        if len(sys.argv) > 1:
            if game_state['turn'] == int(sys.argv[1]):
                a = [1, 2, 3]
                print(a[4], file=sys.stderr, flush=True)

        decision = process_decision(game_state)

        print(f"Sending {decision}", file=sys.stderr, flush=True)
        for move in decision:
            print(move)
        # print(f"================ Turn {game_state['turn']} end ==================\n", file=sys.stderr, flush=True)
