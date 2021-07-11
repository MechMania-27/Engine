import json
import sys
import threading
import time
from flask import Flask, render_template, request


def receive_gamestate():
    gamestate_bytes = sys.stdin.readline()
    return json.loads(gamestate_bytes)


def send_decision(decision: str) -> None:
    print(decision)


def send_item(item: str) -> None:
    print(item)


def send_upgrade(upgrade: str) -> None:
    print(upgrade)


class Logger:
    def __init__(self) -> None:
        pass

    def info(self, message: str) -> None:
        print(f"info: {message}", file=sys.stderr, flush=True)

    def debug(self, message: str) -> None:
        print(f"debug: {message}", file=sys.stderr, flush=True)


logger = Logger()


def get_item() -> str:
    item = "NONE"
    logger.info(f"Sending \"{item}\"")
    return item


def get_upgrade() -> str:
    upgrade = "NONE"
    logger.info(f"Sending \"{upgrade}\"")
    return upgrade


def get_move_decision(game_state) -> str:
    player_num = game_state['playerNum']
    pos = game_state[f"p{player_num}"]["position"]
    logger.info(f"Currently at ({pos['x']},{pos['y']})")
    move = f"move {pos['x']} {pos['y']}"

    logger.info(f"Sending \"{move}\"")
    return move


def get_action_decision(game_state) -> str:
    player_num = game_state['playerNum']
    pos = game_state[f"p{player_num}"]["position"]

    action = f"harvest"
    if action == "harvest":
        action += f" {pos['x']} {pos['y']}"

    logger.info(f"Sending \"{action:.30s}\"")
    return action


cli = sys.modules['flask.cli']
cli.show_server_banner = lambda *x: None

app = Flask(__name__)


@app.route('/test', methods=["GET", "POST"])
def test():
    if request.method == "POST":
        input = request.form['input_command']
        send_decision(input)
        logger.info(f"Sending {input} as decision")
        return render_template("input.html")

    return render_template("input.html")


if __name__ == "__main__":
    threading.Thread(target=app.run).start()
    logger.info(f"About to send item and upgrade")
    send_item(get_item())
    send_upgrade(get_upgrade())

    while True:
        start_time = time.perf_counter_ns()
        game_state = receive_gamestate()
        duration = time.perf_counter_ns() - start_time
        logger.info(f"Receiving game state 1 took {duration // 1e6} ms")

        start_time = time.perf_counter_ns()
        move_decision = get_move_decision(game_state)
        duration = time.perf_counter_ns() - start_time
        logger.info(f"Move decision took {duration // 1e6} ms")

        start_time = time.perf_counter_ns()
        send_decision(move_decision)
        duration = time.perf_counter_ns() - start_time
        logger.info(f"Send move decision took {duration // 1e6} ms")

        start_time = time.perf_counter_ns()
        game_state = receive_gamestate()
        duration = time.perf_counter_ns() - start_time
        logger.info(f"Receiving game state 2 took {duration // 1e6} ms")

        start_time = time.perf_counter_ns()
        action_decision = get_action_decision(game_state)
        duration = time.perf_counter_ns() - start_time
        logger.info(f"Action decision took {duration // 1e6} ms")

        start_time = time.perf_counter_ns()
        send_decision(action_decision)
        duration = time.perf_counter_ns() - start_time
        logger.info(f"Sending action decision took {duration // 1e6} ms")

    # all logging and errors should be redirected to sys.stderr
    # while all commands sent back to the game engine as decision should
    # be sent in stdout using print
