import sys
import random
import time
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
    logger.info("Sending \"NONE\"")
    return "NONE"

def get_action_decision(game_state) -> str:
    logger.info("Sending \"NONE\"")
    return "NONE"

if __name__ == "__main__":
    send_item(get_item())
    send_upgrade(get_upgrade())

    # all logging and errors should be redirected to sys.stderr
    # while all commands sent back to the game engine as decision should
    # be sent in stdout using print
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
