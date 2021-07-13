import json
import sys
import threading
import signal
import time
from flask import Flask, render_template, request
from flask_socketio import SocketIO, emit


class Logger:
    def __init__(self) -> None:
        pass

    def info(self, message: str) -> None:
        print(f"info: {message}", file=sys.stderr, flush=True)

    def debug(self, message: str) -> None:
        print(f"debug: {message}", file=sys.stderr, flush=True)


logger = Logger()

signal.signal(signal.SIGTERM, lambda: exit(0))

action_text = None

cli = sys.modules['flask.cli']
cli.show_server_banner = lambda *x: None

app = Flask(__name__)
socketio = SocketIO(app)


@socketio.on('on_conection')
def handle_on_connection(data):
    logger.info('connected! received data: ' + str(data))


@socketio.on('submit')
def receive_action(text):
    global action_text
    action_text = text['text']


def receive_gamestate():
    gamestate_bytes = sys.stdin.readline()
    return json.loads(gamestate_bytes)


def send_decision(decision: str) -> None:
    logger.info(f"sending decision \"{decision}\"")
    print(decision)


def send_item(item: str) -> None:
    logger.info(f"sending item \"{item}\"")
    print(item)


def send_upgrade(upgrade: str) -> None:
    logger.info(f"sending upgrade \"{upgrade}\"")
    print(upgrade)


def get_item() -> str:
    global action_text
    socketio.emit("request", "Please submit an item to use")
    while action_text is None:
        pass
    item = action_text
    action_text = None
    socketio.emit("request", "Sent. Waiting for next instruction.")
    return item


def get_upgrade() -> str:
    global action_text
    socketio.emit("request", "Please submit an upgrade to use")
    while action_text is None:
        pass
    upgrade = action_text
    action_text = None
    socketio.emit("request", "Sent. Waiting for next instruction.")
    return upgrade


def print_board(board) -> str:
    final_str = "<tt>"
    for col in board:
        for cell in col:
            if cell["crop"]["type"] != "NONE":
                cell_text = f'{cell["crop"]["type"][:1]}{cell["crop"]["growthTimer"]:02d}'
            else:
                cell_text = cell["type"][:3]
            final_str += cell_text + " "
        final_str += "<br>"
    return final_str + "</tt>"


def get_move_decision(game_state) -> str:
    player_num = game_state['playerNum']
    pos = game_state[f"p{player_num}"]["position"]
    logger.info(f"Currently at ({pos['x']},{pos['y']})")

    global action_text
    socketio.emit("request", f"Please submit a move decision.<br>Board State:<br>" +
                  print_board(game_state["tileMap"]["tiles"]) +
                  f"\nPlayer: {player_num}, Current " +
                  f"position: ({pos['x']},{pos['y']})")
    while action_text is None:
        pass
    move = action_text

    action_text = None
    socketio.emit("request", "Sent. Waiting for next instruction.")
    return move


def get_action_decision(game_state) -> str:
    player_num = game_state['playerNum']
    pos = game_state[f"p{player_num}"]["position"]

    global action_text
    socketio.emit("request", f"Please submit an action decision.\nBoard State:\n" +
                  print_board(game_state["tileMap"]["tiles"]) +
                  f"\nPlayer: {player_num}, Current " +
                  f"position: ({pos['x']},{pos['y']})")
    while action_text is None:
        pass
    action = action_text

    logger.info(f"Sending \"{action:.30s}\"")
    action_text = None
    socketio.emit("request", "Sent. Waiting for next instruction.")
    return action


@app.route('/test', methods=["GET", "POST"])
def test():
    if request.method == "POST":
        input = request.form['input_command']
        send_decision(input)
        logger.info(f"Sending {input} as decision")
        return render_template("input.html")

    return render_template("input.html")


if __name__ == "__main__":
    logger.info("Starting Flask server")
    socketio_app = threading.Thread(
        target=lambda: socketio.run(app, host="0.0.0.0", port=8080, debug=False,
                                    use_reloader=False))
    socketio_app.daemon = True
    socketio_app.start()

    time.sleep(3)
    logger.info("About to send item and upgrade")
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
