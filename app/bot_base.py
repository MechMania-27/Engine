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


def receive_gamestate() -> dict:
    """
    Receive the gamestate from the engine, make edits as necessary, then return the usable gamestate
    :return: gamestate ready to be parsed by bot
    """
    gamestate_bytes = sys.stdin.readline()
    gamestate = json.loads(gamestate_bytes)

    # alter the gamestate for ease of parsing later on
    p1_pos = gamestate["p1"]["position"]
    p2_pos = gamestate["p2"]["position"]

    # if player 1 is present, value is b01
    # if player 2 is present, value is b10
    # if both players are present, value is b11
    gamestate["tileMap"]["tiles"][p1_pos["y"]][p1_pos["x"]]["player_present"] = 0
    gamestate["tileMap"]["tiles"][p2_pos["y"]][p2_pos["x"]]["player_present"] = 0
    gamestate["tileMap"]["tiles"][p1_pos["y"]][p1_pos["x"]]["player_present"] |= 1
    gamestate["tileMap"]["tiles"][p2_pos["y"]][p2_pos["x"]]["player_present"] |= 2

    return gamestate


def send_decision(decision: str) -> None:
    """
    Send decision to the engine
    :param decision: Decision to send
    :return: Nothing
    """
    logger.info(f"sending decision \"{decision}\"")
    print(decision)


def get_item() -> str:
    global action_text
    socketio.emit("request", "Please submit an item to use")

    # wait for action_text to be populated (when the user presses submit)
    while action_text is None:
        pass
    item = action_text

    # reset action_text for the next action
    action_text = None

    # display on screen that the bot has received their information
    socketio.emit("request", "Sent. Waiting for next instruction.")
    return item


def get_upgrade() -> str:
    global action_text
    socketio.emit("request", "Please submit an upgrade to use")

    # wait for action_text to be populated (when the user presses submit)
    while action_text is None:
        pass
    upgrade = action_text

    # reset action_text for the next action
    action_text = None

    # display on screen that the bot has received their information
    socketio.emit("request", "Sent. Waiting for next instruction.")
    return upgrade


def get_cell_html(cell: dict) -> str:
    """
    Converts a cell (example below) into raw HTML for a single cell (cells will be contained within
    HTML <table/> attribute)
    ```
    {
      "type": "GRASS",
      "crop": {
        "type": "NONE",
        "growthTimer": 0,
        "value": 0.0
      },
      "p1_item": "NONE",
      "p2_item": "NONE"
    }
    ```

    :param cell: cell dictionary containing the type, crop, and items on the tile
    :return: HTML to be displayed on screen for one cell
    """

    if "player_present" in cell:
        player = cell["player_present"]
        text = None
        if player & 1:
            text = "P1"
        if player & 2:
            text = "P2" if text is None else "P12"
        return text
    elif cell["crop"]["type"] != "NONE":
        return f'{cell["crop"]["type"][:1]}{cell["crop"]["growthTimer"]:02d}'
    else:
        return cell["type"][:3]


def print_board(board: list[list[dict]]) -> str:
    """
    :param board: board (2d array of cell objects)
    :return: HTML for the board to display on screen
    """
    final_str = "<table><tr><td>y\\x</td>"
    col_num = 0
    for _ in board[0]:
        final_str += f"<td>{col_num}</td>"
        col_num += 1
    final_str += "</tr>"

    row_num, col_num = 0, 0
    for col in board:
        final_str += f"<tr><td>{row_num}</td>"
        col_num = 0
        row_num += 1
        for cell in col:
            final_str += f"<td>{get_cell_html(cell)}</td>"
            col_num += 1
        final_str += "</tr>"
    return final_str + "</table>"


def get_move_decision(game_state) -> str:
    player_num = game_state['playerNum']
    pos = game_state[f"p{player_num}"]["position"]
    logger.info(f"Currently at ({pos['x']},{pos['y']})")

    global action_text
    socketio.emit("request", f"Please submit a move decision.<br>Board State:<br>" +
                  print_board(game_state["tileMap"]["tiles"]) +
                  f"\nPlayer: {player_num}, Current " +
                  f"position: ({pos['x']},{pos['y']})")

    # wait for action_text to be populated (when the user presses submit)
    while action_text is None:
        pass
    move = action_text

    # reset action_text for the next action
    action_text = None

    # display on screen that the bot has received their information
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

    # wait for action_text to be populated (when the user presses submit)
    while action_text is None:
        pass
    action = action_text

    # reset action_text for the next action
    action_text = None

    # display on screen that the bot has received their information
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
    send_decision(get_item())
    send_decision(get_upgrade())

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