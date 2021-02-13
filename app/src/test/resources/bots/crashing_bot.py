import sys
from mm27_io import receive_gamestate, send_decision, send_item, send_upgrade
from mm27_io import Logger


def process_decision(game_state):
    return ["move 1 1", "done"]


if __name__ == "__main__":
    logger = Logger()

    logger.info("Sending \"NONE\" and \"NONE\" for Item and Upgrade")

    # Item
    send_item("NONE")

    # Upgrade
    send_upgrade("NONE")

    # all logging and errors should be redirected to sys.stderr
    # while all commands sent back to the game engine as decision should
    # be sent in stdout using print
    while True:
        game_state = receive_gamestate()
        logger.debug(f"================ Turn {game_state['turn']} begin ================")
        logger.info(f"I received: {str(game_state):.40s}...")

        if len(sys.argv) > 1:
            if game_state['turn'] == int(sys.argv[1]):
                a = [1, 2, 3]
                b = a[4]

        decision = process_decision(game_state)

        logger.info(f"Sending {decision}")
        for move in decision:
            send_decision(move)
        logger.debug(f"================ Turn {game_state['turn']} end ==================\n")
