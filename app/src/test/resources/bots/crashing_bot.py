import sys
import random
from mm27_io import receive_gamestate, send_decision, send_item, send_upgrade
from mm27_io import Logger

logger = Logger()


def get_item() -> str:
    logger.info("Sending \"NONE\"")
    return "NONE"


def get_upgrade() -> str:
    logger.info("Sending \"NONE\"")
    return "NONE"


def get_move_decision(game_state) -> str:
    x = random.randint(0, game_state['tileMap']['mapWidth'])
    y = random.randint(0, game_state['tileMap']['mapHeight'])

    move = f"move {x} {y}"
    logger.info(f"Sending \"{move}\"")
    return move


def get_action_decision(game_state) -> str:
    x = random.randint(0, game_state['tileMap']['mapWidth'])
    y = random.randint(0, game_state['tileMap']['mapHeight'])
    crop = "CORN"

    action = f"plant {crop} {x} {y}"
    logger.info(f"Sending \"{action}\"")
    return action


def crash_on_turn(curr_turn: int, turn: int) -> None:
    if curr_turn == turn:
        a = [1, 2, 3]
        b = a[4]


if __name__ == "__main__":
    send_item(get_item())
    send_upgrade(get_upgrade())

    # all logging and errors should be redirected to sys.stderr
    # while all commands sent back to the game engine as decision should
    # be sent in stdout using print
    while True:
        game_state = receive_gamestate()
        move_decision = get_move_decision(game_state)
        send_decision(move_decision)

        crash_on_turn(game_state['turn'], int(sys.argv[1]))

        game_state = receive_gamestate()
        action_decision = get_action_decision(game_state)
        send_decision(action_decision)
