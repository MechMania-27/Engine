import sys
import random
import time
import json

from flask import Flask, render_template, request
import threading

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


app = Flask(__name__)


s = ""

@app.route('/test', methods =["GET", "POST"])
def gfg():
    if request.method == "POST":
        # getting input with name = fname in HTML form
        first_name = request.form.get("fname")
        s = request.form.get("fname")
        return "Your name is "+first_name + last_name
    return render_template("input.html")






if __name__ == "__main__":

    # threading.Thread(target = app.run(host='0.0.0.0')).start()



logger.info(f"About to send item and upgrade")
    send_item(get_item())
    send_upgrade(get_upgrade())

    # all logging and errors should be redirected to sys.stderr
    # while all commands sent back to the game engine as decision should
    # be sent in stdout using print
    while True:



        start_time = time.perf_counter_ns()
        duration = time.perf_counter_ns() - start_time
        logger.info(f"Receiving game state 1 took {duration // 1e6} ms")
        logger.info{}

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


