from integration.buffer import Buffer
from utils.helper import Helper
from utils.log_handler import LogHandler
import sys

def verify_args(arguments: list) -> list:
    if len(arguments) < 2:
        Helper.print_usage()
        raise SystemExit(1)
    return [arg for arg in arguments]

def main():
    args = sys.argv[1:]
    address, port = verify_args(args)
    buffer = Buffer(address, int(port), LogHandler())
    # TODO: create correct loop, obj for send to buffer and dont sent message probability
    while True:
        buffer.send_message(input('enter message: '))

if __name__ == '__main__':
    main()
